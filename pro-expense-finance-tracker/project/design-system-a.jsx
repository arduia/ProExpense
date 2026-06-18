// Pro Expense — Design System · content part A (color, type, icons, categories)
const { ds, Section, SubLabel } = window.DSChrome;

// ════════ COLOR ════════
const SWATCHES = {
  primary: {
    label: 'Primary · Material blue',
    note: 'Actions, active nav, links, the floating Add button, amount accents.',
    items: [
      ['--blue-100', '#b3e5fc', 'Tint'], ['--blue-200', '#81d4fa', '200'],
      ['--blue-300', '#4fc3f7', 'Soft'], ['--blue-500', '#039be5', 'Primary'],
      ['--blue-700', '#0288d1', 'Deep'],
    ],
  },
  success: {
    label: 'Success · Green',
    note: 'On-track budgets, money lent back, positive confirmations.',
    items: [['--green-300', '#81c784', 'Soft'], ['--green-400', '#66bb6a', '400'], ['--green-500', '#4caf50', 'Success']],
  },
  highlight: {
    label: 'Highlight · Yellow',
    note: 'Future/event highlights, coin accents.',
    items: [['--yellow-300', '#fff176', 'Soft'], ['--yellow-500', '#ffeb3b', 'Highlight']],
  },
  danger: {
    label: 'Destructive · Red',
    note: 'Over-budget, money owed, delete & clear-data actions.',
    items: [['--red-300', '#e57373', 'Soft'], ['--red-400', '#ef5350', 'Danger']],
  },
  tag: {
    label: 'Tag accent · Orange',
    note: 'Event @-tags on transactions — the one warm signal in a cool palette.',
    items: [['--tag-tint', '#ffe0b2', 'Tint'], ['--tag-soft', '#ffb74d', 'Soft'], ['--tag', '#fb8c00', 'Tag'], ['--tag-deep', '#ef6c00', 'Deep']],
  },
};
const NEUTRALS = [
  ['--gray-100', '#f5f5f5', 'paper'], ['--gray-200', '#eeeeee', 'paper-2'], ['--gray-300', '#e0e0e0', 'line-strong'],
  ['--gray-500', '#9e9e9e', 'muted'], ['--gray-600', '#757575', 'ink-3'], ['--dark-gray', '#212121', 'ink'], ['--dark', '#000000', '—'],
];
const SEMANTIC = [
  ['Surface', '--card', '#ffffff', 'Cards, sheets, nav'],
  ['App bg', '--paper', '#f5f5f5', 'Phone screen base'],
  ['Text', '--ink', '#212121', 'Primary text'],
  ['Text 2', '--ink-2', '#424242', 'Secondary text'],
  ['Text 3', '--ink-3', '#757575', 'Captions, labels'],
  ['Muted', '--muted', '#9e9e9e', 'Placeholders'],
  ['Line', '--line', 'rgba(33,33,33,.10)', 'Card & row borders'],
  ['Line 2', '--line-2', 'rgba(33,33,33,.06)', 'Inner dividers'],
];

function Swatch({ varName, hex, role, big }) {
  const dark = ['#039be5','#0288d1','#212121','#000000','#757575','#ef5350','#fb8c00','#ef6c00','#4caf50','#66bb6a'].includes(hex);
  return (
    <div style={{ borderRadius: 12, overflow: 'hidden', border: '1px solid var(--line)' }}>
      <div style={{ background: hex, height: big ? 76 : 56, display: 'flex', alignItems: 'flex-end', padding: 10 }}>
        <span style={{ fontFamily: 'var(--mono)', fontSize: 10.5, color: dark ? 'rgba(255,255,255,0.9)' : 'var(--ink-2)' }}>{role}</span>
      </div>
      <div style={{ background: 'var(--card)', padding: '9px 10px' }}>
        <div style={{ fontFamily: 'var(--mono)', fontSize: 11, color: 'var(--ink)' }}>{hex}</div>
        <div style={{ fontFamily: 'var(--mono)', fontSize: 10, color: 'var(--muted)', marginTop: 2 }}>{varName}</div>
      </div>
    </div>
  );
}

