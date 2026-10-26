package games.enchanted.eg_particle_interactions.common.mixin.client.particles;

import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {
    @Inject(method = "addBlockBreakParticles", at = @At("TAIL"))
    private void egpi$afterBlockBreak(BlockPos pos, BlockState state, CallbackInfo ci) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) ParticleSpawner.broken(world, pos, state);
    }
}
