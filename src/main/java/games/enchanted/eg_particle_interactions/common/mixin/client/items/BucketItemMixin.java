package games.enchanted.eg_particle_interactions.common.mixin.client.items;

import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
    @Shadow @Final private Fluid fluid;

    @Inject(
        method = "getEmptiedStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;",
        at = @At("RETURN")
    )
    private void egpi$fluidPlacement(ItemStack stack, PlayerEntity player,
                                     CallbackInfoReturnable<ItemStack> cir) {
        if (fluid == Fluids.EMPTY || player == null || !player.world.isClient) return;

        World world = player.world;
        HitResult hitResult = player.raycast(5.0D, 0.0F, false);
        if (!(hitResult instanceof BlockHitResult)) return;

        BlockHitResult hit = (BlockHitResult) hitResult;
        BlockPos clicked = hit.getBlockPos();
        BlockState clickedState = world.getBlockState(clicked);
        BlockPos fluidPos = clicked;

        if (!clickedState.getMaterial().isLiquid()) {
            BlockPos adjacent = clicked.offset(hit.getSide());
            if (world.getBlockState(adjacent).getMaterial().isLiquid()) {
                fluidPos = adjacent;
            } else if (world.getBlockState(clicked.up()).getMaterial().isLiquid()) {
                fluidPos = clicked.up();
            } else {
                return;
            }
        } else if (clickedState.getMaterial().isLiquid()) {
            // The ray can resolve to the newly placed source if the target was replaceable.
            fluidPos = clicked;
        }

        ParticleSpawner.fluidPlaced(world, fluidPos, world.getBlockState(fluidPos));
    }
}
