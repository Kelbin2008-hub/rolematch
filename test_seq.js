const http = require('http');
const { spawn } = require('child_process');

const edgePath = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe';
const debugPort = 9351;

async function testSequence() {
  const proc = spawn(edgePath, [
    '--headless=new',
    '--remote-debugging-port=' + debugPort,
    '--disable-gpu',
    'about:blank'
  ]);
  await new Promise(r => setTimeout(r, 1500));

  const targets = await new Promise((res, rej) => {
    http.get(`http://127.0.0.1:${debugPort}/json/list`, r => {
      let d = '';
      r.on('data', chunk => d += chunk);
      r.on('end', () => res(JSON.parse(d)));
    }).on('error', rej);
  });

  const ws = new globalThis.WebSocket(targets[0].webSocketDebuggerUrl);
  let id = 1;
  const pending = new Map();
  ws.onmessage = e => {
    const d = JSON.parse(e.data);
    if (d.id && pending.has(d.id)) {
      pending.get(d.id).resolve(d.result);
      pending.delete(d.id);
    }
  };
  await new Promise(r => ws.onopen = r);

  function send(method, params = {}) {
    return new Promise(resolve => {
      const msgId = id++;
      pending.set(msgId, { resolve });
      ws.send(JSON.stringify({ id: msgId, method, params }));
    });
  }

  await send('Page.enable');
  await send('DOM.enable');

  await send('Emulation.setDeviceMetricsOverride', { width: 320, height: 640, deviceScaleFactor: 1, mobile: false });
  
  // Navigate to /
  await send('Page.navigate', { url: 'http://localhost:8080/' });
  await new Promise(r => setTimeout(r, 600));
  const shot = await send('Page.captureScreenshot', { format: 'png' });

  // Navigate to /jobs
  await send('Page.navigate', { url: 'http://localhost:8080/jobs' });
  await new Promise(r => setTimeout(r, 600));

  const res = await send('Runtime.evaluate', {
    expression: `(() => {
      return {
        innerW: window.innerWidth,
        scrollW: Math.max(document.documentElement.scrollWidth, document.body.scrollWidth),
        docScrollW: document.documentElement.scrollWidth,
        bodyScrollW: document.body.scrollWidth
      };
    })()`,
    returnByValue: true
  });

  console.log('Jobs after Landing:', res.result.value);

  ws.close();
  proc.kill();
}

testSequence();

