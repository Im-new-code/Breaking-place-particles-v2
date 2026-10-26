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
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public final class ParticleSpawner {
    public enum ToolAction { TILL, STRIP, FLATTEN, FIRE, BUCKET }

    private ParticleSpawner() {}

    public static void placed(World world, BlockPos pos, BlockState placedState) {
        placed(world, pos, placedState, false);
    }

    /** Spawns edge particles for a successful placement using the state passed to BlockItem.place. */
    public static void placed(World world, BlockPos pos, BlockState placedState, boolean waterAtTargetBeforePlacement) {
        if (world == null || !world.isClient || placedState == null || placedState.isAir()) return;
        ConfigOptions.Settings config = ConfigOptions.get();
        if (!config.enabled || !config.blockPlacement) return;

        // Liquids have an empty outline shape, so handle them before shape traversal.
        if (placedState.getMaterial().isLiquid()) {
            if (config.fluidEffects) spawnFluidPlacementEffects(world, pos, placedState);
            return;
        }

        VoxelShape shape = placedState.getOutlineShape(world, pos, ShapeContext.absent());
        if (shape == null || shape.isEmpty()) {
            // Redstone wire can expose a degenerate outline on some states and
            // renderers. Still emit its custom sprite instead of silently
            // dropping the effect when the edge sampler has no geometry.
            if (isRedstone(placedState) && config.redstoneEffects) {
                for (int i = 0; i < count(config, 4); i++) {
                    double x = pos.getX() + 0.18 + randomParticle(world) * 0.64;
                    double z = pos.getZ() + 0.18 + randomParticle(world) * 0.64;
                    double y = pos.getY() + 0.025 + randomParticle(world) * 0.035;
                    double vx = (randomParticle(world) - 0.5) * 0.045;
                    double vz = (randomParticle(world) - 0.5) * 0.045;
                    spawnAt(world,
                            new BlockStateParticleEffect(PlacementParticleTypes.TINTED_DUST, placedState),
                            x, y, z, vx, 0.015 + randomParticle(world) * 0.025, vz);
                }
            }
            return;
        }

        Random random = world.random;

        int density = placementDensity(placedState);
        if (config.performanceMode) density = Math.min(density, 2);
        final int particlesPerEdge = MathHelper.clamp(density, 1, 4);
        final BlockState stateForParticles = placedState;
        double velocityMultiplier = placementVelocity(placedState);

        shape.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
            double dx = x2 - x1;
            double dy = y2 - y1;
            double dz = z2 - z1;
            double edgeLength = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
            if (edgeLength < 0.0001) return;

            int amount = Math.max(1, MathHelper.ceil(edgeLength * particlesPerEdge));
            for (int i = 0; i < amount; i++) {
                double t = (i + 0.5) / amount;
                double px = x1 + dx * t;
                double py = y1 + dy * t;
                double pz = z1 + dz * t;
                ParticleEffect effect = placementEffect(world, pos, stateForParticles, random, config);
                if (effect == null) continue;

                // Match upstream Particle Interactions: velocity points outward
                // from the block centre, with no extra upward impulse.
                double vx = (px - 0.5) * velocityMultiplier;
                double vy = (py - 0.5) * velocityMultiplier;
                double vz = (pz - 0.5) * velocityMultiplier;
                spawnAt(world, effect, pos.getX() + px, pos.getY() + py, pos.getZ() + pz, vx, vy, vz);
            }
        });

        // Only bubble if this exact target was underwater before placement or remains waterlogged.
        if (config.fluidEffects &&
                (waterAtTargetBeforePlacement || world.getFluidState(pos).isIn(FluidTags.WATER))) {
            spawnUnderwaterBubbles(world, pos, waterAtTargetBeforePlacement || world.getFluidState(pos).isIn(FluidTags.WATER));
        }
    }

    public static void fluidPlaced(World world, BlockPos pos, BlockState state) {
        if (world == null || !world.isClient || state == null || !state.getMaterial().isLiquid()) return;
        ConfigOptions.Settings config = ConfigOptions.get();
        if (config.enabled && config.blockPlacement && config.fluidEffects) {
            spawnFluidPlacementEffects(world, pos, state);
        }
    }

    /** No synthetic block-break particles: breaking stays vanilla in this placement-focused pass. */
    public static void broken(ClientWorld world, BlockPos pos, BlockState state) {
        // Intentionally empty. Keep breaking separate from the placement particle pipeline.
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
                case TILL:
                case STRIP:
                case FLATTEN:
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
        spawnAt(world, ParticleTypes.ELECTRIC_SPARK,
                pos.getX() + 0.25 + random.nextDouble() * 0.5, pos.getY() + 0.25,
                pos.getZ() + 0.25 + random.nextDouble() * 0.5, 0, 0.02, 0);
    }

    public static void furnace(World world, BlockPos pos) {
        if (!enabled(world, true)) return;
        Random random = world.random;
        double x = pos.getX() + 0.3 + random.nextDouble() * 0.4;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
        spawnAt(world, ParticleTypes.SMALL_FLAME, x, y, z, 0, 0.01, 0);
        if (random.nextBoolean()) spawnAt(world, ParticleTypes.SMOKE, x, y + 0.1, z, 0, 0.02, 0);
    }

    public static void redstone(World world, BlockPos pos) {
        if (!enabled(world, false)) return;
        Random random = world.random;
        spawnAt(world, ParticleTypes.ELECTRIC_SPARK,
                pos.getX() + 0.2 + random.nextDouble() * 0.6, pos.getY() + 0.04,
                pos.getZ() + 0.2 + random.nextDouble() * 0.6, 0, 0.01, 0);
    }

    public static void spark(World world, double x, double y, double z) {
        if (!enabled(world, false)) return;
        Random random = world.random;
        spawnAt(world, ParticleTypes.ELECTRIC_SPARK, x, y, z,
                (random.nextDouble() - 0.5) * 0.04, 0.01, (random.nextDouble() - 0.5) * 0.04);
    }

    public static void fallingImpact(World world, BlockPos pos, BlockState state) {
        if (!enabled(world, false) || state == null || state.isAir()) return;
        Random random = world.random;
        for (int i = 0; i < count(ConfigOptions.get(), 3); i++) {
            spawnAt(world, new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                    pos.getX() + random.nextDouble(), pos.getY() + 0.05, pos.getZ() + random.nextDouble(),
                    (random.nextDouble() - 0.5) * 0.06, 0.02, (random.nextDouble() - 0.5) * 0.06);
        }
    }

    public static void itemFrame(World world, double x, double y, double z) {
        if (!enabled(world, false)) return;
        Random random = world.random;
        spawnAt(world,
                new BlockStateParticleEffect(PlacementParticleTypes.TINTED_DUST, Blocks.SAND.getDefaultState()),
                x, y, z,
                (random.nextDouble() - 0.5) * 0.018, 0.005 + random.nextDouble() * 0.01,
                (random.nextDouble() - 0.5) * 0.018);
    }

    private static boolean enabled(World world, boolean fire) {
        if (world == null || !world.isClient) return false;
        ConfigOptions.Settings config = ConfigOptions.get();
        return config.enabled && (fire ? config.fireAndFurnaces : config.entityEffects);
    }

    public static void spawnAt(World world, ParticleEffect particle, double x, double y, double z,
                               double vx, double vy, double vz) {
        if (world != null && world.isClient && particle != null) {
            world.addParticle(particle, x, y, z, vx, vy, vz);
        }
    }

    public static ParticleEffect visualFor(BlockState state) {
        return state == null || state.isAir() ? ParticleTypes.SMOKE : basePlacementEffect(state, ConfigOptions.get());
    }

    private static ParticleEffect placementEffect(World world, BlockPos pos, BlockState state,
                                                  Random random, ConfigOptions.Settings config) {
        boolean snowBlock = state.isOf(Blocks.SNOW) || state.isOf(Blocks.SNOW_BLOCK) || state.isOf(Blocks.POWDER_SNOW);
        if (snowBlock && config.snowflakeSteamInWarmPlaces && isWarmBiome(world, pos) && random.nextInt(5) == 0) {
            return ParticleTypes.POOF;
        }

        boolean grassLike = isGrassBlade(state);
        boolean spawnFirefly = grassLike && config.swampFireflies
                && world.getLightLevel(pos) <= 13
                && world.getBiome(pos).getCategory() == Biome.Category.SWAMP
                && random.nextFloat() > 0.9F;
        if (spawnFirefly) {
            // Minecraft 1.17.1 has no native FIREFLY particle type, so use a small End Rod as the visual.
            return ParticleTypes.END_ROD;
        }
        return basePlacementEffect(state, config);
    }

    private static boolean isWarmBiome(World world, BlockPos pos) {
        Biome.Category category = world.getBiome(pos).getCategory();
        return category == Biome.Category.DESERT
                || category == Biome.Category.JUNGLE
                || category == Biome.Category.SAVANNA
                || category == Biome.Category.MESA
                || category == Biome.Category.NETHER;
    }

    private static ParticleEffect basePlacementEffect(BlockState state, ConfigOptions.Settings config) {
        if (state.isOf(Blocks.SNOW) || state.isOf(Blocks.SNOW_BLOCK) || state.isOf(Blocks.POWDER_SNOW)) {
            return new BlockStateParticleEffect(PlacementParticleTypes.SNOWFLAKE, state);
        }
        if (state.isOf(Blocks.AZALEA) || state.isOf(Blocks.FLOWERING_AZALEA) || state.isIn(BlockTags.LEAVES)) {
            return new BlockStateParticleEffect(PlacementParticleTypes.LEAF, state);
        }
        if (isRedstone(state)) {
            return config.redstoneEffects
                    ? new BlockStateParticleEffect(PlacementParticleTypes.TINTED_DUST, state)
                    : new BlockStateParticleEffect(ParticleTypes.BLOCK, state);
        }
        if (isChain(state)) return new BlockStateParticleEffect(PlacementParticleTypes.CHAIN_SNAP, state);
        if (isHeavyGrassBlade(state)) return new BlockStateParticleEffect(PlacementParticleTypes.HEAVY_GRASS_BLADE, state);
        if (isSugarCane(state)) return new BlockStateParticleEffect(PlacementParticleTypes.SUGAR_CANE, state);
        if (isFlowerPetal(state)) return new BlockStateParticleEffect(PlacementParticleTypes.PETAL, state);
        if (isGrassBlade(state)) return new BlockStateParticleEffect(PlacementParticleTypes.GRASS_BLADE, state);
        if (state.isOf(Blocks.MOSS_BLOCK) || state.isOf(Blocks.MOSS_CARPET)) {
            return new BlockStateParticleEffect(PlacementParticleTypes.MOSS_CLUMP, state);
        }
        if (state.isOf(Blocks.NETHER_PORTAL)) {
            return new BlockStateParticleEffect(PlacementParticleTypes.BLOCK_SHATTER, state);
        }
        String path = Registry.BLOCK.getId(state.getBlock()).getPath();
        if (isDust(path)) return new BlockStateParticleEffect(PlacementParticleTypes.TINTED_DUST, state);
        return new BlockStateParticleEffect(ParticleTypes.BLOCK, state);
    }

    private static void spawnUnderwaterBubbles(World world, BlockPos pos, boolean alreadyUnderwater) {
        Random random = world.random;
        int amount = alreadyUnderwater ? 8 : 4;
        for (int i = 0; i < amount; i++) {
            double x = pos.getX() + 0.15 + random.nextDouble() * 0.7;
            double y = pos.getY() + 0.10 + random.nextDouble() * 0.7;
            double z = pos.getZ() + 0.15 + random.nextDouble() * 0.7;
            spawnAt(world, ParticleTypes.BUBBLE, x, y, z,
                    (random.nextDouble() - 0.5) * 0.04, 0.02 + random.nextDouble() * 0.025,
                    (random.nextDouble() - 0.5) * 0.04);
        }
    }

    /**
     * Match Particle Interactions' Redstone Dust override: redstone wire/dust,
     * redstone blocks, comparators, repeaters, and redstone torches share the
     * dedicated redstone_dust sprite rather than the vanilla block texture.
     */
    private static boolean isRedstone(BlockState state) {
        return state.isOf(Blocks.REDSTONE_BLOCK) || state.isOf(Blocks.REDSTONE_WIRE)
                || state.isOf(Blocks.REPEATER) || state.isOf(Blocks.COMPARATOR)
                || state.isOf(Blocks.REDSTONE_TORCH) || state.isOf(Blocks.REDSTONE_WALL_TORCH);
    }

    private static boolean isChain(BlockState state) { return state.isOf(Blocks.CHAIN); }

    private static boolean isHeavyGrassBlade(BlockState state) {
        return state.isOf(Blocks.COBWEB) || state.isOf(Blocks.TRIPWIRE) || state.isOf(Blocks.HANGING_ROOTS);
    }

    private static boolean isSugarCane(BlockState state) { return state.isOf(Blocks.SUGAR_CANE) || state.isOf(Blocks.BAMBOO); }

    private static boolean isFlowerPetal(BlockState state) {
        return state.isIn(BlockTags.FLOWERS)
                || state.isOf(Blocks.TWISTING_VINES) || state.isOf(Blocks.TWISTING_VINES_PLANT)
                || state.isOf(Blocks.WEEPING_VINES) || state.isOf(Blocks.WEEPING_VINES_PLANT)
                || state.isOf(Blocks.CAVE_VINES) || state.isOf(Blocks.CAVE_VINES_PLANT);
    }

    /**
     * Match Particle Interactions' Grass Blade override, including grass blocks
     * and dirt paths. Item placements use this same state-based selector, so
     * the inventory item and the resulting placed block get the same sprite.
     */
    private static boolean isGrassBlade(BlockState state) {
        return state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.DIRT_PATH)
                || state.isOf(Blocks.GRASS) || state.isOf(Blocks.TALL_GRASS)
                || state.isOf(Blocks.FERN) || state.isOf(Blocks.LARGE_FERN)
                || state.isOf(Blocks.VINE) || state.isOf(Blocks.SWEET_BERRY_BUSH) || state.isOf(Blocks.DEAD_BUSH)
                || state.isOf(Blocks.SEAGRASS) || state.isOf(Blocks.TALL_SEAGRASS) || state.isOf(Blocks.GLOW_LICHEN)
                || state.isOf(Blocks.WHEAT) || state.isOf(Blocks.CARROTS) || state.isOf(Blocks.POTATOES)
                || state.isOf(Blocks.BEETROOTS) || state.isOf(Blocks.MELON_STEM) || state.isOf(Blocks.PUMPKIN_STEM);
    }

    private static boolean isDust(String path) {
        return path.equals("sand") || path.equals("red_sand") || path.equals("gravel")
                || path.equals("soul_sand") || path.equals("soul_soil") || path.endsWith("_concrete_powder");
    }

    private static int placementDensity(BlockState state) {
        if (isRedstone(state) || state.isOf(Blocks.NETHER_PORTAL) || isFlowerPetal(state)) return 3;
        return 2;
    }

    private static double placementVelocity(BlockState state) {
        if (state.isOf(Blocks.NETHER_PORTAL)) return 0.20;
        if (isChain(state)) return 0.22;
        if (isSugarCane(state)) return 0.19;
        if (isFlowerPetal(state)) return 0.18;
        if (isRedstone(state)) return 0.10;
        if (state.isOf(Blocks.SNOW) || state.isOf(Blocks.SNOW_BLOCK) || state.isOf(Blocks.POWDER_SNOW)) return 0.15;
        if (isGrassBlade(state) || isHeavyGrassBlade(state) || state.isIn(BlockTags.LEAVES)) return 0.13;
        if (isDust(Registry.BLOCK.getId(state.getBlock()).getPath())) return 0.10;
        return 0.13;
    }

    private static void spawnFluidPlacementEffects(World world, BlockPos pos, BlockState state) {
        Random random = world.random;
        int amount = Math.max(2, count(ConfigOptions.get(), 4));
        boolean water = state.getMaterial() == Material.WATER;
        for (int i = 0; i < amount; i++) {
            double x = pos.getX() + 0.15 + random.nextDouble() * 0.7;
            double y = pos.getY() + 0.15 + random.nextDouble() * 0.7;
            double z = pos.getZ() + 0.15 + random.nextDouble() * 0.7;
            if (water) {
                spawnAt(world, ParticleTypes.BUBBLE, x, y, z,
                        (random.nextDouble() - 0.5) * 0.06, 0.02 + random.nextDouble() * 0.03,
                        (random.nextDouble() - 0.5) * 0.06);
            } else {
                spawnAt(world, ParticleTypes.LAVA, x, y, z,
                        (random.nextDouble() - 0.5) * 0.025, 0.01,
                        (random.nextDouble() - 0.5) * 0.025);
            }
        }
    }

    private static double randomParticle(World world) {
        return world.random.nextDouble();
    }

    private static int count(ConfigOptions.Settings config, int fallback) {
        int amount = config.particlesPerEvent > 0 ? config.particlesPerEvent : fallback;
        return config.performanceMode ? Math.min(3, amount) : Math.min(16, amount);
    }
}
