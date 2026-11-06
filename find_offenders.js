const CDP = require('chrome-remote-interface');

async function inspectOverflow(url, width) {
  let client;
  try {
    client = await CDP({ port: 9390 });
    const { Page, Emulation, Runtime } = client;
    await Page.enable();
    await Emulation.setDeviceMetricsOverride({ width, height: 800, deviceScaleFactor: 1, mobile: width <= 768 });
    await Page.navigate({ url });
    await Page.loadEventFired();
    await new Promise(r => setTimeout(r, 600));
    const res = await Runtime.evaluate({
      expression: `(() => {
        const docW = document.documentElement.scrollWidth;
        const innerW = window.innerWidth;
        const offenders = [];
        document.querySelectorAll('*').forEach(el => {
          const rect = el.getBoundingClientRect();
          if (rect.right > innerW + 1) {
            offenders.push({
              tag: el.tagName,
              id: el.id,
              className: (el.className || '').toString().slice(0, 100),
              right: Math.round(rect.right),
              width: Math.round(rect.width)
            });
          }
        });
        return { innerW, docW, offenders: offenders.slice(0, 10) };
      })()`,
      returnByValue: true
    });
    console.log(JSON.stringify(res.result.value, null, 2));
  } catch(e) {
    console.error(e);
  } finally {
    if (client) await client.close();
  }
}

async function run() {
  console.log('--- Inspecting /jobs at 320px ---');
  await inspectOverflow('http://localhost:8080/jobs', 320);
  console.log('--- Inspecting /jobs/1 at 320px ---');
  await inspectOverflow('http://localhost:8080/jobs/1', 320);
  console.log('--- Inspecting /register at 320px ---');
  await inspectOverflow('http://localhost:8080/register', 320);
  console.log('--- Inspecting /jobs at 1024px ---');
  await inspectOverflow('http://localhost:8080/jobs', 1024);
}
run();

