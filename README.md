# Particle Interactions — Minecraft 1.17.1 backport

Clean-room Fabric backport of [Particle Interactions by Enchanted-Games](https://github.com/Enchanted-Games/block-place-particles), targeting Minecraft 1.17.1 and Java 16. Existing target-branch implementation files are not used as source.

## Compatibility scope

The 1.17.1 game lacks some content and APIs present in modern releases. Where a particle type is unavailable, this project uses 1.17.1 vanilla particle effects or block-state debris. Modern resource-pack particle overrides and newer content-specific effects cannot be assumed to be identical until each feature is audited and tested.

## Build

The GitHub Actions workflow uses Java 16, Gradle 7.3.3, Loom 0.10, Yarn 1.17.1+build.65, Fabric Loader 0.14.24 and Fabric API 0.46.1+1.17. Run `gradle build`; the mod JAR is produced in `build/libs/`.

## Attribution and licence

Original project by Enchanted-Games: https://github.com/Enchanted-Games/block-place-particles. Original licence: CC BY-NC 4.0. See [LICENSE](LICENSE). This adaptation is not endorsed by the original author.
