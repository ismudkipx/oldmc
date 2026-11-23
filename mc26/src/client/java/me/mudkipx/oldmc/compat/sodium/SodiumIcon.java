package me.mudkipx.oldmc.compat.sodium;

import net.caffeinemc.mods.sodium.api.config.structure.ModOptionsBuilder;
import net.minecraft.resources.Identifier;

final class SodiumIcon {
    static void apply(ModOptionsBuilder mod) {
        var icon = Identifier.fromNamespaceAndPath("oldmc", "textures/gui/icon.png");
        mod.setNonTintedIcon(icon);
    }

    private SodiumIcon() {}
}
