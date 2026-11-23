package me.mudkipx.oldmc.config;

import net.caffeinemc.mods.sodium.api.config.structure.ModOptionsBuilder;
import net.minecraft.resources.Identifier;

final class SodiumResources {
    static void setIcon(ModOptionsBuilder mod) {
        var icon = id("textures/gui/icon.png");
        mod.setNonTintedIcon(icon);
    }

    static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("oldmc", path);
    }

    private SodiumResources() {}
}
