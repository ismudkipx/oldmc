package me.mudkipx.oldmc.config;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Locale;

public enum FallingLeafMode {
    ALL, ADDED_IN_1_21_5, BEFORE_1_21_5, NONE;

    static final FallingLeafMode DEFAULT = ADDED_IN_1_21_5;

    public boolean hides(ParticleType<?> type) {
        if (this == NONE) return false;
        if (type == ParticleTypes.TINTED_LEAVES) return this != BEFORE_1_21_5;
        if (type == ParticleTypes.CHERRY_LEAVES || type == ParticleTypes.PALE_OAK_LEAVES) {
            return this != ADDED_IN_1_21_5;
        }
        if (this != ALL) return false;
        // Poplar particles exist only from 26.3, so the shared JAR cannot reference their fields.
        var id = BuiltInRegistries.PARTICLE_TYPE.getKey(type);
        return id != null && id.getNamespace().equals("minecraft") && switch (id.getPath()) {
            case "red_poplar_leaves", "orange_poplar_leaves", "yellow_poplar_leaves" -> true;
            default -> false;
        };
    }

    String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    static FallingLeafMode fromKey(String key) {
        for (var mode : values()) {
            if (mode.key().equals(key)) return mode;
        }
        return null;
    }
}
