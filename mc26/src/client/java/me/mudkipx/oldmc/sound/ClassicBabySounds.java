package me.mudkipx.oldmc.sound;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class ClassicBabySounds {
    private static final Map<Identifier, Identifier> SOUNDS = buildReplacements();

    private ClassicBabySounds() {}

    public static Identifier replacement(Identifier original) {
        return SOUNDS.getOrDefault(original, original);
    }

    private static Map<Identifier, Identifier> buildReplacements() {
        var sounds = new HashMap<Identifier, Identifier>();
        add(sounds, "cat", "ambient", "beg_for_food", "death", "eat", "hiss", "hurt", "purr", "purreow", "stray_ambient");
        add(sounds, "chicken", "ambient", "death", "hurt", "step");
        add(sounds, "horse", "ambient", "angry", "breathe", "death", "eat", "hurt", "land", "step");
        add(sounds, "pig", "ambient", "death", "hurt", "step");
        add(sounds, "wolf", "ambient", "death", "growl", "hurt", "pant", "step", "whine");
        // Feeding a pig did not play a sound in 1.21.11.
        sounds.put(Identifier.withDefaultNamespace("entity.baby_pig.eat"), SoundManager.INTENTIONALLY_EMPTY_SOUND_LOCATION);
        return Map.copyOf(sounds);
    }

    private static void add(Map<Identifier, Identifier> sounds, String mob, String... actions) {
        for (String action : actions) {
            String name = mob + "." + action;
            sounds.put(Identifier.withDefaultNamespace("entity.baby_" + name), Identifier.withDefaultNamespace("entity." + name));
        }
    }
}
