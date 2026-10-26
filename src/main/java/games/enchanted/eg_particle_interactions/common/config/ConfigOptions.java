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
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("particle_interactions.json");
    private static Settings settings = new Settings();

    private ConfigOptions() {
    }

    public static synchronized void load() {
        try {
            if (Files.notExists(CONFIG_FILE)) {
                save();
                return;
            }
            try (Reader reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
                Settings loaded = GSON.fromJson(reader, Settings.class);
                if (loaded != null) {
                    settings = loaded;
                }
            }
            settings.particlesPerEvent = clamp(settings.particlesPerEvent, 1, 16);
        } catch (Exception exception) {
            System.err.println("[Particle Interactions] Could not read config; defaults are active: " + exception.getMessage());
            settings = new Settings();
        }
    }

    public static synchronized void save() throws java.io.IOException {
        Files.createDirectories(CONFIG_FILE.getParent());
        try (Writer writer = Files.newBufferedWriter(CONFIG_FILE, StandardCharsets.UTF_8)) {
            GSON.toJson(settings, writer);
        }
    }

    public static Settings get() {
        return settings;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static final class Settings {
        public boolean enabled = true;
        public boolean blockPlacement = true;
        public boolean blockBreaking = true;
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
