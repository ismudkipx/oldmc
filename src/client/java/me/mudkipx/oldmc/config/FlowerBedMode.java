package me.mudkipx.oldmc.config;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Locale;

public enum FlowerBedMode {
    ALL, WILDFLOWERS, PINK_PETALS, NONE;

    static final FlowerBedMode DEFAULT = WILDFLOWERS;

    public boolean hides(Block block) {
        return switch (this) {
            case WILDFLOWERS -> block == Blocks.WILDFLOWERS;
            case PINK_PETALS -> block == Blocks.PINK_PETALS;
            case ALL -> block == Blocks.WILDFLOWERS || block == Blocks.PINK_PETALS;
            case NONE -> false;
        };
    }

    String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    static FlowerBedMode fromKey(String key) {
        for (var mode : values()) {
            if (mode.key().equals(key)) return mode;
        }
        return null;
    }
}
