// Hi-fi static screens for flows 02-08
// Reuses CATEGORIES, Icon, CatBadge, CatChip, Button, currencyFmt from proto-ui.jsx
// Reuses PhoneShell, ProtoBottomNav from proto-phone.jsx
// Reuses ContextHeader, QuickAccess, TxnRow, Card, FieldLabel from flow-01-screens.jsx

// ────────────────────────────────────────────────────────────────────
// Local helpers
// ────────────────────────────────────────────────────────────────────
function SectionTitle({ children }) {
  return (
    <div style={{ fontSize: 11, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--ink-3)', fontWeight: 600 }}>
      {children}
    </div>
  );
}

function NavBar({ left, title, right }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '14px 18px 6px' }}>
      <div style={{ minWidth: 60, display: 'flex', alignItems: 'center' }}>{left}</div>
      <div style={{ fontFamily: 'var(--serif)', fontSize: 17, color: 'var(--ink)' }}>{title}</div>
      <div style={{ minWidth: 60, display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>{right}</div>
    </div>
  );
}

function BackBtn({ label = '' }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 4, color: 'var(--ink-2)' }}>
      <Icon name="back" size={20} stroke="var(--ink-2)" strokeWidth={1.8} />
      {label && <span style={{ fontSize: 14 }}>{label}</span>}
    </div>
  );
}

// ════════════════════════════════════════════════════════════════════
// FLOW 02 — JOURNAL
// ════════════════════════════════════════════════════════════════════
function ScreenJournalHi() {
  const days = [
    { label: 'Today · May 25', total: 42, rows: [
      { id: 'j1', amount: 12.40, cat: 'food', note: 'Lunch with M.', time: '12:30 PM' },
      { id: 'j2', amount: 3.50, cat: 'transport', note: '', time: '09:15 AM' },
      { id: 'j3', amount: 5.00, cat: 'coffee', note: 'Oat latte', time: '08:40 AM' },
    ]},
    { label: 'Yesterday · May 24', total: 60, rows: [
      { id: 'j4', amount: 18.00, cat: 'entertainment', note: 'Movie · Dune', time: '08:10 PM', tag: 'Bali Trip' },
      { id: 'j5', amount: 42.00, cat: 'food', note: 'Groceries', time: '05:30 PM' },
    ]},
    { label: 'Mon · May 23', total: 8, rows: [
      { id: 'j6', amount: 8.00, cat: 'transport', note: 'Bus card', time: '07:45 AM' },
    ]},
  ];

  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '14px 22px 0', flexShrink: 0 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div>
              <div style={{ fontSize: 11, color: 'var(--muted)', fontFamily: 'var(--mono)', letterSpacing: '0.06em', textTransform: 'uppercase' }}>Diary view</div>
              <div style={{ fontFamily: 'var(--serif)', fontSize: 32, lineHeight: 1, letterSpacing: '-0.015em', marginTop: 4 }}>Journal</div>
            </div>
          </div>

          {/* search bar */}
          <div style={{ marginTop: 16, padding: '12px 14px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, display: 'flex', alignItems: 'center', gap: 10 }}>
            <Icon name="search" size={16} stroke="var(--muted)" strokeWidth={1.7} />
            <span style={{ fontSize: 13, color: 'var(--muted)' }}>Search notes, amount, category…</span>
          </div>

          {/* filter chips */}
          <div style={{ marginTop: 12, display: 'flex', gap: 6, overflow: 'hidden' }}>
            <FilterChip label="All" active />
            <FilterChip label="Food" />
            <FilterChip label="Transport" />
            <FilterChip label="Bills" />
            <FilterChip label="More" />
          </div>
        </div>

        <div className="proto-scroll" style={{ flex: 1, marginTop: 16, padding: '0 22px 38px' }}>
          {days.map((d, i) => (
            <div key={d.label} style={{ marginTop: i ? 22 : 0 }}>
              <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 10 }}>
                <div style={{ fontFamily: 'var(--serif)', fontSize: 18, letterSpacing: '-0.01em' }}>{d.label}</div>
                <div style={{ fontSize: 12, color: 'var(--muted)', fontFamily: 'var(--mono)' }}>{currencyFmt(d.total)}</div>
              </div>
              {d.rows.map(r => (
                <div key={r.id} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '12px 0', borderBottom: '1px solid var(--line-2)' }}>
                  <CatBadge cat={r.cat} size={36} />
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: 14, color: 'var(--ink)', fontWeight: 500, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                      {r.note || CATEGORIES[r.cat]?.label}
                    </div>
                    <div style={{ fontSize: 11.5, color: 'var(--muted)', display: 'flex', gap: 6, alignItems: 'center' }}>
                      <span>{CATEGORIES[r.cat]?.label}</span>
                      <span>·</span>
                      <span>{r.time}</span>
                      {r.tag && (<><span>·</span><span style={{ color: 'var(--tag)', display: 'inline-flex', alignItems: 'center', gap: 2 }}><Icon name="at" size={10} stroke="var(--tag)" strokeWidth={2}/>{r.tag}</span></>)}
                    </div>
                  </div>
                  <div style={{ fontFamily: 'var(--serif)', fontSize: 16, color: 'var(--ink)' }}>{currencyFmt(r.amount)}</div>
                </div>
              ))}
            </div>
          ))}
        </div>

        <ProtoBottomNav active="journal" />
      </div>
    </PhoneShell>
  );
}

