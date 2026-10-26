# Particle Interactions — Minecraft 1.17.1 backport

Clean-room Fabric backport of [Particle Interactions by Enchanted-Games](https://github.com/Enchanted-Games/block-place-particles), targeting Minecraft 1.17.1 and Java 16. Existing target-branch implementation files are not used as source.

## Block placement particles

Successful BlockItem placement-method calls are intercepted only after Minecraft reports that placement succeeded. The implementation reads the final block state at the target position and samples the state outline shape's edges, applying outward velocity from the shape centre.

Block-specific particles cover snow/powder snow, leaves and azalea foliage, flowers and Nether/cave vines, grass and crops, cobweb/tripwire/hanging roots, sugar cane and bamboo, chains, moss, sand/gravel/concrete powder, redstone components including redstone blocks, and nether portals. Other blocks use vanilla block-state debris. Applicable block item placements use this same path.

The previous modded block-break hook is removed from the active mixin list. Block breaking remains vanilla-only in this placement-focused build; it no longer adds the unrelated composter/XP-like grass particles from the previous implementation.

## Compatibility scope

This is a block-placement-focused backport, not full feature parity with all modern Particle Interactions features. Modern resource-pack emitter rules, every custom particle animation, custom fluid buckets, and the complete configuration UI have not been ported yet. Minecraft 1.17.1 content/APIs unavailable in that version are not included.

## Build

The GitHub Actions workflow uses Java 16, Gradle 7.3.3, Loom 0.10, Yarn 1.17.1+build.65, Fabric Loader 0.14.24 and Fabric API 0.46.1+1.17.

## Attribution and licence

Original project by Enchanted-Games: https://github.com/Enchanted-Games/block-place-particles. Original licence: CC BY-NC 4.0. See LICENSE. This adaptation is not endorsed by the original author.
