package me.mudkipx.oldmc.compat.sodium;

import net.caffeinemc.mods.sodium.api.config.structure.ModOptionsBuilder;
import net.minecraft.resources.ResourceLocation;

final class SodiumIcon {
    static void apply(ModOptionsBuilder mod) {
        var icon = ResourceLocation.fromNamespaceAndPath("oldmc", "textures/gui/icon.png");
        try {
            mod.setNonTintedIcon(icon);
        } catch (NoSuchMethodError missingColorIconApi) {
            // Sodium 0.8.0 only provides the theme-tinted icon API.
            mod.setIcon(icon);
        }
    }

    private SodiumIcon() {}
}
