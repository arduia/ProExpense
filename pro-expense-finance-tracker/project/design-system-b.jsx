// Pro Expense — Design System · content part B (components, motion)
const { ds, Section, SubLabel } = window.DSChrome;

// ════════ BUTTONS ════════
function BtnRow({ variant, desc }) {
  return (
    <div style={{ display: 'grid', gridTemplateColumns: '150px 1fr', alignItems: 'center', gap: 20, padding: '18px 22px', borderTop: '1px solid var(--line-2)' }}>
      <div>
        <div style={{ fontFamily: 'var(--mono)', fontSize: 12, color: 'var(--ink)' }}>{variant}</div>
        <div style={{ fontSize: 11.5, color: 'var(--ink-3)', marginTop: 4, lineHeight: 1.4 }}>{desc}</div>
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
        <Button variant={variant} size="sm">Small</Button>
        <Button variant={variant} size="md">Medium</Button>
        <Button variant={variant} size="lg">Large</Button>
      </div>
    </div>
  );
}

function ButtonSection() {
  return (
    <Section id="buttons" num="05" title="Buttons">
      <p style={ds.lede}>Six variants over three sizes. Primary blue drives the main action on any screen; sage confirms; dark is the high-contrast alternative; secondary and ghost recede. Every button scales to 0.97 on press.</p>
      <div style={{ ...ds.card, overflow: 'hidden', marginTop: 30 }}>
        <BtnRow variant="primary" desc="The main action — save, continue, get started." />
        <BtnRow variant="primary-deep" desc="Primary on tinted/active surfaces." />
        <BtnRow variant="sage" desc="Positive confirm — mark settled, on-track." />
        <BtnRow variant="dark" desc="High-contrast alt on paper." />
        <BtnRow variant="secondary" desc="Outlined secondary action." />
        <BtnRow variant="ghost" desc="Tertiary / cancel — no chrome." />
      </div>
      <div style={{ display: 'flex', gap: 24, marginTop: 18, flexWrap: 'wrap' }}>
        <span style={{ ...ds.mono, color: 'var(--ink-3)' }}>radius&nbsp;&nbsp;sm 10 · md 12 · lg 14</span>
        <span style={{ ...ds.mono, color: 'var(--ink-3)' }}>weight 600</span>
        <span style={{ ...ds.mono, color: 'var(--ink-3)' }}>disabled → opacity 0.4</span>
      </div>
    </Section>
  );
}

