const http = require('http');
const { spawn } = require('child_process');
const fs = require('fs');

const edgePath = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe';
const debugPort = 9334;

const viewports = [
  { name: '320px — minimum supported phone width', width: 320, height: 640 },
  { name: '360px — compact Android phone', width: 360, height: 640 },
  { name: '390px — standard modern phone', width: 390, height: 844 },
  { name: '430px — large phone', width: 430, height: 932 },
  { name: '768px — tablet', width: 768, height: 1024 },
  { name: '1024px — laptop/small desktop', width: 1024, height: 768 },
  { name: '1440px — desktop', width: 1440, height: 900 }
];

const routes = [
  '/',
  '/jobs',
  '/jobs/1',
  '/login',
  '/register',
  '/seeker/dashboard',
  '/seeker/applications',
  '/seeker/resume',
  '/recruiter/dashboard',
  '/recruiter/applications',
  '/recruiter/candidates',
  '/recruiter/jobs',
  '/recruiter/jobs/new',
  '/recruiter/job-intelligence'
];

async function main() {
  console.log('Starting Edge in headless mode with remote debugging on port', debugPort);
  const proc = spawn(edgePath, [
    '--headless=new',
    '--remote-debugging-port=' + debugPort,
    '--disable-gpu',
    '--no-first-run',
    '--no-default-browser-check',
    'about:blank'
  ]);

  await new Promise(r => setTimeout(r, 2000));

  // Get WebSocket URL
  const versionData = await new Promise((resolve, reject) => {
    http.get(`http://127.0.0.1:${debugPort}/json/version`, res => {
      let d = '';
      res.on('data', chunk => d += chunk);
      res.on('end', () => resolve(JSON.parse(d)));
    }).on('error', reject);
  });

  // Get pages list from /json/list
  const targets = await new Promise((resolve, reject) => {
    http.get(`http://127.0.0.1:${debugPort}/json/list`, res => {
      let d = '';
      res.on('data', chunk => d += chunk);
      res.on('end', () => resolve(JSON.parse(d)));
    }).on('error', reject);
  });

  const pageTarget = targets.find(t => t.type === 'page') || targets[0];
  const wsUrl = pageTarget.webSocketDebuggerUrl;
  console.log('Connecting to Page WebSocket:', wsUrl);

  const WebSocket = require('net');
  // Use a simple CDP client via ws
  // Since ws package might not be in node_modules, let's write a standard HTTP / WebSocket client or use Node's built-in fetch/WebSocket in Node 24!
  // Node 24 has global WebSocket!
  const ws = new globalThis.WebSocket(wsUrl);

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

  console.log('Connected to CDP. Enabling domains...');

  function send(method, params = {}) {
    return new Promise((resolve, reject) => {
      const id = msgId++;
      pending.set(id, { resolve, reject });
      ws.send(JSON.stringify({ id, method, params }));
    });
  }

  await send('Page.enable');
  await send('DOM.enable');
  await send('Runtime.enable');

  const results = [];

  for (const vp of viewports) {
    console.log(`\n======================================================`);
    console.log(`TESTING VIEWPORT: ${vp.name} (${vp.width}x${vp.height})`);
    console.log(`======================================================`);

    for (const route of routes) {
      // Set device metrics explicitly for this route
      await send('Emulation.setDeviceMetricsOverride', {
        width: vp.width,
        height: vp.height,
        deviceScaleFactor: 1,
        mobile: vp.width < 768
      });

      const url = 'http://localhost:8080' + route;
      await send('Page.navigate', { url });

      // Wait for page to load
      await new Promise(r => setTimeout(r, 600));

      // Evaluate document scrollWidth and check for overflow
      const evalRes = await send('Runtime.evaluate', {
        expression: `(() => {
          const docEl = document.documentElement;
          const body = document.body;
          const innerW = window.innerWidth;
          const scrollW = Math.max(docEl.scrollWidth, body.scrollWidth);
          const hasHScroll = scrollW > innerW + 1; // 1px threshold for subpixel

          let overflowing = [];
          if (hasHScroll) {
            const all = document.querySelectorAll('*');
            for (let el of all) {
              const r = el.getBoundingClientRect();
              if (r.right > innerW + 1) {
                overflowing.push({
                  tag: el.tagName,
                  id: el.id,
                  cls: (el.className || '').toString().slice(0, 50),
                  right: Math.round(r.right),
                  width: Math.round(r.width)
                });
                if (overflowing.length >= 3) break;
              }
            }
          }

          // Check if hamburger button or navigation exists
          const hasMobileMenuBtn = !!document.getElementById('public-menu-btn') || !!document.querySelector('[onclick*="toggleAppSidebar"]');

          return {
            innerW,
            scrollW,
            hasHScroll,
            overflowing,
            hasMobileMenuBtn
          };
        })()`,
        returnByValue: true
      });

      const res = evalRes.result.value;
      const status = res.hasHScroll ? 'FAIL (OVERFLOW)' : 'PASS';
      console.log(`  [${status}] ${route} -> innerW: ${res.innerW}px, scrollW: ${res.scrollW}px`);
      if (res.hasHScroll) {
        console.log(`    Overflowing elements:`, JSON.stringify(res.overflowing));
      }

      results.push({
        viewport: vp.name,
        width: vp.width,
        route,
        passed: !res.hasHScroll,
        scrollW: res.scrollW,
        innerW: res.innerW,
        overflowing: res.overflowing
      });
    }
  }

  // Summary
  console.log('\n======================================================');
  console.log('SUMMARY TEST RESULTS:');
  const total = results.length;
  const passed = results.filter(r => r.passed).length;
  const failed = results.filter(r => !r.passed).length;
  console.log(`Total tests: ${total} | Passed: ${passed} | Failed: ${failed}`);
  console.log('======================================================');

  ws.close();
  proc.kill();

  fs.writeFileSync('test_results.json', JSON.stringify(results, null, 2));
  process.exit(failed > 0 ? 1 : 0);
}

main().catch(err => {
  console.error('Test runner error:', err);
  process.exit(1);
});
