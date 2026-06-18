// MVP screen wireframes — referenced from app.jsx
// Each export is a function (props: { variant, empty }) → JSX of inner phone content.

// ─────────────────────────────────────────────────────────────
// 03 — HOME
// ─────────────────────────────────────────────────────────────
function ScreenHome({ variant = 'casual', empty = false }) {
  return (
    <>
      <Pad p={18} style={{ flex: 1, overflow: 'hidden', position: 'relative' }}>
        {/* top row */}
        <Row style={{ justifyContent: 'space-between' }}>
          <Stack gap={4}>
            <Line w={70} style={{ height: 5 }} />
            <div style={{ fontFamily: 'var(--serif)', fontSize: 22, lineHeight: 1, letterSpacing: '-0.01em' }}>Home</div>
          </Stack>
          <Glyph size={28} />
        </Row>

        {/* contextual header card — variant aware */}
        <div
          style={{
            marginTop: 18,
            padding: 16,
            border: `1.2px solid ${W_INK}`,
            borderRadius: 14,
            background: W_FILL_2,
            position: 'relative',
          }}
        >
          {variant === 'casual' && (
            <Stack gap={8}>
              <div style={{ fontSize: 10, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: W_MUTED }}>Spent this month</div>
              <div style={{ fontFamily: 'var(--serif)', fontSize: 34, lineHeight: 1, letterSpacing: '-0.02em' }}>{empty ? '$0' : '$420'}</div>
              <Line w="40%" />
            </Stack>
          )}
          {variant === 'budget' && (
            <Stack gap={8}>
              <div style={{ fontSize: 10, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: W_MUTED }}>This month</div>
              <div style={{ fontFamily: 'var(--serif)', fontSize: 28, lineHeight: 1, letterSpacing: '-0.02em' }}>{empty ? '$0' : '$320'} <span style={{ color: W_MUTED, fontSize: 18 }}>/ $500</span></div>
              <div style={{ height: 8, background: '#fff', border: `1px solid ${W_LINE}`, borderRadius: 99, overflow: 'hidden' }}>
                <div style={{ height: '100%', width: empty ? '0%' : '64%', background: W_INK, borderRadius: 99 }} />
              </div>
            </Stack>
          )}
          {variant === 'event' && (
            <Stack gap={8}>
              <Row style={{ justifyContent: 'space-between' }}>
                <div style={{ fontSize: 10, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: W_MUTED }}>Active event</div>
                <div style={{ fontSize: 10, fontFamily: 'var(--mono)', color: W_MUTED }}>14 days left</div>
              </Row>
              <div style={{ fontFamily: 'var(--serif)', fontSize: 18, lineHeight: 1 }}>Bali Trip</div>
              <div style={{ fontFamily: 'var(--serif)', fontSize: 26, lineHeight: 1, letterSpacing: '-0.02em' }}>{empty ? '$2,000' : '$1,240'} <span style={{ color: W_MUTED, fontSize: 14 }}>left</span></div>
              <div style={{ height: 8, background: '#fff', border: `1px solid ${W_LINE}`, borderRadius: 99, overflow: 'hidden' }}>
                <div style={{ height: '100%', width: empty ? '0%' : '38%', background: W_INK, borderRadius: 99 }} />
              </div>
            </Stack>
          )}
        </div>

        {/* Quick access — features that otherwise live in More */}
        <div style={{ marginTop: 20 }}>
          <Row style={{ justifyContent: 'space-between', marginBottom: 10 }}>
            <SectionHead>Quick access</SectionHead>
            <span style={{ fontSize: 10, color: W_MUTED }}>Customize</span>
          </Row>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8 }}>
            {[
              ['Reports', '▤'],
              ['Debt', '◐◑'],
              ['Split', '◈'],
              ['Events', '◇'],
            ].map(([lbl, gl]) => (
              <Stack key={lbl} gap={6} style={{ alignItems: 'center', padding: '10px 4px', border: `1px solid ${W_LINE_2}`, borderRadius: 10, background: W_FILL_2 }}>
                <div style={{ width: 26, height: 26, border: `1px solid ${W_LINE}`, borderRadius: 6, background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 12, color: W_MUTED, fontFamily: 'var(--serif)' }}>{gl}</div>
                <span style={{ fontSize: 10, color: W_INK }}>{lbl}</span>
              </Stack>
            ))}
          </div>
        </div>

        {/* recent transactions list */}
        <div style={{ marginTop: 18 }}>
          <Row style={{ justifyContent: 'space-between', marginBottom: 12 }}>
            <SectionHead>Recent</SectionHead>
            <span style={{ fontSize: 10, color: W_MUTED }}>See all</span>
          </Row>

          {empty ? (
            <Stack gap={10} style={{ alignItems: 'center', paddingTop: 30 }}>
              <div style={{ width: 64, height: 64, border: `1.5px dashed ${W_LINE}`, borderRadius: 12, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <span style={{ fontSize: 24, color: W_MUTED }}>◌</span>
              </div>
              <div style={{ fontFamily: 'var(--serif)', fontStyle: 'italic', fontSize: 14, color: W_INK, textAlign: 'center', maxWidth: 220 }}>
                No expenses yet. Start by logging your first one!
              </div>
              <div style={{ marginTop: 6, padding: '6px 14px', border: `1.2px solid ${W_INK}`, borderRadius: 99, fontSize: 11 }}>Tap +</div>
            </Stack>
          ) : (
            <Stack gap={0}>
              {[
                ['Lunch', 'Food', '$12'],
                ['Metro', 'Transport', '$3'],
                ['Coffee', 'Food', '$5'],
                ['Movie', 'Entertainment', '$18'],
                ['Groceries', 'Food', '$42'],
              ].map(([t, c, a], i) => (
                <Row key={i} style={{ padding: '10px 0', borderTop: i ? `1px solid ${W_LINE_2}` : 'none', justifyContent: 'space-between' }}>
                  <Row gap={10}>
                    <Glyph size={28} />
                    <Stack gap={3}>
                      <div style={{ fontSize: 12, fontWeight: 500, color: W_INK }}>{t}</div>
                      <div style={{ fontSize: 10, color: W_MUTED }}>{c}</div>
                    </Stack>
                  </Row>
                  <div style={{ fontFamily: 'var(--serif)', fontSize: 14 }}>{a}</div>
                </Row>
              ))}
            </Stack>
          )}
        </div>
        <FAB />
      </Pad>
      <BottomNav active="home" />
    </>
  );
}

