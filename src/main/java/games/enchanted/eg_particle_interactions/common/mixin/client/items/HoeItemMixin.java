package games.enchanted.eg_particle_interactions.common.mixin.client.items;
import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner; import net.minecraft.item.HoeItem; import net.minecraft.item.ItemUsageContext; import net.minecraft.util.ActionResult; import net.minecraft.util.math.BlockPos; import net.minecraft.world.World; import org.spongepowered.asm.mixin.Mixin; import org.spongepowered.asm.mixin.injection.At; import org.spongepowered.asm.mixin.injection.Inject; import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(HoeItem.class) public abstract class HoeItemMixin {
 @Inject(method="useOnBlock",at=@At("TAIL")) private void egpi$till(ItemUsageContext c,CallbackInfoReturnable<ActionResult> cir){if(cir.getReturnValue()!=null&&cir.getReturnValue().isAccepted()){World w=c.getWorld();BlockPos p=c.getBlockPos();ParticleSpawner.tool(c,w.getBlockState(p),ParticleSpawner.ToolAction.TILL);}}
}
