package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.render.ClassicDirt;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericMessageScreen.class)
abstract class GenericMessageScreenMixin extends Screen {
    @Shadow private FocusableTextWidget textWidget;

    protected GenericMessageScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    private void oldmc$usePlainMessage(CallbackInfo ci) {
        if (!OldMCOptions.DIRT_BACKGROUND.enabled()) return;
        // Keep the original label available to the narrator, but draw the 1.20.4 text.
        removeWidget(textWidget);
        addWidget(textWidget);
        addRenderableOnly((graphics, mouseX, mouseY, delta) ->
                ClassicDirt.drawMessage(graphics, font, getTitle(), width / 2));
    }

    @Inject(method = "repositionElements", at = @At("RETURN"))
    private void oldmc$positionMessage(CallbackInfo ci) {
        if (OldMCOptions.DIRT_BACKGROUND.enabled() && textWidget != null) {
            textWidget.setY(70);
        }
    }
}
