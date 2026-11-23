package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
abstract class PauseFeedbackMixin {
    // Older menus have separate feedback buttons, or a feedback submenu paired with a custom dialog.
    @Inject(method = "addFeedbackButtons", at = @At("HEAD"), cancellable = true, require = 0)
    private static void oldmc$skipFeedback(CallbackInfo ci) {
        if (OldMCOptions.REMOVE_FEEDBACK.enabled()) ci.cancel();
    }

    @WrapOperation(method = "addFeedbackSubscreenAndCustomDialogButtons", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 0))
    private LayoutElement oldmc$feedbackSubmenu(GridLayout.RowHelper row, LayoutElement item, Operation<LayoutElement> original) {
        return OldMCOptions.REMOVE_FEEDBACK.enabled() ? item : original.call(row, item);
    }

    @WrapOperation(method = "addFeedbackSubscreenAndCustomDialogButtons", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 1))
    private LayoutElement oldmc$customDialog(GridLayout.RowHelper row, LayoutElement item, Operation<LayoutElement> original) {
        if (OldMCOptions.REMOVE_FEEDBACK.enabled() && item instanceof Button button) {
            button.setWidth(204);
            return row.addChild(item, 2);
        }
        return original.call(row, item);
    }

    @WrapOperation(method = "createPauseMenu", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;"))
    private LayoutElement oldmc$playerReporting(GridLayout.RowHelper row, LayoutElement item, Operation<LayoutElement> original) {
        if (OldMCOptions.REMOVE_PLAYER_REPORTING.enabled() && item instanceof Button button
                && button.getMessage().getContents() instanceof TranslatableContents label
                && label.getKey().equals("menu.playerReporting")) return item;
        return original.call(row, item);
    }
}