// ─────────────────────────────────────────────────────────────
// 04a — ADD EXPENSE · Amount
// ─────────────────────────────────────────────────────────────
function ScreenAddAmount() {
  return (
    <Pad p={18} style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <Row style={{ justifyContent: 'space-between' }}>
        <span style={{ fontSize: 13, color: W_INK }}>← Cancel</span>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 14 }}>New expense</div>
        <span style={{ width: 38 }} />
      </Row>

      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'flex-start', paddingTop: 22 }}>
        <div style={{ fontSize: 10, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: W_MUTED, marginBottom: 10 }}>Amount</div>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 56, lineHeight: 1, letterSpacing: '-0.03em' }}>$<span style={{ color: W_MUTED }}>0</span><span style={{ borderRight: `1.5px solid ${W_INK}`, marginLeft: 2, animation: 'blink 1s steps(1) infinite' }}>&nbsp;</span></div>
        <div style={{ marginTop: 6, fontSize: 10, color: W_MUTED }}>USD · home currency</div>

        {/* category chips — defaults + custom */}
        <div style={{ marginTop: 22, width: '100%' }}>
          <Row style={{ justifyContent: 'space-between' }}>
            <SectionHead mute>Default</SectionHead>
            <span style={{ fontSize: 9, color: W_MUTED }}>tap to select</span>
          </Row>
          <Row gap={6} style={{ marginTop: 6, overflow: 'hidden', flexWrap: 'wrap' }}>
            <Chip active>Food</Chip>
            <Chip>Transport</Chip>
            <Chip>Shopping</Chip>
            <Chip>Bills</Chip>
          </Row>
          <SectionHead mute style={{ marginTop: 8 }}>Custom</SectionHead>
          <Row gap={6} style={{ marginTop: 6, overflow: 'hidden', flexWrap: 'wrap' }}>
            <Chip>Coffee runs</Chip>
            <Chip>Pet care</Chip>
            <Chip>+ More</Chip>
          </Row>
        </div>
      </div>

      {/* numeric keypad with inline Next on bottom-right (reachable) */}
      <div style={{ marginTop: 14, display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 6 }}>
        {['1','2','3'].map(k => <Key key={k} k={k} />)}
        <div style={{ gridRow: 'span 2', padding: '14px 0', textAlign: 'center', background: W_INK, color: W_PAPER, borderRadius: 10, fontSize: 13, fontWeight: 600, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>Next →</div>
        {['4','5','6'].map(k => <Key key={k} k={k} />)}
        {['7','8','9'].map(k => <Key key={k} k={k} />)}
        <div style={{ padding: '14px 0', textAlign: 'center', background: W_FILL, border: `1px solid ${W_LINE}`, borderRadius: 10, fontSize: 12, fontWeight: 500 }}>Save</div>
        <Key k="." />
        <Key k="0" />
        <Key k="⌫" />
      </div>
    </Pad>
  );
}

function Key({ k }) {
  return (
    <div style={{ padding: '14px 0', textAlign: 'center', border: `1px solid ${W_LINE_2}`, borderRadius: 10, background: W_FILL_2, fontSize: 18, fontFamily: 'var(--serif)' }}>{k}</div>
  );
}

// ─────────────────────────────────────────────────────────────
// 04b — ADD EXPENSE · Details
// ─────────────────────────────────────────────────────────────
function ScreenAddDetails() {
  return (
    <Pad p={18} style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 14 }}>
      <Row style={{ justifyContent: 'space-between' }}>
        <span style={{ fontSize: 13, color: W_INK }}>← Back</span>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 14 }}>Details</div>
        <span style={{ fontSize: 13, color: W_INK, fontWeight: 600 }}>Save</span>
      </Row>

      {/* read-only amount */}
      <div style={{ padding: '12px 14px', border: `1.2px dashed ${W_LINE}`, borderRadius: 12, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <div style={{ fontSize: 9, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: W_MUTED }}>Amount</div>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 22, lineHeight: 1.1 }}>$12.40</div>
        </div>
        <span style={{ fontSize: 10, color: W_MUTED }}>edit</span>
      </div>

      {/* category — default + custom */}
      <Stack gap={6}>
        <SectionHead mute>Category *</SectionHead>
        <div style={{ fontSize: 9, color: W_MUTED, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em' }}>Default</div>
        <Row gap={6} style={{ flexWrap: 'wrap' }}>
          <Chip active>Food</Chip>
          <Chip>Transport</Chip>
          <Chip>Shopping</Chip>
          <Chip>Bills</Chip>
          <Chip>Health</Chip>
          <Chip>Entertainment</Chip>
        </Row>
        <div style={{ fontSize: 9, color: W_MUTED, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.08em', marginTop: 2 }}>Custom</div>
        <Row gap={6} style={{ flexWrap: 'wrap' }}>
          <Chip>Coffee runs</Chip>
          <Chip>Pet care</Chip>
          <Chip>+ Add</Chip>
        </Row>
      </Stack>

      {/* date + time — single combined input */}
      <Stack gap={6}>
        <SectionHead mute>Date &amp; time</SectionHead>
        <Row style={{ padding: '12px 14px', border: `1px solid ${W_LINE}`, borderRadius: 10, justifyContent: 'space-between', background: W_FILL_2 }}>
          <Row gap={10}>
            <Glyph size={20} />
            <Stack gap={2}>
              <span style={{ fontSize: 12, fontWeight: 500 }}>Today, May 18</span>
              <span style={{ fontSize: 10, color: W_MUTED }}>12 : 30 PM</span>
            </Stack>
          </Row>
          <span style={{ fontSize: 10, color: W_MUTED }}>Change ›</span>
        </Row>
      </Stack>

      {/* note */}
      <Stack gap={6}>
        <SectionHead mute>Note</SectionHead>
        <div style={{ padding: '10px 12px', border: `1px solid ${W_LINE}`, borderRadius: 10, fontSize: 11, color: W_MUTED }}>
          Optional · max 200 chars
        </div>
      </Stack>

      {/* @ tag */}
      <Stack gap={6}>
        <Row style={{ justifyContent: 'space-between' }}>
          <SectionHead mute>@ tag</SectionHead>
          <span style={{ fontSize: 9, color: W_MUTED }}>optional · event or debt</span>
        </Row>
        <Row gap={6} style={{ padding: '8px 10px', border: `1px dashed ${W_LINE}`, borderRadius: 10 }}>
          <span style={{ fontSize: 13, color: W_ACCENT }}>@</span>
          <span style={{ fontSize: 11, color: W_MUTED }}>link to event or debt…</span>
        </Row>
      </Stack>

      {/* save button */}
      <div style={{ marginTop: 'auto', padding: '14px 0', textAlign: 'center', background: W_INK, color: W_PAPER, borderRadius: 12, fontSize: 13, fontWeight: 600 }}>Save expense</div>
    </Pad>
  );
}

// ─────────────────────────────────────────────────────────────
// 05 — JOURNAL
// ─────────────────────────────────────────────────────────────
function ScreenJournal({ empty = false }) {
  const day = (date, total, rows) => (
    <Stack key={date} gap={0}>
      <Row style={{ justifyContent: 'space-between', padding: '12px 0 8px' }}>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 15, letterSpacing: '-0.01em' }}>{date}</div>
        <div style={{ fontSize: 11, color: W_MUTED }}>{total}</div>
      </Row>
      {rows.map((r, i) => (
        <Row key={i} style={{ padding: '8px 0', borderTop: `1px solid ${W_LINE_2}`, justifyContent: 'space-between' }}>
          <Row gap={10}>
            <Glyph size={24} />
            <Stack gap={2}>
              <div style={{ fontSize: 12 }}>{r[0]}</div>
              <div style={{ fontSize: 9, color: W_MUTED }}>{r[1]} · {r[2]}</div>
            </Stack>
          </Row>
          <div style={{ fontFamily: 'var(--serif)', fontSize: 13 }}>{r[3]}</div>
        </Row>
      ))}
    </Stack>
  );

  return (
    <>
      <Pad p={18} style={{ flex: 1, overflow: 'hidden', position: 'relative' }}>
        <Stack gap={4}>
          <Line w={60} />
          <div style={{ fontFamily: 'var(--serif)', fontSize: 22, lineHeight: 1, letterSpacing: '-0.01em' }}>Journal</div>
        </Stack>

        {/* search */}
        <Row style={{ marginTop: 14, padding: '8px 12px', border: `1px solid ${W_LINE}`, borderRadius: 10, gap: 8 }}>
          <span style={{ fontSize: 11, color: W_MUTED }}>⌕</span>
          <span style={{ fontSize: 11, color: W_MUTED }}>Search notes, amount…</span>
        </Row>
        <Row gap={6} style={{ marginTop: 10, overflow: 'hidden' }}>
          <Chip active>All</Chip>
          <Chip>Food</Chip>
          <Chip>Transport</Chip>
          <Chip>Bills</Chip>
        </Row>

        {empty ? (
          <Stack gap={10} style={{ alignItems: 'center', paddingTop: 60 }}>
            <div style={{ width: 72, height: 56, border: `1.5px dashed ${W_LINE}`, borderRadius: 8 }} />
            <div style={{ fontFamily: 'var(--serif)', fontStyle: 'italic', fontSize: 14, color: W_INK, textAlign: 'center', maxWidth: 220 }}>
              Your journal is empty. Log an expense to see it here.
            </div>
          </Stack>
        ) : (
          <Stack gap={4} style={{ marginTop: 14 }}>
            {day('Today · May 18', '$42', [
              ['Lunch', 'Food', '12:30', '$12'],
              ['Metro', 'Transport', '09:15', '$3'],
              ['Coffee', 'Food', '08:40', '$5'],
            ])}
            {day('Yesterday · May 17', '$60', [
              ['Movie', 'Entertainment', '20:10', '$18'],
              ['Groceries', 'Food', '17:30', '$42'],
            ])}
            {day('May 16', '$8', [
              ['Bus card', 'Transport', '07:45', '$8'],
            ])}
          </Stack>
        )}
        <FAB />
      </Pad>
      <BottomNav active="journal" />
    </>
  );
}

