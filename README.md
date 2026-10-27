# Particle Interactions — Minecraft 1.17.1 backport

Clean-room Fabric backport of [Particle Interactions by Enchanted-Games](https://github.com/Enchanted-Games/block-place-particles), targeting Minecraft 1.17.1 and Java 16. Existing target-branch implementation files are not used as source.

## Block placement particles

Successful BlockItem placement-method calls are intercepted only after Minecraft reports that placement succeeded. The implementation reads the final block state at the target position and samples the state outline shape's edges, applying outward velocity from the shape centre.

Custom Particle Interactions-style sprites cover snow/powder snow, leaves and azalea foliage, flowers and Nether/cave vines, grass blocks, dirt paths, grass-like plants and crops, cobweb/tripwire/hanging roots, sugar cane and bamboo, chains, moss, sand/gravel/concrete powder, redstone wire and dust, redstone blocks, comparators, repeaters, redstone torches, and nether portals. Grass-blade particles are used for grass blocks and dirt paths; redstone-dust particles are used for redstone blocks and redstone components. Ordinary blocks keep vanilla block-state debris, as in the upstream override-based design. Item-frame interactions use the custom tinted-dust sprite. Block-item placements read the final client-side state after placement so applicable overrides are selected from the placed block, not a stale candidate state.

The previous modded block-break hook is removed from the active mixin list. Block breaking remains vanilla-only in this placement-focused build; it no longer adds the unrelated composter/XP-like grass particles from the previous implementation.

Hoe, shovel, and axe effects spawn around the actual clicked face with the upstream-style outward impulse. Successful repeater-delay and comparator-mode interactions emit custom tinted-dust particles. Item frames emit dust when the player inserts an item or rotates it; replay playback also watches the item/rotation tracked-data updates, using the displayed block's tint where possible.

## ReplayMod compatibility

ReplayMod is an optional soft dependency. While recording, particles are saved as timestamped particle packets in ReplayMod's existing stream. During playback, those packets are detected to avoid duplicate effects. For older replays that do not contain these particle packets, client-side block-state and item-frame updates reconstruct placement, hoe/shovel, repeater/comparator, and item-frame effects. Replay detection uses reflection; there is no compile-time ReplayMod dependency.

## Compatibility scope

This is a block-placement-focused backport, not full feature parity with all modern Particle Interactions features. Modern resource-pack emitter rules, every custom particle animation, custom fluid buckets, and the complete configuration UI have not been ported yet. Minecraft 1.17.1 content/APIs unavailable in that version are not included.

## Build

The GitHub Actions workflow uses Java 16, Gradle 7.3.3, Loom 0.10, Yarn 1.17.1+build.65, Fabric Loader 0.14.24 and Fabric API 0.46.1+1.17.

## Attribution and licence

Original project by Enchanted-Games: https://github.com/Enchanted-Games/block-place-particles. Original licence: CC BY-NC 4.0. See LICENSE. This adaptation is not endorsed by the original author.

The grass-blade, tinted-dust, moss-clump, and tinted-leaf sprite frames are imported from the upstream Particle Interactions `1.21-v0.4` source assets. Particle JSON texture identifiers are relative to `textures/particle` (for example `eg_particle_interactions:grass_blade/0`), so they must not include an extra `particle/` path segment. The 1.17.1 movement/provider code remains a compatibility implementation, rather than a byte-for-byte port of the modern runtime.
