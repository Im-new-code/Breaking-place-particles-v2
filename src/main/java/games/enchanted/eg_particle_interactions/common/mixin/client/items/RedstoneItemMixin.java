package games.enchanted.eg_particle_interactions.common.mixin.client.items;

import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.RedstoneItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneItem.class)
public abstract class RedstoneItemMixin {
    @Inject(method = "useOnBlock", at = @At("TAIL"))
    private void egpi$redstonePlacement(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() == null || !cir.getReturnValue().isAccepted()) return;
        World world = context.getWorld();
        if (!world.isClient) return;
        BlockPos clicked = context.getBlockPos();
        BlockPos[] candidates = {clicked.offset(context.getSide()), clicked.up(), clicked};
        for (BlockPos candidate : candidates) {
            if (world.getBlockState(candidate).isOf(Blocks.REDSTONE_WIRE)) {
                ParticleSpawner.placed(world, candidate, world.getBlockState(candidate));
                return;
            }
        }
    }
}
