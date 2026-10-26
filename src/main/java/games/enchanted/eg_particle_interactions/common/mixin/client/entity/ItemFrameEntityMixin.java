package games.enchanted.eg_particle_interactions.common.mixin.client.entity;

import games.enchanted.eg_particle_interactions.common.config.ConfigOptions;
import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin {
    @Inject(method = "setHeldItemStack(Lnet/minecraft/item/ItemStack;Z)V", at = @At("TAIL"))
    private void egpi$itemFrameDust(ItemStack stack, boolean update, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        World world = entity.world;
        ConfigOptions.Settings config = ConfigOptions.get();
        if (config.enabled && config.entityEffects && world.isClient && world.random.nextInt(3) == 0) {
            ParticleSpawner.itemFrame(world, entity.getX(), entity.getY() + 0.1, entity.getZ());
        }
    }
}
