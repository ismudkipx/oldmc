package me.mudkipx.oldmc.debug;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static me.mudkipx.oldmc.debug.DebugItem.*;

/** Arranges only the lines actually emitted by the active vanilla entries in this frame. */
public final class ClassicDebugLayout {
    private final Map<DebugItem, List<String>> rows = new EnumMap<>(DebugItem.class);

    public List<String> collect(DebugItem item) {
        return rows.computeIfAbsent(item, ignored -> new ArrayList<>());
    }

    private List<String> lines(DebugItem item) {
        return rows.getOrDefault(item, List.of());
    }

    public List<String> column(boolean left, List<String> vanillaLines) {
        List<String> result = new ArrayList<>();
        if (left) {
            addGameInformation(result);
        } else {
            addSystemInformation(result);
        }
        if (result.isEmpty()) {
            return vanillaLines;
        }
        // Unknown entries and the vanilla keybind footer keep their original lines.
        if (!vanillaLines.isEmpty() && !vanillaLines.getFirst().isEmpty()) {
            result.add("");
        }
        result.addAll(vanillaLines);
        return result;
    }

    private void addGameInformation(List<String> out) {
        out.addAll(lines(GAME_VERSION));
        List<String> performance = lines(SIMPLE_PERFORMANCE_IMPACTORS);
        List<String> fps = new ArrayList<>(lines(FPS));
        if (!performance.isEmpty()) {
            fps.add(performance.getFirst());
        }
        fps.addAll(lines(GPU_UTILIZATION));
        if (!fps.isEmpty()) {
            out.add(String.join(" ", fps));
        }
        append(out, TPS, CHUNK_RENDER_STATS, ENTITY_RENDER_STATS, PARTICLE_RENDER_STATS, CHUNK_SOURCE_STATS);

        List<String> position = lines(PLAYER_POSITION);
        List<String> coordinates = new ArrayList<>();
        List<String> relative = lines(PLAYER_SECTION_POSITION);
        boolean combined = false;
        for (String line : position) {
            if (line.contains(" FC: ")) {
                out.add(line);
            } else if (line.startsWith("Block: ") && relative.size() == 1
                    && relative.getFirst().startsWith("Section-relative: ")) {
                // Include the leading space so each two-digit component is unpadded equally.
                String offsets = relative.getFirst().substring("Section-relative:".length())
                        .replace(" 0", " ").stripLeading();
                coordinates.add(line + " [" + offsets + "]");
                combined = true;
            } else {
                coordinates.add(line);
            }
        }
        if (!combined) {
            coordinates.addAll(relative);
        }
        append(coordinates, PLAYER_SPEED, LIGHT_LEVELS, HEIGHTMAP, BIOME);
        List<String> difficulty = lines(LOCAL_DIFFICULTY);
        List<String> day = lines(DAY_COUNT);
        if (difficulty.size() == 1 && day.size() == 1 && day.getFirst().startsWith("Day #")) {
            coordinates.add(difficulty.getFirst() + " (Day " + day.getFirst().substring(5) + ")");
        } else {
            coordinates.addAll(difficulty);
            coordinates.addAll(day);
        }
        append(coordinates, CHUNK_GENERATION_STATS, ENTITY_SPAWN_COUNTS, POST_EFFECT, POST_EFFECTS, SOUND_MOOD, SOUND_CACHE);
        if (performance.size() > 1) {
            coordinates.addAll(performance.subList(1, performance.size()));
        }
        section(out, coordinates);
    }

    private void addSystemInformation(List<String> out) {
        // All supported system-spec entries emit Java, CPU, then one or more display lines.
        List<String> specs = lines(SYSTEM_SPECS);
        if (!specs.isEmpty()) {
            out.add(specs.getFirst());
        }
        append(out, MEMORY, DETAILED_MEMORY);
        if (specs.size() >= 2) {
            section(out, specs.subList(1, 2));
            section(out, specs.subList(2, specs.size()));
        }
        addTarget(out, LOOKING_AT_BLOCK, LOOKING_AT_BLOCK_STATE, LOOKING_AT_BLOCK_TAGS);
        addTarget(out, LOOKING_AT_FLUID, LOOKING_AT_FLUID_STATE, LOOKING_AT_FLUID_TAGS);
        addTarget(out, LOOKING_AT_ENTITY, LOOKING_AT_ENTITY_TAGS);
    }

    private void addTarget(List<String> out, DebugItem... items) {
        List<String> target = new ArrayList<>();
        append(target, items);
        section(out, target);
    }

    private void append(List<String> out, DebugItem... items) {
        for (DebugItem item : items) {
            out.addAll(lines(item));
        }
    }

    private static void section(List<String> out, List<String> section) {
        if (!section.isEmpty()) {
            if (!out.isEmpty()) {
                out.add("");
            }
            out.addAll(section);
        }
    }
}