function FilterChip({ label, active }) {
  return (
    <div style={{
      padding: '6px 12px', border: '1px solid ' + (active ? 'var(--ink)' : 'var(--line-strong)'),
      borderRadius: 99, fontSize: 12, fontWeight: active ? 600 : 500,
      background: active ? 'var(--ink)' : 'transparent', color: active ? 'var(--paper-warm)' : 'var(--ink-2)',
      whiteSpace: 'nowrap',
    }}>{label}</div>
  );
}

function ScreenJournalDetailHi() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar
          left={<BackBtn label="Journal" />}
          title=""
          right={<Icon name="more" size={20} stroke="var(--ink-2)" strokeWidth={1.8} />}
        />
        <div className="proto-scroll" style={{ flex: 1, padding: '8px 22px 20px' }}>
          {/* hero — large amount */}
          <div style={{ textAlign: 'center', marginTop: 20 }}>
            <CatBadge cat="food" size={64} />
            <div style={{ fontSize: 11, color: CATEGORIES.food.color, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', marginTop: 14, fontWeight: 600 }}>Food</div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 60, lineHeight: 1, letterSpacing: '-0.025em', marginTop: 6 }}>$12.40</div>
            <div style={{ fontSize: 12, color: 'var(--muted)', marginTop: 6 }}>Wed, May 25 · 12:30 PM</div>
          </div>

          {/* note */}
          <div style={{ marginTop: 28 }}>
            <SectionTitle>Note</SectionTitle>
            <div style={{ marginTop: 8, padding: '14px 16px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, fontFamily: 'var(--serif)', fontStyle: 'italic', fontSize: 15, lineHeight: 1.4, color: 'var(--ink-2)' }}>
              "Lunch with M. at the noodle place on 5th. Tip included."
            </div>
          </div>

          {/* meta — compact inline row, not a colored block */}
          <div style={{ marginTop: 18 }}>
            <SectionTitle>Linked to</SectionTitle>
            <div style={{ marginTop: 8, padding: '10px 12px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 12, display: 'flex', alignItems: 'center', gap: 10 }}>
              <div style={{ width: 28, height: 28, borderRadius: 8, background: 'var(--tag-tint)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                <Icon name="at" size={14} stroke="var(--tag-deep)" strokeWidth={2} />
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 13, color: 'var(--ink)', fontWeight: 500 }}>Bali Trip</div>
                <div style={{ fontSize: 11, color: 'var(--muted)' }}>Event · May 12 — May 26</div>
              </div>
              <Icon name="chevron-right" size={14} stroke="var(--muted)" strokeWidth={1.6} />
            </div>
          </div>

          {/* actions */}
          <div style={{ marginTop: 24, display: 'flex', gap: 8 }}>
            <Button variant="secondary" size="md" fullWidth>Edit</Button>
            <Button variant="secondary" size="md" fullWidth style={{ color: 'var(--danger)', borderColor: 'var(--danger-soft)' }}>Delete</Button>
          </div>
        </div>
      </div>
    </PhoneShell>
  );
}

// ════════════════════════════════════════════════════════════════════
// FLOW 03 — MORE / REPORTS / CATEGORY LIST
// ════════════════════════════════════════════════════════════════════
function ScreenMoreHi() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '14px 22px 8px' }}>
          <div style={{ fontSize: 11, color: 'var(--muted)', fontFamily: 'var(--mono)', letterSpacing: '0.06em', textTransform: 'uppercase' }}>Settings & extras</div>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 32, lineHeight: 1, letterSpacing: '-0.015em', marginTop: 4 }}>More</div>
        </div>

        <div className="proto-scroll" style={{ flex: 1, padding: '8px 22px 16px' }}>
          {/* profile */}
          <div style={{ marginTop: 8, padding: '14px 16px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, display: 'flex', alignItems: 'center', gap: 12 }}>
            <div style={{ width: 44, height: 44, borderRadius: 99, background: 'var(--blue-100)', color: 'var(--blue-700)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'var(--serif)', fontSize: 22, fontWeight: 600 }}>M</div>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 15, fontWeight: 600, color: 'var(--ink)' }}>Maya</div>
              <div style={{ fontSize: 12, color: 'var(--muted)' }}>All data local · no account</div>
            </div>
          </div>

          <SettingsGroup title="Features" rows={[
            { icon: 'feat-reports', tint: 'var(--blue-100)', stroke: 'var(--blue-700)', label: 'Reports' },
            { icon: 'feat-debt',    tint: 'var(--blue-100)', stroke: 'var(--blue-700)', label: 'Debt Tracker' },
            { icon: 'feat-split',   tint: 'var(--blue-100)', stroke: 'var(--blue-700)', label: 'Shared Costs' },
            { icon: 'feat-events',  tint: 'var(--blue-100)', stroke: 'var(--blue-700)', label: 'Category List' },
          ]} />

          <SettingsGroup title="Settings" rows={[
            { icon: 'cat-default', tint: 'var(--gray-100)', stroke: 'var(--ink-2)', label: 'Currency', detail: 'USD' },
            { icon: 'cat-bills',   tint: 'var(--gray-100)', stroke: 'var(--ink-2)', label: 'Monthly budget', detail: 'Off' },
            { icon: 'eye',         tint: 'var(--gray-100)', stroke: 'var(--ink-2)', label: 'PIN authentication', detail: 'Off' },
            { icon: 'note',        tint: 'var(--gray-100)', stroke: 'var(--ink-2)', label: 'Language', detail: 'English' },
            { icon: 'sparkle',     tint: 'var(--gray-100)', stroke: 'var(--ink-2)', label: 'Theme', detail: 'System' },
          ]} />

          <SettingsGroup title="Data" rows={[
            { icon: 'feat-reports', tint: 'var(--gray-100)', stroke: 'var(--ink-2)', label: 'Export data', detail: 'CSV · JSON' },
            { icon: 'close',        tint: 'var(--gray-100)', stroke: 'var(--danger)', label: 'Clear data', destructive: true },
          ]} />

          <div style={{ marginTop: 18, textAlign: 'center', fontSize: 11, color: 'var(--muted)', fontFamily: 'var(--mono)' }}>v0.1.0 · MVP</div>
        </div>

        <ProtoBottomNav active="more" />
      </div>
    </PhoneShell>
  );
}

function SettingsGroup({ title, rows }) {
  return (
    <div style={{ marginTop: 22 }}>
      <SectionTitle>{title}</SectionTitle>
      <div style={{ marginTop: 8, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 16, overflow: 'hidden' }}>
        {rows.map((r, i) => (
          <div key={r.label} style={{
            display: 'flex', alignItems: 'center', gap: 12, padding: '13px 14px',
            borderTop: i ? '1px solid var(--line-2)' : 'none',
          }}>
            <div style={{ width: 32, height: 32, borderRadius: 9, background: r.tint, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <Icon name={r.icon} size={16} stroke={r.stroke} strokeWidth={1.7} />
            </div>
            <span style={{ flex: 1, fontSize: 14, color: r.destructive ? 'var(--danger)' : 'var(--ink)', fontWeight: 500 }}>{r.label}</span>
            {r.detail && <span style={{ fontSize: 12, color: 'var(--muted)' }}>{r.detail}</span>}
            <Icon name="chevron-right" size={14} stroke="var(--muted)" strokeWidth={1.6} />
          </div>
        ))}
      </div>
    </div>
  );
}

function ScreenReportsHi() {
  // donut: food 38%, transport 22%, shopping 18%, bills 14%, entertainment 8%
  const segs = [
    { cat: 'food', pct: 38, amount: 478 },
    { cat: 'transport', pct: 22, amount: 274 },
    { cat: 'shopping', pct: 18, amount: 224 },
    { cat: 'bills', pct: 14, amount: 170 },
    { cat: 'entertainment', pct: 8, amount: 101 },
  ];
  const circumference = 2 * Math.PI * 15.91;
  let cumulative = 25; // start offset

  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar left={<BackBtn label="More" />} title="Reports" right={null} />

        <div className="proto-scroll" style={{ flex: 1, padding: '0 22px 38px' }}>
          {/* period selector */}
          <div style={{ marginTop: 8, display: 'flex', justifyContent: 'center' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 14, padding: '8px 16px', background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 99 }}>
              <Icon name="back" size={14} stroke="var(--muted)" strokeWidth={1.8} />
              <span style={{ fontSize: 13, fontWeight: 600 }}>May 2026</span>
              <Icon name="chevron-right" size={14} stroke="var(--muted)" strokeWidth={1.8} />
            </div>
          </div>

          {/* total */}
          <div style={{ marginTop: 22, textAlign: 'center' }}>
            <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: 'var(--muted)' }}>Total spent</div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 56, lineHeight: 1, letterSpacing: '-0.025em', marginTop: 4 }}>$1,247</div>
            <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 6 }}>Daily avg <strong style={{ color: 'var(--ink)' }}>$50</strong> · 25 days in</div>
          </div>

          {/* donut */}
          <div style={{ marginTop: 18, display: 'flex', justifyContent: 'center', position: 'relative' }}>
            <svg width="200" height="200" viewBox="0 0 42 42">
              <circle cx="21" cy="21" r="15.91" fill="transparent" stroke="rgba(43,31,23,0.08)" strokeWidth="5" />
              {segs.map((s, i) => {
                const dash = (s.pct / 100) * circumference;
                const offset = cumulative;
                cumulative -= (s.pct / 100) * circumference;
                return (
                  <circle
                    key={s.cat}
                    cx="21" cy="21" r="15.91"
                    fill="transparent"
                    stroke={CATEGORIES[s.cat].color}
                    strokeWidth="5"
                    strokeDasharray={`${dash} ${circumference - dash}`}
                    strokeDashoffset={offset}
                    strokeLinecap="butt"
                  />
                );
              })}
            </svg>
            <div style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
              <div style={{ fontSize: 10.5, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--muted)' }}>by category</div>
              <div style={{ fontFamily: 'var(--serif)', fontSize: 22, lineHeight: 1, marginTop: 2 }}>5</div>
            </div>
          </div>

          {/* top categories */}
          <div style={{ marginTop: 18 }}>
            <SectionTitle>Top categories</SectionTitle>
            <div style={{ marginTop: 8, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, overflow: 'hidden' }}>
              {segs.map((s, i) => (
                <div key={s.cat} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '12px 14px', borderTop: i ? '1px solid var(--line-2)' : 'none' }}>
                  <div style={{ width: 8, height: 8, borderRadius: 99, background: CATEGORIES[s.cat].color }} />
                  <CatBadge cat={s.cat} size={28} />
                  <span style={{ flex: 1, fontSize: 13, fontWeight: 500 }}>{CATEGORIES[s.cat].label}</span>
                  <span style={{ fontSize: 11, color: 'var(--muted)', fontFamily: 'var(--mono)' }}>{s.pct}%</span>
                  <span style={{ fontFamily: 'var(--serif)', fontSize: 15, minWidth: 60, textAlign: 'right' }}>{currencyFmt(s.amount)}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </PhoneShell>
  );
}

