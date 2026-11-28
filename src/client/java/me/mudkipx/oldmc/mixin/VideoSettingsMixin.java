package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.config.OldMCSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(VideoSettingsScreen.class)
abstract class VideoSettingsMixin extends OptionsSubScreen {
    protected VideoSettingsMixin(Screen parent, Options options, Component title) {
        super(parent, options, title);
    }

    @Inject(method = "addOptions", at = @At("RETURN"))
    private void oldmc$addSettings(CallbackInfo ci) {
        list.addSmall(List.of(Button.builder(Component.translatable("oldmc.settings"),
                button -> oldmc$openSettings()).width(150).build()));
    }

    @Unique
    private void oldmc$openSettings() {
        Minecraft client = Minecraft.getInstance();
        Screen settings = new OldMCSettingsScreen(this);
        if (!OldMCOptions.IS_26_2_OR_LATER) {
            client.setScreen(settings);
            return;
        }

        // Gui gained setScreen in 26.2. The shared 26.x JAR compiles against 26.1.
        try {
            Gui.class.getMethod("setScreen", Screen.class).invoke(client.gui, settings);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not open OldMC settings", e);
        }
    }
}
