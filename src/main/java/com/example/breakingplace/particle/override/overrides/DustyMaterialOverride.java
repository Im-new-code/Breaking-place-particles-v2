package com.example.breakingplace.particle.override.overrides;

import com.example.breakingplace.particle.override.BlockParticleOverride;
import com.example.breakingplace.particle.override.ParticleOrigin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Loose, granular materials keep their own texture (still {@code BLOCK}
 * particles built from the block's actual state, same as vanilla) but use a
 * softer, more numerous "puff of dust" motion instead of vanilla's sharper
 * break-fragment kick - closer to how Particle Interactions treats sand and
 * gravel.
 * <p>
 * This is a real behavioral change (count/velocity), not a texture swap, so
 * unlike {@link SnowOverride}/{@link RedstoneOverride} there's no new visual
 * risk - it still uses the exact texture vanilla would have used anyway.
 */
public class DustyMaterialOverride implements BlockParticleOverride {

    private static final Set<Block> DUSTY_BLOCKS = buildDustyBlockSet();

    private static Set<Block> buildDustyBlockSet() {
        Set<Block> blocks = new HashSet<>(Set.of(
                Blocks.SAND,
                Blocks.RED_SAND,
                Blocks.GRAVEL,
                Blocks.SOUL_SAND,
                Blocks.SOUL_SOIL
        ));
        // All 16 concrete powder colours behave the same way; listing them
        // explicitly (rather than checking a tag that doesn't exist for this
        // category) keeps this override self-contained.
        blocks.add(Blocks.WHITE_CONCRETE_POWDER);
        blocks.add(Blocks.ORANGE_CONCRETE_POWDER);
        blocks.add(Blocks.MAGENTA_CONCRETE_POWDER);
        blocks.add(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
        blocks.add(Blocks.YELLOW_CONCRETE_POWDER);
        blocks.add(Blocks.LIME_CONCRETE_POWDER);
        blocks.add(Blocks.PINK_CONCRETE_POWDER);
        blocks.add(Blocks.GRAY_CONCRETE_POWDER);
        blocks.add(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
        blocks.add(Blocks.CYAN_CONCRETE_POWDER);
        blocks.add(Blocks.PURPLE_CONCRETE_POWDER);
        blocks.add(Blocks.BLUE_CONCRETE_POWDER);
        blocks.add(Blocks.BROWN_CONCRETE_POWDER);
        blocks.add(Blocks.GREEN_CONCRETE_POWDER);
        blocks.add(Blocks.RED_CONCRETE_POWDER);
        blocks.add(Blocks.BLACK_CONCRETE_POWDER);
        return blocks;
    }

    @Override
    public boolean matches(BlockState state) {
        return DUSTY_BLOCKS.contains(state.getBlock());
    }

    @Override
    public boolean spawn(MinecraftClient client, World world, BlockPos pos, BlockState state, ParticleOrigin origin) {
        Random random = world.getRandom();
        int count = origin == ParticleOrigin.BREAK ? 12 : 6;
        BlockStateParticleEffect effect = new BlockStateParticleEffect(ParticleTypes.BLOCK, state);

        for (int i = 0; i < count; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();

            // Soft, low-energy puff rather than a sharp break-fragment kick.
            double vx = (random.nextDouble() - 0.5) * 0.03;
            double vy = origin == ParticleOrigin.BREAK ? 0.01 : 0.015 + random.nextDouble() * 0.01;
            double vz = (random.nextDouble() - 0.5) * 0.03;

            client.particleManager.addParticle(effect, x, y, z, vx, vy, vz);
        }
        return true;
    }
}
