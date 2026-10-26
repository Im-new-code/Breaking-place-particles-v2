package games.enchanted.eg_particle_interactions.common.ambient;

import games.enchanted.eg_particle_interactions.common.config.ConfigOptions;
import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public final class AmbientParticleTicker {
    private static int ticks;

    private AmbientParticleTicker() {
    }

    public static void tick(MinecraftClient client) {
        if (++ticks % 10 != 0 || client.world == null || client.player == null) return;
        ConfigOptions.Settings config = ConfigOptions.get();
        if (!config.enabled) return;

        ClientWorld world = client.world;
        Random random = world.random;
        BlockPos playerPos = client.player.getBlockPos();
        long dayTime = world.getTime() % 24000L;
        boolean night = dayTime > 13000L && dayTime < 23000L;

        for (int i = 0; i < 3; i++) {
            BlockPos sample = playerPos.add(random.nextInt(17) - 8, random.nextInt(9) - 4, random.nextInt(17) - 8);
            BlockState state = world.getBlockState(sample);
            if (config.ambientLeaves && state.isIn(BlockTags.LEAVES) && random.nextInt(45) == 0) {
                ParticleSpawner.spawnAt(world, ParticleTypes.COMPOSTER,
                        sample.getX() + random.nextDouble(), sample.getY() + 0.15D + random.nextDouble() * 0.4D,
                        sample.getZ() + random.nextDouble(), (random.nextDouble() - 0.5D) * 0.015D,
                        -0.003D, (random.nextDouble() - 0.5D) * 0.015D);
            }

            if (config.swampFireflies && night && random.nextInt(5) == 0
                    && world.getBiome(sample).getCategory() == Biome.Category.SWAMP) {
                ParticleSpawner.spawnAt(world, ParticleTypes.END_ROD,
                        sample.getX() + random.nextDouble(), sample.getY() + 0.2D + random.nextDouble(),
                        sample.getZ() + random.nextDouble(), 0, 0.004D, 0);
            }

            if (config.fluidEffects && random.nextInt(28) == 0) {
                if (world.getFluidState(sample).isIn(FluidTags.WATER)) {
                    ParticleSpawner.spawnAt(world, ParticleTypes.BUBBLE,
                            sample.getX() + 0.2D + random.nextDouble() * 0.6D, sample.getY() + 0.1D,
                            sample.getZ() + 0.2D + random.nextDouble() * 0.6D, 0, 0.025D, 0);
                } else if (state.isOf(Blocks.LAVA)) {
                    ParticleSpawner.spawnAt(world, ParticleTypes.LAVA,
                            sample.getX() + random.nextDouble(), sample.getY() + 0.95D,
                            sample.getZ() + random.nextDouble(), 0, 0, 0);
                }
            }
        }

        if (config.movementParticles) {
            Vec3d velocity = client.player.getVelocity();
            double speedSquared = velocity.x * velocity.x + velocity.z * velocity.z;
            BlockPos underPlayer = playerPos.down();
            BlockState ground = world.getBlockState(underPlayer);
            if (speedSquared > 0.025D && !ground.isAir() && random.nextInt(5) == 0) {
                ParticleSpawner.spawnAt(world, ParticleSpawner.visualFor(ground), client.player.getX(),
                        client.player.getY() + 0.05D, client.player.getZ(),
                        -velocity.x * 0.15D, 0.01D, -velocity.z * 0.15D);
            }
        }
    }
}
