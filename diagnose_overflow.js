const http = require('http');
const { spawn } = require('child_process');

const edgePath = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe';
const debugPort = 9390;

async function checkOffenders(path, width) {
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

  await send('Emulation.setDeviceMetricsOverride', {
    width: width,
    height: 800,
    deviceScaleFactor: 1,
    mobile: false
  });

  await send('Page.navigate', { url: 'about:blank' });
  await new Promise(res => setTimeout(res, 100));

  await send('Page.navigate', { url: 'http://localhost:8080' + path });
  await new Promise(res => setTimeout(res, 800));

  const evalRes = await send('Runtime.evaluate', {
    expression: `(() => {
      const innerW = window.innerWidth;
      const docW = document.documentElement.scrollWidth;
      const offenders = [];
      document.querySelectorAll('*').forEach(el => {
        const rect = el.getBoundingClientRect();
        if (rect.right > innerW + 1 || el.scrollWidth > innerW + 1) {
          offenders.push({
            tag: el.tagName,
            id: el.id,
            className: (el.className || '').toString().slice(0, 80),
            rectRight: Math.round(rect.right),
            scrollWidth: el.scrollWidth,
            textSnippet: (el.innerText || '').slice(0, 40).replace(/\\n/g, ' ')
          });
        }
      });
      return { innerW, docW, totalOffenders: offenders.length, topOffenders: offenders.slice(0, 15) };
    })()`,
    returnByValue: true
  });

  console.log(`\n=== PATH: ${path} @ ${width}px ===`);
  console.log(JSON.stringify(evalRes.result.value, null, 2));

  ws.close();
}

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

  try {
    await checkOffenders('/jobs', 320);
    await checkOffenders('/jobs/1', 320);
    await checkOffenders('/register', 320);
  } finally {
    proc.kill();
  }
}

run().catch(console.error);

