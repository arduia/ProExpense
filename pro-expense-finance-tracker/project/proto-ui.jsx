// Hi-fi UI primitives & icons for the Pro Expense prototype
// All icons are stroke-based SVG. Categories have their own accent colors.

// ───────── icons ─────────
function Icon({ name, size = 22, stroke = 'currentColor', strokeWidth = 1.6, style = {} }) {
  const props = { width: size, height: size, viewBox: '0 0 24 24', fill: 'none', stroke, strokeWidth, strokeLinecap: 'round', strokeLinejoin: 'round', style };
  switch (name) {
    case 'home': return (<svg {...props}><path d="M4 11.5 12 5l8 6.5V19a1 1 0 0 1-1 1h-4v-6h-6v6H5a1 1 0 0 1-1-1Z" /></svg>);
    case 'budget': return (<svg {...props}><path d="M4 7h16v12a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1Z" /><path d="M4 7V5a1 1 0 0 1 1-1h11l4 3" /><circle cx="16" cy="13" r="1.5" /></svg>);
    case 'journal': return (<svg {...props}><path d="M5 4h13a1 1 0 0 1 1 1v15l-3-2-3 2-3-2-3 2-3-2V5a1 1 0 0 1 1-1Z" /><path d="M9 8h6M9 12h6" /></svg>);
    case 'more': return (<svg {...props}><circle cx="6" cy="12" r="1.4" fill={stroke} stroke="none"/><circle cx="12" cy="12" r="1.4" fill={stroke} stroke="none"/><circle cx="18" cy="12" r="1.4" fill={stroke} stroke="none"/></svg>);
    case 'plus': return (<svg {...props}><path d="M12 5v14M5 12h14" /></svg>);
    case 'minus': return (<svg {...props}><path d="M5 12h14" /></svg>);
    case 'back': return (<svg {...props}><path d="M15 6l-6 6 6 6" /></svg>);
    case 'close': return (<svg {...props}><path d="M6 6l12 12M18 6 6 18" /></svg>);
    case 'chevron-down': return (<svg {...props}><path d="M6 9l6 6 6-6" /></svg>);
    case 'chevron-right': return (<svg {...props}><path d="M9 6l6 6-6 6" /></svg>);
    case 'search': return (<svg {...props}><circle cx="11" cy="11" r="6"/><path d="m20 20-4-4"/></svg>);
    case 'bell': return (<svg {...props}><path d="M6 16V11a6 6 0 1 1 12 0v5l1.5 2h-15Z" /><path d="M10 20a2 2 0 0 0 4 0" /></svg>);
    case 'sparkle': return (<svg {...props}><path d="M12 4v4M12 16v4M4 12h4M16 12h4M6 6l3 3M15 15l3 3M6 18l3-3M15 9l3-3" /></svg>);
    case 'check': return (<svg {...props}><path d="M5 12l5 5L20 7" /></svg>);
    case 'at': return (<svg {...props}><circle cx="12" cy="12" r="4"/><path d="M16 8v5a3 3 0 0 0 6 0v-1a10 10 0 1 0-4 8"/></svg>);
    case 'calendar': return (<svg {...props}><rect x="4" y="5" width="16" height="15" rx="2" /><path d="M4 10h16M9 3v4M15 3v4" /></svg>);
    case 'clock': return (<svg {...props}><circle cx="12" cy="12" r="8"/><path d="M12 8v4l3 2"/></svg>);
    case 'note': return (<svg {...props}><path d="M6 4h9l4 4v12a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1Z"/><path d="M15 4v4h4M9 13h6M9 17h4"/></svg>);
    case 'user': return (<svg {...props}><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-6 8-6s8 2 8 6"/></svg>);
    // categories
    case 'cat-food': return (<svg {...props}><path d="M7 4v8a3 3 0 0 0 3 3v5M7 4v4M10 4v4M17 4c-2 0-4 2.5-4 6s2 4 4 4v6"/></svg>);
    case 'cat-transport': return (<svg {...props}><path d="M5 14V9a3 3 0 0 1 3-3h8a3 3 0 0 1 3 3v5"/><rect x="3" y="14" width="18" height="5" rx="1.5"/><circle cx="8" cy="19" r="1.4"/><circle cx="16" cy="19" r="1.4"/><path d="M8 9h8"/></svg>);
    case 'cat-shopping': return (<svg {...props}><path d="M5 9h14l-1.2 11a1 1 0 0 1-1 .9H7.2a1 1 0 0 1-1-.9Z"/><path d="M9 9V6a3 3 0 0 1 6 0v3"/></svg>);
    case 'cat-bills': return (<svg {...props}><path d="M6 3h12v18l-3-2-3 2-3-2-3 2Z"/><path d="M9 8h6M9 12h6M9 16h3"/></svg>);
    case 'cat-health': return (<svg {...props}><path d="M12 6v12M6 12h12"/><circle cx="12" cy="12" r="9"/></svg>);
    case 'cat-entertainment': return (<svg {...props}><circle cx="12" cy="12" r="9"/><path d="M10 9l5 3-5 3Z" fill={stroke} stroke={stroke}/></svg>);
    case 'cat-coffee': return (<svg {...props}><path d="M5 9h12v6a4 4 0 0 1-4 4H9a4 4 0 0 1-4-4Z"/><path d="M17 11h2a2 2 0 0 1 0 4h-2M8 5v2M11 4v3M14 5v2"/></svg>);
    case 'cat-pet': return (<svg {...props}><circle cx="6" cy="9" r="1.6"/><circle cx="10" cy="6" r="1.6"/><circle cx="14" cy="6" r="1.6"/><circle cx="18" cy="9" r="1.6"/><path d="M8 16c0-2.5 1.8-5 4-5s4 2.5 4 5c0 2-1.6 4-4 4s-4-2-4-4Z"/></svg>);
    case 'cat-default': return (<svg {...props}><circle cx="12" cy="12" r="8"/><path d="M9 12h6M12 9v6"/></svg>);
    // features
    case 'feat-reports': return (<svg {...props}><path d="M5 19V8M11 19V5M17 19v-7"/><path d="M3 19h18"/></svg>);
    case 'feat-debt': return (<svg {...props}><circle cx="9" cy="8" r="3"/><circle cx="17" cy="14" r="3"/><path d="M3 19c0-2.5 2.7-5 6-5M14 21c0-2 .8-4 3-4"/></svg>);
    case 'feat-split': return (<svg {...props}><circle cx="8" cy="8" r="3"/><circle cx="17" cy="8" r="3"/><circle cx="12" cy="17" r="3"/><path d="M10 10l3.5 4M14.5 10 12 14"/></svg>);
    case 'feat-events': return (<svg {...props}><path d="M5 7h14l-2 13a1 1 0 0 1-1 .9H8a1 1 0 0 1-1-.9Z"/><path d="M8 7V5a4 4 0 0 1 8 0v2"/></svg>);
    case 'eye': return (<svg {...props}><path d="M2 12c2-5 6-7.5 10-7.5S20 7 22 12c-2 5-6 7.5-10 7.5S4 17 2 12Z"/><circle cx="12" cy="12" r="3"/></svg>);
    case 'fingerprint': return (<svg {...props}><path d="M11 3a8 8 0 0 1 8 8M7 7a6 6 0 0 1 10 4M9 20.5c-2-3-3-6-3-9 0-2.5 2-5 5-5s5 2.5 5 5c0 4-1 7-3 10M12 11c0 4-1 7-2 10M14 14c-.5 3-1.4 5-2.5 7M5.5 14c.4 1.7 1 3.2 2 5"/></svg>);
    default: return (<svg {...props}><circle cx="12" cy="12" r="9" /></svg>);
  }
}

// Category metadata — name → icon + accent color (Material blue family + signals)
const CATEGORIES = {
  food:          { id: 'food',          label: 'Food',          icon: 'cat-food',          color: '#039be5', tint: '#e1f5fe', custom: false },
  transport:     { id: 'transport',     label: 'Transport',     icon: 'cat-transport',     color: '#0288d1', tint: '#b3e5fc', custom: false },
  shopping:      { id: 'shopping',      label: 'Shopping',      icon: 'cat-shopping',      color: '#ef5350', tint: '#ffcdd2', custom: false },
  bills:         { id: 'bills',         label: 'Bills',         icon: 'cat-bills',         color: '#757575', tint: '#eeeeee', custom: false },
  health:        { id: 'health',        label: 'Health',        icon: 'cat-health',        color: '#4caf50', tint: '#c8e6c9', custom: false },
  entertainment: { id: 'entertainment', label: 'Entertainment', icon: 'cat-entertainment', color: '#0277bd', tint: '#81d4fa', custom: false },
  coffee:        { id: 'coffee',        label: 'Coffee runs',   icon: 'cat-coffee',        color: '#9e9e9e', tint: '#e0e0e0', custom: true },
  pet:           { id: 'pet',           label: 'Pet care',      icon: 'cat-pet',           color: '#66bb6a', tint: '#dcedc8', custom: true },
};
const DEFAULT_CATEGORY_IDS = ['food', 'transport', 'shopping', 'bills', 'health', 'entertainment'];
const CUSTOM_CATEGORY_IDS = ['coffee', 'pet'];

// Category icon badge — circular, tinted
function CatBadge({ cat, size = 36 }) {
  const meta = CATEGORIES[cat] || CATEGORIES.food;
  return (
    <div style={{
      width: size, height: size, borderRadius: '50%',
      background: meta.tint, color: meta.color,
      display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
    }}>
      <Icon name={meta.icon} size={Math.round(size * 0.52)} stroke={meta.color} strokeWidth={1.7} />
    </div>
  );
}

// Chip — selectable
function CatChip({ cat, selected, onClick }) {
  const meta = CATEGORIES[cat] || CATEGORIES.food;
  const styleSelected = {
    background: meta.color,
    color: '#fffdf6',
    borderColor: meta.color,
  };
  const styleIdle = {
    background: 'transparent',
    color: 'var(--ink-2)',
    borderColor: 'var(--line-strong)',
  };
  return (
    <button
      type="button"
      className="proto-tap"
      onClick={onClick}
      style={{
        display: 'inline-flex', alignItems: 'center', gap: 6,
        padding: '7px 12px 7px 8px',
        border: '1.2px solid',
        borderRadius: 99,
        fontSize: 12.5,
        fontWeight: selected ? 600 : 500,
        whiteSpace: 'nowrap',
        ...(selected ? styleSelected : styleIdle),
      }}
    >
      <Icon name={meta.icon} size={14} stroke={selected ? '#fffdf6' : meta.color} strokeWidth={1.8} />
      <span>{meta.label}</span>
    </button>
  );
}

// Format amount string for display ($ + commas + decimal)
function formatAmount(amountStr) {
  if (!amountStr || amountStr === '') return { whole: '0', decimal: null, isPlaceholder: true };
  const hasDecimal = amountStr.includes('.');
  const [w, d] = amountStr.split('.');
  const wholeFormatted = (w || '0').replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  return {
    whole: wholeFormatted,
    decimal: hasDecimal ? (d || '') : null,
    isPlaceholder: false,
  };
}
function amountToFloat(s) {
  if (!s) return 0;
  return parseFloat(s) || 0;
}

// Touch button — generic
function Button({ children, onClick, variant = 'primary', size = 'md', disabled, style = {}, fullWidth = false }) {
  const variants = {
    primary: { background: 'var(--clay)', color: '#fffdf6', border: '1.4px solid var(--clay)' },
    'primary-deep': { background: 'var(--clay-deep)', color: '#fffdf6', border: '1.4px solid var(--clay-deep)' },
    secondary: { background: 'transparent', color: 'var(--ink)', border: '1.4px solid var(--line-strong)' },
    ghost: { background: 'transparent', color: 'var(--ink)', border: '1.4px solid transparent' },
    sage: { background: 'var(--sage)', color: '#fffdf6', border: '1.4px solid var(--sage)' },
    dark: { background: 'var(--ink)', color: 'var(--paper-warm)', border: '1.4px solid var(--ink)' },
  };
  const sizes = {
    sm: { padding: '8px 14px', fontSize: 12, borderRadius: 10 },
    md: { padding: '12px 18px', fontSize: 14, borderRadius: 12 },
    lg: { padding: '16px 22px', fontSize: 15, borderRadius: 14 },
  };
  const v = variants[variant] || variants.primary;
  const s = sizes[size] || sizes.md;
  return (
    <button
      type="button"
      className="proto-tap"
      onClick={disabled ? undefined : onClick}
      style={{
        fontFamily: 'var(--sans)',
        fontWeight: 600,
        letterSpacing: '-0.005em',
        width: fullWidth ? '100%' : undefined,
        opacity: disabled ? 0.4 : 1,
        cursor: disabled ? 'not-allowed' : 'pointer',
        ...v, ...s, ...style,
      }}
    >
      {children}
    </button>
  );
}

Object.assign(window, { Icon, CATEGORIES, DEFAULT_CATEGORY_IDS, CUSTOM_CATEGORY_IDS, CatBadge, CatChip, formatAmount, amountToFloat, Button });

// ── Shared helpers also used by static screens ────────────────────────
function currencyFmt(n) {
  if (n == null || isNaN(n)) return '$0';
  return '$' + n.toLocaleString('en-US', { minimumFractionDigits: n % 1 === 0 ? 0 : 2, maximumFractionDigits: 2 });
}

const ACTIVE_EVENTS = [
  { id: 'bali', name: 'Bali Trip', range: 'May 12 — May 26' },
  { id: 'john', name: "John's Wedding", range: 'Jun 04 — Jun 06' },
];
const ACTIVE_DEBTS = [
  { id: 'lent-john', label: 'Lent · John', amount: 50 },
  { id: 'owe-sarah', label: 'Owe · Sarah', amount: 30 },
];

Object.assign(window, { currencyFmt, ACTIVE_EVENTS, ACTIVE_DEBTS });
