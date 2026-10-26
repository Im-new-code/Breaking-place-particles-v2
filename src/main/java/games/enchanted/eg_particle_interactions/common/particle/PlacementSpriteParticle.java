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

/**
 * 1.17.1-compatible motion port of Particle Interactions' FallingPetal and
 * FloatingDust particles. Placement velocity is supplied by ParticleSpawner;
 * this class adds only the small symmetric random motion used upstream.
 */
public final class PlacementSpriteParticle extends SpriteBillboardParticle {
    public enum Kind {
        SNOWFLAKE, LEAF, PETAL, GRASS_BLADE, HEAVY_GRASS_BLADE, SUGAR_CANE,
        CHAIN_SNAP, MOSS_CLUMP, TINTED_DUST, REDSTONE_DUST, BLOCK_SHATTER
    }

    private final Kind kind;
    private final boolean fallingPetalMotion;
    private final boolean floatingDustMotion;
    private final float spinAcceleration;
    private final float fixedSpin;
    private float spinSpeed;

    public PlacementSpriteParticle(ClientWorld world, double x, double y, double z,
                                   double velocityX, double velocityY, double velocityZ,
                                   BlockState state, SpriteProvider sprites, Kind kind) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.kind = kind;
        this.fallingPetalMotion = isFallingPetalKind(kind);
        this.floatingDustMotion = kind == Kind.TINTED_DUST || kind == Kind.REDSTONE_DUST;

        setSprite(sprites);
        setMaxAge(lifetime(kind, this.random));

        float baseGravity = 0.25F + this.random.nextFloat() * 0.13F;
        this.gravityStrength = baseGravity * gravityMultiplier(kind);
        // Upstream particles collide with blocks, so they settle naturally.
        this.collidesWithWorld = true;

        this.angle = this.random.nextFloat() * 6.2831855F;
        this.prevAngle = this.angle;
        this.fixedSpin = fixedSpin(kind);
        this.spinAcceleration = this.fallingPetalMotion
                ? (float) Math.toRadians(this.random.nextBoolean() ? -5.0F : 5.0F)
                : 0.0F;

        scale(size(kind, this.random));

        int color = colorFor(state, world, x, y, z, kind);
        setColor(((color >> 16) & 255) / 255.0F,
                 ((color >> 8) & 255) / 255.0F,
                 (color & 255) / 255.0F);

        // Mirrors upstream FallingPetal/FloatingDust constructor jitter:
        // each axis gets symmetric +/-0.05 velocity variation, not a lift.
        this.velocityX += (this.random.nextDouble() - 0.5) * 0.1;
        this.velocityY += (this.random.nextDouble() - 0.5) * 0.1;
        this.velocityZ += (this.random.nextDouble() - 0.5) * 0.1;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        if (this.fallingPetalMotion) {
            this.spinSpeed += this.spinAcceleration / 2.0F;
            this.prevAngle = this.angle;
            if (!this.onGround) this.angle += this.spinSpeed / 5.0F;

            this.velocityX *= 0.95;
            this.velocityY *= 0.90;
            this.velocityZ *= 0.95;
            super.tick();
            return;
        }

        if (this.floatingDustMotion) {
            this.velocityX *= 0.95;
            this.velocityY *= 0.90;
            this.velocityZ *= 0.95;
            this.gravityStrength *= 0.98F;
            super.tick();
            // 1.17.1 lacks the modern Particle.friction field. Apply the
            // upstream 0.995 damping directly after vanilla's movement.
            this.velocityX *= 0.995;
            this.velocityY *= 0.995;
            this.velocityZ *= 0.995;
            return;
        }