// local copy of the QuickAccess tile (matches flow-01-screens.jsx)
function QuickAccess({ label, icon, tint, stroke }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 7, padding: '12px 4px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14 }}>
      <div style={{ width: 36, height: 36, borderRadius: 11, background: tint, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Icon name={icon} size={18} stroke={stroke} strokeWidth={1.8} />
      </div>
      <span style={{ fontSize: 11, color: 'var(--ink-2)', fontWeight: 500 }}>{label}</span>
    </div>
  );
}

// ════════ SURFACES ════════
function SurfaceSection() {
  return (
    <Section id="surfaces" num="06" title="Surfaces & elevation">
      <p style={ds.lede}>White cards float on warm paper with a soft two-layer shadow and a hairline border. Radius scales with element size — chips are fully round, cards 16–18px, sheets 22px, the phone body 54px.</p>

      <div style={{ display: 'grid', gridTemplateColumns: '1.3fr 1fr', gap: 20, marginTop: 30 }}>
        <div>
          <SubLabel>Card</SubLabel>
          <div style={{ background: 'var(--card)', borderRadius: 18, padding: 18, boxShadow: '0 1px 0 rgba(43,31,23,0.04), 0 6px 14px rgba(43,31,23,0.04)', border: '1px solid var(--line)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div>
                <div style={{ fontFamily: 'var(--mono)', fontSize: 10.5, textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink-3)', fontWeight: 600 }}>Spent today</div>
                <div style={{ fontFamily: 'var(--serif)', fontSize: 40, letterSpacing: '-0.02em', color: 'var(--ink)', marginTop: 4 }}>$42.00</div>
              </div>
              <svg width="86" height="40" viewBox="0 0 86 40" fill="none"><path d="M2 30 L14 22 L26 26 L38 14 L50 18 L62 10 L74 14 L84 6" stroke="var(--clay)" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/><circle cx="84" cy="6" r="3" fill="var(--clay)"/></svg>
            </div>
            <div style={{ fontFamily: 'var(--mono)', fontSize: 10.5, color: 'var(--muted)', marginTop: 14 }}>radius 18 · padding 18 · 1px --line</div>
          </div>
        </div>
        <div>
          <SubLabel>Radius scale</SubLabel>
          <div style={{ ...ds.card, padding: 18, display: 'flex', flexDirection: 'column', gap: 12 }}>
            {[['Chip / pill', 99], ['Quick-access', 14], ['Card', 18], ['Sheet', 22], ['Phone body', 54]].map(([l, r]) => (
              <div key={l} style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
                <div style={{ width: 46, height: 30, background: 'var(--blue-100)', border: '1.5px solid var(--blue-500)', borderRadius: Math.min(r, 22) }} />
                <span style={{ fontSize: 13, color: 'var(--ink-2)', flex: 1 }}>{l}</span>
                <span style={{ ...ds.mono, color: 'var(--ink-3)' }}>{r}px</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      <SubLabel style={{ marginTop: 36 }}>Quick-access tiles</SubLabel>
      <div style={{ ...ds.card, padding: 20, display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 12 }}>
        <QuickAccess label="Reports" icon="feat-reports" tint="var(--blue-100)" stroke="var(--blue-700)" />
        <QuickAccess label="Debts" icon="feat-debt" tint="#c8e6c9" stroke="var(--green-500)" />
        <QuickAccess label="Split" icon="feat-split" tint="var(--tag-tint)" stroke="var(--tag-deep)" />
        <QuickAccess label="Events" icon="feat-events" tint="var(--yellow-300)" stroke="#f9a825" />
      </div>
    </Section>
  );
}

// ════════ DATA DISPLAY ════════
function MiniRow({ cat, note, meta, amount, tag }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '12px 8px', borderBottom: '1px solid var(--line-2)' }}>
      <CatBadge cat={cat} size={38} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 14, color: 'var(--ink)', fontWeight: 500 }}>{note}</div>
        <div style={{ fontSize: 11.5, color: 'var(--muted)', display: 'flex', gap: 6, alignItems: 'center' }}>
          <span>{meta}</span>
          {tag && (<><span>·</span><span style={{ color: 'var(--tag)', display: 'inline-flex', alignItems: 'center', gap: 2 }}><Icon name="at" size={10} stroke="var(--tag)" strokeWidth={2} />{tag}</span></>)}
        </div>
      </div>
      <div style={{ fontFamily: 'var(--serif)', fontSize: 18, color: 'var(--ink)' }}>{amount}</div>
    </div>
  );
}

function FilterChipDoc({ label, active }) {
  return (
    <div style={{ padding: '6px 12px', border: '1px solid ' + (active ? 'var(--ink)' : 'var(--line-strong)'), borderRadius: 99, fontSize: 12, fontWeight: active ? 600 : 500, background: active ? 'var(--ink)' : 'transparent', color: active ? 'var(--paper-warm)' : 'var(--ink-2)', whiteSpace: 'nowrap' }}>{label}</div>
  );
}

function DataSection() {
  return (
    <Section id="data" num="07" title="Data & list patterns">
      <p style={ds.lede}>Transactions are the core unit: a tinted category badge, a two-line label/meta block, and a serif amount on the right. Day groups carry a serif header and a mono running total. Event tags glow orange.</p>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20, marginTop: 30 }}>
        <div>
          <SubLabel>Transaction rows</SubLabel>
          <div style={{ ...ds.card, padding: '6px 14px' }}>
            <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', padding: '12px 8px 6px' }}>
              <span style={{ fontFamily: 'var(--serif)', fontSize: 18, letterSpacing: '-0.01em' }}>Today · May 25</span>
              <span style={{ fontFamily: 'var(--mono)', fontSize: 12, color: 'var(--muted)' }}>$42</span>
            </div>
            <MiniRow cat="food" note="Lunch with M." meta="Food · 12:30 PM" amount="$12.40" />
            <MiniRow cat="coffee" note="Oat latte" meta="Coffee runs · 08:40 AM" amount="$5.00" />
            <MiniRow cat="entertainment" note="Movie · Dune" meta="Entertainment" amount="$18.00" tag="Bali Trip" />
          </div>
        </div>
        <div>
          <SubLabel>Search field</SubLabel>
          <div style={{ padding: '12px 14px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, display: 'flex', alignItems: 'center', gap: 10 }}>
            <Icon name="search" size={16} stroke="var(--muted)" strokeWidth={1.7} />
            <span style={{ fontSize: 13, color: 'var(--muted)' }}>Search notes, amount, category…</span>
          </div>

          <SubLabel style={{ marginTop: 26 }}>Filter chips</SubLabel>
          <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
            <FilterChipDoc label="All" active />
            <FilterChipDoc label="Food" />
            <FilterChipDoc label="Transport" />
            <FilterChipDoc label="Bills" />
            <FilterChipDoc label="More" />
          </div>

          <SubLabel style={{ marginTop: 26 }}>Validation</SubLabel>
          <div style={{ ...ds.card, padding: 18, textAlign: 'center' }}>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 44, color: 'var(--muted-2)', letterSpacing: '-0.025em' }}><span style={{ fontSize: 22, color: 'var(--clay)' }}>$</span>0</div>
            <div style={{ fontSize: 12, color: 'var(--clay)', fontWeight: 500, marginTop: 4 }}>Amount must be greater than $0</div>
          </div>
        </div>
      </div>
    </Section>
  );
}

