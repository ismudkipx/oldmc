package me.mudkipx.oldmc.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;

/** An in-memory copy of the default preset. Never read or write the player's debug-profile.json. */
final class DefaultProfileEntries extends DebugScreenEntryList {
    DefaultProfileEntries() {
        super(Minecraft.getInstance().gameDirectory);
    }

    @Override
    public void load() {
        loadProfile(DebugScreenProfile.DEFAULT);
    }

    @Override
    public void save() {}
}
