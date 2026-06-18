// Onboarding illustrations — composed scene SVGs (blue / white / dark-line).
// Each is a self-contained <svg> at ~240×200 viewBox. Style: flat, rounded,
// 2px dark-gray line work, blue fills, white surfaces, soft blue-100 backdrops.

const ILLO = {
  ink: '#212121',
  line: '#212121',
  blue: '#039be5',
  blueDeep: '#0288d1',
  blue300: '#4fc3f7',
  blue100: '#b3e5fc',
  blue50: '#e1f5fe',
  white: '#ffffff',
  gray: '#9e9e9e',
  grayLine: '#bdbdbd',
};

// 01 — WELCOME: an open notebook with a coin/pen, friendly hello
function IlloWelcome() {
  return (
    <svg width="240" height="200" viewBox="0 0 240 200" fill="none">
      {/* backdrop blob */}
      <ellipse cx="120" cy="108" rx="92" ry="78" fill={ILLO.blue50} />
      <circle cx="186" cy="44" r="9" fill={ILLO.blue100} />
      <circle cx="44" cy="150" r="6" fill={ILLO.blue100} />
      {/* notebook */}
      <g>
        <rect x="62" y="58" width="116" height="92" rx="10" fill={ILLO.white} stroke={ILLO.line} strokeWidth="3"/>
        {/* spine */}
        <line x1="120" y1="58" x2="120" y2="150" stroke={ILLO.line} strokeWidth="3"/>
        {/* left lines */}
        <line x1="74" y1="80" x2="108" y2="80" stroke={ILLO.grayLine} strokeWidth="3" strokeLinecap="round"/>
        <line x1="74" y1="94" x2="108" y2="94" stroke={ILLO.grayLine} strokeWidth="3" strokeLinecap="round"/>
        <line x1="74" y1="108" x2="100" y2="108" stroke={ILLO.grayLine} strokeWidth="3" strokeLinecap="round"/>
        {/* right: big hello check + dollar */}
        <circle cx="150" cy="92" r="16" fill={ILLO.blue}/>
        <path d="M143 92 l5 5 l9 -10" stroke={ILLO.white} strokeWidth="3.4" strokeLinecap="round" strokeLinejoin="round"/>
        <line x1="132" y1="118" x2="166" y2="118" stroke={ILLO.grayLine} strokeWidth="3" strokeLinecap="round"/>
        <line x1="132" y1="130" x2="156" y2="130" stroke={ILLO.grayLine} strokeWidth="3" strokeLinecap="round"/>
      </g>
      {/* coin */}
      <g>
        <circle cx="58" cy="138" r="18" fill={ILLO.blue300} stroke={ILLO.line} strokeWidth="3"/>
        <path d="M58 129 v18 M53 134 h8 a3 3 0 0 1 0 6 h-6 a3 3 0 0 0 0 6 h8" stroke={ILLO.white} strokeWidth="2.6" strokeLinecap="round" strokeLinejoin="round" fill="none"/>
      </g>
      {/* sparkle */}
      <path d="M180 120 v12 M174 126 h12" stroke={ILLO.blue} strokeWidth="3" strokeLinecap="round"/>
    </svg>
  );
}