// ─────────────────────────────────────────────────────────────
// 06 — JOURNAL DETAIL
// ─────────────────────────────────────────────────────────────
function ScreenJournalDetail() {
  return (
    <Pad p={18} style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 14 }}>
      <Row style={{ justifyContent: 'space-between' }}>
        <span style={{ fontSize: 13 }}>← Journal</span>
        <span style={{ fontSize: 18, color: W_MUTED }}>⋯</span>
      </Row>

      <Stack gap={6} style={{ alignItems: 'center', textAlign: 'center', marginTop: 16 }}>
        <Glyph size={40} />
        <div style={{ fontSize: 10, color: W_MUTED, marginTop: 6 }}>FOOD</div>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 48, lineHeight: 1, letterSpacing: '-0.02em', marginTop: 4 }}>$12.40</div>
        <div style={{ fontSize: 11, color: W_MUTED }}>May 18, 2026 · 12:30 PM</div>
      </Stack>

      <Stack gap={6} style={{ marginTop: 12 }}>
        <SectionHead mute>Note</SectionHead>
        <div style={{ padding: 12, border: `1px solid ${W_LINE}`, borderRadius: 10, fontSize: 12, color: W_INK, fontFamily: 'var(--serif)', fontStyle: 'italic' }}>
          Lunch with M. at the noodle place on 5th. Tip included.
        </div>
      </Stack>

      <Stack gap={6}>
        <SectionHead mute>Tagged to</SectionHead>
        <Row style={{ padding: '8px 12px', border: `1px solid ${W_LINE}`, borderRadius: 10 }}>
          <span style={{ fontSize: 11, color: W_ACCENT }}>@</span>
          <span style={{ fontSize: 12, marginLeft: 6 }}>Bali Trip</span>
        </Row>
      </Stack>

      <div style={{ marginTop: 'auto', display: 'flex', gap: 8 }}>
        <div style={{ flex: 1, padding: '12px 0', textAlign: 'center', border: `1px solid ${W_INK}`, borderRadius: 12, fontSize: 12 }}>Edit</div>
        <div style={{ flex: 1, padding: '12px 0', textAlign: 'center', border: `1px solid ${W_LINE}`, borderRadius: 12, fontSize: 12, color: W_MUTED }}>Delete</div>
      </div>
    </Pad>
  );
}

