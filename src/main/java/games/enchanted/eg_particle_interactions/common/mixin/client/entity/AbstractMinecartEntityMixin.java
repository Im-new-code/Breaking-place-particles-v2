package games.enchanted.eg_particle_interactions.common.mixin.client.entity;

import games.enchanted.eg_particle_interactions.common.config.ConfigOptions;
import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void egpi$minecartWheelSparks(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        World world = entity.getWorld();
        if (!ConfigOptions.get().enabled || !ConfigOptions.get().entityEffects || !world.isClient) return;
        Vec3d velocity = entity.getVelocity();
        if (velocity.x * velocity.x + velocity.z * velocity.z > 0.025D && world.random.nextInt(8) == 0) {
            ParticleSpawner.spark(world, entity.getX() - velocity.x * 0.4D,
                    entity.getY() + 0.08D, entity.getZ() - velocity.z * 0.4D);
        }
    }
}
