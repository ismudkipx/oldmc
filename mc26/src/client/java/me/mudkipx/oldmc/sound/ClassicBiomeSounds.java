package me.mudkipx.oldmc.sound;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class ClassicBiomeSounds {
    private static final Map<Identifier, Identifier> SOUNDS = buildReplacements();

    private ClassicBiomeSounds() {}

    public static Identifier replacement(Identifier original) {
        return SOUNDS.getOrDefault(original, original);
    }

    private static Map<Identifier, Identifier> buildReplacements() {
        var sounds = new HashMap<>(ClassicWolfSounds.replacements(Identifier::withDefaultNamespace));
        add(sounds, "cow", "moody", "ambient", "death", "hurt", "step");
        add(sounds, "chicken", "picky", "ambient", "death", "hurt");
        for (String variant : new String[]{"big", "mini"}) {
            add(sounds, "pig", variant, "ambient", "death", "hurt");
        }
        // Feeding a pig was silent before 26.1, including for the classic variant.
        for (String pig : new String[]{"pig", "pig_big", "pig_mini"}) {
            sounds.put(Identifier.withDefaultNamespace("entity." + pig + ".eat"),
                    SoundManager.INTENTIONALLY_EMPTY_SOUND_LOCATION);
        }
        return Map.copyOf(sounds);
    }

    private static void add(Map<Identifier, Identifier> sounds, String mob, String variant, String... actions) {
        for (String action : actions) {
            sounds.put(Identifier.withDefaultNamespace("entity." + mob + "_" + variant + "." + action),
                    Identifier.withDefaultNamespace("entity." + mob + "." + action));
        }
    }
}
