package me.mudkipx.oldmc.debug;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;

import java.util.IdentityHashMap;
import java.util.Map;

public final class DebugEntryLookup {
    private static final Map<DebugScreenEntry, DebugItem> ITEMS = new IdentityHashMap<>();

    static {
        DebugScreenEntries.allEntries().forEach((id, entry) -> {
            DebugItem item = DebugItem.find(id.getNamespace(), id.getPath());
            if (item != null) {
                ITEMS.put(entry, item);
            }
        });
    }

    public static DebugItem get(DebugScreenEntry entry) {
        return ITEMS.get(entry);
    }

    private DebugEntryLookup() {}
}
