package com.example.breakingplace.particle.override.overrides;

import com.example.breakingplace.particle.override.BlockParticleOverride;
import com.example.breakingplace.particle.override.ParticleOrigin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.Random;
import java.util.Set;

/**
 * Redstone components get vanilla's colorable {@code DUST} particle
 * (the same particle type redstone wire itself already uses when powered)
 * instead of a plain block fragment.
 * <p>
 * NOTE: {@link DustParticleEffect}'s constructor signature has changed
 * across Minecraft versions. This is written for the 1.17.1 Yarn mappings
 * as best known ({@code DustParticleEffect(Vec3f color, float scale)}); if
 * your local mappings differ, adjust the single call in
 * {@link #dustEffect()} accordingly - everything else in this class is
 * unaffected.
 */
public class RedstoneOverride implements BlockParticleOverride {

    private static final Set<Block> REDSTONE_BLOCKS = Set.of(
            Blocks.REDSTONE_WIRE,
            Blocks.REDSTONE_BLOCK,
            Blocks.REPEATER,
            Blocks.COMPARATOR,
            Blocks.REDSTONE_TORCH,
            Blocks.REDSTONE_WALL_TORCH
    );

    @Override
    public boolean matches(BlockState state) {
        return REDSTONE_BLOCKS.contains(state.getBlock());
    }

    @Override
    public boolean spawn(MinecraftClient client, World world, BlockPos pos, BlockState state, ParticleOrigin origin) {
        Random random = world.getRandom();
        int count = origin == ParticleOrigin.BREAK ? 8 : 4;
        DustParticleEffect effect = dustEffect();

        for (int i = 0; i < count; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();

            double vx = (random.nextDouble() - 0.5) * 0.02;
            double vy = 0.01;
            double vz = (random.nextDouble() - 0.5) * 0.02;

            client.particleManager.addParticle(effect, x, y, z, vx, vy, vz);
        }
        return true;
    }

    private static DustParticleEffect dustEffect() {
        // Bright redstone red-orange, roughly matching redstone wire's own particle color.
        return new DustParticleEffect(new Vec3f(1.0F, 0.15F, 0.02F), 1.0F);
    }
}
