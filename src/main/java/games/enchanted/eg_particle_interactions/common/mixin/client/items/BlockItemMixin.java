package games.enchanted.eg_particle_interactions.common.mixin.client.items;

import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(method = "useOnBlock", at = @At("TAIL"))
    private void egpi$afterPlacement(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() == null || !cir.getReturnValue().isAccepted()) return;
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (!state.isAir()) ParticleSpawner.placed(world, pos, state);
    }
}
