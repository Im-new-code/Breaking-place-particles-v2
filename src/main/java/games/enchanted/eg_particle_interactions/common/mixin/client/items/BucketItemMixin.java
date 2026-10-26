package games.enchanted.eg_particle_interactions.common.mixin.client.items;

import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
    @Inject(
        method = "placeFluid(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/hit/BlockHitResult;)Z",
        at = @At("RETURN")
    )
    private void egpi$fluidPlacement(PlayerEntity player, World world, BlockPos pos, BlockHitResult hit,
                                     CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || world == null || !world.isClient) return;
        BlockState placed = world.getBlockState(pos);
        ParticleSpawner.fluidPlaced(world, pos, placed);
    }
}
