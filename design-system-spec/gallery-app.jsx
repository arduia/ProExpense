// Pro Expense — Component Gallery (Compose spec)
// Single page; each component gets its own isolated spec section.
// Live renders come from window.CAPTURES (spec-captures.jsx).

const g = {
  page: { background: '#f3f1ec', color: 'var(--ink)', minHeight: '100vh', fontFamily: 'var(--sans)' },
  wrap: { maxWidth: 1100, margin: '0 auto', padding: '0 40px' },
  mono: { fontFamily: 'var(--mono)', fontSize: 11 },
  card: { background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, boxShadow: '0 1px 0 rgba(33,33,33,0.03), 0 6px 16px rgba(33,33,33,0.04)' },
};

// ── spec data — one entry per component ──
const SPEC = [
  {
    id: 'button', num: '01', title: 'Button', md: 'components/button.md',
    purpose: 'Primary action control — 6 variants × 3 sizes, press-scale 0.97.',
    stages: [['Variants', 'button-variants'], ['Sizes', 'button-sizes'], ['States', 'button-states']],
    dims: [['Radius', 'sm 10 · md 12 · lg 14'], ['Padding', 'sm 8×14 · md 12×18 · lg 16×22'], ['Font', 'Manrope 600 · 12 / 14 / 15'], ['Disabled', 'alpha 0.4']],
    tokens: ['clay', 'clayDeep', 'sage', 'ink', 'lineStrong', 'onFilled #FFFDF6'],
    m3: 'Button (filled) · OutlinedButton (secondary) · TextButton (ghost). Override colors, contentPadding, shape.',
    behavior: ['Stateless — one onClick per tap, no toggle/loading state.', 'Press → scale 0.97 + opacity 0.92 (80ms), reverts on release.', 'Disabled suppresses onClick at alpha 0.4 (0.55–0.6 on keypad actions).'],
  },
  {
    id: 'category', num: '02', title: 'Category — Badge & Chip', md: 'components/category.md',
    purpose: 'Per-category icon + accent + tint. Badges read in rows; chips select.',
    stages: [['Badges (48dp)', 'cat-badges'], ['Chips — idle / selected', 'cat-chips']],
    dims: [['Badge', 'circle · 38 row / 48 picker · icon 0.52×'], ['Chip pad', '7×12×7×8 · radius 99'], ['Chip font', '12.5 · idle 500 / sel 600']],
    tokens: ['accent (per cat)', 'tint (per cat)', 'ink2', 'lineStrong'],
    m3: 'Badge: custom Box+CircleShape. Chip: FilterChip (selectedContainerColor = accent) or custom Row.',
    behavior: ['Badge is display-only; chip is interactive.', 'Chip = single-select — one category always chosen (no toggle-off).', '“+ More” opens the Category Picker sheet, which applies + closes.'],
  },
  {
    id: 'filter-chip', num: '03', title: 'Filter Chip', md: 'components/filter-chip.md',
    purpose: 'Single-select monochrome list filter — ink-filled when active.',
    stages: [['Idle / active', 'filter-chips']],
    dims: [['Padding', '6×12 · radius 99'], ['Font', '12 · idle 500 / active 600'], ['Layout', 'LazyRow · gap 6']],
    tokens: ['ink', 'paper', 'ink2', 'lineStrong'],
    m3: 'FilterChip (single-selection group) · selectedContainerColor = ink · shape CircleShape.',
    behavior: ['Single-select; “All” is the default active state.', 'Filters the list immediately — no Apply step.', 'Row scrolls horizontally; “More” opens the category sheet.'],
  },
  {
    id: 'transaction-row', num: '04', title: 'Transaction Row & Day Group', md: 'components/transaction-row.md',
    purpose: 'Core list unit — badge, two-line label, serif amount; grouped by day.',
    stages: [['Day group', 'txn-daygroup']],
    dims: [['Row pad', '12 v · 8 h · gap 12 · divider line2'], ['Note', 'Manrope 14 / 500'], ['Meta', 'Manrope 11.5 muted'], ['Amount', 'Serif 18']],
    tokens: ['ink', 'muted', 'line2', 'tag (event)'],
    m3: 'Custom Row (not ListItem). Weighted Column for note/meta · trailing serif Text · HorizontalDivider(line2).',
    behavior: ['Tap a row to open its edit sheet.', 'Empty note falls back to the category label; long notes ellipsize.', 'Newly committed rows pulse clayTint→transparent (1800ms) on insert.'],
  },
  {
    id: 'card-surfaces', num: '05', title: 'Card & Surfaces', md: 'components/card-surfaces.md',
    purpose: 'White cards on warm paper — hairline border, soft 2-layer shadow.',
    stages: [['Card', 'spent-card']],
    dims: [['Radius', 'card 16–18 · sheet 22 · pill 99'], ['Padding', '18'], ['Border', '1dp line'], ['Shadow', '0 6px 16px rgba(33,33,33,.04)']],
    tokens: ['card #FFFFFF', 'line', 'ink', 'clay (graph)'],
    m3: 'Card/Surface · containerColor white · tonalElevation 0 · custom soft shadow (no M3 tint).',
    behavior: ['Home context card taps to cycle persona (casual→budget→event).', 'Progress bars fill to min(100%, spent / budget).', 'No elevation change on press — scale-tap only if interactive.'],
  },
  {
    id: 'amount-entry', num: '06', title: 'Amount Entry — Keypad', md: 'components/amount-entry.md',
    purpose: 'Serif numeric keypad + inline validation; logs in under 5 s.',
    stages: [['Keypad', 'keypad'], ['Validation', 'validation']],
    dims: [['Grid', '3-col · gap 8 · key radius 12'], ['Keys', 'Serif 22 (money = serif)'], ['Actions', 'Save (secondary) · Next (primary)'], ['Threshold', '≤ $0 → alpha 0.55 + shake']],
    tokens: ['ink', 'clay', 'muted2', 'lineStrong'],
    m3: 'Fully custom. LazyVerticalGrid(Fixed 3) · Surface keys · Animatable shake offset.',
    behavior: ['Max 7 whole + 2 decimal digits; single “.”; ⌫ deletes; leading zeros stripped.', 'canProceed = value > 0 — Save/Next disable below $0.', 'Disabled-tap shakes (320ms) + reveals helper text.', 'A non-empty amount persists a draft → restore prompt on relaunch.'],
  },
  {
    id: 'search-field', num: '07', title: 'Search Field', md: 'components/search-field.md',
    purpose: 'Lightweight search input — leading glyph, clearable.',
    stages: [['Empty', 'search-empty'], ['Filled', 'search-filled']],
    dims: [['Padding', '12×14 · radius 14'], ['Icon', 'search 16 · clear 15'], ['Font', 'Manrope 13']],
    tokens: ['card', 'line', 'muted', 'ink'],
    m3: 'Custom Row in Surface + BasicTextField (OutlinedTextField is too heavy). Trailing clear when non-empty.',
    behavior: ['Typing filters the list live — no submit/enter.', 'Clear (×) appears only when the query is non-empty.', 'Cursor is the only focus signal — no focus ring.'],
  },
  {
    id: 'quick-access', num: '08', title: 'Quick-Access Tiles', md: 'components/quick-access.md',
    purpose: 'Home-screen feature shortcuts — 4-up labelled icon tiles.',
    stages: [['Tiles', 'quick-access']],
    dims: [['Tile', 'radius 14 · pad 12×4 · gap 7'], ['Icon chip', '36 · radius 11 · glyph 18'], ['Label', 'Manrope 11 / 500']],
    tokens: ['feature tint', 'glyph accent', 'line', 'ink2'],
    m3: 'Row of weight(1f) Surfaces (or LazyVerticalGrid Fixed 4). Note chip radius 11 ≠ tile radius 14.',
    behavior: ['Each tile navigates to its feature on tap.', 'No selected/toggle state — pure navigation.', '“Customize” (reorder) reserved, not yet active.'],
  },
  {
    id: 'bottom-nav', num: '09', title: 'Bottom Navigation', md: 'components/bottom-nav.md',
    purpose: 'Floating glass tab bar with a raised central Add FAB.',
    stages: [['Home active', 'nav-home'], ['Budget active', 'nav-budget']],
    dims: [['Bar', 'blur 22 · top corners 8 · pad 8×12×16'], ['Tab', 'icon 25 · active stroke 2.1 / 1.7'], ['Add', '64 circle · raised −24 · border 3']],
    tokens: ['blue500', '#8E8E93 inactive', 'white 0.86'],
    m3: 'NavigationBar for 4 tabs (indicator transparent) + custom raised FloatingActionButton + blur backdrop.',
    behavior: ['Tab tap switches active (colour + stroke 2.1 + label 600).', 'Shown only on top-level screens; hidden during the add flow.', 'Center Add resets the draft and starts a fresh add flow.'],
  },
  {
    id: 'bottom-sheet', num: '10', title: 'Bottom Sheet', md: 'components/bottom-sheet.md',
    purpose: 'Slides up over a scrim for create / edit flows.',
    stages: [['Sheet', 'bottom-sheet']],
    dims: [['Corners', 'top 22 · max-h 78%'], ['Handle', '36×4 pill'], ['Enter', 'sheet-up 340ms'], ['Footer', '2 btns · gap 10 · weight 1']],
    tokens: ['card', 'scrim rgba(43,31,23,.42)', 'ink'],
    m3: 'ModalBottomSheet · shape top 22 · containerColor white · custom 36×4 dragHandle.',
    behavior: ['Opens sheet-up (340ms); dismiss via scrim / close / swipe-down.', 'Selecting usually applies + closes in one tap.', 'Tag picker: event vs debt are mutually exclusive (greys the other).'],
  },
  {
    id: 'toast', num: '11', title: 'Toast', md: 'components/toast.md',
    purpose: 'Transient confirmation pill — rises, holds, auto-dismisses.',
    stages: [['Success', 'toast']],
    dims: [['Shape', 'pill 99 · pad 10×16'], ['Font', 'Manrope 13'], ['Motion', 'toast-up 2400ms'], ['Icon', 'check 16 · green300']],
    tokens: ['ink', 'paper', 'green300 (success)'],
    m3: 'Custom Snackbar · containerColor ink · CircleShape · status-coloured leading icon.',
    behavior: ['Fires after a commit; auto-dismisses after ~2500ms.', 'A new toast replaces the current one — single at a time.', 'Non-interactive — no action button in the success case.'],
  },
  {
    id: 'icons', num: '12', title: 'Iconography', md: 'components/icons.md',
    purpose: 'One stroke set · 24×24 grid · 1.6dp (2.0–2.4 active).',
    stages: [['Set', 'icons']],
    dims: [['Grid', '24×24'], ['Stroke', '1.6 · active 2.0–2.4'], ['Caps/joins', 'round'], ['Sizes', 'nav 25 · list 16–18 · chip 14']],
    tokens: ['ink', 'currentColor tint'],
    m3: 'ImageVector (materialPath) — stroke-based, fill null, round caps. Active bumps strokeWidth.',
    behavior: ['Presentational — the parent owns the tap target.', 'Tint inherits currentColor / the passed tint.', 'Active/primary bumps strokeWidth (1.6→2.0–2.4).'],
  },
];

