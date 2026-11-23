package me.mudkipx.oldmc.config;

import net.caffeinemc.mods.sodium.api.config.structure.ModOptionsBuilder;
import net.minecraft.resources.ResourceLocation;

final class SodiumResources {
    static void setIcon(ModOptionsBuilder mod) {
        var icon = id("textures/gui/icon.png");
        try {
            mod.setNonTintedIcon(icon);
        } catch (NoSuchMethodError missingColorIconApi) {
            // Sodium 0.8.0 only provides the theme-tinted icon API.
            mod.setIcon(icon);
        }
    }

    static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("oldmc", path);
    }

    private SodiumResources() {}
}
