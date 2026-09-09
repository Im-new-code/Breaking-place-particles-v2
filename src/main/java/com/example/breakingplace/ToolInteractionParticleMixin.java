package com.example.breakingplace.mixin;

import com.example.breakingplace.config.ModConfig;
import com.example.breakingplace.particle.ParticleSpawnHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds particle feedback for tool interactions that change a block in place,
 * which vanilla 1.17.1 gives no particle effect for at all:
 * <ul>
 *     <li>Hoe: tilling dirt/grass into farmland</li>
 *     <li>Axe: stripping logs, and scraping oxidation/wax off copper blocks</li>
 *     <li>Shovel: flattening dirt/grass into a path</li>
 * </ul>
 * {@link AxeItem}, {@link HoeItem}, and {@link ShovelItem} all override
 * {@code useOnBlock(ItemUsageContext)} with the same signature (inherited from
 * {@code Item}), so one mixin can safely target all three instead of writing
 * three near-identical classes.
 * <p>
 * The block state immediately before the interaction is captured at the method
 * head and compared against the state afterwards. Particles are only spawned
 * when the interaction actually changed the block to a different {@code Block}
 * (log -> stripped log, dirt/grass -> farmland, grass block -> dirt path,
 * oxidized copper -> less-oxidized/waxed copper). This deliberately does NOT
 * hardcode which blocks transform into which, so it keeps working if other
 * mods add their own strippable/tillable/scrapeable blocks.
 * <p>
 * It also deliberately does NOT fire for interactions that only change a
 * property on the SAME block (for example a shovel dousing a lit campfire,
 * which only flips the {@code lit} property) - vanilla already spawns smoke
 * particles for that case, and firing here too would create an unwanted
 * duplicate effect layered on top of it.
 */
@Mixin({AxeItem.class, HoeItem.class, ShovelItem.class})
public abstract class ToolInteractionParticleMixin {

    @Unique
    private BlockState breakingplaceparticles$oldState;

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    private void breakingplaceparticles$captureOldState(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        breakingplaceparticles$oldState = context.getWorld().getBlockState(context.getBlockPos());
    }

    @Inject(method = "useOnBlock", at = @At("RETURN"))
    private void breakingplaceparticles$spawnTransformParticles(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        BlockState oldState = breakingplaceparticles$oldState;
        breakingplaceparticles$oldState = null;

        ActionResult result = cir.getReturnValue();
        if (result == null || !result.isAccepted() || oldState == null) {
            return;
        }
        if (!ModConfig.get().toolInteractionParticlesEnabled) {
            return;
        }

        World world = context.getWorld();
        if (!world.isClient()) {
            return;
        }

        BlockPos pos = context.getBlockPos();
        BlockState newState = world.getBlockState(pos);

        if (newState.getBlock() == oldState.getBlock()) {
            return; // same block, e.g. a property-only change - not a transformation we care about
        }

        ParticleSpawnHelper.spawnToolInteractionParticles(MinecraftClient.getInstance(), pos, newState);
    }
}
