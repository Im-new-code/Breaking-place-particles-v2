package games.enchanted.eg_particle_interactions.common.mixin.client.items;

import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Unique private boolean egpi$waterAtTargetBeforePlacement;

    @Inject(
        method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z",
        at = @At("HEAD")
    )
    private void egpi$captureWater(ItemPlacementContext context, BlockState state,
                                   CallbackInfoReturnable<Boolean> cir) {
        this.egpi$waterAtTargetBeforePlacement =
                context.getWorld().getFluidState(context.getBlockPos()).isIn(FluidTags.WATER);
    }

    @Inject(
        method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z",
        at = @At("RETURN")
    )
    private void egpi$place(ItemPlacementContext context, BlockState placedState,
                            CallbackInfoReturnable<Boolean> cir) {
        boolean wasUnderwater = this.egpi$waterAtTargetBeforePlacement;
        this.egpi$waterAtTargetBeforePlacement = false;
        if (!cir.getReturnValueZ()) return;
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();

        // Read the final client-side state after placement. Some block items
        // normalize properties during placement; using the original candidate
        // state can select/tint a particle from the wrong state.
        BlockState actualState = world.getBlockState(pos);
        if (actualState.isAir()) actualState = placedState;
        ParticleSpawner.placed(world, pos, actualState, wasUnderwater);
    }
}
