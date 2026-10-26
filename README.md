# Particle Interactions — Minecraft 1.17.1 backport

A clean-room compatibility port of [Particle Interactions by Enchanted-Games](https://github.com/Enchanted-Games/block-place-particles).

Target: Minecraft 1.17.1, Fabric, Yarn build 65, Java 16. This branch does not reuse the target repository's previous implementation.

This is a compatibility-first implementation using the 1.17.1 particle and mixin APIs. Some features from modern releases depend on blocks, particle types, resource-pack systems, or rendering APIs that did not exist in 1.17.1; those must use vanilla substitutes or remain explicitly marked as unavailable.

Original project license: CC BY-NC 4.0. See LICENSE for attribution and licence details. Non-commercial use only.