// 02 — QUICK LOG: a phone with a big + and a fast amount entry, speed lines
function IlloQuickLog() {
  return (
    <svg width="240" height="200" viewBox="0 0 240 200" fill="none">
      <ellipse cx="120" cy="108" rx="92" ry="78" fill={ILLO.blue50} />
      {/* speed lines */}
      <line x1="30" y1="78" x2="56" y2="78" stroke={ILLO.blue100} strokeWidth="5" strokeLinecap="round"/>
      <line x1="24" y1="100" x2="52" y2="100" stroke={ILLO.blue100} strokeWidth="5" strokeLinecap="round"/>
      <line x1="32" y1="122" x2="56" y2="122" stroke={ILLO.blue100} strokeWidth="5" strokeLinecap="round"/>
      {/* phone */}
      <rect x="84" y="46" width="86" height="124" rx="16" fill={ILLO.white} stroke={ILLO.line} strokeWidth="3"/>
      <rect x="84" y="46" width="86" height="40" rx="16" fill={ILLO.blue}/>
      <rect x="84" y="70" width="86" height="16" fill={ILLO.blue}/>
      {/* amount on screen */}
      <text x="127" y="74" textAnchor="middle" fontFamily="Geist, sans-serif" fontSize="20" fontWeight="700" fill={ILLO.white}>$12</text>
      {/* category rows */}
      <rect x="96" y="98" width="62" height="10" rx="5" fill={ILLO.blue100}/>
      <rect x="96" y="114" width="44" height="10" rx="5" fill={ILLO.gray} opacity="0.4"/>
      {/* big plus button */}
      <circle cx="150" cy="150" r="22" fill={ILLO.blue} stroke={ILLO.white} strokeWidth="3"/>
      <path d="M150 140 v20 M140 150 h20" stroke={ILLO.white} strokeWidth="3.6" strokeLinecap="round"/>
      {/* lightning = speed */}
      <path d="M64 150 l-10 16 h8 l-4 14 14 -20 h-9 z" fill={ILLO.blue300} stroke={ILLO.line} strokeWidth="2.4" strokeLinejoin="round"/>
    </svg>
  );
}

// 03 — SHARED COSTS: a bill split between people, arrows fanning out
function IlloSharedCosts() {
  return (
    <svg width="240" height="200" viewBox="0 0 240 200" fill="none">
      <ellipse cx="120" cy="108" rx="92" ry="78" fill={ILLO.blue50} />
      {/* receipt */}
      <path d="M96 44 h48 a4 4 0 0 1 4 4 v62 l-7 -5 -7 5 -7 -5 -7 5 -7 -5 -7 5 -7 -5 v-62 a4 4 0 0 1 4 -4 z" fill={ILLO.white} stroke={ILLO.line} strokeWidth="3" strokeLinejoin="round"/>
      <line x1="104" y1="60" x2="140" y2="60" stroke={ILLO.grayLine} strokeWidth="3" strokeLinecap="round"/>
      <line x1="104" y1="72" x2="132" y2="72" stroke={ILLO.grayLine} strokeWidth="3" strokeLinecap="round"/>
      <text x="120" y="98" textAnchor="middle" fontFamily="Geist, sans-serif" fontSize="16" fontWeight="700" fill={ILLO.blue}>$120</text>
      {/* three people */}
      {[
        { x: 60,  c: ILLO.blue },
        { x: 120, c: ILLO.blue300 },
        { x: 180, c: ILLO.blueDeep },
      ].map((p, i) => (
        <g key={i}>
          <line x1="120" y1="118" x2={p.x} y2="150" stroke={ILLO.blue} strokeWidth="2.6" strokeDasharray="4 4" strokeLinecap="round"/>
          <circle cx={p.x} cy="160" r="12" fill={p.c} stroke={ILLO.line} strokeWidth="3"/>
          <circle cx={p.x} cy="156" r="4" fill={ILLO.white}/>
          <path d={`M${p.x-7} 168 a7 7 0 0 1 14 0`} fill={ILLO.white}/>
          <rect x={p.x-16} y="176" width="32" height="13" rx="6" fill={ILLO.white} stroke={ILLO.line} strokeWidth="2"/>
          <text x={p.x} y="185" textAnchor="middle" fontFamily="Geist, sans-serif" fontSize="8" fontWeight="700" fill={ILLO.ink}>$40</text>
        </g>
      ))}
    </svg>
  );
}

