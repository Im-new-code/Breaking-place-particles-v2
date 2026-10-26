package games.enchanted.eg_particle_interactions.common.mixin.client.entity;

import games.enchanted.eg_particle_interactions.common.config.ConfigOptions;
import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlazeEntity.class)
public abstract class BlazeEntityMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void egpi$blazeSparks(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        World world = entity.getWorld();
        if (ConfigOptions.get().enabled && ConfigOptions.get().entityEffects
                && world.isClient && world.random.nextInt(12) == 0) {
            ParticleSpawner.spawnAt(world, ParticleTypes.SMALL_FLAME,
                    entity.getX() + (world.random.nextDouble() - 0.5D) * 0.5D,
                    entity.getY() + 0.4D + world.random.nextDouble() * 0.6D,
                    entity.getZ() + (world.random.nextDouble() - 0.5D) * 0.5D, 0, 0.015D, 0);
        }
    }
}