function ColorSection() {
  return (
    <Section id="color" num="01" title="Color">
      <p style={ds.lede}>A cool Material-blue core, three signal hues (green / yellow / red) for financial state, and a single warm orange reserved for event tags. Everything else is a calibrated neutral ramp.</p>
      <div style={{ marginTop: 34, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 28 }}>
        {Object.entries(SWATCHES).map(([k, grp]) => (
          <div key={k} style={{ gridColumn: k === 'primary' ? '1 / -1' : 'auto' }}>
            <SubLabel>{grp.label}</SubLabel>
            <div style={{ display: 'grid', gridTemplateColumns: `repeat(${grp.items.length}, 1fr)`, gap: 10 }}>
              {grp.items.map(([v, h, r]) => <Swatch key={v} varName={v} hex={h} role={r} big={k === 'primary'} />)}
            </div>
            <p style={{ fontSize: 12.5, color: 'var(--ink-3)', lineHeight: 1.5, marginTop: 12 }}>{grp.note}</p>
          </div>
        ))}
      </div>

      <SubLabel style={{ marginTop: 44 }}>Neutrals</SubLabel>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 10 }}>
        {NEUTRALS.map(([v, h, r]) => <Swatch key={v} varName={v} hex={h} role={r} />)}
      </div>

      <SubLabel style={{ marginTop: 44 }}>Semantic tokens</SubLabel>
      <div style={{ ...ds.card, overflow: 'hidden' }}>
        {SEMANTIC.map(([name, v, h, use], i) => (
          <div key={v} style={{ display: 'grid', gridTemplateColumns: '120px 1fr 150px 1fr', alignItems: 'center', gap: 16, padding: '13px 18px', borderTop: i ? '1px solid var(--line-2)' : 'none' }}>
            <span style={{ fontSize: 13, fontWeight: 600, color: 'var(--ink)' }}>{name}</span>
            <span style={{ fontFamily: 'var(--mono)', fontSize: 11.5, color: 'var(--blue-700)' }}>{v}</span>
            <span style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <span style={{ width: 18, height: 18, borderRadius: 5, background: h, border: '1px solid var(--line)' }} />
              <span style={{ fontFamily: 'var(--mono)', fontSize: 11, color: 'var(--ink-3)' }}>{h}</span>
            </span>
            <span style={{ fontSize: 12.5, color: 'var(--ink-3)' }}>{use}</span>
          </div>
        ))}
      </div>
    </Section>
  );
}

// ════════ TYPE ════════
function TypeSpecimen({ family, name, varName, weights, sample, note }) {
  return (
    <div style={{ ...ds.card, padding: 26 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 18 }}>
        <span style={{ fontFamily: family, fontSize: 22, color: 'var(--ink)' }}>{name}</span>
        <span style={{ fontFamily: 'var(--mono)', fontSize: 11, color: 'var(--blue-700)' }}>{varName}</span>
      </div>
      <div style={{ fontFamily: family, fontSize: 46, lineHeight: 1.05, letterSpacing: family === 'var(--serif)' ? '-0.02em' : '-0.01em', color: 'var(--ink)' }}>{sample}</div>
      <div style={{ display: 'flex', gap: 18, flexWrap: 'wrap', marginTop: 20, paddingTop: 16, borderTop: '1px solid var(--line-2)' }}>
        {weights.map(([w, lbl]) => (
          <span key={lbl} style={{ fontFamily: family, fontWeight: w, fontStyle: lbl === 'Italic' ? 'italic' : 'normal', fontSize: 15, color: 'var(--ink-2)' }}>{lbl} {w}</span>
        ))}
      </div>
      <p style={{ fontSize: 12.5, color: 'var(--ink-3)', lineHeight: 1.5, marginTop: 16 }}>{note}</p>
    </div>
  );
}

const TYPE_SCALE = [
  ['Display amount', 'var(--serif)', 64, 1, '-0.025em', '$1,240'],
  ['Screen title', 'var(--serif)', 32, 1, '-0.015em', 'Journal'],
  ['Section head', 'var(--serif)', 18, 1.1, '-0.01em', 'Today · May 25'],
  ['Body', 'var(--sans)', 14, 1.4, '0', 'Lunch with M.'],
  ['Caption', 'var(--sans)', 11.5, 1.4, '0', 'Food · 12:30 PM'],
  ['Eyebrow / label', 'var(--mono)', 11, 1.3, '0.1em', 'AMOUNT · USD'],
];

