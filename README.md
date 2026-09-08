# Breaking Place Particles

A tiny, lightweight Fabric mod for Minecraft 1.17.1 that:

- Plays a shape-aware block-particle effect at the true final position whenever a block is successfully placed (correct even for slabs, stairs, fences, panes, plants, scaffolding, and blocks placed against a replaceable block like tall grass, snow, or water).
- Plays the same particle effect when a hoe, axe, or shovel transforms a block in place (tilling, log stripping, copper scraping, path flattening) — interactions vanilla itself gives no particle feedback for.

Built to stay usable on low-end/Android (PojavLauncher, MobileGlues/MGES) hardware: it reuses vanilla's own particle system rather than adding a custom one, only runs on the client, and only does any work at the moment of an actual interaction (never per-tick).

## Build

Use Gradle with Java 17:

```bash
gradle build
```

The finished jar will be in:

```bash
build/libs/
```
