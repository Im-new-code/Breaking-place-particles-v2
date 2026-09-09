package com.example.breakingplace.particle;

import com.example.breakingplace.particle.override.ParticleOrigin;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;

/**
 * Single place where this mod actually asks vanilla to spawn particles.
 * <p>
 * Mixins in this mod only detect *that* something happened (a block was placed,
 * a tool changed a block in place) and then hand off to this class. Keeping the
 * "how to make particles appear" logic out of the mixins keeps each mixin small
 * and focused, and means there is exactly one code path to reason about when
 * checking for duplicate or incorrect particle spawns.
 * <p>
 * The actual default particle (and any material-specific override) is produced
 * by {@link ParticleManager#addBlockBreakParticles(BlockPos, BlockState)} and
 * {@code ParticleOverrideMixin}, which intercepts that same vanilla method for
 * both this class's calls AND vanilla's own real block-breaking calls. That
 * keeps the override lookup in exactly one place regardless of who triggered it.
 */
public final class ParticleSpawnHelper {

    /**
     * Set immediately before asking vanilla to spawn a block particle, cleared
     * immediately after. {@code ParticleOverrideMixin} reads this to tell a
     * placement/tool-interaction call apart from a "real" vanilla block-break
     * call (which leaves this at {@code null}). This is only ever touched on
     * the client thread and never re-entrantly, so a plain static field is
     * enough - no need for a ThreadLocal.
     */
    private static ParticleOrigin pendingOrigin = null;

    private ParticleSpawnHelper() {
    }

    /**
     * Spawns particles for a block that was just placed, at its true final
     * position and using its true final block state.
     */
    public static void spawnPlacementParticles(MinecraftClient client, BlockPos pos, BlockState state) {
        spawnShapeAwareParticles(client, pos, state, ParticleOrigin.PLACE);
    }

    /**
     * Spawns a particle burst for a tool interaction that changed a block in
     * place (tilling, stripping, scraping oxidation/wax off copper, flattening
     * dirt into a path) where vanilla itself gives no particle feedback at all.
     * Treated the same as a placement for burst intensity - it's a "something
     * new appeared here" event, not a destructive one.
     */
    public static void spawnToolInteractionParticles(MinecraftClient client, BlockPos pos, BlockState resultingState) {
        spawnShapeAwareParticles(client, pos, resultingState, ParticleOrigin.PLACE);
    }

    private static void spawnShapeAwareParticles(MinecraftClient client, BlockPos pos, BlockState state, ParticleOrigin origin) {
        if (client == null || pos == null || state == null || state.isAir()) {
            return;
        }

        ParticleManager particleManager = client.particleManager;
        if (particleManager == null) {
            return;
        }

        pendingOrigin = origin;
        try {
            particleManager.addBlockBreakParticles(pos, state);
        } finally {
            pendingOrigin = null;
        }
    }

    /**
     * @return the origin of the call currently in flight from this class, or
     * {@link ParticleOrigin#BREAK} if none is in flight - meaning vanilla
     * itself triggered the particle call, i.e. a real block break.
     * Only meant to be called from {@code ParticleOverrideMixin}.
     */
    public static ParticleOrigin currentOriginOrBreak() {
        return pendingOrigin != null ? pendingOrigin : ParticleOrigin.BREAK;
    }
}
