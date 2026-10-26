package games.enchanted.eg_particle_interactions.common.particle;

import games.enchanted.eg_particle_interactions.common.config.ConfigOptions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public final class ParticleSpawner {
    public enum ToolAction { TILL, STRIP, FLATTEN, FIRE, BUCKET }

    private ParticleSpawner() {
    }

    public static void placed(World world, BlockPos pos, BlockState state) {
        ConfigOptions.Settings config = ConfigOptions.get();
        if (!config.enabled || !config.blockPlacement || !world.isClient || state.isAir()) return;
        spawnBlockBurst(world, pos, state, visualFor(state));
    }

    public static void broken(ClientWorld world, BlockPos pos, BlockState state) {
        ConfigOptions.Settings config = ConfigOptions.get();
        if (!config.enabled || !config.blockBreaking || state.isAir()) return;
        spawnBlockBurst(world, pos, state, visualFor(state));
    }

    public static void tool(World world, BlockPos pos, BlockState state, ToolAction action) {
        ConfigOptions.Settings config = ConfigOptions.get();
        if (!config.enabled || !config.toolInteractions || !world.isClient) return;

        Random random = world.random;
        int amount = count(config, 3);
        for (int i = 0; i < amount; i++) {
            double x = pos.getX() + 0.15D + random.nextDouble() * 0.7D;
            double y = pos.getY() + 0.2D + random.nextDouble() * 0.65D;
            double z = pos.getZ() + 0.15D + random.nextDouble() * 0.7D;
            switch (action) {
                case TILL:
                    spawnAt(world, ParticleTypes.COMPOSTER, x, y, z,
                            (random.nextDouble() - 0.5D) * 0.035D, 0.015D + random.nextDouble() * 0.035D,
                            (random.nextDouble() - 0.5D) * 0.035D);
                    if (i < 2 && !state.isAir()) {
                        spawnAt(world, new BlockStateParticleEffect(ParticleTypes.BLOCK, state), x, y, z, 0, 0.025D, 0);
                    }
                    break;
                case STRIP:
                    if (!state.isAir()) {
                        spawnAt(world, new BlockStateParticleEffect(ParticleTypes.BLOCK, state), x, y, z,
                                (random.nextDouble() - 0.5D) * 0.06D, 0.025D, (random.nextDouble() - 0.5D) * 0.06D);
                    }
                    if (i == 0) spawnAt(world, ParticleTypes.CRIT, x, y, z, 0, 0.02D, 0);
                    break;
                case FLATTEN:
                    spawnAt(world, ParticleTypes.CLOUD, x, pos.getY() + 0.08D, z,
                            (random.nextDouble() - 0.5D) * 0.04D, 0.01D, (random.nextDouble() - 0.5D) * 0.04D);
                    break;
                case FIRE:
                    spawnAt(world, i % 2 == 0 ? ParticleTypes.ELECTRIC_SPARK : ParticleTypes.SMALL_FLAME,
                            x, y, z, (random.nextDouble() - 0.5D) * 0.08D,
                            0.02D + random.nextDouble() * 0.04D, (random.nextDouble() - 0.5D) * 0.08D);
                    break;
                case BUCKET:
                    spawnAt(world, i % 2 == 0 ? ParticleTypes.BUBBLE : ParticleTypes.BUBBLE_POP,
                            x, y, z, (random.nextDouble() - 0.5D) * 0.04D, 0.02D, (random.nextDouble() - 0.5D) * 0.04D);
                    break;
                default:
                    break;
            }
        }
    }

    public static void campfire(World world, BlockPos pos) {
        ConfigOptions.Settings config = ConfigOptions.get();
        if (!config.enabled || !config.fireAndFurnaces || !world.isClient) return;
        Random random = world.random;
        spawnAt(world, ParticleTypes.ELECTRIC_SPARK,
                pos.getX() + 0.25D + random.nextDouble() * 0.5D, pos.getY() + 0.25D,
                pos.getZ() + 0.25D + random.nextDouble() * 0.5D,
                (random.nextDouble() - 0.5D) * 0.025D, 0.015D + random.nextDouble() * 0.03D,
                (random.nextDouble() - 0.5D) * 0.025D);
    }

    public static void furnace(World world, BlockPos pos) {
        ConfigOptions.Settings config = ConfigOptions.get();
        if (!config.enabled || !config.fireAndFurnaces || !world.isClient) return;
        Random random = world.random;
        double x = pos.getX() + 0.3D + random.nextDouble() * 0.4D;
        double y = pos.getY() + 0.45D + random.nextDouble() * 0.35D;
        double z = pos.getZ() + 0.3D + random.nextDouble() * 0.4D;
        spawnAt(world, ParticleTypes.SMALL_FLAME, x, y, z, 0, 0.01D, 0);
        if (random.nextBoolean()) spawnAt(world, ParticleTypes.SMOKE, x, y + 0.1D, z, 0, 0.02D, 0);
    }

    public static void redstone(World world, BlockPos pos) {
        ConfigOptions.Settings config = ConfigOptions.get();
        if (!config.enabled || !config.redstoneEffects || !world.isClient) return;
        Random random = world.random;
        spawnAt(world, ParticleTypes.ELECTRIC_SPARK,
                pos.getX() + 0.2D + random.nextDouble() * 0.6D, pos.getY() + 0.04D,
                pos.getZ() + 0.2D + random.nextDouble() * 0.6D,
                (random.nextDouble() - 0.5D) * 0.02D, 0.01D, (random.nextDouble() - 0.5D) * 0.02D);
    }

    public static void spark(World world, double x, double y, double z) {
        ConfigOptions.Settings config = ConfigOptions.get();
        if (!config.enabled || !config.entityEffects || !world.isClient) return;
        Random random = world.random;
        spawnAt(world, ParticleTypes.ELECTRIC_SPARK, x, y, z,
                (random.nextDouble() - 0.5D) * 0.04D, random.nextDouble() * 0.025D, (random.nextDouble() - 0.5D) * 0.04D);
    }

    public static void fallingImpact(World world, BlockPos pos, BlockState state) {
        ConfigOptions.Settings config = ConfigOptions.get();
        if (!config.enabled || !config.entityEffects || !world.isClient || state.isAir()) return;
        Random random = world.random;
        for (int i = 0; i < count(config, 3); i++) {
            spawnAt(world, new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                    pos.getX() + random.nextDouble(), pos.getY() + 0.05D, pos.getZ() + random.nextDouble(),
                    (random.nextDouble() - 0.5D) * 0.08D, 0.02D, (random.nextDouble() - 0.5D) * 0.08D);
        }
        spawnAt(world, ParticleTypes.CLOUD, pos.getX() + 0.5D, pos.getY() + 0.08D, pos.getZ() + 0.5D, 0, 0.01D, 0);
    }

    public static void itemFrame(World world, double x, double y, double z) {
        ConfigOptions.Settings config = ConfigOptions.get();
        if (!config.enabled || !config.entityEffects || !world.isClient) return;
        Random random = world.random;
        spawnAt(world, ParticleTypes.CLOUD, x + (random.nextDouble() - 0.5D) * 0.35D,
                y + (random.nextDouble() - 0.5D) * 0.35D, z + (random.nextDouble() - 0.5D) * 0.08D, 0, 0.003D, 0);
    }

    public static void spawnAt(World world, ParticleEffect effect, double x, double y, double z,
                               double velocityX, double velocityY, double velocityZ) {
        if (world != null && world.isClient && world instanceof ClientWorld) {
            world.addParticle(effect, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    public static ParticleEffect visualFor(BlockState state) {
        if (state.isOf(Blocks.SNOW) || state.isOf(Blocks.SNOW_BLOCK) || state.isOf(Blocks.POWDER_SNOW)) return ParticleTypes.SNOWFLAKE;
        if (state.isIn(BlockTags.LEAVES)) return ParticleTypes.FALLING_SPORE_BLOSSOM;
        if (state.isOf(Blocks.MOSS_BLOCK) || state.isOf(Blocks.MOSS_CARPET) || state.getBlock() instanceof FlowerBlock
                || state.isOf(Blocks.GRASS) || state.isOf(Blocks.TALL_GRASS) || state.isOf(Blocks.FERN) || state.isOf(Blocks.LARGE_FERN)) {
            return ParticleTypes.COMPOSTER;
        }
        if (state.isOf(Blocks.REDSTONE_WIRE) || state.isOf(Blocks.REPEATER) || state.isOf(Blocks.COMPARATOR)) return ParticleTypes.ELECTRIC_SPARK;
        if (state.getMaterial().isLiquid()) return ParticleTypes.BUBBLE;
        return new BlockStateParticleEffect(ParticleTypes.BLOCK, state);
    }

    private static void spawnBlockBurst(World world, BlockPos pos, BlockState state, ParticleEffect effect) {
        Random random = world.random;
        for (int i = 0; i < count(ConfigOptions.get(), 5); i++) {
            double x = pos.getX() + 0.1D + random.nextDouble() * 0.8D;
            double y = pos.getY() + 0.1D + random.nextDouble() * 0.8D;
            double z = pos.getZ() + 0.1D + random.nextDouble() * 0.8D;
            spawnAt(world, effect, x, y, z,
                    (random.nextDouble() - 0.5D) * 0.09D, 0.005D + random.nextDouble() * 0.035D,
                    (random.nextDouble() - 0.5D) * 0.09D);
        }
    }

    private static int count(ConfigOptions.Settings config, int fallback) {
        int amount = config.particlesPerEvent > 0 ? config.particlesPerEvent : fallback;
        amount = Math.max(1, Math.min(16, amount));
        return config.performanceMode ? Math.min(3, amount) : amount;
    }
}
