package games.enchanted.eg_particle_interactions.common.replay;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Optional ReplayMod bridge. Reflection keeps ReplayMod a soft dependency.
 * Client-created particles are saved as timestamped vanilla particle packets,
 * so replay playback recreates the original effect and custom particle type.
 */
public final class ReplayModCompatibility {
    private static final String RECORDING_CLASS = "com.replaymod.recording.ReplayModRecording";
    private static final String CONNECTION_HANDLER_CLASS = "com.replaymod.recording.handler.ConnectionEventHandler";
    private static final String PACKET_LISTENER_CLASS = "com.replaymod.recording.packet.PacketListener";
    private static final String REPLAY_CLASS = "com.replaymod.replay.ReplayModReplay";

    private static volatile boolean resolved;
    private static boolean recordingAvailable;
    private static boolean replayAvailable;
    private static boolean warned;
    private static Field recordingInstance;
    private static Method getConnectionEventHandler;
    private static Method getPacketListener;
    private static Method savePacket;
    private static Field replayInstance;
    private static Method getReplayHandler;

    private ReplayModCompatibility() {}

    private static synchronized void resolve() {
        if (resolved) return;
        ClassLoader loader = ReplayModCompatibility.class.getClassLoader();
        try {
            Class<?> recording = Class.forName(RECORDING_CLASS, false, loader);
            Class<?> connectionHandler = Class.forName(CONNECTION_HANDLER_CLASS, false, loader);
            Class<?> listener = Class.forName(PACKET_LISTENER_CLASS, false, loader);
            recordingInstance = recording.getField("instance");
            getConnectionEventHandler = recording.getMethod("getConnectionEventHandler");
            getPacketListener = connectionHandler.getMethod("getPacketListener");
            savePacket = listener.getMethod("save", Packet.class);
            recordingAvailable = true;
        } catch (ClassNotFoundException e) {
            recordingAvailable = false;
        } catch (NoSuchFieldException | NoSuchMethodException | LinkageError e) {
            recordingAvailable = false;
            warn("ReplayMod recording API was not recognised; replay particle capture is disabled.");
        }

        try {
            Class<?> replay = Class.forName(REPLAY_CLASS, false, loader);
            replayInstance = replay.getField("instance");
            getReplayHandler = replay.getMethod("getReplayHandler");
            replayAvailable = true;
        } catch (ClassNotFoundException e) {
            replayAvailable = false;
        } catch (NoSuchFieldException | NoSuchMethodException | LinkageError e) {
            replayAvailable = false;
            warn("ReplayMod playback API was not recognised; replay-local particle suppression is disabled.");
        }
        resolved = true;
    }

    /** True only when ReplayMod has an active ReplayHandler for playback/rendering. */
    public static boolean isReplayPlayback() {
        resolve();
        if (!replayAvailable) return false;
        try {
            Object instance = replayInstance.get(null);
            return instance != null && getReplayHandler.invoke(instance) != null;
        } catch (ReflectiveOperationException | LinkageError e) {
            warn("Could not query ReplayMod playback state; local particles remain enabled.");
            return false;
        }
    }

    /** Save one exact particle event into an active ReplayMod recording, if present. */
    public static void recordParticle(ParticleEffect effect, double x, double y, double z,
                                      double velocityX, double velocityY, double velocityZ) {
        resolve();
        if (!recordingAvailable || effect == null) return;
        try {
            Object recorder = recordingInstance.get(null);
            if (recorder == null) return;
            Object connectionHandler = getConnectionEventHandler.invoke(recorder);
            if (connectionHandler == null) return;
            Object listener = getPacketListener.invoke(connectionHandler);
            if (listener == null) return; // ReplayMod is installed but not recording.

            // count=0 means one particle; offsets * speed are its velocity.
            ParticleS2CPacket packet = new ParticleS2CPacket(effect, false, x, y, z,
                    (float) velocityX, (float) velocityY, (float) velocityZ, 1.0F, 0);
            savePacket.invoke(listener, packet);
        } catch (ReflectiveOperationException | LinkageError e) {
            warn("Could not store a particle in the ReplayMod recording; live particles are unaffected.");
        }
    }

    private static synchronized void warn(String message) {
        if (warned) return;
        warned = true;
        System.err.println("[Particle Interactions] " + message);
    }
}
