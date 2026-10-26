package games.enchanted.eg_particle_interactions.common.particle;
import games.enchanted.eg_particle_interactions.common.config.ConfigOptions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import java.util.Random;
public final class ParticleSpawner {
    public enum ToolAction { TILL, STRIP, FLATTEN, FIRE, BUCKET }
    private ParticleSpawner() {}

    /** Edge-following placement particles, using the state Minecraft is actually placing. */
    public static void placed(World world, BlockPos pos, BlockState state) {
        if (world == null || !world.isClient || state == null || state.isAir()) return;
        ConfigOptions.Settings config = ConfigOptions.get();
        if (!config.enabled || !config.blockPlacement) return;
        VoxelShape shape = state.getOutlineShape(world, pos, ShapeContext.absent());
        if (shape == null || shape.isEmpty()) return;
        Box bounds = shape.getBoundingBox();
        double centerX = (bounds.minX + bounds.maxX) * 0.5;
        double centerY = (bounds.minY + bounds.maxY) * 0.5;
        double centerZ = (bounds.minZ + bounds.maxZ) * 0.5;
        Random random = world.random;
        int density = placementDensity(state);
        if (config.performanceMode) density = Math.min(density, 2);
        density = MathHelper.clamp(density, 1, 4);
        double outwardVelocity = placementVelocity(state);
        final int particlesPerEdge = density;
        shape.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
            double dx = x2 - x1, dy = y2 - y1, dz = z2 - z1;
            double edgeLength = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
            if (edgeLength < 0.0001) return;
            int amount = Math.max(1, MathHelper.ceil(edgeLength * particlesPerEdge));
            for (int i = 0; i < amount; i++) {
                double t = (i + 0.5) / amount;
                double px = x1 + dx * t, py = y1 + dy * t, pz = z1 + dz * t;
                ParticleEffect effect = placementEffect(state, random);
                if (effect == null) continue;
                double vx = (px - centerX) * outwardVelocity + (random.nextDouble() - 0.5) * 0.004;
                double vy = (py - centerY) * outwardVelocity + 0.002 + random.nextDouble() * 0.008;
                double vz = (pz - centerZ) * outwardVelocity + (random.nextDouble() - 0.5) * 0.004;
                spawnAt(world, effect, pos.getX() + px, pos.getY() + py, pos.getZ() + pz, vx, vy, vz);
            }
        });
        if (config.fluidEffects && state.getMaterial().isLiquid()) spawnFluidPlacementEffects(world, pos, state);
    }

    public static void broken(ClientWorld world, BlockPos pos, BlockState state) {
        // Vanilla-only until the full break override pipeline is implemented.
    }

    public static void tool(World world, BlockPos pos, BlockState state, ToolAction action) {
        ConfigOptions.Settings config = ConfigOptions.get();
        if (world == null || !world.isClient || !config.enabled || !config.toolInteractions) return;
        Random random = world.random;
        for (int i = 0; i < count(config, 3); i++) {
            double x = pos.getX() + 0.15 + random.nextDouble() * 0.7;
            double y = pos.getY() + 0.2 + random.nextDouble() * 0.6;
            double z = pos.getZ() + 0.15 + random.nextDouble() * 0.7;
            switch (action) {
                case TILL: case STRIP: case FLATTEN:
                    spawnAt(world, new BlockStateParticleEffect(ParticleTypes.BLOCK, state), x, y, z,
                            (random.nextDouble() - 0.5) * 0.05, 0.02, (random.nextDouble() - 0.5) * 0.05);
                    break;
                case FIRE:
                    spawnAt(world, i % 2 == 0 ? ParticleTypes.ELECTRIC_SPARK : ParticleTypes.SMALL_FLAME,
                            x, y, z, (random.nextDouble() - 0.5) * 0.06, 0.025, (random.nextDouble() - 0.5) * 0.06);
                    break;
                case BUCKET:
                    spawnAt(world, i % 2 == 0 ? ParticleTypes.BUBBLE : ParticleTypes.BUBBLE_POP, x, y, z, 0, 0.02, 0);
                    break;
            }
        }
    }
    public static void campfire(World world, BlockPos pos) {
        if (!enabled(world, true)) return;
        Random random = world.random;
        spawnAt(world, ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.25 + random.nextDouble() * 0.5,
                pos.getY() + 0.25, pos.getZ() + 0.25 + random.nextDouble() * 0.5, 0, 0.02, 0);
    }
    public static void furnace(World world, BlockPos pos) {
        if (!enabled(world, true)) return;
        Random random = world.random;
        double x = pos.getX() + 0.3 + random.nextDouble() * 0.4, y = pos.getY() + 0.5, z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
        spawnAt(world, ParticleTypes.SMALL_FLAME, x, y, z, 0, 0.01, 0);
        if (random.nextBoolean()) spawnAt(world, ParticleTypes.SMOKE, x, y + 0.1, z, 0, 0.02, 0);
    }
    public static void redstone(World world, BlockPos pos) {
        if (!enabled(world, false)) return;
        Random random = world.random;
        spawnAt(world, ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.2 + random.nextDouble() * 0.6, pos.getY() + 0.04,
                pos.getZ() + 0.2 + random.nextDouble() * 0.6, 0, 0.01, 0);
    }
    public static void spark(World world, double x, double y, double z) {
        if (!enabled(world, false)) return;
        Random random = world.random;
        spawnAt(world, ParticleTypes.ELECTRIC_SPARK, x, y, z, (random.nextDouble() - 0.5) * 0.04, 0.01, (random.nextDouble() - 0.5) * 0.04);
    }
    public static void fallingImpact(World world, BlockPos pos, BlockState state) {
        if (!enabled(world, false) || state == null || state.isAir()) return;
        Random random = world.random;
        for (int i = 0; i < count(ConfigOptions.get(), 3); i++) {
            spawnAt(world, new BlockStateParticleEffect(ParticleTypes.BLOCK, state), pos.getX() + random.nextDouble(), pos.getY() + 0.05,
                    pos.getZ() + random.nextDouble(), (random.nextDouble() - 0.5) * 0.06, 0.02, (random.nextDouble() - 0.5) * 0.06);
        }
    }
    public static void itemFrame(World world, double x, double y, double z) {
        if (!enabled(world, false)) return;
        spawnAt(world, ParticleTypes.CLOUD, x, y, z, 0, 0.003, 0);
    }
    private static boolean enabled(World world, boolean fire) {
        if (world == null || !world.isClient) return false;
        ConfigOptions.Settings config = ConfigOptions.get();
        return config.enabled && (fire ? config.fireAndFurnaces : config.entityEffects);
    }
    public static void spawnAt(World world, ParticleEffect particle, double x, double y, double z, double vx, double vy, double vz) {
        if (world != null && world.isClient && particle != null) world.addParticle(particle, x, y, z, vx, vy, vz);
    }
    public static ParticleEffect visualFor(BlockState state) {
        if (state == null || state.isAir()) return ParticleTypes.SMOKE;
        return basePlacementEffect(state);
    }
    private static ParticleEffect placementEffect(BlockState state, Random random) {
        if (state.isOf(Blocks.GRASS_BLOCK) && random.nextFloat() > 0.70F) {
            return new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState());
        }
        return basePlacementEffect(state);
    }
    private static ParticleEffect basePlacementEffect(BlockState state) {
        if (state.isOf(Blocks.SNOW) || state.isOf(Blocks.SNOW_BLOCK)) return new BlockStateParticleEffect(PlacementParticleTypes.SNOWFLAKE, state);
        if (isRedstone(state)) return new BlockStateParticleEffect(PlacementParticleTypes.REDSTONE_DUST, state);
        if (state.isIn(BlockTags.LEAVES)) return new BlockStateParticleEffect(PlacementParticleTypes.LEAF, state);
        if (isGrassBlade(state)) return new BlockStateParticleEffect(PlacementParticleTypes.GRASS_BLADE, state);
        if (state.isIn(BlockTags.FLOWERS)) return new BlockStateParticleEffect(PlacementParticleTypes.PETAL, state);
        if (state.isOf(Blocks.MOSS_BLOCK) || state.isOf(Blocks.MOSS_CARPET)) return new BlockStateParticleEffect(PlacementParticleTypes.MOSS_CLUMP, state);
        String path = Registry.BLOCK.getId(state.getBlock()).getPath();
        if (isDust(path)) return new BlockStateParticleEffect(PlacementParticleTypes.TINTED_DUST, state);
        if (state.isOf(Blocks.NETHER_PORTAL)) return new BlockStateParticleEffect(PlacementParticleTypes.BLOCK_SHATTER, state);
        return new BlockStateParticleEffect(ParticleTypes.BLOCK, state);
    }
    private static boolean isRedstone(BlockState state) {
        return state.isOf(Blocks.REDSTONE_BLOCK) || state.isOf(Blocks.REDSTONE_WIRE) || state.isOf(Blocks.REPEATER)
                || state.isOf(Blocks.COMPARATOR) || state.isOf(Blocks.REDSTONE_TORCH) || state.isOf(Blocks.REDSTONE_WALL_TORCH);
    }
    private static boolean isGrassBlade(BlockState state) {
        return state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.DIRT_PATH) || state.isOf(Blocks.GRASS) || state.isOf(Blocks.TALL_GRASS)
                || state.isOf(Blocks.FERN) || state.isOf(Blocks.LARGE_FERN) || state.isOf(Blocks.VINE) || state.isOf(Blocks.SWEET_BERRY_BUSH)
                || state.isOf(Blocks.DEAD_BUSH) || state.isOf(Blocks.SEAGRASS) || state.isOf(Blocks.TALL_SEAGRASS) || state.isOf(Blocks.GLOW_LICHEN)
                || state.isOf(Blocks.WHEAT) || state.isOf(Blocks.CARROTS) || state.isOf(Blocks.POTATOES) || state.isOf(Blocks.BEETROOTS)
                || state.isOf(Blocks.MELON_STEM) || state.isOf(Blocks.PUMPKIN_STEM);
    }
    private static boolean isDust(String path) {
        return path.equals("sand") || path.equals("red_sand") || path.equals("gravel") || path.equals("soul_sand") || path.equals("soul_soil")
                || path.endsWith("_concrete_powder");
    }
    private static int placementDensity(BlockState state) {
        if (isRedstone(state) || state.isOf(Blocks.NETHER_PORTAL)) return 3;
        if (state.isIn(BlockTags.FLOWERS)) return 3;
        return 2;
    }
    private static double placementVelocity(BlockState state) {
        if (state.isOf(Blocks.NETHER_PORTAL)) return 0.18;
        if (isRedstone(state)) return 0.10;
        if (state.isOf(Blocks.SNOW) || state.isOf(Blocks.SNOW_BLOCK)) return 0.15;
        if (isGrassBlade(state) || state.isIn(BlockTags.LEAVES)) return 0.13;
        if (isDust(Registry.BLOCK.getId(state.getBlock()).getPath())) return 0.10;
        return 0.13;
    }
    private static void spawnFluidPlacementEffects(World world, BlockPos pos, BlockState state) {
        Random random = world.random;
        int amount = Math.max(2, count(ConfigOptions.get(), 4));
        boolean water = state.getMaterial() == Material.WATER;
        for (int i = 0; i < amount; i++) {
            double x = pos.getX() + 0.15 + random.nextDouble() * 0.7, y = pos.getY() + 0.15 + random.nextDouble() * 0.7, z = pos.getZ() + 0.15 + random.nextDouble() * 0.7;
            if (water) spawnAt(world, ParticleTypes.BUBBLE, x, y, z, (random.nextDouble() - 0.5) * 0.06, 0.02 + random.nextDouble() * 0.03, (random.nextDouble() - 0.5) * 0.06);
            else spawnAt(world, ParticleTypes.LAVA, x, y, z, (random.nextDouble() - 0.5) * 0.025, 0.01, (random.nextDouble() - 0.5) * 0.025);
        }
    }
    private static int count(ConfigOptions.Settings config, int fallback) {
        int amount = config.particlesPerEvent > 0 ? config.particlesPerEvent : fallback;
        return config.performanceMode ? Math.min(3, amount) : Math.min(16, amount);
    }
}
