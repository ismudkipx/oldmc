package me.mudkipx.oldmc.compat.sodium;

import me.mudkipx.oldmc.config.OldMCSettingsScreen;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class OldMCSodiumConfig implements ConfigEntryPoint {
    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        var mod = builder.registerOwnModOptions();
        mod.formatVersion(version -> version.split("[-+]", 2)[0]);
        SodiumIcon.apply(mod);
        mod.addPage(builder.createExternalPage()
                .setName(Component.translatable("oldmc.settings"))
                .setScreenConsumer(parent -> Minecraft.getInstance().setScreenAndShow(new OldMCSettingsScreen(parent))));
    }
}
