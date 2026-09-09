package com.example.breakingplace.particle.override;

/**
 * What kind of interaction is asking for particles right now.
 * <p>
 * Placement and breaking share the same override lookup (a snow block should
 * look like snow either way), but they intentionally use different particle
 * counts/velocities - breaking should feel more violent than placing, per the
 * mod's design goals. Overrides receive this so they can vary their output
 * instead of behaving identically for both events.
 */
public enum ParticleOrigin {
    PLACE,
    BREAK
}