// ─────────────────────────────────────────────────────────────
// 12 — REPORTS
// ─────────────────────────────────────────────────────────────
function ScreenReports({ empty = false }) {
  return (
    <Pad p={18} style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 14 }}>
      <Row style={{ justifyContent: 'space-between' }}>
        <span style={{ fontSize: 13 }}>← More</span>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 14 }}>Reports</div>
        <span style={{ width: 30 }} />
      </Row>

      <Row style={{ padding: '6px 10px', border: `1px solid ${W_LINE}`, borderRadius: 99, alignSelf: 'center', gap: 10, marginTop: 4 }}>
        <span style={{ fontSize: 11, color: W_MUTED }}>‹</span>
        <span style={{ fontSize: 12 }}>May 2026</span>
        <span style={{ fontSize: 11, color: W_MUTED }}>›</span>
      </Row>

      {empty ? (
        <Stack gap={10} style={{ alignItems: 'center', paddingTop: 60 }}>
          <div style={{ width: 80, height: 60, border: `1.5px dashed ${W_LINE}`, borderRadius: 8 }} />
          <div style={{ fontFamily: 'var(--serif)', fontStyle: 'italic', fontSize: 13, color: W_INK, textAlign: 'center', maxWidth: 220 }}>
            No data yet. Start logging to see your spending insights.
          </div>
        </Stack>
      ) : (
        <>
          <Stack gap={4} style={{ alignItems: 'center', marginTop: 8 }}>
            <div style={{ fontSize: 10, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: W_MUTED }}>Total spent</div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 42, lineHeight: 1, letterSpacing: '-0.02em' }}>$1,247</div>
            <div style={{ fontSize: 10, color: W_MUTED }}>Daily avg · $69.30</div>
          </Stack>

          {/* donut */}
          <div style={{ alignSelf: 'center', position: 'relative', marginTop: 6 }}>
            <svg width="160" height="160" viewBox="0 0 42 42">
              <circle cx="21" cy="21" r="15.91" fill="transparent" stroke={W_LINE_2} strokeWidth="6" />
              <circle cx="21" cy="21" r="15.91" fill="transparent" stroke={W_INK} strokeWidth="6" strokeDasharray="38 62" strokeDashoffset="25" />
              <circle cx="21" cy="21" r="15.91" fill="transparent" stroke="#6b6a63" strokeWidth="6" strokeDasharray="22 78" strokeDashoffset="-13" />
              <circle cx="21" cy="21" r="15.91" fill="transparent" stroke="#9a988f" strokeWidth="6" strokeDasharray="18 82" strokeDashoffset="-35" />
            </svg>
            <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 10, color: W_MUTED, fontFamily: 'var(--mono)' }}>
              by category
            </div>
          </div>

          <Stack gap={8} style={{ marginTop: 4 }}>
            <SectionHead mute>Top categories</SectionHead>
            {[
              ['Food', '$478', '38%'],
              ['Transport', '$274', '22%'],
              ['Shopping', '$224', '18%'],
              ['Bills', '$170', '14%'],
            ].map(([n, a, p], i) => (
              <Row key={i} style={{ justifyContent: 'space-between', padding: '6px 0', borderTop: i ? `1px solid ${W_LINE_2}` : 'none' }}>
                <Row gap={8}>
                  <div style={{ width: 8, height: 8, borderRadius: 2, background: ['#1a1a1a','#6b6a63','#9a988f','#cfcdc2'][i] }} />
                  <span style={{ fontSize: 12 }}>{n}</span>
                </Row>
                <Row gap={8}>
                  <span style={{ fontSize: 11, color: W_MUTED }}>{p}</span>
                  <span style={{ fontFamily: 'var(--serif)', fontSize: 13 }}>{a}</span>
                </Row>
              </Row>
            ))}
          </Stack>
        </>
      )}
    </Pad>
  );
}

// ─────────────────────────────────────────────────────────────
// 13 — MORE / SETTINGS
// ─────────────────────────────────────────────────────────────
function ScreenMore() {
  const row = (label, detail) => (
    <Row key={label} style={{ padding: '12px 12px', borderTop: `1px solid ${W_LINE_2}`, justifyContent: 'space-between' }}>
      <Row gap={10}>
        <Glyph size={22} />
        <span style={{ fontSize: 13 }}>{label}</span>
      </Row>
      <Row gap={6}>
        {detail && <span style={{ fontSize: 11, color: W_MUTED }}>{detail}</span>}
        <span style={{ fontSize: 11, color: W_MUTED }}>›</span>
      </Row>
    </Row>
  );
  const group = (title, rows) => (
    <Stack gap={0} style={{ marginTop: 14 }}>
      <SectionHead mute style={{ paddingLeft: 12, marginBottom: 6 }}>{title}</SectionHead>
      <div style={{ border: `1px solid ${W_LINE}`, borderRadius: 12, background: W_FILL_2, overflow: 'hidden' }}>
        {rows.map((r, i) => (
          <Row key={i} style={{ padding: '12px', borderTop: i ? `1px solid ${W_LINE_2}` : 'none', justifyContent: 'space-between' }}>
            <Row gap={10}>
              <Glyph size={22} />
              <span style={{ fontSize: 13 }}>{r[0]}</span>
            </Row>
            <Row gap={6}>
              {r[1] && <span style={{ fontSize: 11, color: W_MUTED }}>{r[1]}</span>}
              <span style={{ fontSize: 11, color: W_MUTED }}>›</span>
            </Row>
          </Row>
        ))}
      </div>
    </Stack>
  );
  return (
    <>
      <Pad p={18} style={{ flex: 1, overflow: 'hidden' }}>
        <Stack gap={4}>
          <Line w={50} />
          <div style={{ fontFamily: 'var(--serif)', fontSize: 22, lineHeight: 1, letterSpacing: '-0.01em' }}>More</div>
        </Stack>

        {group('Features', [
          ['Reports', null],
          ['Category List', null],
        ])}
        {group('Settings', [
          ['Currency', 'USD'],
          ['Monthly budget', 'Off'],
          ['PIN authentication', 'Off'],
          ['Language', 'English'],
          ['Theme', 'System'],
        ])}
        {group('Data', [
          ['Export data', 'CSV · JSON'],
          ['Clear data', null],
        ])}

        <div style={{ marginTop: 14, textAlign: 'center', fontSize: 10, color: W_MUTED, fontFamily: 'var(--mono)' }}>v0.1.0 · MVP</div>
      </Pad>
      <BottomNav active="more" />
    </>
  );
}

// ─────────────────────────────────────────────────────────────
// 11 — CATEGORY LIST
// ─────────────────────────────────────────────────────────────
function ScreenCategoryList() {
  const defaultCats = ['Food', 'Transport', 'Shopping', 'Bills', 'Health', 'Entertainment'];
  const customCats = ['Coffee runs', 'Pet care'];
  const row = (name, locked, draggable) => (
    <Row key={name} style={{ padding: '12px', borderTop: `1px solid ${W_LINE_2}`, justifyContent: 'space-between' }}>
      <Row gap={10}>
        <Glyph size={22} />
        <span style={{ fontSize: 13 }}>{name}</span>
        {locked && <span style={{ fontSize: 9, color: W_MUTED, fontFamily: 'var(--mono)', padding: '1px 6px', border: `1px solid ${W_LINE}`, borderRadius: 99 }}>locked</span>}
      </Row>
      {draggable ? (
        <span style={{ fontSize: 14, color: W_MUTED }}>≡</span>
      ) : (
        <span style={{ fontSize: 11, color: W_MUTED }}>›</span>
      )}
    </Row>
  );

  return (
    <Pad p={18} style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 14 }}>
      <Row style={{ justifyContent: 'space-between' }}>
        <span style={{ fontSize: 13 }}>← More</span>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 14 }}>Categories</div>
        <span style={{ fontSize: 18 }}>+</span>
      </Row>

      <Stack gap={6}>
        <SectionHead mute style={{ paddingLeft: 4 }}>Default · always first</SectionHead>
        <div style={{ border: `1px solid ${W_LINE}`, borderRadius: 12, background: W_FILL_2, overflow: 'hidden' }}>
          {defaultCats.map((c, i) => (
            <div key={c} style={{ borderTop: i ? `1px solid ${W_LINE_2}` : 'none' }}>{row(c, true, false)}</div>
          ))}
        </div>
      </Stack>

      <Stack gap={6}>
        <SectionHead mute style={{ paddingLeft: 4 }}>Custom · drag to reorder</SectionHead>
        <div style={{ border: `1px solid ${W_LINE}`, borderRadius: 12, background: W_FILL_2, overflow: 'hidden' }}>
          {customCats.map((c, i) => (
            <div key={c} style={{ borderTop: i ? `1px solid ${W_LINE_2}` : 'none' }}>{row(c, false, true)}</div>
          ))}
        </div>
      </Stack>

      <div style={{ marginTop: 'auto', textAlign: 'center', fontSize: 10, color: W_MUTED, padding: 8 }}>
        Order here mirrors chip order in Add Expense
      </div>
    </Pad>
  );
}

