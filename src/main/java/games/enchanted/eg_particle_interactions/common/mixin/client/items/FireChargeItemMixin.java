package games.enchanted.eg_particle_interactions.common.mixin.client.items;
import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner; import net.minecraft.item.FireChargeItem; import net.minecraft.item.ItemUsageContext; import net.minecraft.util.ActionResult; import org.spongepowered.asm.mixin.Mixin; import org.spongepowered.asm.mixin.injection.At; import org.spongepowered.asm.mixin.injection.Inject; import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(FireChargeItem.class) public abstract class FireChargeItemMixin {
 @Inject(method="useOnBlock",at=@At("TAIL")) private void egpi$fire(ItemUsageContext c,CallbackInfoReturnable<ActionResult> cir){if(cir.getReturnValue()!=null&&cir.getReturnValue().isAccepted())ParticleSpawner.tool(c.getWorld(),c.getBlockPos(),c.getWorld().getBlockState(c.getBlockPos()),ParticleSpawner.ToolAction.FIRE);}
}
