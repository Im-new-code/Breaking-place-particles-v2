package com.example.breakingplace.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Minimal, dependency-free configuration.
 * <p>
 * Deliberately NOT built on a config-screen library (YACL, Cloth Config,
 * etc.) - the mod explicitly avoids extra mandatory dependencies, and a
 * handful of booleans/ints don't need one. This uses {@code Gson}, which
 * ships inside Minecraft itself, so there is nothing new to download.
 * <p>
 * The file lives at {@code config/breakingplaceparticles.json} and is
 * created with these defaults the first time the mod runs. Edit it by hand
 * and restart the game (or your launcher's "reload resources" if you add
 * that later) to apply changes.
 */
public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "breakingplaceparticles.json";

    private static ModConfig instance;

    // --- Options -------------------------------------------------------

    /** Master switch for material-specific particle overrides (snow, redstone, dust). */
    public boolean blockOverridesEnabled = true;

    /** Master switch for the extra underwater bubble burst on place/break. */
    public boolean underwaterBubblesEnabled = true;

    public int underwaterBubblesOnPlace = 4;
    public int underwaterBubblesOnBreak = 6;

    /** Master switch for the hoe/axe/shovel transformation particles. */
    public boolean toolInteractionParticlesEnabled = true;

    // ---------------------------------------------------------------------

    public static synchronized ModConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static ModConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);

        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            } catch (IOException | RuntimeException e) {
                // Fall through to defaults below; a malformed config file should
                // never prevent the mod (or the game) from starting.
            }
        }

        ModConfig defaults = new ModConfig();
        defaults.save();
        return defaults;
    }

    public void save() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            // Non-fatal: worst case the mod just runs with in-memory defaults this session.
        }
    }
}
