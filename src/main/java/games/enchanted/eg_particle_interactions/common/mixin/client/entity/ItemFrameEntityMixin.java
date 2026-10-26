package games.enchanted.eg_particle_interactions.common.mixin.client.entity;
import games.enchanted.eg_particle_interactions.common.config.ConfigOptions; import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner; import net.minecraft.entity.Entity; import net.minecraft.entity.decoration.ItemFrameEntity; import net.minecraft.world.World; import org.spongepowered.asm.mixin.Mixin; import org.spongepowered.asm.mixin.injection.At; import org.spongepowered.asm.mixin.injection.Inject; import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ItemFrameEntity.class) public abstract class ItemFrameEntityMixin {
 @Inject(method="tick",at=@At("TAIL")) private void egpi$itemFrameDust(CallbackInfo ci){Entity e=(Entity)(Object)this;World w=e.getWorld();if(ConfigOptions.get().enabled&&ConfigOptions.get().entityEffects&&w.isClient&&w.random.nextInt(180)==0)ParticleSpawner.itemFrame(w,e.getX(),e.getY()+.1,e.getZ());}
}
