// Pro Expense — Design System · root
const { Hero, NavStrip } = window.DSChrome;
const { ColorSection, TypeSection, IconSection, CategorySection } = window.DSContentA;
const { ButtonSection, SurfaceSection, DataSection, NavSection, MotionSection } = window.DSContentB;
const { ds, Section } = window.DSChrome;

// ── Foundations overview (principles) ──
const PRINCIPLES = [
  ['Calm, financial clarity', 'Cool blues and quiet neutrals keep the focus on numbers. Color is reserved for meaning — never decoration.'],
  ['Serif for money, sans for UI', 'Instrument Serif gives amounts and titles a considered, editorial weight; Geist keeps the interface neutral and legible.'],
  ['Signal over noise', 'Green, yellow, red and orange each carry one job — on-track, future, over/owed, and event tags. Nothing competes.'],
  ['One stroke language', '24px stroke icons at a consistent 1.6px weight, rounded caps, paired with tinted category badges.'],
];

function Foundations() {
  return (
    <Section id="foundations" num="00" title="Foundations">
      <p style={ds.lede}>Pro Expense is an account-free, offline-first expense tracker. Its design language pairs a Material-blue core with a warm-paper canvas and an editorial serif voice — built to make money feel calm and legible.</p>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, marginTop: 32 }}>
        {PRINCIPLES.map(([t, d]) => (
          <div key={t} style={{ ...ds.card, padding: 24 }}>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 22, letterSpacing: '-0.01em', color: 'var(--ink)' }}>{t}</div>
            <p style={{ fontSize: 13.5, lineHeight: 1.6, color: 'var(--ink-2)', margin: '10px 0 0' }}>{d}</p>
          </div>
        ))}
      </div>
    </Section>
  );
}

function App() {
  return (
    <div style={ds.page}>
      <Hero />
      <NavStrip />
      <div style={{ paddingTop: 56 }}>
        <Foundations />
        <ColorSection />
        <TypeSection />
        <IconSection />
        <CategorySection />
        <ButtonSection />
        <SurfaceSection />
        <DataSection />
        <NavSection />
        <MotionSection />
      </div>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
