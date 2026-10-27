package games.enchanted.eg_particle_interactions.common.mixin.client.replay;

import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import games.enchanted.eg_particle_interactions.common.replay.ReplayModCompatibility;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ReplayBlockUpdateMixin {
    @Unique private BlockPos egpi$singlePos;
    @Unique private BlockState egpi$singleBefore;
    @Unique private boolean egpi$singleReplayUpdate;
    @Unique private Map<BlockPos, BlockState> egpi$deltaBefore = Collections.emptyMap();

    @Inject(method = "onParticle(Lnet/minecraft/network/packet/s2c/play/ParticleS2CPacket;)V", at = @At("HEAD"))
    private void egpi$observeReplayParticle(ParticleS2CPacket packet, CallbackInfo ci) {
        ReplayModCompatibility.noteReplayParticle(packet);
    }

    @Inject(method = "onBlockUpdate(Lnet/minecraft/network/packet/s2c/play/BlockUpdateS2CPacket;)V", at = @At("HEAD"))
    private void egpi$captureSingleBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
        this.egpi$singleReplayUpdate = false;
        this.egpi$singlePos = null;
        this.egpi$singleBefore = null;
        if (!ReplayModCompatibility.isReplayPlayback()) return;
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;
        this.egpi$singlePos = packet.getPos().toImmutable();
        this.egpi$singleBefore = world.getBlockState(this.egpi$singlePos);
        this.egpi$singleReplayUpdate = true;
    }

    @Inject(method = "onBlockUpdate(Lnet/minecraft/network/packet/s2c/play/BlockUpdateS2CPacket;)V", at = @At("TAIL"))
    private void egpi$replaySingleBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
        if (!this.egpi$singleReplayUpdate) return;
        BlockPos pos = this.egpi$singlePos;
        BlockState before = this.egpi$singleBefore;
        this.egpi$singleReplayUpdate = false;
        this.egpi$singlePos = null;
        this.egpi$singleBefore = null;

        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null || pos == null || before == null) return;
        if (ReplayModCompatibility.hasRecentReplayParticle(pos)) return;
        ParticleSpawner.replayBlockChange(world, pos, before, world.getBlockState(pos));
    }

    @Inject(method = "onChunkDeltaUpdate(Lnet/minecraft/network/packet/s2c/play/ChunkDeltaUpdateS2CPacket;)V", at = @At("HEAD"))
    private void egpi$captureChunkDelta(ChunkDeltaUpdateS2CPacket packet, CallbackInfo ci) {
        this.egpi$deltaBefore = Collections.emptyMap();
        if (!ReplayModCompatibility.isReplayPlayback()) return;
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;
        Map<BlockPos, BlockState> before = new HashMap<>();
        packet.visitUpdates((pos, state) -> before.put(pos.toImmutable(), world.getBlockState(pos)));
        this.egpi$deltaBefore = before;
    }

    @Inject(method = "onChunkDeltaUpdate(Lnet/minecraft/network/packet/s2c/play/ChunkDeltaUpdateS2CPacket;)V", at = @At("TAIL"))
    private void egpi$replayChunkDelta(ChunkDeltaUpdateS2CPacket packet, CallbackInfo ci) {
        Map<BlockPos, BlockState> before = this.egpi$deltaBefore;
        this.egpi$deltaBefore = Collections.emptyMap();
        if (before.isEmpty() || !ReplayModCompatibility.isReplayPlayback()) return;
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;
        for (Entry<BlockPos, BlockState> entry : before.entrySet()) {
            BlockPos pos = entry.getKey();
            if (!ReplayModCompatibility.hasRecentReplayParticle(pos)) {
                ParticleSpawner.replayBlockChange(world, pos, entry.getValue(), world.getBlockState(pos));
            }
        }
    }
}
