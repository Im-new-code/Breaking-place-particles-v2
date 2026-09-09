# Breaking Place Particles

A Fabric mod for Minecraft 1.17.1 - a 1.17.1-compatible take on Particle
Interactions. Entirely client-side; reuses vanilla's own particle machinery
wherever a vanilla type already fits, and adds a small set of new custom
particle types (with generated placeholder textures, not copied from any
reference mod) where nothing vanilla-native applies.

## NOT compiled/tested

This project was written and reasoned through without a working Minecraft/
Fabric/Gradle toolchain available (no network access in the environment it
was built in). Everything is written against 1.17.1 Yarn mappings as
precisely as could be researched, but **you need to run the build yourself**
to catch anything that doesn't compile. See "Known risk areas" below for the
handful of specific lines most likely to need a small fix.

## Feature summary

**Block placement** - shape-aware particle burst at the block's true final
position (correct for slabs/stairs/fences/scaffolding/replaceable blocks).

**Tool interactions** - hoe tilling, axe stripping/copper-scraping, shovel
path-flattening all get particles vanilla itself doesn't provide.

**Material-specific block particle overrides** (place AND break):
- Snow / snow block / powder snow -> vanilla `SNOWFLAKE`
- Redstone wire/block/repeater/comparator/torch -> vanilla colorable `DUST`
- Sand/red sand/gravel/soul sand/soul soil/concrete powder -> own texture, softer "dust puff" motion
- All six 1.17.1 leaf types -> drifting, species-tinted custom leaf particles
- Flowers (grouped by color) -> small floating petal particles
- Grass/tall grass/ferns/vines/crops/seagrass/kelp/glow lichen/dead bush/cobweb/tripwire -> grass-blade particles
- Moss block/moss carpet -> moss-fleck particles

**Underwater bubbles** - layered on top of any of the above when submerged.

**Ambient & movement effects** (all O(1) per tick - bounded random sampling,
never a chunk/world scan):
- Falling leaves drift down near the player from nearby leaf blocks
- Walking through grass/ferns/leaves/vines/crops/cobwebs kicks up a particle
- Swamp-at-night ambient fireflies

**Sparks & embers**:
- Flint and steel ignition sparks
- Extra embers on lit furnaces/blast furnaces/smokers (piggybacking on their
  existing vanilla ambient-particle tick)
- Extra sparks/embers on lit campfires (normal and soul)

**Configuration** - `config/breakingplaceparticles.json` (created on first
run) toggles every system above individually.

## What's still not here

Blaze sparks, anvil/stonecutter/item-frame particles, falling-block impact
particles, minecart wheel sparks, slime/honey interaction particles, and
water-splash/lava-bubble enhancements from the original wishlist were left
out of this pass - each needs its own hook point researched the same way the
systems above were, and this response was already a large amount of new
surface area to add responsibly in one pass.

## Known risk areas (check these first if the build fails)

- `RedstoneOverride.dustEffect()` - `DustParticleEffect`'s constructor
  signature is the one API surface not double-checked against a real 1.17.1
  build; the `Vec3f`-based constructor used is the best-confidence guess.
- All custom `Particle` subclasses assume 1.17.1's field names
  (`colorRed`/`colorGreen`/`colorBlue`/`colorAlpha`, `setColor`,
  `setColorAlpha`) rather than the `red`/`green`/`blue`/`alpha`/`setAlpha`
  naming used from 1.19 onward - this was confirmed against 1.17.1-specific
  Yarn javadoc, but worth an eye if something doesn't compile.
- `AbstractFurnaceBlock`/`CampfireBlock`'s `randomDisplayTick` signature and
  `FlintAndSteelItem`/hoe/axe/shovel's `useOnBlock` are assumed to keep
  their standard Yarn names; these have been stable across many versions but
  weren't individually verified against 1.17.1 mappings docs.

## Build

Use Gradle with Java 17:

```bash
gradle build
```

The finished jar will be in:

```bash
build/libs/
```