        this.prevAngle = this.angle;
        this.angle += this.fixedSpin;
        this.velocityX *= 0.95;
        this.velocityY *= 0.90;
        this.velocityZ *= 0.95;
        super.tick();
    }

    private static boolean isFallingPetalKind(Kind kind) {
        return kind == Kind.LEAF || kind == Kind.PETAL
                || kind == Kind.GRASS_BLADE || kind == Kind.HEAVY_GRASS_BLADE
                || kind == Kind.SUGAR_CANE || kind == Kind.MOSS_CLUMP;
    }

    private static float gravityMultiplier(Kind kind) {
        switch (kind) {
            case HEAVY_GRASS_BLADE:
            case MOSS_CLUMP:
                return 2.0F;
            case TINTED_DUST:
            case REDSTONE_DUST:
                return 0.7F;
            case SNOWFLAKE:
                return 0.2F;
            default:
                return 1.0F;
        }
    }

    private static int lifetime(Kind kind, Random random) {
        if (kind == Kind.BLOCK_SHATTER || kind == Kind.CHAIN_SNAP) {
            return 8 + random.nextInt(4);
        }
        // Particle Interactions' FallingPetal and FloatingDust lifetime.
        return (int) (16.0F / (random.nextFloat() * 0.8F + 0.2F)) + 2;
    }

    private static float size(Kind kind, Random random) {
        switch (kind) {
            case LEAF:
            case PETAL:
                return random.nextBoolean() ? 0.07F : 0.08F;
            case GRASS_BLADE:
            case HEAVY_GRASS_BLADE:
            case SUGAR_CANE:
                return random.nextBoolean() ? 0.10F : 0.12F;
            case MOSS_CLUMP:
                return 0.08F + random.nextFloat() * 0.04F;
            case TINTED_DUST:
            case REDSTONE_DUST:
                return random.nextBoolean() ? 0.095F : 0.125F;
            case SNOWFLAKE:
                return 0.08F + random.nextFloat() * 0.04F;
            case CHAIN_SNAP:
                return 0.16F + random.nextFloat() * 0.08F;
            case BLOCK_SHATTER:
                return 0.25F + random.nextFloat() * 0.12F;
            default:
                return 0.10F;
        }
    }

    private static float fixedSpin(Kind kind) {
        switch (kind) {
            case BLOCK_SHATTER:
                return 0.20F;
            case CHAIN_SNAP:
                return 0.08F;
            case SNOWFLAKE:
                return 0.015F;
            default:
                return 0.0F;
        }
    }

    private static int colorFor(BlockState state, ClientWorld world, double x, double y, double z, Kind kind) {
        Identifier id = Registry.BLOCK.getId(state.getBlock());
        String path = id == null ? "" : id.getPath();
        switch (kind) {
            case SNOWFLAKE:
                return 0xE4F5FF;
            case LEAF:
            case GRASS_BLADE:
            case HEAVY_GRASS_BLADE:
            case SUGAR_CANE: {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null) {
                    BlockColors colors = client.getBlockColors();
                    int tint = colors.getColor(state, world,
                            new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)), 0);
                    if (tint != -1) return tint;
                }
                if (kind == Kind.LEAF) return 0x6C9C45;
                if (kind == Kind.HEAVY_GRASS_BLADE) return 0x80A850;
                return kind == Kind.SUGAR_CANE ? 0x62A64B : 0x74B447;
            }
            case PETAL:
                if (path.contains("blue_orchid") || path.contains("cornflower")) return 0x6B9AF7;
                if (path.contains("poppy") || (path.contains("tulip") && !path.contains("white"))) return 0xE65354;
                if (path.contains("dandelion") || path.contains("sunflower")) return 0xF4D64D;
                if (path.contains("allium")) return 0xB184DE;
                if (path.contains("azure_bluet") || path.contains("oxeye_daisy")) return 0xE9E7D9;
                return 0xEFA1C4;
            case MOSS_CLUMP:
                return 0xFFFFFF;
            case TINTED_DUST:
                MinecraftClient dustClient = MinecraftClient.getInstance();
                if (dustClient != null) {
                    int tint = dustClient.getBlockColors().getColor(state, world,
                            new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)), 0);
                    if (tint != -1 && tint != 0xFFFFFF) {
                        int r = (tint >> 16) & 255;
                        int g = (tint >> 8) & 255;
                        int b = tint & 255;
                        return ((int) (r * 0.75F) << 16)
                                | ((int) (g * 0.75F) << 8)
                                | (int) (b * 0.75F);
                    }
                }
                if (path.contains("redstone") || path.equals("repeater") || path.equals("comparator")) return 0xB52A24;
                return dustColor(path);
            case REDSTONE_DUST:
                return 0xB52A24;
            case CHAIN_SNAP:
                return 0xB8B9B5;
            case BLOCK_SHATTER:
                return 0xA95AE4;
            default:
                return 0xFFFFFF;
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
