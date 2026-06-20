# Quick-Access Tiles

Feature shortcuts on the Home screen — a 4-up grid of labelled icon tiles.

![Quick-access tiles](../screenshots/quick-access.png)

- Tile: `Column(CenterHorizontally)`, white card, 1dp `line`, radius **14dp**, padding `12 × 4` dp, gap 7dp.
- Icon chip: 36dp square, radius **11dp**, background = feature tint, glyph 18dp stroke 1.8.
- Label: Manrope 11sp / 500, `ink2`.

| Tile | Tint | Glyph stroke | Icon |
|---|---|---|---|
| Reports | `blue100` | `blue700` | `feat-reports` |
| Debts | `#C8E6C9` | `green500` | `feat-debt` |
| Split | `tagTint` | `tagDeep` | `feat-split` |
| Events | `yellow300` | `#F9A825` | `feat-events` |

## Behavior
- Each tile **navigates to its feature** (Reports / Debts / Split / Events) on tap, with the standard `scale 0.97` press feedback.
- The grid is fixed at four tiles; a **"Customize"** affordance in the section header is reserved for reordering (not yet active).
- Tiles hold no selected/toggle state — they are pure navigation.

## Compose notes
`Row` of four `Modifier.weight(1f)` tiles (or `LazyVerticalGrid` Fixed(4)), each a `Surface(onClick, shape = RoundedCornerShape(14.dp))`. The inner icon chip is a `Box(Modifier.size(36.dp).clip(RoundedCornerShape(11.dp)).background(tint))`. Note the chip radius (11dp) differs from the tile radius (14dp).
