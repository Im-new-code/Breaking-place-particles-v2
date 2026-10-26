package games.enchanted.eg_particle_interactions.fabric;
import games.enchanted.eg_particle_interactions.common.particle.PlacementParticleTypes;
import net.fabricmc.api.ModInitializer;
public final class FabricCommonEntry implements ModInitializer {
    @Override public void onInitialize() { PlacementParticleTypes.registerTypes(); }
}
