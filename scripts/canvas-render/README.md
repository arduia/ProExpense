# Canvas render harness

Renders a component straight from the Blue Banking design canvas source with real
React + Babel, so it can be screenshotted and compared directly against a Roborazzi
screenshot — pixel-for-pixel, not a code-values comparison and not a stale pre-rendered
asset from the project's own `screenshots/` folder (those can predate the current design
and are unreliable — confirmed once: `design-system-spec/screenshots/screens/home-casual.png`
in the canvas project turned out to be from an earlier, non-Blue-Banking iteration).

## Why this exists

Comparing token values (`padding: '18px 20px'` vs `dimens.cardPadding`) catches spacing
drift but misses layout-level differences — e.g. a card that's supposed to float up into
the header on canvas but sits flush in the implementation. Only an actual rendered
comparison catches that class of defect. See AGENTS.md's "Visual verification against
canvas" step for when to use this.

## One-time / per-session setup

1. **Fetch the canvas source** via the `DesignSync` tool (`get_file`, projectId
   `79eccec0-6ad2-477a-95ec-18df4a5dc017`) and write each into this directory:
   - `proto-ui.jsx` (Icon, CATEGORIES, CatBadge — shared primitives)
   - `variant-blue-app.jsx` (`VB`/`VBDark` palettes, `buildVB`, the core screens, and the
     `VariantBlueApp` artboard tree)
   - `variant-blue-services.jsx` (Events/Debt/Shared/Categories/etc. sub-screens)
   - `variant-blue-edgecases.jsx` (validation/error/empty states)

   These are gitignored — re-fetch fresh each session, don't assume yesterday's copy is
   current.

2. **Strip the auto-render line** from the bottom of `variant-blue-app.jsx`:
   ```
   ReactDOM.createRoot(document.getElementById('root')).render(<VariantBlueApp />);
   ```
   (delete or comment it out — `render.html` does its own targeted render instead of
   mounting the whole pan/zoom canvas picker tool.)

3. **Install deps** (`unpkg.com` is proxy-blocked in this environment — use npm, which is
   allowed):
   ```bash
   cd scripts/canvas-render
   npm install
   ```

## Finding the right component name

**Don't assume a function name in `variant-blue-app.jsx` is the adopted screen** — the
file defines several unused alternate variants (e.g. `VBHomeClassic`/`VBCardCasual` exist
alongside the actually-wired `VBHomeSpendTrip`/`VBCardSpendTrip`). The canonical mapping
is `VariantBlueApp`'s own `<DCArtboard>` tree near the bottom of the file — each
`<DCArtboard>` wraps exactly the component that's live in the canvas, e.g.:
```jsx
<DCArtboard id="vb-home-spendtrip" label="02 · Home · Spend + Trip" ...><L.VBHomeSpendTrip /></DCArtboard>
<DCArtboard id="vb-budget" label="04 · Budget" ...><L.VBBudget /></DCArtboard>
```
`L.*` / `D.*` (light/dark) come from `variant-blue-app.jsx` (`group=base`), `LS.*`/`DS.*`
from `variant-blue-services.jsx` (`group=services`), `LE.*`/`DE.*` from
`variant-blue-edgecases.jsx` (`group=edge`).

## Rendering a screenshot

```bash
node shot.js <Component> [theme] [group] [outPath]
# theme: light | dark (default light)   group: base | services | edge (default base)
```

Examples:
```bash
node shot.js VBHomeSpendTrip light base home-light.png
node shot.js VBHomeSpendTrip dark  base home-dark.png
node shot.js VBBudget        light base budget-light.png
node shot.js VBSharedSummary light services shared-summary.png
node shot.js VBEdgeEventWarning light edge event-warning-edge.png
```

The script starts a local static server, launches the pre-installed Chromium
(`PLAYWRIGHT_BROWSERS_PATH=/opt/pw-browsers`, already configured in this environment — no
`playwright install` needed), screenshots just the `#root` element (so the output is the
raw component, not page chrome), and prints any JS errors it hit (a blank/broken render
usually means a missing dependency file or a stale `variant-blue-app.jsx` render-line
strip).

Then compare directly against the matching Roborazzi PNG in `app/src/test/screenshots/`.
