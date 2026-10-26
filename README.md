# Particle Interactions — Minecraft 1.17.1 backport

Clean-room Fabric backport of [Particle Interactions by Enchanted-Games](https://github.com/Enchanted-Games/block-place-particles), targeting Minecraft 1.17.1 and Java 16. Existing target-branch implementation files are not used as source.

## Block placement particles

Successful BlockItem placement-method calls are intercepted only after Minecraft reports that placement succeeded. Particles are sampled along the placed block's actual outline-shape edges, with outward velocity from the shape centre. The block state passed by Minecraft is used directly, so placement effects do not depend on the pre-placement state at the clicked face.

Block-specific pixel sprites are implemented for snow, leaves, petals/flowers, grass and crops, moss, sand/gravel/concrete powder, redstone devices including redstone blocks, and portal shatter effects where the state has a renderable shape. Other blocks fall back to vanilla block-state debris. Water and lava block-state placements receive fluid-specific bubble or lava particles.

The previous modded block-break hook was removed from the active mixin list. Block breaking uses vanilla particles for now; the mod no longer adds its unrelated composter/XP-like grass particles on break.

## Compatibility scope

This is a placement-focused backport pass, not full parity with all modern Particle Interactions features. Modern resource-pack emitter rules, every custom particle animation, and the complete configuration UI have not been ported yet. Minecraft 1.17.1 content/APIs unavailable in modern versions is not included.

## Build

The GitHub Actions workflow uses Java 16, Gradle 7.3.3, Loom 0.10, Yarn 1.17.1+build.65, Fabric Loader 0.14.24 and Fabric API 0.46.1+1.17. Run Gradle build; the mod JAR is produced in build/libs/.

## Attribution and licence

Original project by Enchanted-Games: https://github.com/Enchanted-Games/block-place-particles. Original licence: CC BY-NC 4.0. See LICENSE. This adaptation is not endorsed by the original author.