// ─────────────────────────────────────────────────────────────
// 01 — SPLASH
// ─────────────────────────────────────────────────────────────
function ScreenSplash() {
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 18, background: W_PAPER }}>
      <div style={{ width: 84, height: 84, border: `1.5px solid ${W_INK}`, borderRadius: 22, background: W_FILL, position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', inset: 0, background: `linear-gradient(135deg, transparent calc(50% - 0.5px), ${W_LINE} 50%, transparent calc(50% + 0.5px))` }} />
      </div>
      <div style={{ fontFamily: 'var(--serif)', fontSize: 24, letterSpacing: '-0.01em' }}>Pro Expense</div>
      <div style={{ position: 'absolute', bottom: 60, left: 0, right: 0, display: 'flex', justifyContent: 'center' }}>
        <div style={{ width: 24, height: 3, background: W_LINE, borderRadius: 99 }} />
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// 02 — ONBOARDING (5 slides)
// ─────────────────────────────────────────────────────────────
function ScreenOnboarding({ slide = 1 }) {
  const slides = [
    { title: 'Welcome', body: 'Your personal finance notebook.', glyph: '◐' },
    { title: 'Quick Log', body: 'Log expenses in seconds — amount, category, done.', glyph: '✚' },
    { title: 'Shared Costs', body: 'Split bills instantly — enter total, number of people, done.', glyph: '◈' },
    { title: 'Event Budget', body: 'Plan and track any event budget — trips, weddings, parties.', glyph: '◇' },
    { title: 'Journal', body: 'Review your spending like a diary, day by day.', glyph: '▤' },
  ];
  const isLast = slide === 5;
  const s = slides[slide - 1];
  return (
    <Pad p={18} style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <Row style={{ justifyContent: 'flex-end' }}>
        <span style={{ fontSize: 12, color: W_MUTED }}>Skip ›</span>
      </Row>

      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 18, textAlign: 'center', padding: '0 20px' }}>
        <div style={{ width: 140, height: 140, border: `1.2px dashed ${W_LINE}`, borderRadius: 20, background: W_FILL_2, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 40, color: W_MUTED, fontFamily: 'var(--serif)' }}>
          {s.glyph}
        </div>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 28, letterSpacing: '-0.02em', lineHeight: 1 }}>{s.title}</div>
        <div style={{ fontSize: 13, color: '#3a3a32', maxWidth: 240 }}>{s.body}</div>
      </div>

      {/* pagination dots */}
      <Row gap={6} style={{ justifyContent: 'center', marginBottom: 16 }}>
        {[1,2,3,4,5].map((i) => (
          <div key={i} style={{ width: i === slide ? 18 : 6, height: 6, background: i === slide ? W_INK : W_LINE, borderRadius: 99, transition: 'width 0.2s' }} />
        ))}
      </Row>

      {isLast ? (
        <div style={{ padding: '14px 0', textAlign: 'center', background: W_INK, color: W_PAPER, borderRadius: 12, fontSize: 13, fontWeight: 600 }}>Get started</div>
      ) : (
        <Row style={{ justifyContent: 'space-between', padding: '8px 4px' }}>
          <span style={{ fontSize: 12, color: W_MUTED }}>‹ Back</span>
          <span style={{ fontSize: 12, color: W_INK, fontWeight: 600 }}>Next ›</span>
        </Row>
      )}
    </Pad>
  );
}

