package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.mudkipx.oldmc.gui.PauseMenuFriends;
import me.mudkipx.oldmc.gui.PauseMenuTextButton;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractButton.class)
abstract class PauseButtonMixin extends AbstractWidget implements PauseMenuTextButton {
    @Unique private boolean oldmc$textLabel;
    @Shadow protected abstract void extractDefaultSprite(GuiGraphicsExtractor graphics);
    @Shadow protected abstract void extractDefaultLabel(ActiveTextCollector text);

    protected PauseButtonMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public void oldmc$useTextLabel() {
        oldmc$textLabel = true;
    }

    @WrapWithCondition(method = "extractWidgetRenderState", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/AbstractButton;extractContents(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"))
    private boolean oldmc$drawTextButton(AbstractButton button, GuiGraphicsExtractor graphics,
                                         int mouseX, int mouseY, float delta) {
        if (!oldmc$textLabel) return true;
        extractDefaultSprite(graphics);
        var text = graphics.textRendererForWidget(button, GuiGraphicsExtractor.HoveredTextEffects.NONE);
        int requests = button instanceof PauseMenuFriends friend ? friend.oldmc$incomingRequests() : 0;
        if (button.active && requests > 0) {
            extractScrollingStringOverContents(text, button.getMessage().copy().append(" (" + requests + ")"), 2);
        } else {
            extractDefaultLabel(text);
        }
        return false;
    }
}