function TypeSection() {
  return (
    <Section id="type" num="02" title="Typography">
      <p style={ds.lede}>Three families with clear jobs: Instrument Serif carries the display voice (titles, money), Geist handles all UI body text, and Geist Mono sets every uppercase eyebrow and numeric label.</p>
      <div style={{ marginTop: 34, display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 16 }}>
        <TypeSpecimen family="var(--serif)" name="Instrument Serif" varName="--serif" weights={[[400, 'Regular'], [400, 'Italic']]} sample="Money & titles" note="Display only — screen titles, day headers, large amounts. Never for body or controls." />
        <TypeSpecimen family="var(--sans)" name="Geist" varName="--sans" weights={[[400, 'Reg'], [500, 'Med'], [600, 'Semi'], [700, 'Bold']]} sample="Interface text" note="All body, buttons, list rows. 500–600 for emphasis, 400 for secondary copy." />
        <TypeSpecimen family="var(--mono)" name="Geist Mono" varName="--mono" weights={[[400, 'Reg'], [500, 'Med'], [600, 'Semi']]} sample="LABELS · 0123" note="Uppercase eyebrows, tab labels, timestamps & tabular figures. Letter-spacing 0.08–0.12em." />
      </div>

      <SubLabel style={{ marginTop: 44 }}>Type scale</SubLabel>
      <div style={{ ...ds.card, overflow: 'hidden' }}>
        {TYPE_SCALE.map(([role, fam, size, lh, ls, sample], i) => (
          <div key={role} style={{ display: 'grid', gridTemplateColumns: '180px 1fr 90px', alignItems: 'center', gap: 20, padding: '16px 22px', borderTop: i ? '1px solid var(--line-2)' : 'none' }}>
            <div>
              <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--ink)' }}>{role}</div>
              <div style={{ fontFamily: 'var(--mono)', fontSize: 10.5, color: 'var(--muted)', marginTop: 3, textTransform: ls !== '0' ? 'uppercase' : 'none' }}>{fam.replace('var(--', '').replace(')', '')}</div>
            </div>
            <div style={{ fontFamily: fam, fontSize: Math.min(size, 40), lineHeight: lh, letterSpacing: ls, color: 'var(--ink)', textTransform: ls === '0.1em' ? 'uppercase' : 'none', overflow: 'hidden', whiteSpace: 'nowrap', textOverflow: 'ellipsis' }}>{sample}</div>
            <div style={{ fontFamily: 'var(--mono)', fontSize: 11, color: 'var(--ink-3)', textAlign: 'right' }}>{size}px</div>
          </div>
        ))}
      </div>
    </Section>
  );
}

// ════════ ICONS ════════
const ICON_GROUPS = {
  'Navigation & chrome': ['home', 'budget', 'journal', 'more', 'plus', 'minus', 'back', 'close', 'chevron-down', 'chevron-right', 'search', 'bell', 'check'],
  'Detail & meta': ['sparkle', 'at', 'calendar', 'clock', 'note', 'user', 'eye', 'fingerprint'],
  'Feature shortcuts': ['feat-reports', 'feat-debt', 'feat-split', 'feat-events'],
};

function IconCell({ name }) {
  return (
    <div style={{ ...ds.card, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 10, padding: '18px 8px' }}>
      <Icon name={name} size={24} stroke="var(--ink)" strokeWidth={1.6} />
      <span style={{ fontFamily: 'var(--mono)', fontSize: 10, color: 'var(--ink-3)', textAlign: 'center' }}>{name}</span>
    </div>
  );
}

function IconSection() {
  return (
    <Section id="icons" num="03" title="Iconography">
      <p style={ds.lede}>One stroke-based set, drawn on a 24×24 grid at 1.6px weight (2px when active). Rounded caps and joins throughout. Active nav and primary actions bump the stroke to feel heavier.</p>
      {Object.entries(ICON_GROUPS).map(([grp, names]) => (
        <div key={grp} style={{ marginTop: 32 }}>
          <SubLabel>{grp}</SubLabel>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 10 }}>
            {names.map(n => <IconCell key={n} name={n} />)}
          </div>
        </div>
      ))}
    </Section>
  );
}

// ════════ CATEGORIES ════════
function CategorySection() {
  const ids = [...DEFAULT_CATEGORY_IDS, ...CUSTOM_CATEGORY_IDS];
  return (
    <Section id="categories" num="04" title="Category system">
      <p style={ds.lede}>Every expense maps to a category, each with a dedicated icon, accent and matching tint. Badges (circular, tinted) appear in lists; chips drive selection. Defaults ship with the app; users add custom ones.</p>

      <SubLabel style={{ marginTop: 34 }}>Badges — used in list rows</SubLabel>
      <div style={{ ...ds.card, padding: 24, display: 'flex', flexWrap: 'wrap', gap: 26 }}>
        {ids.map(id => (
          <div key={id} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 8, width: 78 }}>
            <CatBadge cat={id} size={48} />
            <span style={{ fontSize: 11.5, color: 'var(--ink-2)', fontWeight: 500 }}>{CATEGORIES[id].label}</span>
            <span style={{ fontFamily: 'var(--mono)', fontSize: 9.5, color: 'var(--muted)' }}>{CATEGORIES[id].color}</span>
            {CATEGORIES[id].custom && <span style={{ fontFamily: 'var(--mono)', fontSize: 8.5, color: 'var(--blue-700)', letterSpacing: '0.08em' }}>CUSTOM</span>}
          </div>
        ))}
      </div>

      <SubLabel style={{ marginTop: 36 }}>Chips — selectable (idle / selected)</SubLabel>
      <div style={{ ...ds.card, padding: 24, display: 'flex', flexWrap: 'wrap', gap: 10 }}>
        {ids.map((id, i) => <CatChip key={id} cat={id} selected={i % 3 === 0} onClick={() => {}} />)}
      </div>
    </Section>
  );
}

window.DSContentA = { ColorSection, TypeSection, IconSection, CategorySection };
