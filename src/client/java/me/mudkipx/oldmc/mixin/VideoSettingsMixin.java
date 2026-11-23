package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.config.OldMCSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
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
                button -> Minecraft.getInstance().setScreenAndShow(new OldMCSettingsScreen(this))).width(150).build()));
    }
}
