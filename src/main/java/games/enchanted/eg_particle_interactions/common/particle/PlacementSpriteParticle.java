package games.enchanted.eg_particle_interactions.common.particle;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import java.util.Random;
public final class PlacementSpriteParticle extends SpriteBillboardParticle {
    public enum Kind { SNOWFLAKE, LEAF, PETAL, GRASS_BLADE, MOSS_CLUMP, TINTED_DUST, REDSTONE_DUST, BLOCK_SHATTER }
    private final Kind kind;
    private final float spin;
    public PlacementSpriteParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ,
                                   BlockState state, SpriteProvider sprites, Kind kind) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.kind = kind;
        setSprite(sprites);
        setMaxAge(lifetime(kind, this.random));
        scale(size(kind, this.random));
        this.gravityStrength = gravity(kind);
        this.collidesWithWorld = false;
        this.angle = this.random.nextFloat() * 6.2831855F;
        this.prevAngle = this.angle;
        this.spin = spin(kind) * (this.random.nextBoolean() ? 1.0F : -1.0F);
        int color = colorFor(state, world, x, y, z, kind);
        setColor(((color >> 16) & 255) / 255.0F, ((color >> 8) & 255) / 255.0F, (color & 255) / 255.0F);
        if (kind == Kind.LEAF || kind == Kind.PETAL || kind == Kind.GRASS_BLADE) {
            this.velocityY += 0.006 + this.random.nextDouble() * 0.012;
        }
        this.velocityX += (this.random.nextDouble() - 0.5) * 0.006;
        this.velocityZ += (this.random.nextDouble() - 0.5) * 0.006;
    }
    @Override public ParticleTextureSheet getType() { return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT; }
    @Override public void tick() {
        float previousAngle = this.angle;
        super.tick();
        this.prevAngle = previousAngle;
        this.angle = previousAngle + this.spin;
        this.velocityX *= 0.96;
        this.velocityZ *= 0.96;
        if (this.kind == Kind.LEAF || this.kind == Kind.PETAL || this.kind == Kind.GRASS_BLADE) this.velocityY = Math.max(this.velocityY, -0.045);
    }
    private static int lifetime(Kind kind, Random random) {
        switch (kind) {
            case LEAF: case PETAL: case GRASS_BLADE: return 12 + random.nextInt(7);
            case BLOCK_SHATTER: return 8 + random.nextInt(4);
            default: return 9 + random.nextInt(5);
        }
    }
    private static float size(Kind kind, Random random) {
        switch (kind) {
            case GRASS_BLADE: case LEAF: case PETAL: return 0.22F + random.nextFloat() * 0.13F;
            case BLOCK_SHATTER: return 0.25F + random.nextFloat() * 0.12F;
            case TINTED_DUST: return 0.18F + random.nextFloat() * 0.12F;
            default: return 0.21F + random.nextFloat() * 0.11F;
        }
    }
    private static float gravity(Kind kind) {
        switch (kind) {
            case LEAF: case PETAL: return 0.035F;
            case GRASS_BLADE: return 0.055F;
            case SNOWFLAKE: return 0.025F;
            case REDSTONE_DUST: return 0.11F;
            case BLOCK_SHATTER: return 0.16F;
            default: return 0.10F;
        }
    }
    private static float spin(Kind kind) {
        switch (kind) {
            case LEAF: case PETAL: return 0.12F;
            case GRASS_BLADE: return 0.08F;
            case BLOCK_SHATTER: return 0.20F;
            default: return 0.045F;
        }
    }
    private static int colorFor(BlockState state, ClientWorld world, double x, double y, double z, Kind kind) {
        Identifier id = Registry.BLOCK.getId(state.getBlock());
        String path = id == null ? "" : id.getPath();
        switch (kind) {
            case SNOWFLAKE: return 0xE4F5FF;
            case LEAF:
            case GRASS_BLADE: {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null) {
                    BlockColors colors = client.getBlockColors();
                    int tint = colors.getColor(state, world, new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)), 0);
                    if (tint != -1) return tint;
                }
                return kind == Kind.LEAF ? 0x6C9C45 : 0x74B447;
            }
            case PETAL:
                if (path.contains("blue_orchid") || path.contains("cornflower")) return 0x6B9AF7;
                if (path.contains("poppy") || (path.contains("tulip") && !path.contains("white"))) return 0xE65354;
                if (path.contains("dandelion") || path.contains("sunflower")) return 0xF4D64D;
                if (path.contains("allium")) return 0xB184DE;
                if (path.contains("azure_bluet") || path.contains("oxeye_daisy")) return 0xE9E7D9;
                return 0xEFA1C4;
            case MOSS_CLUMP: return 0x79A94B;
            case TINTED_DUST: return dustColor(path);
            case REDSTONE_DUST: return 0xF13C35;
            case BLOCK_SHATTER: return 0xA95AE4;
            default: return 0xFFFFFF;
        }
    }
    private static int dustColor(String path) {
        if (path.contains("red_sand")) return 0xC77A4A;
        if (path.contains("soul_sand")) return 0x80624C;
        if (path.contains("soul_soil")) return 0x4D3730;
        if (path.contains("gravel")) return 0x9B988D;
        if (path.contains("sand")) return 0xD9C58C;
        if (path.contains("white_concrete_powder")) return 0xE8E8E1;
        if (path.contains("orange_concrete_powder")) return 0xE68B35;
        if (path.contains("magenta_concrete_powder")) return 0xB64AB3;
        if (path.contains("light_blue_concrete_powder")) return 0x5CAFCB;
        if (path.contains("yellow_concrete_powder")) return 0xE8C64D;
        if (path.contains("lime_concrete_powder")) return 0x8DBF3D;
        if (path.contains("pink_concrete_powder")) return 0xD98AA6;
        if (path.contains("light_gray_concrete_powder")) return 0xA3A5A1;
        if (path.contains("gray_concrete_powder")) return 0x55585A;
        if (path.contains("cyan_concrete_powder")) return 0x318E8F;
        if (path.contains("purple_concrete_powder")) return 0x8B55B8;
        if (path.contains("blue_concrete_powder")) return 0x4656A8;
        if (path.contains("brown_concrete_powder")) return 0x805B3D;
        if (path.contains("green_concrete_powder")) return 0x678139;
        if (path.contains("red_concrete_powder")) return 0xAE4C46;
        if (path.contains("black_concrete_powder")) return 0x343638;
        return 0xC8BDA1;
    }
}