function ScreenCategoryListHi() {
  const defaults = ['food', 'transport', 'shopping', 'bills', 'health', 'entertainment'];
  const custom = ['coffee', 'pet'];
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <NavBar
          left={<BackBtn label="More" />}
          title="Categories"
          right={<div style={{ width: 32, height: 32, borderRadius: 9, background: 'var(--clay)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><Icon name="plus" size={18} stroke="#fffdf6" strokeWidth={2.2}/></div>}
        />

        <div className="proto-scroll" style={{ flex: 1, padding: '8px 22px 16px' }}>
          <div style={{ marginTop: 8 }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <SectionTitle>Default · always first</SectionTitle>
              <span style={{ fontSize: 10, color: 'var(--muted)', fontFamily: 'var(--mono)' }}>locked</span>
            </div>
            <div style={{ marginTop: 8, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, overflow: 'hidden' }}>
              {defaults.map((id, i) => (
                <CategoryRow key={id} id={id} locked first={i === 0} />
              ))}
            </div>
          </div>

          <div style={{ marginTop: 22 }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <SectionTitle>Custom · drag to reorder</SectionTitle>
              <span style={{ fontSize: 10, color: 'var(--muted)', fontFamily: 'var(--mono)' }}>{custom.length} categories</span>
            </div>
            <div style={{ marginTop: 8, background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 14, overflow: 'hidden' }}>
              {custom.map((id, i) => (
                <CategoryRow key={id} id={id} draggable first={i === 0} />
              ))}
            </div>
          </div>

          <button style={{
            marginTop: 18, width: '100%', padding: '14px',
            background: 'transparent', border: '1.4px dashed var(--line-strong)', borderRadius: 14,
            color: 'var(--ink-3)', fontSize: 13, fontWeight: 500, cursor: 'pointer',
            display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6,
          }}>
            <Icon name="plus" size={16} stroke="var(--ink-3)" strokeWidth={1.8}/>
            Create new category
          </button>

          <div style={{ marginTop: 18, textAlign: 'center', fontSize: 11, color: 'var(--muted)', lineHeight: 1.5, padding: '0 16px' }}>
            Order here mirrors chip order in Add Expense
          </div>
        </div>
      </div>
    </PhoneShell>
  );
}

function CategoryRow({ id, locked, draggable, first }) {
  const cat = CATEGORIES[id];
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '13px 14px', borderTop: first ? 'none' : '1px solid var(--line-2)' }}>
      <CatBadge cat={id} size={34} />
      <span style={{ flex: 1, fontSize: 14, fontWeight: 500 }}>{cat.label}</span>
      {locked && (
        <span style={{ fontSize: 9, fontFamily: 'var(--mono)', color: 'var(--muted)', textTransform: 'uppercase', letterSpacing: '0.08em', border: '1px solid var(--line)', borderRadius: 99, padding: '2px 7px' }}>locked</span>
      )}
      {draggable && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 2, color: 'var(--muted-2)' }}>
          <div style={{ width: 12, height: 1.5, background: 'currentColor', borderRadius: 99 }} />
          <div style={{ width: 12, height: 1.5, background: 'currentColor', borderRadius: 99 }} />
          <div style={{ width: 12, height: 1.5, background: 'currentColor', borderRadius: 99 }} />
        </div>
      )}
    </div>
  );
}

