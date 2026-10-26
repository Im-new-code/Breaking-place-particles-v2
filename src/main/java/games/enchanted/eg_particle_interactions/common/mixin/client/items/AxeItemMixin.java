package games.enchanted.eg_particle_interactions.common.mixin.client.items;

import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin {
    @Inject(method = "useOnBlock", at = @At("TAIL"))
    private void egpi$afterStripOrScrape(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != null && cir.getReturnValue().isAccepted()) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();
            BlockState state = world.getBlockState(pos);
            ParticleSpawner.tool(world, pos, state, ParticleSpawner.ToolAction.STRIP);
        }
    }
}