// 04 — EVENT BUDGET: a calendar / flag with a progress ring
function IlloEventBudget() {
  const R = 30, C = 2 * Math.PI * R, pct = 0.62;
  return (
    <svg width="240" height="200" viewBox="0 0 240 200" fill="none">
      <ellipse cx="120" cy="108" rx="92" ry="78" fill={ILLO.blue50} />
      {/* destination flag pin */}
      <g>
        <line x1="74" y1="60" x2="74" y2="150" stroke={ILLO.line} strokeWidth="3" strokeLinecap="round"/>
        <path d="M74 60 h34 l-8 9 8 9 h-34 z" fill={ILLO.blue} stroke={ILLO.line} strokeWidth="3" strokeLinejoin="round"/>
      </g>
      {/* progress ring */}
      <g transform="translate(158 104)">
        <circle r={R} fill={ILLO.white} stroke={ILLO.grayLine} strokeWidth="9"/>
        <circle r={R} fill="none" stroke={ILLO.blue} strokeWidth="9" strokeLinecap="round"
          strokeDasharray={`${C * pct} ${C}`} transform="rotate(-90)"/>
        <text x="0" y="-2" textAnchor="middle" fontFamily="Geist, sans-serif" fontSize="14" fontWeight="700" fill={ILLO.ink}>62%</text>
        <text x="0" y="12" textAnchor="middle" fontFamily="Geist, sans-serif" fontSize="8" fill={ILLO.gray}>of budget</text>
      </g>
      {/* small calendar */}
      <g>
        <rect x="58" y="158" width="40" height="32" rx="6" fill={ILLO.white} stroke={ILLO.line} strokeWidth="3"/>
        <line x1="58" y1="168" x2="98" y2="168" stroke={ILLO.line} strokeWidth="3"/>
        <line x1="68" y1="154" x2="68" y2="162" stroke={ILLO.line} strokeWidth="3" strokeLinecap="round"/>
        <line x1="88" y1="154" x2="88" y2="162" stroke={ILLO.line} strokeWidth="3" strokeLinecap="round"/>
        <circle cx="78" cy="179" r="4" fill={ILLO.blue}/>
      </g>
      <path d="M150 52 v10 M145 57 h10" stroke={ILLO.blue300} strokeWidth="3" strokeLinecap="round"/>
    </svg>
  );
}

// 05 — JOURNAL: stacked day-cards like a diary, with entry rows
function IlloJournal() {
  return (
    <svg width="240" height="200" viewBox="0 0 240 200" fill="none">
      <ellipse cx="120" cy="108" rx="92" ry="78" fill={ILLO.blue50} />
      {/* back card */}
      <rect x="74" y="50" width="108" height="44" rx="10" fill={ILLO.white} stroke={ILLO.grayLine} strokeWidth="3" opacity="0.7"/>
      {/* middle card */}
      <rect x="66" y="74" width="108" height="50" rx="10" fill={ILLO.white} stroke={ILLO.line} strokeWidth="3"/>
      <circle cx="84" cy="92" r="7" fill={ILLO.blue100}/>
      <line x1="98" y1="88" x2="140" y2="88" stroke={ILLO.grayLine} strokeWidth="3" strokeLinecap="round"/>
      <text x="160" y="93" textAnchor="end" fontFamily="Geist, sans-serif" fontSize="11" fontWeight="700" fill={ILLO.blue}>$12</text>
      <line x1="98" y1="104" x2="124" y2="104" stroke={ILLO.grayLine} strokeWidth="3" strokeLinecap="round"/>
      {/* front card */}
      <rect x="58" y="104" width="116" height="56" rx="12" fill={ILLO.white} stroke={ILLO.line} strokeWidth="3"/>
      <text x="70" y="122" fontFamily="Geist, sans-serif" fontSize="9" fontWeight="700" fill={ILLO.ink}>TODAY</text>
      <circle cx="78" cy="140" r="8" fill={ILLO.blue}/>
      <path d="M74 140 l3 3 l6 -6" stroke={ILLO.white} strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round"/>
      <line x1="94" y1="136" x2="138" y2="136" stroke={ILLO.grayLine} strokeWidth="3" strokeLinecap="round"/>
      <line x1="94" y1="146" x2="120" y2="146" stroke={ILLO.grayLine} strokeWidth="3" strokeLinecap="round"/>
      <text x="162" y="142" textAnchor="end" fontFamily="Geist, sans-serif" fontSize="12" fontWeight="700" fill={ILLO.blue}>$42</text>
    </svg>
  );
}

const ONBOARD_ILLOS = {
  1: IlloWelcome,
  2: IlloQuickLog,
  3: IlloSharedCosts,
  4: IlloEventBudget,
  5: IlloJournal,
};

Object.assign(window, { ONBOARD_ILLOS, IlloWelcome, IlloQuickLog, IlloSharedCosts, IlloEventBudget, IlloJournal });