// ════════ NAVIGATION & FEEDBACK ════════
function NavSection() {
  return (
    <Section id="nav" num="08" title="Navigation & feedback">
      <p style={ds.lede}>A modern iOS floating tab bar — a detached, fully-rounded glass bar inset from the screen edges with a soft drop shadow, SF-style tabs, and the Add action raised into a clean circular button. Bottom sheets slide up over a scrim for create/edit flows; toasts confirm and auto-dismiss.</p>

      <SubLabel style={{ marginTop: 30 }}>Bottom navigation</SubLabel>
      <div style={{ width: 390, ...ds.card, overflow: 'visible', paddingTop: 18 }}>
        <ProtoBottomNav active="home" />
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20, marginTop: 36 }}>
        <div>
          <SubLabel>Bottom sheet</SubLabel>
          <div style={{ position: 'relative', height: 180, borderRadius: 16, overflow: 'hidden', background: 'rgba(43,31,23,0.42)', border: '1px solid var(--line)' }}>
            <div style={{ position: 'absolute', left: 0, right: 0, bottom: 0, background: 'var(--card)', borderTopLeftRadius: 22, borderTopRightRadius: 22, boxShadow: '0 -8px 24px rgba(0,0,0,0.15)', padding: '0 20px 20px' }}>
              <div style={{ width: 36, height: 4, borderRadius: 99, background: 'rgba(43,31,23,0.18)', margin: '10px auto 14px' }} />
              <div style={{ fontFamily: 'var(--serif)', fontSize: 18 }}>Edit expense</div>
              <div style={{ display: 'flex', gap: 10, marginTop: 14 }}>
                <Button variant="ghost" size="md" style={{ flex: 1 }}>Cancel</Button>
                <Button variant="primary" size="md" style={{ flex: 1 }}>Save</Button>
              </div>
            </div>
          </div>
        </div>
        <div>
          <SubLabel>Toast</SubLabel>
          <div style={{ height: 180, borderRadius: 16, background: 'var(--paper)', border: '1px solid var(--line)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <div style={{ background: 'var(--ink)', color: 'var(--paper-warm)', padding: '10px 16px', borderRadius: 99, fontSize: 13, display: 'flex', alignItems: 'center', gap: 8, boxShadow: '0 8px 18px rgba(0,0,0,0.18)' }}>
              <Icon name="check" size={16} stroke="var(--green-300)" strokeWidth={2.2} />
              Expense saved
            </div>
          </div>
        </div>
      </div>
    </Section>
  );
}

// ════════ MOTION ════════
const MOTION = [
  ['Screen · forward', 'slide-in-right', '280ms', 'cubic-bezier(.22,.61,.36,1)'],
  ['Screen · back', 'slide-in-left', '280ms', 'cubic-bezier(.22,.61,.36,1)'],
  ['Sheet', 'sheet-up', '340ms', 'cubic-bezier(.22,.61,.36,1)'],
  ['Toast', 'toast-up', '2400ms', 'ease (auto-dismiss)'],
  ['Fade up', 'fade-up', '200ms', 'ease'],
  ['New row pulse', 'pulse-hl', '1800ms', 'ease (tint → transparent)'],
  ['Validation', 'shake', '280ms', 'ease'],
  ['Tap', 'scale 0.97', '80ms', 'ease'],
];

function MotionSection() {
  return (
    <Section id="motion" num="09" title="Motion">
      <p style={ds.lede}>Movement is brisk and eased, never bouncy. Screens slide on a shared curve, sheets rise from the bottom, and feedback (toasts, row pulses, shake) is short and self-clearing.</p>
      <div style={{ ...ds.card, overflow: 'hidden', marginTop: 30 }}>
        {MOTION.map(([role, key, dur, ease], i) => (
          <div key={role} style={{ display: 'grid', gridTemplateColumns: '180px 200px 90px 1fr', alignItems: 'center', gap: 16, padding: '14px 22px', borderTop: i ? '1px solid var(--line-2)' : 'none' }}>
            <span style={{ fontSize: 13, fontWeight: 600, color: 'var(--ink)' }}>{role}</span>
            <span style={{ fontFamily: 'var(--mono)', fontSize: 11.5, color: 'var(--blue-700)' }}>{key}</span>
            <span style={{ fontFamily: 'var(--mono)', fontSize: 11.5, color: 'var(--ink-2)' }}>{dur}</span>
            <span style={{ fontFamily: 'var(--mono)', fontSize: 11, color: 'var(--ink-3)' }}>{ease}</span>
          </div>
        ))}
      </div>
      <div style={{ textAlign: 'center', padding: '60px 0 10px', color: 'var(--muted)', fontFamily: 'var(--mono)', fontSize: 11.5, letterSpacing: '0.08em' }}>
        PRO EXPENSE · DESIGN SYSTEM · MIRRORS THE SHIPPED HI-FI BUILD
      </div>
    </Section>
  );
}

window.DSContentB = { ButtonSection, SurfaceSection, DataSection, NavSection, MotionSection };