function Live({ keyName }) {
  return (
    <div style={{ background: 'var(--paper)', border: '1px solid var(--line)', borderRadius: 12, padding: 28, display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: 120, overflow: 'hidden' }}>
      {window.CAPTURES[keyName] || <span style={{ color: 'red' }}>missing</span>}
    </div>
  );
}

function SubLabel({ children }) {
  return <div style={{ fontFamily: 'var(--mono)', fontSize: 10, textTransform: 'uppercase', letterSpacing: '0.12em', color: 'var(--ink-3)', fontWeight: 600, marginBottom: 10 }}>{children}</div>;
}

function SpecBlock({ c }) {
  return (
    <section id={c.id} style={{ padding: '0 0 64px', scrollMarginTop: 70 }}>
      <div style={g.wrap}>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 14, marginBottom: 6 }}>
          <span style={{ fontFamily: 'var(--mono)', fontSize: 11, color: 'var(--blue-700)', letterSpacing: '0.14em' }}>{c.num}</span>
          <h2 style={{ fontFamily: 'var(--serif)', fontSize: 32, letterSpacing: '-0.02em', lineHeight: 1.05, margin: 0 }}>{c.title}</h2>
        </div>
        <p style={{ fontSize: 14.5, lineHeight: 1.5, color: 'var(--ink-2)', margin: '0 0 24px', maxWidth: 640 }}>{c.purpose}</p>

        {/* live stages */}
        <div style={{ display: 'grid', gridTemplateColumns: c.stages.length > 1 ? 'repeat(auto-fit, minmax(240px, 1fr))' : '1fr', gap: 16 }}>
          {c.stages.map(([label, key]) => (
            <div key={key}>
              <SubLabel>{label}</SubLabel>
              <Live keyName={key} />
            </div>
          ))}
        </div>

        {/* spec grid */}
        <div style={{ display: 'grid', gridTemplateColumns: '1.4fr 1fr', gap: 16, marginTop: 22 }}>
          <div style={{ ...g.card, padding: '6px 20px' }}>
            <div style={{ padding: '14px 0 4px' }}><SubLabel>Dimensions</SubLabel></div>
            {c.dims.map(([k, v], i) => (
              <div key={k} style={{ display: 'grid', gridTemplateColumns: '110px 1fr', gap: 14, padding: '9px 0', borderTop: i ? '1px solid var(--line-2)' : 'none' }}>
                <span style={{ fontSize: 12.5, fontWeight: 600, color: 'var(--ink)' }}>{k}</span>
                <span style={{ fontFamily: 'var(--mono)', fontSize: 11.5, color: 'var(--ink-2)', lineHeight: 1.45 }}>{v}</span>
              </div>
            ))}
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <div style={{ ...g.card, padding: 18 }}>
              <SubLabel>Tokens</SubLabel>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
                {c.tokens.map(t => <span key={t} style={{ fontFamily: 'var(--mono)', fontSize: 10.5, padding: '4px 9px', borderRadius: 7, background: 'var(--paper-2)', color: 'var(--ink-2)' }}>{t}</span>)}
              </div>
            </div>
            <div style={{ ...g.card, padding: 18, flex: 1 }}>
              <SubLabel>Material 3</SubLabel>
              <p style={{ fontSize: 12.5, lineHeight: 1.5, color: 'var(--ink-2)', margin: 0 }}>{c.m3}</p>
              <a href={c.md} style={{ display: 'inline-flex', alignItems: 'center', gap: 5, marginTop: 12, fontFamily: 'var(--mono)', fontSize: 11, color: 'var(--blue-700)', textDecoration: 'none' }}>
                full spec
                <Icon name="chevron-right" size={13} stroke="var(--blue-700)" strokeWidth={2} />
              </a>
            </div>
          </div>
        </div>

        {c.behavior && (
          <div style={{ ...g.card, padding: '16px 20px 18px', marginTop: 16 }}>
            <SubLabel>Behavior</SubLabel>
            <ul style={{ margin: 0, paddingLeft: 18, display: 'flex', flexDirection: 'column', gap: 7 }}>
              {c.behavior.map((b, i) => <li key={i} style={{ fontSize: 13, lineHeight: 1.5, color: 'var(--ink-2)' }}>{b}</li>)}
            </ul>
          </div>
        )}
      </div>
    </section>
  );
}

