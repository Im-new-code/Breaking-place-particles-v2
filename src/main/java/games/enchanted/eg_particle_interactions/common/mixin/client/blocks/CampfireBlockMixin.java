package games.enchanted.eg_particle_interactions.common.mixin.client.blocks;

import games.enchanted.eg_particle_interactions.common.config.ConfigOptions;
import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin {
    @Inject(method = "randomDisplayTick", at = @At("TAIL"))
    private void egpi$extraCampfireSparks(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (ConfigOptions.get().enabled && ConfigOptions.get().fireAndFurnaces
                && state.get(CampfireBlock.LIT) && random.nextInt(3) == 0) {
            ParticleSpawner.campfire(world, pos);
        }
    }
}
