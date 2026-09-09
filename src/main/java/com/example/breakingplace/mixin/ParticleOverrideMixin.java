package com.example.breakingplace.mixin;

import com.example.breakingplace.particle.ParticleSpawnHelper;
import com.example.breakingplace.particle.override.BlockParticleOverrides;
import com.example.breakingplace.particle.override.ParticleOrigin;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts vanilla's own block-particle entry point so material-specific
 * overrides (snow, redstone, loose materials) apply uniformly whether the
 * call came from:
 * <ul>
 *     <li>this mod's own placement/tool-interaction hooks ({@link ParticleSpawnHelper}), or</li>
 *     <li>vanilla itself, when the player actually breaks a block.</li>
 * </ul>
 * This is the single choke point both paths already go through, so hooking
 * only here means an override can never fire twice for the same event, and
 * there's no need for a second, separate breaking-specific mixin.
 * <p>
 * If an override matches, this cancels the vanilla method (which would
 * otherwise spawn a generic block-fragment particle) and substitutes the
 * override's own particles. If nothing matches, this does nothing and
 * vanilla's default shape-aware particle runs exactly as before. Either way,
 * an underwater bubble burst is layered on top when appropriate.
 */
@Mixin(ParticleManager.class)
public abstract class ParticleOverrideMixin {

    @Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
    private void breakingplaceparticles$override(BlockPos pos, BlockState state, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        if (world == null || state.isAir()) {
            return;
        }

        ParticleOrigin origin = ParticleSpawnHelper.currentOriginOrBreak();

        boolean handled = BlockParticleOverrides.tryOverride(client, world, pos, state, origin);
        BlockParticleOverrides.spawnUnderwaterBubblesIfNeeded(client, world, pos, origin);

        if (handled) {
            ci.cancel();
        }
    }
}
