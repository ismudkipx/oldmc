package me.mudkipx.oldmc.debug;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Vanilla entry IDs. Some entries were split or renamed between releases. */
public enum DebugItem {
    GAME_VERSION, FPS, SIMPLE_PERFORMANCE_IMPACTORS, GPU_UTILIZATION, TPS,
    CHUNK_RENDER_STATS, ENTITY_RENDER_STATS, PARTICLE_RENDER_STATS, CHUNK_SOURCE_STATS,
    PLAYER_POSITION, PLAYER_SECTION_POSITION, LIGHT_LEVELS, HEIGHTMAP, BIOME,
    LOCAL_DIFFICULTY, DAY_COUNT, CHUNK_GENERATION_STATS, ENTITY_SPAWN_COUNTS,
    POST_EFFECT, POST_EFFECTS, SOUND_MOOD,
    SYSTEM_SPECS, MEMORY, LOOKING_AT_BLOCK, LOOKING_AT_BLOCK_STATE, LOOKING_AT_BLOCK_TAGS,
    LOOKING_AT_FLUID, LOOKING_AT_FLUID_STATE, LOOKING_AT_FLUID_TAGS, LOOKING_AT_ENTITY,
    DETAILED_MEMORY(false), PLAYER_SPEED(false), SOUND_CACHE(false), LOOKING_AT_ENTITY_TAGS(false);

    private static final Map<String, DebugItem> BY_PATH = new HashMap<>();

    static {
        for (DebugItem item : values()) {
            BY_PATH.put(item.name().toLowerCase(Locale.ROOT), item);
        }
    }

    public final boolean classicDefault;

    DebugItem() {
        this(true);
    }

    DebugItem(boolean classicDefault) {
        this.classicDefault = classicDefault;
    }

    public static DebugItem find(String namespace, String path) {
        return "minecraft".equals(namespace) ? BY_PATH.get(path) : null;
    }
}
