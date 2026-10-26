package games.enchanted.eg_particle_interactions.common.mixin.client.entity;

import games.enchanted.eg_particle_interactions.common.config.ConfigOptions;
import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void egpi$fallingBlockImpact(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        World world = entity.getWorld();
        if (ConfigOptions.get().enabled && ConfigOptions.get().entityEffects
                && world.isClient && entity.isOnGround() && world.random.nextInt(4) == 0) {
            BlockPos pos = entity.getBlockPos();
            ParticleSpawner.fallingImpact(world, pos, world.getBlockState(pos));
        }
    }
}
