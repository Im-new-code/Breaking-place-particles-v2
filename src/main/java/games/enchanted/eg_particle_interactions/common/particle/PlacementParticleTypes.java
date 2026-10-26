package games.enchanted.eg_particle_interactions.common.particle;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
public final class PlacementParticleTypes {
    private static final String MOD_ID = "eg_particle_interactions";
    public static ParticleType<BlockStateParticleEffect> SNOWFLAKE, LEAF, PETAL, GRASS_BLADE;
    public static ParticleType<BlockStateParticleEffect> HEAVY_GRASS_BLADE, SUGAR_CANE, CHAIN_SNAP;
    public static ParticleType<BlockStateParticleEffect> MOSS_CLUMP, TINTED_DUST, REDSTONE_DUST, BLOCK_SHATTER;
    private static boolean typesRegistered;
    private static boolean factoriesRegistered;
    private PlacementParticleTypes() {}
    public static synchronized void registerTypes() {
        if (typesRegistered) return;
        SNOWFLAKE = register("placement_snowflake");
        LEAF = register("placement_leaf");
        PETAL = register("placement_petal");
        GRASS_BLADE = register("placement_grass_blade");
        HEAVY_GRASS_BLADE = register("placement_heavy_grass_blade");
        SUGAR_CANE = register("placement_sugar_cane");
        CHAIN_SNAP = register("placement_chain_snap");
        MOSS_CLUMP = register("placement_moss_clump");
        TINTED_DUST = register("placement_tinted_dust");
        REDSTONE_DUST = register("placement_redstone_dust");
        BLOCK_SHATTER = register("placement_block_shatter");
        typesRegistered = true;
    }
    private static ParticleType<BlockStateParticleEffect> register(String path) {
        ParticleType<BlockStateParticleEffect> type = new ParticleType<BlockStateParticleEffect>(false, BlockStateParticleEffect.PARAMETERS_FACTORY) {
            @Override public Codec<BlockStateParticleEffect> getCodec() {
                return BlockStateParticleEffect.method_29128(this);
            }
        };
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, path), type);
    }
    public static synchronized void registerFactories() {
        if (factoriesRegistered) return;
        if (!typesRegistered) throw new IllegalStateException("Particle types must be registered before client factories");
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(SNOWFLAKE, factory(PlacementSpriteParticle.Kind.SNOWFLAKE));
        registry.register(LEAF, factory(PlacementSpriteParticle.Kind.LEAF));
        registry.register(PETAL, factory(PlacementSpriteParticle.Kind.PETAL));
        registry.register(GRASS_BLADE, factory(PlacementSpriteParticle.Kind.GRASS_BLADE));
        registry.register(HEAVY_GRASS_BLADE, factory(PlacementSpriteParticle.Kind.HEAVY_GRASS_BLADE));
        registry.register(SUGAR_CANE, factory(PlacementSpriteParticle.Kind.SUGAR_CANE));
        registry.register(CHAIN_SNAP, factory(PlacementSpriteParticle.Kind.CHAIN_SNAP));
        registry.register(MOSS_CLUMP, factory(PlacementSpriteParticle.Kind.MOSS_CLUMP));
        registry.register(TINTED_DUST, factory(PlacementSpriteParticle.Kind.TINTED_DUST));
        registry.register(REDSTONE_DUST, factory(PlacementSpriteParticle.Kind.REDSTONE_DUST));
        registry.register(BLOCK_SHATTER, factory(PlacementSpriteParticle.Kind.BLOCK_SHATTER));
        factoriesRegistered = true;
    }
    private static ParticleFactoryRegistry.PendingParticleFactory<BlockStateParticleEffect> factory(PlacementSpriteParticle.Kind kind) {
        return sprites -> (effect, world, x, y, z, velocityX, velocityY, velocityZ) ->
            new PlacementSpriteParticle(world, x, y, z, velocityX, velocityY, velocityZ, effect.getBlockState(), sprites, kind);
    }
}
