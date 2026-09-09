package com.example.breakingplace;

import com.example.breakingplace.ambient.AmbientEffectsTicker;
import com.example.breakingplace.particle.custom.EmberParticle;
import com.example.breakingplace.particle.custom.FireflyParticle;
import com.example.breakingplace.particle.custom.GrassBladeParticle;
import com.example.breakingplace.particle.custom.LeafParticle;
import com.example.breakingplace.particle.custom.ModParticleTypes;
import com.example.breakingplace.particle.custom.MossParticle;
import com.example.breakingplace.particle.custom.PetalParticle;
import com.example.breakingplace.particle.custom.SparkParticle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

/**
 * This mod is client-only ({@code "environment": "client"} in fabric.mod.json),
 * so everything - including particle *type* registration, which for a mod
 * that talks to a server would normally happen in a common {@code ModInitializer}
 * - happens here in one place. There is nothing server-visible about any of
 * this, so there's no reason to split it across two entrypoints.
 */
public class ModParticlesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModParticleTypes.register();

        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();

        // Leaves - one factory per species, each baking in its own tint.
        registry.register(ModParticleTypes.LEAF_OAK, sprite -> new LeafParticle.Factory(sprite, 0.42F, 0.63F, 0.20F));
        registry.register(ModParticleTypes.LEAF_SPRUCE, sprite -> new LeafParticle.Factory(sprite, 0.31F, 0.45F, 0.32F));
        registry.register(ModParticleTypes.LEAF_BIRCH, sprite -> new LeafParticle.Factory(sprite, 0.55F, 0.66F, 0.23F));
        registry.register(ModParticleTypes.LEAF_JUNGLE, sprite -> new LeafParticle.Factory(sprite, 0.36F, 0.62F, 0.18F));
        registry.register(ModParticleTypes.LEAF_ACACIA, sprite -> new LeafParticle.Factory(sprite, 0.55F, 0.58F, 0.18F));
        registry.register(ModParticleTypes.LEAF_DARK_OAK, sprite -> new LeafParticle.Factory(sprite, 0.28F, 0.38F, 0.14F));

        // Petals - grouped representative colors.
        registry.register(ModParticleTypes.PETAL_YELLOW, sprite -> new PetalParticle.Factory(sprite, 1.0F, 0.85F, 0.15F));
        registry.register(ModParticleTypes.PETAL_RED, sprite -> new PetalParticle.Factory(sprite, 0.85F, 0.15F, 0.15F));
        registry.register(ModParticleTypes.PETAL_WHITE, sprite -> new PetalParticle.Factory(sprite, 0.92F, 0.92F, 0.88F));
        registry.register(ModParticleTypes.PETAL_BLUE, sprite -> new PetalParticle.Factory(sprite, 0.25F, 0.45F, 0.85F));
        registry.register(ModParticleTypes.PETAL_PURPLE_PINK, sprite -> new PetalParticle.Factory(sprite, 0.75F, 0.35F, 0.65F));

        registry.register(ModParticleTypes.GRASS_BLADE, GrassBladeParticle.Factory::new);
        registry.register(ModParticleTypes.MOSS_CLUMP, MossParticle.Factory::new);
        registry.register(ModParticleTypes.FIREFLY, FireflyParticle.Factory::new);
        registry.register(ModParticleTypes.SPARK, SparkParticle.Factory::new);
        registry.register(ModParticleTypes.EMBER, EmberParticle.Factory::new);

        AmbientEffectsTicker.register();
    }
}
