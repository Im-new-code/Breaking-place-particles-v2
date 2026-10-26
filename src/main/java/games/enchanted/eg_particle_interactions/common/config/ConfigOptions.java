package games.enchanted.eg_particle_interactions.common.config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
public final class ConfigOptions {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("particle_interactions.json");
    private static Settings settings = new Settings();
    private ConfigOptions() {}
    public static synchronized void load() {
        try {
            if (Files.notExists(FILE)) { save(); return; }
            try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                Settings loaded = GSON.fromJson(reader, Settings.class);
                if (loaded != null) settings = loaded;
            }
            settings.particlesPerEvent = Math.max(1, Math.min(16, settings.particlesPerEvent));
        } catch (Exception e) {
            System.err.println("[Particle Interactions] Config load failed; defaults active: " + e.getMessage());
            settings = new Settings();
        }
    }
    public static synchronized void save() throws java.io.IOException {
        Files.createDirectories(FILE.getParent());
        try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) { GSON.toJson(settings, writer); }
    }
    public static Settings get() { return settings; }
    public static final class Settings {
        public boolean enabled = true;
        public boolean blockPlacement = true;
        public boolean blockBreaking = false;
        public boolean toolInteractions = true;
        public boolean fireAndFurnaces = true;
        public boolean redstoneEffects = true;
        public boolean fluidEffects = true;
        public boolean entityEffects = true;
        public boolean ambientLeaves = true;
        public boolean swampFireflies = true;
        public boolean movementParticles = true;
        public boolean performanceMode = true;
        public int particlesPerEvent = 5;
    }
}
