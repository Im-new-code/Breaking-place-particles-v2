package games.enchanted.eg_particle_interactions.common.mixin.client.items;
import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("RETURN"))
    private void egpi$place(ItemPlacementContext context, BlockState requestedState, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();
            BlockState placedState = world.getBlockState(pos);
            if (placedState.isAir()) placedState = requestedState;
            ParticleSpawner.placed(world, pos, placedState);
        }
    }
}
