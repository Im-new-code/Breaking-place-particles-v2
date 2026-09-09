package com.example.breakingplace.particle.override;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A material-specific replacement for the default block particle.
 * <p>
 * Implementations decide (a) whether they apply to a given {@link BlockState},
 * and (b) if so, how to actually spawn particles for it. Returning {@code true}
 * from {@link #spawn} tells the caller the default vanilla block-fragment
 * particle should be suppressed for this event; an override that doesn't match
 * simply isn't asked to spawn anything, and the default particle plays as normal.
 */
public interface BlockParticleOverride {

    /**
     * @return true if this override handles the given block state (and should
     * be asked to spawn particles instead of the default block fragments).
     */
    boolean matches(BlockState state);

    /**
     * Spawn this override's particles. Only called when {@link #matches} was true.
     *
     * @return true if particles were actually spawned and the default block
     * particle should be suppressed; false to fall back to the default anyway
     * (e.g. an override that only wants to apply under extra conditions it
     * checks internally).
     */
    boolean spawn(MinecraftClient client, World world, BlockPos pos, BlockState state, ParticleOrigin origin);
}