// ─────────────────────────────────────────────────────────────
// 15 — PIN ENTRY
// ─────────────────────────────────────────────────────────────
function ScreenPinEntry({ filled = 3 }) {
  return (
    <Pad p={18} style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <Stack gap={6} style={{ alignItems: 'center', marginTop: 30 }}>
        <div style={{ width: 48, height: 48, border: `1.2px solid ${W_LINE}`, borderRadius: 12, background: W_FILL_2 }} />
        <div style={{ fontFamily: 'var(--serif)', fontSize: 20, marginTop: 12 }}>Enter PIN</div>
        <div style={{ fontSize: 11, color: W_MUTED }}>6 digits to unlock</div>
      </Stack>

      {/* dots */}
      <Row gap={10} style={{ justifyContent: 'center', marginTop: 24 }}>
        {[1,2,3,4,5,6].map((i) => (
          <div key={i} style={{ width: 12, height: 12, borderRadius: 99, background: i <= filled ? W_INK : 'transparent', border: `1.2px solid ${i <= filled ? W_INK : W_LINE}` }} />
        ))}
      </Row>

      {/* biometric icon — just the fingerprint, no label */}
      <div style={{ marginTop: 22, display: 'flex', justifyContent: 'center' }}>
        <div style={{ width: 48, height: 48, borderRadius: 99, border: `1.2px solid ${W_INK}`, background: W_FILL_2, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <svg width="24" height="26" viewBox="0 0 22 24" fill="none">
            <path d="M11 2C7 2 4 4.5 3 7" stroke={W_INK} strokeWidth="1.2" strokeLinecap="round" />
            <path d="M19 7C18 4.5 15 2 11 2" stroke={W_INK} strokeWidth="1.2" strokeLinecap="round" />
            <path d="M5 10C5 7 8 5 11 5C14 5 17 7 17 10C17 14 16 17 14 20" stroke={W_INK} strokeWidth="1.2" strokeLinecap="round" />
            <path d="M8 11C8 9 9.5 8 11 8C12.5 8 14 9 14 11C14 14 13.5 17 11.5 21" stroke={W_INK} strokeWidth="1.2" strokeLinecap="round" />
            <path d="M11 11C11 13 10.5 16 9 22" stroke={W_INK} strokeWidth="1.2" strokeLinecap="round" />
            <path d="M3.5 14C4 17 5 19 6.5 22" stroke={W_INK} strokeWidth="1.2" strokeLinecap="round" />
          </svg>
        </div>
      </div>

      {/* keypad */}
      <div style={{ marginTop: 'auto', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 8 }}>
        {['1','2','3','4','5','6','7','8','9','','0','⌫'].map((k, i) => k === '' ? <div key={i} /> : (
          <div key={i} style={{ padding: '14px 0', textAlign: 'center', border: `1px solid ${W_LINE_2}`, borderRadius: 99, background: W_FILL_2, fontSize: 18, fontFamily: 'var(--serif)' }}>{k}</div>
        ))}
      </div>

      <div style={{ marginTop: 14, textAlign: 'center', fontSize: 11, color: W_MUTED, textDecoration: 'underline' }}>Forgot PIN?</div>
    </Pad>
  );
}

// ─────────────────────────────────────────────────────────────
// 14 — PIN SETUP
// ─────────────────────────────────────────────────────────────
function ScreenPinSetup() {
  return (
    <Pad p={18} style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 14 }}>
      <Row style={{ justifyContent: 'space-between' }}>
        <span style={{ fontSize: 13 }}>← Settings</span>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 14 }}>PIN authentication</div>
        <span style={{ width: 30 }} />
      </Row>

      <Stack gap={6} style={{ marginTop: 8 }}>
        <SectionHead mute>Enable</SectionHead>
        <Row style={{ padding: '12px', border: `1px solid ${W_LINE}`, borderRadius: 12, background: W_FILL_2, justifyContent: 'space-between' }}>
          <span style={{ fontSize: 13 }}>PIN authentication</span>
          <div style={{ width: 36, height: 22, borderRadius: 99, background: W_INK, position: 'relative' }}>
            <div style={{ position: 'absolute', right: 2, top: 2, width: 18, height: 18, borderRadius: 99, background: W_PAPER }} />
          </div>
        </Row>
        <Row style={{ padding: '12px', border: `1px solid ${W_LINE}`, borderRadius: 12, background: W_FILL_2, justifyContent: 'space-between' }}>
          <Stack gap={2}>
            <span style={{ fontSize: 13 }}>Use Face ID</span>
            <span style={{ fontSize: 10, color: W_MUTED }}>Requires PIN to be enabled</span>
          </Stack>
          <div style={{ width: 36, height: 22, borderRadius: 99, background: W_LINE_2, position: 'relative' }}>
            <div style={{ position: 'absolute', left: 2, top: 2, width: 18, height: 18, borderRadius: 99, background: W_PAPER, border: `1px solid ${W_LINE}` }} />
          </div>
        </Row>
      </Stack>

      <Stack gap={6}>
        <SectionHead mute>New PIN · 6 digits</SectionHead>
        <Row style={{ padding: 12, border: `1px solid ${W_LINE}`, borderRadius: 12, justifyContent: 'space-between', background: W_FILL_2 }}>
          <Row gap={10}>
            {[1,2,3,4,5,6].map((i) => (
              <div key={i} style={{ width: 12, height: 12, borderRadius: 99, background: i <= 6 ? W_INK : 'transparent', border: `1.2px solid ${i <= 6 ? W_INK : W_LINE}` }} />
            ))}
          </Row>
          <EyeIcon />
        </Row>
      </Stack>

      <Stack gap={6}>
        <SectionHead mute>Confirm PIN</SectionHead>
        <Row style={{ padding: 12, border: `1px solid ${W_LINE}`, borderRadius: 12, justifyContent: 'space-between' }}>
          <Row gap={10}>
            {[1,2,3,4,5,6].map((i) => (
              <div key={i} style={{ width: 12, height: 12, borderRadius: 99, background: i <= 3 ? W_INK : 'transparent', border: `1.2px solid ${i <= 3 ? W_INK : W_LINE}` }} />
            ))}
          </Row>
          <EyeIcon off />
        </Row>
        <div style={{ fontSize: 10, color: W_MUTED, padding: '0 4px' }}>"PINs do not match" inline error if mismatch — confirm field clears, original PIN stays</div>
      </Stack>

      <Stack gap={6}>
        <SectionHead mute>Recovery · required</SectionHead>
        <Row style={{ padding: '12px', border: `1px solid ${W_LINE}`, borderRadius: 12, justifyContent: 'space-between' }}>
          <span style={{ fontSize: 12 }}>Security question</span>
          <span style={{ fontSize: 11, color: W_MUTED }}>Pick one ›</span>
        </Row>
        <Row style={{ padding: '10px 12px', border: `1px dashed ${W_LINE}`, borderRadius: 12 }}>
          <span style={{ fontSize: 11, color: W_MUTED }}>Answer…</span>
        </Row>
      </Stack>

      <div style={{ marginTop: 'auto', padding: '14px 0', textAlign: 'center', background: W_INK, color: W_PAPER, borderRadius: 12, fontSize: 13, fontWeight: 600 }}>Save PIN</div>
    </Pad>
  );
}

// ─────────────────────────────────────────────────────────────
// 07 — EVENT BUDGET (list)
// ─────────────────────────────────────────────────────────────
function ScreenEventBudget({ empty = false }) {
  const card = (name, range, left, pct, state) => (
    <Stack key={name} gap={8} style={{ padding: 14, border: `1.2px solid ${state === 'over' ? W_INK : W_LINE}`, borderRadius: 14, background: W_FILL_2 }}>
      <Row style={{ justifyContent: 'space-between' }}>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 16, letterSpacing: '-0.01em' }}>{name}</div>
        {state === 'over' && <span style={{ fontSize: 9, fontFamily: 'var(--mono)', padding: '2px 6px', border: `1px solid ${W_INK}`, borderRadius: 99 }}>OVER</span>}
        {state === 'closed' && <span style={{ fontSize: 9, fontFamily: 'var(--mono)', padding: '2px 6px', color: W_MUTED, border: `1px solid ${W_LINE}`, borderRadius: 99 }}>CLOSED</span>}
      </Row>
      <div style={{ fontSize: 10, color: W_MUTED }}>{range}</div>
      <Row style={{ justifyContent: 'space-between', alignItems: 'baseline' }}>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 22, lineHeight: 1 }}>{left}</div>
        <span style={{ fontSize: 10, color: W_MUTED }}>remaining</span>
      </Row>
      <div style={{ height: 6, background: '#fff', border: `1px solid ${W_LINE}`, borderRadius: 99, overflow: 'hidden' }}>
        <div style={{ height: '100%', width: `${Math.min(pct, 100)}%`, background: pct > 100 ? '#1a1a1a' : pct > 80 ? '#3a3a32' : W_MUTED, borderRadius: 99 }} />
      </div>
    </Stack>
  );
  return (
    <>
      <Pad p={18} style={{ flex: 1, overflow: 'hidden', position: 'relative' }}>
        <Row style={{ justifyContent: 'space-between' }}>
          <Stack gap={4}>
            <Line w={50} />
            <div style={{ fontFamily: 'var(--serif)', fontSize: 22, lineHeight: 1, letterSpacing: '-0.01em' }}>Events</div>
          </Stack>
          <Row gap={4} style={{ padding: '6px 12px', border: `1.2px solid ${W_INK}`, borderRadius: 99, background: W_PAPER, alignItems: 'center' }}>
            <span style={{ fontSize: 14, lineHeight: 1, fontWeight: 300 }}>+</span>
            <span style={{ fontSize: 11, fontWeight: 500 }}>New event</span>
          </Row>
        </Row>

        {empty ? (
          <Stack gap={10} style={{ alignItems: 'center', paddingTop: 80 }}>
            <div style={{ width: 64, height: 72, border: `1.5px dashed ${W_LINE}`, borderRadius: 8 }} />
            <div style={{ fontFamily: 'var(--serif)', fontStyle: 'italic', fontSize: 14, color: W_INK, textAlign: 'center', maxWidth: 220 }}>
              No active events. Create one to start tracking.
            </div>
            <Row gap={6} style={{ marginTop: 6, padding: '8px 16px', border: `1.2px solid ${W_INK}`, borderRadius: 99, fontSize: 11, fontWeight: 500, background: W_FILL_2 }}>
              <span style={{ fontSize: 14, lineHeight: 1, fontWeight: 300 }}>+</span>
              <span>Create event</span>
            </Row>
          </Stack>
        ) : (
          <Stack gap={12} style={{ marginTop: 16 }}>
            {card('Bali Trip', 'May 12 – May 26', '$1,240 / $2,000', 38, 'active')}
            {card("John's Wedding", 'Jun 04 – Jun 06', '$340 / $800', 58, 'active')}
            {card('Birthday party', 'Apr 28', '−$45 over', 112, 'over')}
          </Stack>
        )}
        <FAB />
      </Pad>
      <BottomNav active="budget" />
    </>
  );
}

