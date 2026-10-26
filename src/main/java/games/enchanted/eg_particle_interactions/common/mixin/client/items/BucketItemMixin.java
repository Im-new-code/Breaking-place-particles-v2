package games.enchanted.eg_particle_interactions.common.mixin.client.items;

import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
    @Inject(method = "use", at = @At("TAIL"))
    private void egpi$afterBucketUse(World world, PlayerEntity user, Hand hand,
                                    CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        TypedActionResult<ItemStack> result = cir.getReturnValue();
        if (result != null && result.getResult().isAccepted()) {
            BlockPos pos = user.getBlockPos();
            ParticleSpawner.tool(world, pos, world.getBlockState(pos), ParticleSpawner.ToolAction.BUCKET);
        }
    }
}
