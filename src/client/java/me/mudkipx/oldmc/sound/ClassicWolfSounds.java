package me.mudkipx.oldmc.sound;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ClassicWolfSounds {
    private ClassicWolfSounds() {}

    public static <T> Map<T, T> replacements(Function<String, T> minecraftId) {
        var sounds = new HashMap<T, T>();
        for (String action : new String[]{"ambient", "death", "growl", "hurt", "pant", "whine"}) {
            T classic = minecraftId.apply("entity.wolf." + action);
            for (String variant : new String[]{"angry", "big", "cute", "grumpy", "puglin", "sad"}) {
                sounds.put(minecraftId.apply("entity.wolf_" + variant + "." + action), classic);
            }
        }
        return Map.copyOf(sounds);
    }
}