function GalleryHero() {
  return (
    <header style={{ background: 'var(--ink)', color: '#fff', padding: '56px 0 50px' }}>
      <div style={g.wrap}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 36 }}>
          <div style={{ width: 38, height: 38, borderRadius: 11, background: 'var(--blue-500)', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 6px 16px rgba(3,155,229,0.5)' }}>
            <Icon name="plus" size={22} stroke="#fff" strokeWidth={2.2} />
          </div>
          <span style={{ fontFamily: 'var(--mono)', fontSize: 12, letterSpacing: '0.12em', textTransform: 'uppercase', color: 'rgba(255,255,255,0.7)' }}>Pro Expense</span>
        </div>
        <div style={{ fontFamily: 'var(--mono)', fontSize: 11, letterSpacing: '0.16em', textTransform: 'uppercase', color: 'var(--blue-300)', marginBottom: 16 }}>Component Gallery · Compose spec</div>
        <h1 style={{ fontFamily: 'var(--serif)', fontSize: 64, lineHeight: 0.98, letterSpacing: '-0.03em', margin: 0, maxWidth: 760 }}>
          Every component,<br />ready to build.
        </h1>
        <p style={{ fontSize: 16, lineHeight: 1.6, color: 'rgba(255,255,255,0.72)', maxWidth: 600, marginTop: 22 }}>
          Each component rendered live in isolation, with the dimensions, tokens and Material 3 mapping needed to implement it in Jetpack Compose. Specs mirror the shipped Hi-Fi build. Full per-component docs live in <span style={{ fontFamily: 'var(--mono)', fontSize: 13, color: 'var(--blue-300)' }}>/components/*.md</span>.
        </p>
      </div>
    </header>
  );
}

function GalleryNav() {
  return (
    <nav style={{ position: 'sticky', top: 0, zIndex: 20, background: 'rgba(243,241,236,0.86)', backdropFilter: 'blur(10px)', borderBottom: '1px solid var(--line)' }}>
      <div style={{ ...g.wrap, display: 'flex', gap: 4, overflowX: 'auto', padding: '11px 40px' }}>
        {SPEC.map(c => (
          <a key={c.id} href={'#' + c.id} style={{ fontFamily: 'var(--mono)', fontSize: 11, letterSpacing: '0.03em', color: 'var(--ink-3)', textDecoration: 'none', padding: '6px 11px', borderRadius: 8, whiteSpace: 'nowrap' }}>{c.title.split(' — ')[0].split(' & ')[0]}</a>
        ))}
      </div>
    </nav>
  );
}

function GalleryApp() {
  return (
    <div style={g.page}>
      <GalleryHero />
      <GalleryNav />
      <div style={{ paddingTop: 48 }}>
        {SPEC.map(c => <SpecBlock key={c.id} c={c} />)}
        <div style={{ textAlign: 'center', padding: '40px 0 60px', color: 'var(--muted)', fontFamily: 'var(--mono)', fontSize: 11, letterSpacing: '0.08em' }}>
          PRO EXPENSE · COMPONENT GALLERY · SEE tokens.md FOR FOUNDATIONS
        </div>
      </div>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<GalleryApp />);