// ─────────────────────────────────────────────────────────────
// 08 — EVENT DETAIL
// ─────────────────────────────────────────────────────────────
function ScreenEventDetail() {
  return (
    <Pad p={18} style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 14 }}>
      <Row style={{ justifyContent: 'space-between' }}>
        <span style={{ fontSize: 13 }}>← Events</span>
        <span style={{ fontSize: 18, color: W_MUTED }}>⋯</span>
      </Row>

      <Stack gap={4}>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 28, letterSpacing: '-0.02em', lineHeight: 1 }}>Bali Trip</div>
        <div style={{ fontSize: 11, color: W_MUTED }}>May 12 – May 26 · active</div>
      </Stack>

      {/* budget summary */}
      <div style={{ padding: 14, border: `1.2px solid ${W_INK}`, borderRadius: 14, background: W_FILL_2 }}>
        <Row style={{ justifyContent: 'space-between' }}>
          <Stack gap={2}>
            <div style={{ fontSize: 9, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: W_MUTED }}>Remaining</div>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 28, lineHeight: 1, letterSpacing: '-0.02em' }}>$1,240</div>
          </Stack>
          <Stack gap={2} style={{ alignItems: 'flex-end' }}>
            <div style={{ fontSize: 10, color: W_MUTED }}>$760 spent</div>
            <div style={{ fontSize: 10, color: W_MUTED }}>$2,000 budget</div>
          </Stack>
        </Row>
        <div style={{ marginTop: 12, height: 8, background: '#fff', border: `1px solid ${W_LINE}`, borderRadius: 99, overflow: 'hidden' }}>
          <div style={{ height: '100%', width: '38%', background: W_INK, borderRadius: 99 }} />
        </div>
      </div>

      <Row style={{ justifyContent: 'space-between' }}>
        <SectionHead mute>Linked expenses</SectionHead>
        <span style={{ fontSize: 10, color: W_ACCENT }}>+ add tagged</span>
      </Row>

      <Stack gap={0}>
        {[
          ['Hotel night', 'Bills', '$180'],
          ['Lunch · beach', 'Food', '$22'],
          ['Scooter rental', 'Transport', '$15'],
          ['Souvenirs', 'Shopping', '$48'],
          ['Dinner · seafood', 'Food', '$64'],
        ].map(([n, c, a], i) => (
          <Row key={i} style={{ padding: '10px 0', borderTop: i ? `1px solid ${W_LINE_2}` : 'none', justifyContent: 'space-between' }}>
            <Row gap={10}>
              <Glyph size={24} />
              <Stack gap={2}>
                <div style={{ fontSize: 12 }}>{n}</div>
                <div style={{ fontSize: 9, color: W_MUTED }}>{c} · @Bali Trip</div>
              </Stack>
            </Row>
            <div style={{ fontFamily: 'var(--serif)', fontSize: 13 }}>{a}</div>
          </Row>
        ))}
      </Stack>
    </Pad>
  );
}

