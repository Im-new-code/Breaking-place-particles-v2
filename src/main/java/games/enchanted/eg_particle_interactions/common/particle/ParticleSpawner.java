package games.enchanted.eg_particle_interactions.common.particle;

import games.enchanted.eg_particle_interactions.common.config.ConfigOptions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;

public final class ParticleSpawner {
    public enum ToolAction { TILL, STRIP, FLATTEN, FIRE, BUCKET }
    private ParticleSpawner() {}
    public static void placed(World world, BlockPos pos, BlockState state) {
        ConfigOptions.Settings c=ConfigOptions.get();
        if(c.enabled && c.blockPlacement && world.isClient && !state.isAir()) burst(world,pos,state,visualFor(state));
    }
    public static void broken(ClientWorld world, BlockPos pos, BlockState state) {
        ConfigOptions.Settings c=ConfigOptions.get();
        if(c.enabled && c.blockBreaking && !state.isAir()) burst(world,pos,state,visualFor(state));
    }
    public static void tool(World world, BlockPos pos, BlockState state, ToolAction action) {
        ConfigOptions.Settings c=ConfigOptions.get();
        if(!c.enabled || !c.toolInteractions || !world.isClient) return;
        Random r=world.random;
        for(int i=0;i<count(c,3);i++) {
            double x=pos.getX()+.15+r.nextDouble()*.7, y=pos.getY()+.2+r.nextDouble()*.6, z=pos.getZ()+.15+r.nextDouble()*.7;
            switch(action) {
                case TILL: spawnAt(world,ParticleTypes.COMPOSTER,x,y,z,0,.025,0); break;
                case STRIP: spawnAt(world,new BlockStateParticleEffect(ParticleTypes.BLOCK,state),x,y,z,(r.nextDouble()-.5)*.05,.02,(r.nextDouble()-.5)*.05); break;
                case FLATTEN: spawnAt(world,ParticleTypes.CLOUD,x,pos.getY()+.08,z,0,.01,0); break;
                case FIRE: spawnAt(world,i%2==0?ParticleTypes.ELECTRIC_SPARK:ParticleTypes.SMALL_FLAME,x,y,z,(r.nextDouble()-.5)*.06,.025,(r.nextDouble()-.5)*.06); break;
                case BUCKET: spawnAt(world,i%2==0?ParticleTypes.BUBBLE:ParticleTypes.BUBBLE_POP,x,y,z,0,.02,0); break;
            }
        }
    }
    public static void campfire(World w,BlockPos p) { if(!enabled(w,true))return; Random r=w.random; spawnAt(w,ParticleTypes.ELECTRIC_SPARK,p.getX()+.25+r.nextDouble()*.5,p.getY()+.25,p.getZ()+.25+r.nextDouble()*.5,0,.02,0); }
    public static void furnace(World w,BlockPos p) { if(!enabled(w,true))return; Random r=w.random; double x=p.getX()+.3+r.nextDouble()*.4,y=p.getY()+.5,z=p.getZ()+.3+r.nextDouble()*.4; spawnAt(w,ParticleTypes.SMALL_FLAME,x,y,z,0,.01,0); if(r.nextBoolean())spawnAt(w,ParticleTypes.SMOKE,x,y+.1,z,0,.02,0); }
    public static void redstone(World w,BlockPos p) { if(!enabled(w,false))return; Random r=w.random; spawnAt(w,ParticleTypes.ELECTRIC_SPARK,p.getX()+.2+r.nextDouble()*.6,p.getY()+.04,p.getZ()+.2+r.nextDouble()*.6,0,.01,0); }
    public static void spark(World w,double x,double y,double z) { if(!enabled(w,false))return; Random r=w.random; spawnAt(w,ParticleTypes.ELECTRIC_SPARK,x,y,z,(r.nextDouble()-.5)*.04,.01,(r.nextDouble()-.5)*.04); }
    public static void fallingImpact(World w,BlockPos p,BlockState s) { if(!enabled(w,false)||s.isAir())return; Random r=w.random; for(int i=0;i<count(ConfigOptions.get(),3);i++)spawnAt(w,new BlockStateParticleEffect(ParticleTypes.BLOCK,s),p.getX()+r.nextDouble(),p.getY()+.05,p.getZ()+r.nextDouble(),(r.nextDouble()-.5)*.06,.02,(r.nextDouble()-.5)*.06); }
    public static void itemFrame(World w,double x,double y,double z) { if(!enabled(w,false))return; spawnAt(w,ParticleTypes.CLOUD,x,y,z,0,.003,0); }
    private static boolean enabled(World w,boolean fire) { ConfigOptions.Settings c=ConfigOptions.get(); return c.enabled&&(fire?c.fireAndFurnaces:c.entityEffects)&&w.isClient; }
    public static void spawnAt(World w,ParticleEffect p,double x,double y,double z,double vx,double vy,double vz) { if(w!=null&&w.isClient)w.addParticle(p,x,y,z,vx,vy,vz); }
    public static ParticleEffect visualFor(BlockState s) {
        if(s.isOf(Blocks.SNOW)||s.isOf(Blocks.SNOW_BLOCK))return ParticleTypes.SNOWFLAKE;
        if(s.isIn(net.minecraft.tag.BlockTags.LEAVES))return ParticleTypes.FALLING_SPORE_BLOSSOM;
        if(s.isOf(Blocks.MOSS_BLOCK)||s.isOf(Blocks.MOSS_CARPET)||s.getBlock() instanceof FlowerBlock||s.isOf(Blocks.GRASS)||s.isOf(Blocks.TALL_GRASS)||s.isOf(Blocks.FERN)||s.isOf(Blocks.LARGE_FERN))return ParticleTypes.COMPOSTER;
        if(s.isOf(Blocks.REDSTONE_WIRE)||s.isOf(Blocks.REPEATER)||s.isOf(Blocks.COMPARATOR))return ParticleTypes.ELECTRIC_SPARK;
        if(s.getMaterial().isLiquid())return ParticleTypes.BUBBLE;
        return new BlockStateParticleEffect(ParticleTypes.BLOCK,s);
    }
    private static void burst(World w,BlockPos p,BlockState s,ParticleEffect effect) {
        Random r=w.random; int n=count(ConfigOptions.get(),5);
        for(int i=0;i<n;i++)spawnAt(w,effect,p.getX()+.1+r.nextDouble()*.8,p.getY()+.1+r.nextDouble()*.8,p.getZ()+.1+r.nextDouble()*.8,(r.nextDouble()-.5)*.08,.01+r.nextDouble()*.03,(r.nextDouble()-.5)*.08);
    }
    private static int count(ConfigOptions.Settings c,int fallback) { int n=c.particlesPerEvent>0?c.particlesPerEvent:fallback; return c.performanceMode?Math.min(3,n):Math.min(16,n); }
}
