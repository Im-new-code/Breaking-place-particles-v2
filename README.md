# Breaking Place Particles

A lightweight Fabric mod for Minecraft 1.17.1, built as a 1.17.1-compatible
take on Particle Interactions. It works entirely client-side and reuses
vanilla's own particle machinery wherever possible instead of adding a
parallel particle system.

## What it does today

**Block placement**
Plays a shape-aware particle burst at the block's true final position when a
block is placed - correct for slabs, stairs, fences, panes, plants,
scaffolding, and blocks placed against a replaceable block (tall grass, snow,
water), where the final position differs from the clicked block.

**Tool interactions**
Hoe tilling, axe log-stripping/copper-scraping, and shovel path-flattening all
get a particle burst - vanilla itself gives none of these any particle
feedback.

**Material-specific particle overrides** *(new)*
Instead of every block producing identical fragments, a small set of
materials get an appearance that actually matches them, for both placing
*and* breaking:
- Snow, snow blocks, and powder snow -> vanilla's own snowflake particle
- Redstone wire/block/repeater/comparator/torches -> vanilla's own colorable
  dust particle
- Sand, red sand, gravel, soul sand, soul soil, and all 16 concrete powder
  colours -> their normal texture, but with a softer "puff of dust" motion
  instead of a sharp break-fragment kick

Anything not covered by an override still gets vanilla's default,
shape-aware block-fragment particle - nothing regresses for blocks with no
override.

**Underwater bubbles** *(new)*
A small additional bubble burst plays on top of the above whenever a
place/break/tool-interaction happens underwater.

**Configuration** *(new)*
`config/breakingplaceparticles.json` (created on first run) lets you toggle
block overrides, underwater bubbles, and tool-interaction particles, and
tune the underwater bubble counts - no extra config-screen library required.

## What's intentionally not here yet

Leaves, grass blades, flower petals, moss, fireflies, movement/sprinting
particles, and machine/tool sparks & embers (furnace, campfire, anvil,
stonecutter, minecart, blaze, falling-block impacts, slime, honey) all need
genuinely new particle *textures* and custom particle-type registration -
a bigger, separate phase of work, not a drop-in addition to the current
foundation. See the project's development notes for the proposed next phases.

## Build

Use Gradle with Java 17:

```bash
gradle build
```

The finished jar will be in:

```bash
build/libs/
```