// ─────────────────────────────────────────────────────────────
// 09 — DEBT TRACKER
// ─────────────────────────────────────────────────────────────
function ScreenDebtTracker({ empty = false }) {
  const row = (name, amount, date, settled) => (
    <Row key={name} style={{ padding: '12px', borderTop: `1px solid ${W_LINE_2}`, justifyContent: 'space-between', opacity: settled ? 0.5 : 1 }}>
      <Row gap={10}>
        <div style={{ width: 28, height: 28, border: `1.2px solid ${settled ? W_LINE : W_INK}`, borderRadius: 99, background: settled ? 'transparent' : W_FILL, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 11, fontWeight: 500 }}>
          {name[0]}
        </div>
        <Stack gap={2}>
          <Row gap={6}>
            <span style={{ fontSize: 12, fontWeight: 500 }}>{name}</span>
            {settled && <span style={{ fontSize: 9, fontFamily: 'var(--mono)', padding: '1px 5px', border: `1px solid ${W_LINE}`, borderRadius: 99, color: W_MUTED }}>settled</span>}
          </Row>
          <div style={{ fontSize: 9, color: W_MUTED }}>{date}</div>
        </Stack>
      </Row>
      <div style={{ fontFamily: 'var(--serif)', fontSize: 14 }}>{amount}</div>
    </Row>
  );
  return (
    <Pad p={18} style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 14, position: 'relative' }}>
      <Row style={{ justifyContent: 'space-between' }}>
        <span style={{ fontSize: 13 }}>← More</span>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 14 }}>Debt</div>
        <span style={{ fontSize: 16 }}>+</span>
      </Row>

      {/* toggle */}
      <Row style={{ padding: 4, background: W_FILL_2, border: `1px solid ${W_LINE}`, borderRadius: 99 }}>
        <div style={{ flex: 1, padding: '8px 0', textAlign: 'center', background: W_PAPER, border: `1px solid ${W_INK}`, borderRadius: 99, fontSize: 12, fontWeight: 500 }}>I Lent</div>
        <div style={{ flex: 1, padding: '8px 0', textAlign: 'center', fontSize: 12, color: W_MUTED }}>I Owe</div>
      </Row>

      {empty ? (
        <Stack gap={10} style={{ alignItems: 'center', paddingTop: 80 }}>
          <div style={{ width: 80, height: 60, border: `1.5px dashed ${W_LINE}`, borderRadius: 8, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 24, color: W_MUTED }}>◐◑</div>
          <div style={{ fontFamily: 'var(--serif)', fontStyle: 'italic', fontSize: 14, color: W_INK, textAlign: 'center', maxWidth: 220 }}>
            No records yet. Track who owes who here.
          </div>
          <div style={{ marginTop: 6, padding: '8px 16px', border: `1.2px solid ${W_INK}`, borderRadius: 99, fontSize: 11 }}>+ Add record</div>
        </Stack>
      ) : (
        <>
          {/* summary */}
          <Row style={{ padding: '12px 14px', border: `1.2px solid ${W_INK}`, borderRadius: 12, background: W_FILL_2, justifyContent: 'space-between' }}>
            <Stack gap={2}>
              <div style={{ fontSize: 9, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: W_MUTED }}>Lent out</div>
              <div style={{ fontFamily: 'var(--serif)', fontSize: 22, lineHeight: 1 }}>$135</div>
            </Stack>
            <div style={{ fontSize: 10, color: W_MUTED }}>3 active</div>
          </Row>

          <Stack gap={0}>
            <SectionHead mute style={{ padding: '4px 12px' }}>Active</SectionHead>
            <div style={{ border: `1px solid ${W_LINE}`, borderRadius: 12, background: W_FILL_2, overflow: 'hidden' }}>
              {row('John', '$50', 'May 12 · "dinner at Nobu"', false)}
              {row('Maya', '$25', 'May 08 · due May 30', false)}
              {row('Sarah', '$60', 'Apr 28', false)}
            </div>
          </Stack>

          <Stack gap={0}>
            <SectionHead mute style={{ padding: '4px 12px' }}>Settled</SectionHead>
            <div style={{ border: `1px solid ${W_LINE_2}`, borderRadius: 12, background: 'transparent', overflow: 'hidden' }}>
              {row('Aiko', '$20', 'Apr 14', true)}
            </div>
          </Stack>
        </>
      )}
    </Pad>
  );
}

// ─────────────────────────────────────────────────────────────
// 10 — SHARED COSTS
// ─────────────────────────────────────────────────────────────
function ScreenSharedCosts({ empty = false }) {
  return (
    <Pad p={18} style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 12 }}>
      <Row style={{ justifyContent: 'space-between' }}>
        <span style={{ fontSize: 13 }}>← More</span>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 14 }}>Split a bill</div>
        <span style={{ width: 30 }} />
      </Row>

      {/* total amount */}
      <Stack gap={6} style={{ alignItems: 'center', marginTop: 4 }}>
        <div style={{ fontSize: 10, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: W_MUTED }}>Total</div>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 44, lineHeight: 1, letterSpacing: '-0.02em' }}>$120</div>
      </Stack>

      {/* people stepper */}
      <Stack gap={6}>
        <SectionHead mute>People</SectionHead>
        <Row style={{ padding: '10px 14px', border: `1px solid ${W_LINE}`, borderRadius: 12, justifyContent: 'space-between' }}>
          <div style={{ width: 28, height: 28, border: `1px solid ${W_INK}`, borderRadius: 99, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 14 }}>−</div>
          <span style={{ fontFamily: 'var(--serif)', fontSize: 20 }}>4</span>
          <div style={{ width: 28, height: 28, border: `1px solid ${W_INK}`, borderRadius: 99, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 14 }}>+</div>
        </Row>
      </Stack>

      {/* split mode */}
      <Row style={{ padding: 4, background: W_FILL_2, border: `1px solid ${W_LINE}`, borderRadius: 99 }}>
        <div style={{ flex: 1, padding: '7px 0', textAlign: 'center', background: W_PAPER, border: `1px solid ${W_INK}`, borderRadius: 99, fontSize: 11, fontWeight: 500 }}>Equal</div>
        <div style={{ flex: 1, padding: '7px 0', textAlign: 'center', fontSize: 11, color: W_MUTED }}>Custom</div>
      </Row>

      {/* per-person */}
      <div style={{ padding: '14px', background: W_FILL_2, border: `1.2px solid ${W_INK}`, borderRadius: 14 }}>
        <div style={{ fontSize: 9, fontFamily: 'var(--mono)', textTransform: 'uppercase', letterSpacing: '0.1em', color: W_MUTED }}>Per person</div>
        <div style={{ fontFamily: 'var(--serif)', fontSize: 32, lineHeight: 1, letterSpacing: '-0.02em', marginTop: 4 }}>$30</div>
        <Stack gap={4} style={{ marginTop: 10 }}>
          {['Person 1', 'Person 2', 'Person 3', 'Person 4'].map((p, i) => (
            <Row key={i} style={{ justifyContent: 'space-between' }}>
              <span style={{ fontSize: 11, color: W_INK }}>{p}</span>
              <span style={{ fontSize: 11, fontFamily: 'var(--serif)' }}>$30</span>
            </Row>
          ))}
        </Stack>
      </div>

      {/* note */}
      <Row style={{ padding: '8px 12px', border: `1px solid ${W_LINE}`, borderRadius: 10 }}>
        <span style={{ fontSize: 11, color: W_MUTED }}>Note · "Dinner at Nobu"</span>
      </Row>

      <div style={{ marginTop: 'auto', padding: '14px 0', textAlign: 'center', background: W_INK, color: W_PAPER, borderRadius: 12, fontSize: 13, fontWeight: 600 }}>Save split</div>
    </Pad>
  );
}

// Eye icon — visibility toggle for password/PIN inputs
function EyeIcon({ off = false, size = 18 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
      <path d="M2 12C4 7 8 4 12 4C16 4 20 7 22 12C20 17 16 20 12 20C8 20 4 17 2 12Z" stroke={off ? W_MUTED : W_INK} strokeWidth="1.4" />
      <circle cx="12" cy="12" r="3" stroke={off ? W_MUTED : W_INK} strokeWidth="1.4" />
      {off && <line x1="4" y1="4" x2="20" y2="20" stroke={W_MUTED} strokeWidth="1.4" />}
    </svg>
  );
}

Object.assign(window, {
  ScreenHome, ScreenAddAmount, ScreenAddDetails, ScreenJournal,
  ScreenJournalDetail, ScreenReports, ScreenMore, ScreenCategoryList,
  ScreenSplash, ScreenOnboarding, ScreenPinEntry, ScreenPinSetup,
  ScreenEventBudget, ScreenEventDetail, ScreenDebtTracker, ScreenSharedCosts,
});
