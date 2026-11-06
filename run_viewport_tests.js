const http = require('http');
const { spawn } = require('child_process');
const fs = require('fs');

const edgePath = 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe';
const debugPort = 9390;

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
  { path: '/', label: 'Landing Page' },
  { path: '/jobs', label: 'Job Board' },
  { path: '/jobs/1', label: 'Job Detail' },
  { path: '/login', label: 'Sign In' },
  { path: '/register', label: 'Register' },
  { path: '/seeker/dashboard', label: 'Seeker Dashboard' },
  { path: '/seeker/applications', label: 'Seeker Applications' },
  { path: '/seeker/resume', label: 'Resume Hub' },
  { path: '/recruiter/dashboard', label: 'Recruiter Dashboard' },
  { path: '/recruiter/applications', label: 'Applications Pipeline' },
  { path: '/recruiter/candidates', label: 'Candidates Talent Pool' },
  { path: '/recruiter/jobs', label: 'Manage Jobs' },
  { path: '/recruiter/jobs/new', label: 'Job Requisition Creator' },
  { path: '/recruiter/job-intelligence', label: 'Job Intelligence' }
];

async function run() {
  console.log('Launching Edge headless CDP on port', debugPort);
  const proc = spawn(edgePath, [
    '--headless=new',
    '--remote-debugging-port=' + debugPort,
    '--disable-gpu',
    '--no-first-run',
    '--no-default-browser-check',
    'about:blank'
  ]);

  await new Promise(r => setTimeout(r, 2000));

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
  await send('DOM.enable');
  await send('Runtime.enable');

  const fullReport = [];

  for (const vp of viewports) {
    console.log(`\n========================================================================`);
    console.log(`TESTING VIEWPORT: ${vp.name} (${vp.width}x${vp.height})`);
    console.log(`========================================================================`);

    for (const r of routes) {
      // 1. Reset device metrics
      await send('Emulation.setDeviceMetricsOverride', {
        width: vp.width,
        height: vp.height,
        deviceScaleFactor: 1,
        mobile: false
      });

      // 2. Clear page cache by navigating to blank
      await send('Page.navigate', { url: 'about:blank' });
      await new Promise(res => setTimeout(res, 150));

      // 3. Navigate to actual target route
      const url = 'http://localhost:8080' + r.path;
      await send('Page.navigate', { url });
      await new Promise(res => setTimeout(res, 700));

      const evalRes = await send('Runtime.evaluate', {
        expression: `(() => {
          const innerW = window.innerWidth;
          const docEl = document.documentElement;
          const body = document.body;
          const scrollW = Math.max(docEl.scrollWidth, body.scrollWidth);
          const hasHScroll = scrollW > innerW + 1;

          let overflowing = [];
          if (hasHScroll) {
            const all = document.querySelectorAll('*');
            for (let el of all) {
              const rect = el.getBoundingClientRect();
              if (rect.right > innerW + 1) {
                overflowing.push({
                  tag: el.tagName,
                  id: el.id,
                  cls: (el.className || '').toString().slice(0, 50),
                  right: Math.round(rect.right),
                  width: Math.round(rect.width)
                });
                if (overflowing.length >= 3) break;
              }
            }
          }

          const hasPublicMobileNav = !!document.getElementById('public-menu-btn');
          const hasAppSidebarToggle = !!document.querySelector('[onclick*="toggleAppSidebar"]');

          return {
            innerW,
            scrollW,
            hasHScroll,
            overflowing,
            hasMobileNav: hasPublicMobileNav || hasAppSidebarToggle
          };
        })()`,
        returnByValue: true
      });

      const data = evalRes.result ? evalRes.result.value : null;
      if (!data) {
        console.error(`  [WARN] No evaluation data returned for ${r.path}`);
        continue;
      }

      const pass = !data.hasHScroll;
      const statusStr = pass ? 'PASS' : 'FAIL';

      console.log(`  [${statusStr}] ${r.label} (${r.path}) -> inner: ${data.innerW}px | scroll: ${data.scrollW}px | overflow: ${data.hasHScroll ? 'YES' : 'NONE'}`);

      fullReport.push({
        viewportWidth: vp.width,
        viewportName: vp.name,
        routePath: r.path,
        routeLabel: r.label,
        passed: pass,
        innerW: data.innerW,
        scrollW: data.scrollW,
        overflowing: data.overflowing
      });
    }
  }

  ws.close();
  proc.kill();

  fs.writeFileSync('viewport_testing_report.json', JSON.stringify(fullReport, null, 2));

  console.log('\n========================================================================');
  const total = fullReport.length;
  const passed = fullReport.filter(x => x.passed).length;
  const failed = fullReport.filter(x => !x.passed).length;
  console.log(`FINAL VIEWPORT TEST SUMMARY: ${passed}/${total} PASSED (${failed} failed)`);
  console.log('========================================================================');

  process.exit(failed > 0 ? 1 : 0);
}

run().catch(err => {
  console.error('Test execution error:', err);
  process.exit(1);
});
