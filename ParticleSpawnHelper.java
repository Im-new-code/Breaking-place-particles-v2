package com.example.breakingplace.particle;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;

/**
 * Single place where this mod actually spawns particles.
 * <p>
 * Mixins in this mod only detect *that* something happened (a block was placed,
 * a tool changed a block in place) and then hand off to this class. Keeping the
 * "how to make particles appear" logic out of the mixins keeps each mixin small
 * and focused, and means there is exactly one code path to reason about when
 * checking for duplicate or incorrect particle spawns.
 * <p>
 * Everything here reuses vanilla's own particle machinery on purpose:
 * {@link ParticleManager#addBlockBreakParticles(BlockPos, BlockState)} already
 * reads the block's real {@code VoxelShape} and distributes particles
 * proportionally across it with an outward initial velocity from the shape's
 * center. That is functionally the same approach Particle Interactions uses for
 * its own block place/break particles (edge/volume sampling of the voxel shape,
 * velocity relative to the shape center) - so re-implementing that math by hand
 * would only add code, risk, and CPU cost for no visible benefit on this version
 * of the game. It also means slabs, stairs, fences, panes, plants, scaffolding,
 * and other irregular shapes are already handled correctly, because vanilla
 * already handles them correctly for normal block breaking.
 */
public final class ParticleSpawnHelper {

    private ParticleSpawnHelper() {
    }

    /**
     * Spawns particles for a block that was just placed, at its true final
     * position and using its true final block state.
     */
    public static void spawnPlacementParticles(MinecraftClient client, BlockPos pos, BlockState state) {
        spawnShapeAwareParticles(client, pos, state);
    }

    /**
     * Spawns a particle burst for a tool interaction that changed a block in
     * place (tilling, stripping, scraping oxidation/wax off copper, flattening
     * dirt into a path) where vanilla itself gives no particle feedback at all.
     * <p>
     * This intentionally reuses the exact same algorithm as placement particles.
     * It is already cheap - it only ever runs once per successful interaction,
     * never per-tick - and it is already shape-aware, so there is no need for a
     * separate "lighter" implementation.
     */
    public static void spawnToolInteractionParticles(MinecraftClient client, BlockPos pos, BlockState resultingState) {
        spawnShapeAwareParticles(client, pos, resultingState);
    }

    private static void spawnShapeAwareParticles(MinecraftClient client, BlockPos pos, BlockState state) {
        if (client == null || pos == null || state == null || state.isAir()) {
            return;
        }

        ParticleManager particleManager = client.particleManager;
        if (particleManager != null) {
            particleManager.addBlockBreakParticles(pos, state);
        }
    }
}