// ════════════════════════════════════════════════════════════════════
// FLOW 04 — FIRST LAUNCH (Splash + Onboarding)
// ════════════════════════════════════════════════════════════════════
function ScreenSplashHi() {
  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', position: 'relative' }}>
        <div style={{ width: 88, height: 88, borderRadius: 16, background: 'var(--blue-500)', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 12px 24px rgba(3,155,229,0.32)' }}>
          {/* app icon glyph — ic_pro_expense */}
          <svg width="88" height="88" viewBox="8 8 176 176" fill="none">
            <path fillRule="evenodd" d="M140,126V66a10,10,0,1,0-14-14H66A10,10,0,1,0,52,66v60a10,10,0,1,0,13.17,15h61.66A10,10,0,1,0,140,126Zm-14.63,3H66.63A9.91,9.91,0,0,0,64,126V66a9.39,9.39,0,0,0,2-2h60a9.39,9.39,0,0,0,2,2v60A9.91,9.91,0,0,0,125.37,129Z" fill="var(--white)"/>
          </svg>
        </div>
        <div style={{ marginTop: 22, fontFamily: 'var(--serif)', fontSize: 32, letterSpacing: '-0.015em', color: 'var(--ink)' }}>Pro Expense</div>
        <div style={{ marginTop: 6, fontSize: 13, color: 'var(--muted)' }}>Your finance notebook</div>

        {/* loading dots */}
        <div style={{ position: 'absolute', bottom: 80, display: 'flex', gap: 6 }}>
          {[0, 1, 2].map(i => (
            <div key={i} style={{ width: 6, height: 6, borderRadius: 99, background: 'var(--blue-500)', opacity: 0.3 + i * 0.2 }} />
          ))}
        </div>
      </div>
    </PhoneShell>
  );
}

function ScreenOnboardingHi({ slide = 1 }) {
  const slides = [
    { title: 'Welcome',       body: 'Your personal finance notebook.' },
    { title: 'Quick Log',     body: 'Log expenses in seconds — amount, category, done.' },
    { title: 'Shared Costs',  body: 'Split bills instantly — total, people, done.' },
    { title: 'Event Budget',  body: 'Plan and track any event budget — trips, weddings, parties.' },
    { title: 'Journal',       body: 'Review your spending like a diary, day by day.' },
  ];
  const isLast = slide === 5;
  const s = slides[slide - 1];
  const Illo = ONBOARD_ILLOS[slide] || ONBOARD_ILLOS[1];

  return (
    <PhoneShell>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        {/* skip */}
        <div style={{ padding: '14px 22px', display: 'flex', justifyContent: 'flex-end' }}>
          {!isLast && <button style={{ background: 'transparent', border: 'none', fontSize: 13, color: 'var(--muted)', cursor: 'pointer' }}>Skip</button>}
        </div>

        {/* hero illustration */}
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 28px', textAlign: 'center' }}>
          <div style={{ width: 280, maxWidth: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Illo />
          </div>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 38, letterSpacing: '-0.02em', lineHeight: 1.05, marginTop: 28 }}>{s.title}</div>
          <div style={{ fontSize: 15, color: 'var(--ink-2)', lineHeight: 1.45, marginTop: 12, maxWidth: 280 }}>{s.body}</div>
        </div>

        {/* nav row — Back · dots · Next, lifted well clear of the CTA below to avoid mis-taps */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0 28px', marginBottom: 48 }}>
          <span style={{ fontSize: 13, color: slide === 1 ? 'transparent' : 'var(--muted)', minWidth: 44 }}>‹ Back</span>
          <div style={{ display: 'flex', justifyContent: 'center', gap: 6 }}>
            {[1,2,3,4,5].map(i => (
              <div key={i} style={{
                width: i === slide ? 22 : 6, height: 6,
                background: i === slide ? 'var(--clay)' : 'rgba(43,31,23,0.18)',
                borderRadius: 99, transition: 'width 0.2s',
              }} />
            ))}
          </div>
          <span style={{ fontSize: 13.5, color: isLast ? 'transparent' : 'var(--ink)', fontWeight: 600, minWidth: 44, textAlign: 'right' }}>Next ›</span>
        </div>

        {/* CTA — primary, bottom-anchored so an eager user starts from any slide */}
        <div style={{ padding: '0 22px 38px' }}>
          <Button variant="primary" size="lg" fullWidth>Get started</Button>
        </div>
      </div>
    </PhoneShell>
  );
}

Object.assign(window, {
  ScreenJournalHi, ScreenJournalDetailHi,
  ScreenMoreHi, ScreenReportsHi, ScreenCategoryListHi,
  ScreenSplashHi, ScreenOnboardingHi,
  SectionTitle, NavBar, BackBtn,
});
