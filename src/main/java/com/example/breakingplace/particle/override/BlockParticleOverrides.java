package com.example.breakingplace.particle.override;

import com.example.breakingplace.config.ModConfig;
import com.example.breakingplace.particle.override.overrides.DustyMaterialOverride;
import com.example.breakingplace.particle.override.overrides.RedstoneOverride;
import com.example.breakingplace.particle.override.overrides.SnowOverride;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Central dispatch point for "what particle should this block produce".
 * <p>
 * This is intentionally a short, ordered list rather than a generic
 * data-driven config system (like Particle Interactions' JSON override
 * system) - with only a handful of material categories that make sense for
 * 1.17.1, a small hardcoded list is easier to reason about, faster to check,
 * and has nothing to parse/validate at startup. If this list grows
 * significantly in a later phase, it's straightforward to swap the backing
 * store without changing the call sites below.
 */
public final class BlockParticleOverrides {

    private static final List<BlockParticleOverride> OVERRIDES = List.of(
            new SnowOverride(),
            new RedstoneOverride(),
            new DustyMaterialOverride()
    );

    private BlockParticleOverrides() {
    }

    /**
     * @return true if an override handled this block and the default vanilla
     * block-fragment particle should be suppressed.
     */
    public static boolean tryOverride(MinecraftClient client, World world, BlockPos pos, BlockState state, ParticleOrigin origin) {
        if (!ModConfig.get().blockOverridesEnabled) {
            return false;
        }
        for (BlockParticleOverride override : OVERRIDES) {
            if (override.matches(state) && override.spawn(client, world, pos, state, origin)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a small burst of bubbles on top of whatever else spawned, if the
     * interaction happened underwater. This never suppresses the main
     * particle - bubbles are additive, matching how water reacts to any
     * disturbance regardless of what caused it.
     */
    public static void spawnUnderwaterBubblesIfNeeded(MinecraftClient client, World world, BlockPos pos, ParticleOrigin origin) {
        if (!ModConfig.get().underwaterBubblesEnabled) {
            return;
        }
        if (!world.getFluidState(pos).isIn(FluidTags.WATER)) {
            return;
        }

        Random random = world.getRandom();
        int count = origin == ParticleOrigin.BREAK
                ? ModConfig.get().underwaterBubblesOnBreak
                : ModConfig.get().underwaterBubblesOnPlace;

        for (int i = 0; i < count; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            client.particleManager.addParticle(
                    ParticleTypes.BUBBLE,
                    x, y, z,
                    0.0, 0.08 + random.nextDouble() * 0.06, 0.0
            );
        }
    }
}
