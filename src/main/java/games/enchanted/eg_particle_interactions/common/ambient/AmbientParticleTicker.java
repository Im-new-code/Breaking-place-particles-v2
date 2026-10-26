package games.enchanted.eg_particle_interactions.common.ambient;

import games.enchanted.eg_particle_interactions.common.config.ConfigOptions;
import games.enchanted.eg_particle_interactions.common.particle.ParticleSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import java.util.Random;

public final class AmbientParticleTicker {
    private static int ticks;
    private AmbientParticleTicker() {}
    public static void tick(MinecraftClient client) {
        if(++ticks%10!=0||client.world==null||client.player==null)return;
        ConfigOptions.Settings c=ConfigOptions.get(); if(!c.enabled)return;
        ClientWorld w=client.world; Random r=w.random; BlockPos player=client.player.getBlockPos();
        long time=w.getTime()%24000L; boolean night=time>13000L&&time<23000L;
        for(int i=0;i<3;i++) {
            BlockPos p=player.add(r.nextInt(17)-8,r.nextInt(9)-4,r.nextInt(17)-8);
            BlockState s=w.getBlockState(p);
            if(c.ambientLeaves&&s.isIn(BlockTags.LEAVES)&&r.nextInt(45)==0)
                ParticleSpawner.spawnAt(w,ParticleTypes.FALLING_SPORE_BLOSSOM,p.getX()+r.nextDouble(),p.getY()+.2,p.getZ()+r.nextDouble(),(r.nextDouble()-.5)*.015,-.003,(r.nextDouble()-.5)*.015);
            if(c.swampFireflies&&night&&r.nextInt(5)==0&&w.getBiome(p).getCategory()==Biome.Category.SWAMP)
                ParticleSpawner.spawnAt(w,ParticleTypes.END_ROD,p.getX()+r.nextDouble(),p.getY()+.3+r.nextDouble(),p.getZ()+r.nextDouble(),0,.004,0);
            if(c.fluidEffects&&r.nextInt(28)==0) {
                if(w.getFluidState(p).isIn(FluidTags.WATER))ParticleSpawner.spawnAt(w,ParticleTypes.BUBBLE,p.getX()+.5,p.getY()+.1,p.getZ()+.5,0,.025,0);
                else if(s.isOf(Blocks.LAVA))ParticleSpawner.spawnAt(w,ParticleTypes.LAVA,p.getX()+r.nextDouble(),p.getY()+.95,p.getZ()+r.nextDouble(),0,0,0);
            }
        }
        if(c.movementParticles) {
            Vec3d v=client.player.getVelocity(); BlockPos groundPos=player.down(); BlockState ground=w.getBlockState(groundPos);
            if(v.x*v.x+v.z*v.z>.025&&!ground.isAir()&&r.nextInt(5)==0)
                ParticleSpawner.spawnAt(w,ParticleSpawner.visualFor(ground),client.player.getX(),client.player.getY()+.05,client.player.getZ(),-v.x*.15,.01,-v.z*.15);
        }
    }
}
