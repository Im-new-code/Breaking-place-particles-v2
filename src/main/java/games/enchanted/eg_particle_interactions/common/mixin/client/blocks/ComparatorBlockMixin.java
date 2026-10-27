package games.enchanted.eg_particle_interactions.common.mixin.client.blocks;

import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin {
    @Inject(method = "onUse", at = @At("TAIL"))
    private void egpi$modeDust(BlockState state, World world, BlockPos pos, PlayerEntity player,
                               Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient && cir.getReturnValue() != null && cir.getReturnValue().isAccepted()) {
            BlockState actual = world.getBlockState(pos);
            ParticleSpawner.redstoneInteraction(world, pos,
                    actual.isOf(Blocks.COMPARATOR) ? actual : state, hit.getPos(), hit.getSide());
        }
    }
}
