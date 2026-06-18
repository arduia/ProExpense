// Flow 06 — Event Budget · Creation sub-flow (happy path)
// Empty state → Create Event sheet (filled & valid) → (existing list → detail)
// Reuses PhoneShell, NavBar, BackBtn, Icon, Button, ProtoBottomNav, SectionTitle,
// EdgeBottomSheet, Field, currencyFmt — all global.

// 1 — Events empty state (fresh user, no events yet)
function ScreenEventEmptyHi() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '14px 22px 8px' }}>
          <div style={{ fontSize: 11, color: 'var(--muted)', fontFamily: 'var(--mono)', letterSpacing: '0.06em', textTransform: 'uppercase' }}>Budget tracker</div>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 32, lineHeight: 1, letterSpacing: '-0.015em', marginTop: 4 }}>Events</div>
        </div>

        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 40px', textAlign: 'center' }}>
          <div style={{ width: 96, height: 96, borderRadius: 28, background: 'var(--blue-100)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Icon name="feat-events" size={44} stroke="var(--blue-700)" strokeWidth={1.5} />
          </div>
          <div style={{ marginTop: 22, fontFamily: 'var(--serif)', fontSize: 23, letterSpacing: '-0.01em', color: 'var(--ink)' }}>No active events</div>
          <div style={{ marginTop: 8, fontSize: 13.5, color: 'var(--muted)', lineHeight: 1.5 }}>
            Set a budget for a trip, wedding or party — then tag expenses to track it in real time.
          </div>
          <div style={{ marginTop: 22 }}>
            <Button variant="primary" size="lg" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <Icon name="plus" size={17} stroke="#fffdf6" strokeWidth={2.4} /> Create event
            </Button>
          </div>
        </div>

        <ProtoBottomNav active="budget" />
      </div>
    </PhoneShell>
  );
}

// 2 — Create Event sheet, filled & valid (happy path, button enabled)
function ScreenEventCreateHi() {
  return (
    <PhoneShell>
      {/* dimmed list behind the sheet */}
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '14px 22px 8px' }}>
          <div style={{ fontSize: 11, color: 'var(--muted)', fontFamily: 'var(--mono)', letterSpacing: '0.06em', textTransform: 'uppercase' }}>Budget tracker</div>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 32, lineHeight: 1, letterSpacing: '-0.015em', marginTop: 4 }}>Events</div>
        </div>
      </div>

      <EdgeBottomSheet title="New event" height={560}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 18, marginTop: 4 }}>
          {/* name */}
          <div>
            <SheetLabel>Event name</SheetLabel>
            <Field>
              <span style={{ fontSize: 15, color: 'var(--ink)' }}>Bali Trip 2026</span>
              <span style={{ fontSize: 11, fontFamily: 'var(--mono)', color: 'var(--muted)' }}>14/30</span>
            </Field>
          </div>

          {/* dates */}
          <div>
            <SheetLabel>Dates</SheetLabel>
            <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
              <Field>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <Icon name="calendar" size={16} stroke="var(--ink-3)" strokeWidth={1.7} />
                  <span style={{ fontSize: 14, color: 'var(--ink)' }}>May 12</span>
                </div>
              </Field>
              <Icon name="back" size={16} stroke="var(--muted)" strokeWidth={2} style={{ transform: 'rotate(180deg)', flexShrink: 0 }} />
              <Field>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <Icon name="calendar" size={16} stroke="var(--ink-3)" strokeWidth={1.7} />
                  <span style={{ fontSize: 14, color: 'var(--ink)' }}>May 26</span>
                </div>
              </Field>
            </div>
          </div>

          {/* budget */}
          <div>
            <SheetLabel>Total budget</SheetLabel>
            <Field>
              <span style={{ fontFamily: 'var(--serif)', fontSize: 24, letterSpacing: '-0.01em', color: 'var(--ink)' }}>$2,000</span>
              <span style={{ fontSize: 11, fontFamily: 'var(--mono)', color: 'var(--muted)' }}>USD</span>
            </Field>
          </div>
        </div>

        <div style={{ marginTop: 24 }}>
          <Button variant="primary" size="lg" fullWidth>Create event</Button>
        </div>
      </EdgeBottomSheet>
    </PhoneShell>
  );
}

function SheetLabel({ children }) {
  return (
    <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink-3)', fontWeight: 600, marginBottom: 6 }}>
      {children}
    </div>
  );
}

Object.assign(window, { ScreenEventEmptyHi, ScreenEventCreateHi });
