package games.enchanted.eg_particle_interactions.common.mixin.client.blocks;
import games.enchanted.eg_particle_interactions.common.config.ConfigOptions; import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner; import net.minecraft.block.BlockState; import net.minecraft.block.RedstoneWireBlock; import net.minecraft.util.math.BlockPos; import net.minecraft.world.World; import org.spongepowered.asm.mixin.Mixin; import org.spongepowered.asm.mixin.injection.At; import org.spongepowered.asm.mixin.injection.Inject; import org.spongepowered.asm.mixin.injection.callback.CallbackInfo; import java.util.Random;
@Mixin(RedstoneWireBlock.class) public abstract class RedstoneWireBlockMixin {
 @Inject(method="randomDisplayTick",at=@At("TAIL")) private void egpi$dust(BlockState s,World w,BlockPos p,Random r,CallbackInfo ci){if(ConfigOptions.get().enabled&&ConfigOptions.get().redstoneEffects&&s.get(RedstoneWireBlock.POWER)>0&&r.nextInt(3)==0)ParticleSpawner.redstone(w,p);}
}
