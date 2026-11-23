package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.render.ClassicDirt;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GenericMessageScreen.class, LevelLoadingScreen.class})
abstract class LoadingBackgroundMixin {
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void oldmc$drawDirt(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (OldMCOptions.DIRT_BACKGROUND.enabled()) {
            ClassicDirt.drawBackground(graphics);
            ci.cancel();
        }
    }
}
