package com.example.breakingplace.mixin;

import com.example.breakingplace.particle.ParticleSpawnHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Spawns break-style particles at the position a block was actually placed at.
 * <p>
 * This is the ONLY hook in the mod that reacts to placement. The previous
 * implementation had two separate hooks - one on {@code BlockItem#useOnBlock}
 * and one on a client world block-update method - which could both fire for the
 * same placement and spawn the particle effect twice.
 * <p>
 * It hooks {@link BlockItem#place(ItemPlacementContext)} rather than
 * {@code useOnBlock}. By the time {@code place} runs, the given
 * {@link ItemPlacementContext} has already resolved the correct target
 * position for us: its constructor decides internally whether to place
 * directly at the targeted block (for replaceable blocks such as tall grass,
 * snow layers, water, or scaffolding you're climbing through) or offset onto
 * the clicked face (for solid, non-replaceable blocks). Reading
 * {@code context.getBlockPos()} here always returns that already-correct
 * position.
 * <p>
 * The previous implementation instead always did
 * {@code context.getBlockPos().offset(context.getSide())} by hand - which
 * re-applies an offset that {@code ItemPlacementContext} may have already
 * decided NOT to apply, producing particles one block away from where the
 * block actually landed whenever the target was replaceable (grass, snow,
 * scaffolding, etc.).
 */
@Mixin(BlockItem.class)
public abstract class BlockPlaceParticleMixin {

    @Inject(method = "place", at = @At("RETURN"))
    private void breakingplaceparticles$spawnPlaceParticles(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        ActionResult result = cir.getReturnValue();
        if (result == null || !result.isAccepted()) {
            return;
        }

        World world = context.getWorld();
        if (!world.isClient()) {
            return;
        }

        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        ParticleSpawnHelper.spawnPlacementParticles(MinecraftClient.getInstance(), pos, state);
    }
}
