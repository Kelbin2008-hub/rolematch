const http = require('http');
const { spawn } = require('child_process');

const edgePath = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe';
const debugPort = 9390;

const testCases = [
  { path: '/jobs', vp: 320 },
  { path: '/jobs', vp: 360 },
  { path: '/jobs', vp: 1024 },
  { path: '/jobs/1', vp: 320 },
  { path: '/register', vp: 320 },
  { path: '/register', vp: 360 },
  { path: '/register', vp: 390 },
  { path: '/register', vp: 768 }
];

async function run() {
  const proc = spawn(edgePath, [
    '--headless=new',
    '--remote-debugging-port=' + debugPort,
    '--disable-gpu',
    '--no-first-run',
    '--no-default-browser-check',
    'about:blank'
  ]);

  await new Promise(r => setTimeout(r, 1500));

  const targets = await new Promise((resolve, reject) => {
    http.get(`http://127.0.0.1:${debugPort}/json/list`, res => {
      let d = '';
      res.on('data', chunk => d += chunk);
      res.on('end', () => resolve(JSON.parse(d)));
    }).on('error', reject);
  });

  const pageTarget = targets.find(t => t.type === 'page') || targets[0];
  const ws = new globalThis.WebSocket(pageTarget.webSocketDebuggerUrl);

  let msgId = 1;
  const pending = new Map();

  ws.onmessage = (event) => {
    const data = JSON.parse(event.data);
    if (data.id && pending.has(data.id)) {
      const { resolve, reject } = pending.get(data.id);
      pending.delete(data.id);
      if (data.error) reject(data.error);
      else resolve(data.result);
    }
  };

  await new Promise((resolve, reject) => {
    ws.onopen = resolve;
    ws.onerror = reject;
  });

  function send(method, params = {}) {
    return new Promise((resolve, reject) => {
      const id = msgId++;
      pending.set(id, { resolve, reject });
      ws.send(JSON.stringify({ id, method, params }));
    });
  }

  await send('Page.enable');
  await send('Runtime.enable');

  for (const tc of testCases) {
    await send('Emulation.setDeviceMetricsOverride', {
      width: tc.vp,
      height: 700,
      deviceScaleFactor: 1,
      mobile: tc.vp <= 768
    });

    await send('Page.navigate', { url: 'about:blank' });
    await new Promise(res => setTimeout(res, 100));

    await send('Page.navigate', { url: 'http://localhost:8080' + tc.path });
    await new Promise(res => setTimeout(res, 600));

    const evalRes = await send('Runtime.evaluate', {
      expression: `(() => {
        const innerW = window.innerWidth;
        const docEl = document.documentElement;
        const body = document.body;
        const scrollW = Math.max(docEl.scrollWidth, body.scrollWidth);
        const hasHScroll = scrollW > innerW + 1;

        let offenders = [];
        if (hasHScroll) {
          const all = document.querySelectorAll('*');
          for (let el of all) {
            const rect = el.getBoundingClientRect();
            if (rect.right > innerW + 1) {
              offenders.push({
                tag: el.tagName,
                id: el.id,
                className: (el.className || '').toString().slice(0, 70),
                right: Math.round(rect.right),
                width: Math.round(rect.width)
              });
              if (offenders.length >= 6) break;
            }
          }
        }
        return { innerW, scrollW, hasHScroll, offenders };
      })()`,
      returnByValue: true
    });

    const data = evalRes.result.value;
    console.log(`[${tc.path} @ ${tc.vp}px] -> inner: ${data.innerW}, scroll: ${data.scrollW}, hasHScroll: ${data.hasHScroll}`);
    if (data.offenders.length > 0) {
      console.log('   Offenders:', JSON.stringify(data.offenders, null, 2));
    }
  }

  ws.close();
  proc.kill();
}

run().catch(console.error);

