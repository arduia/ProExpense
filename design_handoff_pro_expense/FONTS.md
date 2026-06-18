# Pro Expense — Fonts

Self-contained web-font package extracted from the Hi-Fi build. Everything needed to render the type system **offline**, with no Google Fonts dependency.

## What's in here

| File | Purpose |
|---|---|
| `fonts.css` | `@font-face` rules pointing at the local `.woff2` files. Link this instead of the Google Fonts `<link>`. |
| `fonts/*.woff2` | 32 font binaries — latin + latin-ext subsets, every weight/style the build uses. |
| `font-specimen.html` | Visual proof sheet rendering each family from the local files. |

## How to use

Replace the Google Fonts `<link>` in any page with:

```html
<link rel="stylesheet" href="fonts.css">
```

The `font-family` names are unchanged (`'Manrope'`, `'Plus Jakarta Sans'`, `'Geist Mono'`, `'Instrument Serif'`), so existing CSS variables in `proto-brand.css` keep working untouched.

## Families

| Family | Role (`--token`) | Weights bundled | Styles | Source |
|---|---|---|---|---|
| **Manrope** | `--sans` — all UI, body, buttons, list rows | 300 · 400 · 500 · 600 · 700 · 800 | normal | Google Fonts (OFL) |
| **Plus Jakarta Sans** | UI fallback / alt sans in stack | 400 · 500 · 600 · 700 · 800 | normal | Google Fonts (OFL) |
| **Geist Mono** | `--mono` — eyebrows, tab labels, timestamps, tabular figures | 400 · 500 · 600 | normal | Google Fonts (OFL) |
| **Instrument Serif** | `--serif` — display: titles, day headers, large amounts | 400 | normal · italic | Google Fonts (OFL) |

> Note: the design tokens name the display face "Instrument Serif" and the sans "Geist / Manrope." The actual binaries shipped are **Manrope** (sans), **Plus Jakarta Sans** (alt sans), **Geist Mono** (mono), and **Instrument Serif** (serif) — matching the `<link>` in the Hi-Fi files.

## Font-family stacks

The build references these via CSS variables in `proto-brand.css`. Keep the fallbacks — they cover the gap before the web font loads and any platform missing it.

| Token | Stack |
|---|---|
| `--sans` | `"Manrope", "Roboto", -apple-system, system-ui, sans-serif` |
| `--serif` | `"Instrument Serif", "SF Pro Display", -apple-system, "Plus Jakarta Sans", "Manrope", system-ui` |
| `--mono` | `"Geist Mono", "Roboto Mono", ui-monospace, monospace` |

## Type scale

| Role | Family | Size | Line height | Letter-spacing | Weight | Example |
|---|---|---|---|---|---|---|
| Display amount | mono | 64px | 1.0 | -0.025em | 400 | `$12.50` |
| List amount | mono | 18px | 1.1 | -0.01em | 400 | `$12.40` |
| Screen title | sans | 32px | 1.0 | -0.015em | 600 | `Journal` |
| Section head | sans | 18px | 1.1 | -0.01em | 600 | `Today · May 25` |
| Display flourish | serif (italic) | 32px | 1.0 | -0.015em | 400 | `Hi, Maya` |
| Body | sans | 14px | 1.4 | 0 | 400–600 | `Lunch with M.` |
| Caption | sans | 11.5px | 1.4 | 0 | 400 | `Food · 12:30 PM` |
| Eyebrow / label | mono | 11px | 1.3 | 0.10em (uppercase) | 500 | `AMOUNT · USD` |
| Tab / timestamp | mono | 10–12px | 1.3 | 0.08em | 400–500 | `12:30 PM` |

## Usage rules

- **Mono for money and figures.** Geist Mono carries display amounts, list amounts, eyebrows, timestamps, and keypad digits. Use `tnum` / tabular figures.
- **Sans is the workhorse.** Manrope carries screen titles, day headers, body, list rows, and buttons. Use 500–600 for emphasis, 400 for secondary text. Buttons are 600 at -0.005em.
- **Serif = flourish only.** Instrument Serif italic for editorial accents (e.g. the user's name on Home). Not for amounts or screen titles in the Android build.
- **Italics** are available only in Instrument Serif. No italic sans or mono shipped.

## Google Fonts equivalent

If you'd rather load from the CDN instead of the local files, this `<link>` matches the bundled faces:

```html
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Manrope:wght@300;400;500;600;700;800&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=Geist+Mono:wght@400;500;600&family=Instrument+Serif:ital@0;1&display=swap" rel="stylesheet">
```

## Subsets

Each family ships **latin** and **latin-ext** subsets (covers Western + Central/Eastern European Latin). Cyrillic, Greek, Vietnamese and symbol subsets were dropped to keep the package lean — re-export from Google Fonts if you need them.

## File naming

```
fonts/<family-slug>-<weight>[-italic]-<subset>.woff2

e.g.  manrope-600-latin.woff2
      instrument-serif-400-italic-latin-ext.woff2
```

## Licensing

All four families are licensed under the **SIL Open Font License 1.1** — free to bundle and ship in commercial products. Keep the OFL notice if you redistribute the font files on their own.

---

*Pro Expense · font package · mirrors the shipped Hi-Fi build.*
