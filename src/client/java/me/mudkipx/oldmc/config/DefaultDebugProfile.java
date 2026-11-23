package me.mudkipx.oldmc.config;

import me.mudkipx.oldmc.debug.DebugItem;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;

import java.util.HashMap;
import java.util.Map;

public final class DefaultDebugProfile {
    private static Map<?, DebugScreenEntryStatus> selected(Map<?, DebugScreenEntryStatus> vanilla) {
        if (!OldMCOptions.DEBUG.enabled()) return vanilla;

        var defaults = new HashMap<Object, DebugScreenEntryStatus>(vanilla);
        var overlayOnly = vanilla.get(DebugScreenEntries.GAME_VERSION);
        for (var id : DebugScreenEntries.allEntries().keySet()) {
            var item = DebugItem.find(id.getNamespace(), id.getPath());
            if (item != null && item.classicDefault) defaults.putIfAbsent(id, overlayOnly);
        }
        return defaults;
    }

    public static Map<DebugScreenProfile, Map<?, DebugScreenEntryStatus>> resolve(
            Map<DebugScreenProfile, Map<?, DebugScreenEntryStatus>> profiles) {
        var vanilla = profiles.get(DebugScreenProfile.DEFAULT);
        var target = selected(vanilla);
        if (target == vanilla) return profiles;
        var result = new HashMap<>(profiles);
        result.put(DebugScreenProfile.DEFAULT, target);
        return result;
    }

    public static boolean matches(DebugScreenEntryList entries) {
        var defaults = selected(DebugScreenEntries.PROFILES.get(DebugScreenProfile.DEFAULT));
        for (var id : DebugScreenEntries.allEntries().keySet()) {
            if (entries.getStatus(id) != defaults.getOrDefault(id, DebugScreenEntryStatus.NEVER)) return false;
        }
        return true;
    }

    private DefaultDebugProfile() {}
}
