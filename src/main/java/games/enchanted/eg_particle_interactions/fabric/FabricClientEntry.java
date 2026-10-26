package games.enchanted.eg_particle_interactions.fabric;

import games.enchanted.eg_particle_interactions.common.config.ConfigOptions;
import games.enchanted.eg_particle_interactions.common.ambient.AmbientParticleTicker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class FabricClientEntry implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigOptions.load();
        ClientTickEvents.END_CLIENT_TICK.register(AmbientParticleTicker::tick);
    }
}
