package com.example.breakingplace.particle.override.overrides;

import com.example.breakingplace.particle.override.BlockParticleOverride;
import com.example.breakingplace.particle.override.ParticleOrigin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;
import java.util.Set;

/**
 * Snow layers, snow blocks, and powder snow (all present in 1.17.1) get
 * vanilla's own {@link ParticleTypes#SNOWFLAKE} instead of a solid block
 * fragment - it's a stock particle type, so this is zero extra asset risk.
 */
public class SnowOverride implements BlockParticleOverride {

    private static final Set<Block> SNOW_BLOCKS = Set.of(
            Blocks.SNOW,
            Blocks.SNOW_BLOCK,
            Blocks.POWDER_SNOW
    );

    @Override
    public boolean matches(BlockState state) {
        return SNOW_BLOCKS.contains(state.getBlock());
    }

    @Override
    public boolean spawn(MinecraftClient client, World world, BlockPos pos, BlockState state, ParticleOrigin origin) {
        Random random = world.getRandom();
        int count = origin == ParticleOrigin.BREAK ? 10 : 5;

        for (int i = 0; i < count; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();

            double vx = (random.nextDouble() - 0.5) * 0.05;
            double vy = origin == ParticleOrigin.BREAK ? 0.02 : -0.02 - random.nextDouble() * 0.02;
            double vz = (random.nextDouble() - 0.5) * 0.05;

            client.particleManager.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, vx, vy, vz);
        }
        return true;
    }
}
