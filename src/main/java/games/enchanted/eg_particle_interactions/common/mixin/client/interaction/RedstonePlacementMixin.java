package games.enchanted.eg_particle_interactions.common.mixin.client.interaction;

import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class RedstonePlacementMixin {
    @Inject(
        method = "interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;",
        at = @At("RETURN")
    )
    private void egpi$redstoneDustPlacement(ClientPlayerEntity player, ClientWorld world, Hand hand,
                                            BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() == null || !cir.getReturnValue().isAccepted()) return;
        ItemStack stack = player.getStackInHand(hand);
        if (!"item.minecraft.redstone".equals(stack.getTranslationKey())) return;

        BlockPos clicked = hit.getBlockPos();
        BlockState clickedState = world.getBlockState(clicked);
        BlockPos target = clickedState.getMaterial().isReplaceable() ? clicked : clicked.offset(hit.getSide());

        // In this Yarn version redstone dust has no named RedstoneItem class to target.
        // Particle placement uses a candidate state only after vanilla reports the interaction accepted.
        BlockState wire = Blocks.REDSTONE_WIRE.getDefaultState();
        BlockState inWorld = world.getBlockState(target);
        if (inWorld.isOf(Blocks.REDSTONE_WIRE)) {
            ParticleSpawner.placed(world, target, inWorld);
        } else if (wire.canPlaceAt(world, target)) {
            ParticleSpawner.placed(world, target, wire);
        } else if (hit.getSide() == Direction.UP && wire.canPlaceAt(world, clicked.up())) {
            ParticleSpawner.placed(world, clicked.up(), wire);
        }
    }
}
