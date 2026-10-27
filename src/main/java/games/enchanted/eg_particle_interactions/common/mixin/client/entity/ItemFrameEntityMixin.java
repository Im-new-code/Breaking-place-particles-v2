package games.enchanted.eg_particle_interactions.common.mixin.client.entity;

import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import games.enchanted.eg_particle_interactions.common.replay.ReplayModCompatibility;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin {
    @Shadow @Final private static TrackedData<ItemStack> ITEM_STACK;
    @Shadow @Final private static TrackedData<Integer> ROTATION;
    @Unique private long egpi$lastDustTick = Long.MIN_VALUE;

    // Live interaction includes both inserting/removing an item and rotating it.
    @Inject(method = "interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            at = @At("TAIL"))
    private void egpi$itemFrameInteract(PlayerEntity player, Hand hand,
                                        CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() == null || !cir.getReturnValue().isAccepted()) return;
        egpi$spawnDust((Entity) (Object) this, false);
    }

    // Replay playback updates tracked data rather than calling interact().
    @Inject(method = "onTrackedDataSet(Lnet/minecraft/entity/data/TrackedData;)V", at = @At("TAIL"))
    private void egpi$replayItemFrameUpdate(TrackedData<?> data, CallbackInfo ci) {
        if (!ReplayModCompatibility.isReplayPlayback()) return;
        if (data != ROTATION && data != ITEM_STACK) return;
        egpi$spawnDust((Entity) (Object) this, true);
    }

    @Unique
    private void egpi$spawnDust(Entity entity, boolean replay) {
        World world = entity.world;
        if (world == null || !world.isClient) return;
        long tick = world.getTime();
        if (this.egpi$lastDustTick == tick) return;

        ItemFrameEntity frame = (ItemFrameEntity) (Object) this;
        ItemStack stack = frame.getHeldItemStack();
        BlockState displayedState = stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem
                ? ((BlockItem) stack.getItem()).getBlock().getDefaultState()
                : Blocks.SAND.getDefaultState();
        BlockPos framePos = BlockPos.ofFloored(entity.getX(), entity.getY(), entity.getZ());

        if (replay) {
            if (ReplayModCompatibility.hasRecentReplayParticle(framePos)) {
                this.egpi$lastDustTick = tick;
                return;
            }
            ParticleSpawner.replayItemFrame(world, entity.getX(), entity.getY() + 0.1, entity.getZ(),
                    displayedState, framePos);
        } else {
            ParticleSpawner.itemFrame(world, entity.getX(), entity.getY() + 0.1, entity.getZ(), displayedState);
        }
        this.egpi$lastDustTick = tick;
    }
}
