package games.enchanted.eg_particle_interactions.common.mixin.client.entity;

import games.enchanted.eg_particle_interactions.common.config.ConfigOptions;
import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class BlazeEntityMixin {
    @Inject(method = "tickMovement()V", at = @At("TAIL"))
    private void egpi$blazeSparks(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof BlazeEntity)) return;

        World world = entity.world;
        ConfigOptions.Settings config = ConfigOptions.get();
        if (config.enabled && config.entityEffects && world.isClient && world.random.nextInt(12) == 0) {
            ParticleSpawner.spawnAt(
                world,
                ParticleTypes.SMALL_FLAME,
                entity.getX() + (world.random.nextDouble() - 0.5) * 0.5,
                entity.getY() + 0.4 + world.random.nextDouble() * 0.6,
                entity.getZ() + (world.random.nextDouble() - 0.5) * 0.5,
                0, 0.015, 0
            );
        }
    }
}
