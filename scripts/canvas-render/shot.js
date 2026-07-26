// Renders one canvas component (from the Blue Banking design canvas source) with real
// React/Babel and screenshots it — for visually verifying app screens against the actual
// canvas, not a stale pre-rendered asset. See README.md for the full setup.
//
// Usage: node shot.js <Component> [theme] [group] [outPath]
//   Component  e.g. VBHomeSpendTrip, VBBudget, VBSharedSummary, VBEdgeEventWarning
//   theme      light | dark                          (default: light)
//   group      base | services | edge                (default: base)
//              base=variant-blue-app.jsx, services=variant-blue-services.jsx,
//              edge=variant-blue-edgecases.jsx
//   outPath    output PNG path                        (default: ./out.png)
//
// Requires proto-ui.jsx, variant-blue-app.jsx, variant-blue-services.jsx,
// variant-blue-edgecases.jsx to be present in this directory (fetch fresh via DesignSync —
// see README.md) and `npm install` to have been run once.
const http = require('http');
const path = require('path');
const fs = require('fs');
const { chromium } = require('playwright');

const [, , component = 'VBHomeSpendTrip', theme = 'light', group = 'base', outPath = 'out.png'] = process.argv;

const MIME = { '.html': 'text/html', '.js': 'text/javascript', '.jsx': 'text/javascript', '.map': 'application/json', '.css': 'text/css' };

function serveDir(dir, port) {
  return new Promise((resolve) => {
    const server = http.createServer((req, res) => {
      const filePath = path.join(dir, decodeURIComponent(req.url.split('?')[0]));
      fs.readFile(filePath, (err, data) => {
        if (err) { res.writeHead(404); res.end('not found'); return; }
        res.writeHead(200, { 'Content-Type': MIME[path.extname(filePath)] || 'application/octet-stream' });
        res.end(data);
      });
    });
    server.listen(port, '127.0.0.1', () => resolve(server));
  });
}

function findChromium() {
  const base = process.env.PLAYWRIGHT_BROWSERS_PATH || '/opt/pw-browsers';
  const dir = fs.readdirSync(base).find((d) => d.startsWith('chromium-'));
  return path.join(base, dir, 'chrome-linux', 'chrome');
}

(async () => {
  const port = 8934 + Math.floor(Math.random() * 1000);
  const server = await serveDir(__dirname, port);
  const browser = await chromium.launch({ executablePath: findChromium() });
  const page = await browser.newPage({ viewport: { width: 500, height: 950 } });
  const errors = [];
  page.on('console', (msg) => { if (msg.type() === 'error') errors.push(msg.text()); });
  page.on('pageerror', (err) => errors.push('pageerror: ' + err.message));

  const url = `http://127.0.0.1:${port}/render.html?component=${component}&theme=${theme}&group=${group}`;
  await page.goto(url, { waitUntil: 'networkidle', timeout: 30000 });
  await page.waitForTimeout(1200);
  await page.locator('#root').screenshot({ path: outPath });

  const fatal = errors.filter((e) => !e.includes('ERR_CONNECTION_RESET') && !e.includes('404'));
  if (fatal.length) console.error('JS ERRORS (check output for a blank/broken render):', fatal);
  console.log(`Wrote ${outPath}`);

  await browser.close();
  server.close();
})();
