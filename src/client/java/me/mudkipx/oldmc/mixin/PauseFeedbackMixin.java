package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.mudkipx.oldmc.config.OldMCModMenu;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.fabricmc.loader.api.FabricLoader;
import me.mudkipx.oldmc.screen.WidgetLabels;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
abstract class PauseFeedbackMixin {
    @Unique private Button oldmc$iconAnchor;
    @Unique private LayoutElement oldmc$iconRowButton;

    @Unique
    private static boolean oldmc$removeFeedback(boolean inPauseMenu) {
        // Mod Menu's Replace mode needs the vanilla feedback widget as its insertion point.
        return OldMCOptions.REMOVE_FEEDBACK.enabled()
                && !(inPauseMenu && FabricLoader.getInstance().isModLoaded("modmenu")
                    && OldMCModMenu.replacesPauseFeedback());
    }

    // Older menus have separate feedback buttons, or a feedback submenu paired with a custom dialog.
    @Inject(method = "addFeedbackButtons", at = @At("HEAD"), cancellable = true, require = 0)
    private static void oldmc$skipFeedback(Screen screen, GridLayout.RowHelper row, CallbackInfo ci) {
        boolean inPauseMenu = screen instanceof PauseScreen;
        if (oldmc$removeFeedback(inPauseMenu)) {
            if (inPauseMenu && FabricLoader.getInstance().isModLoaded("modmenu")) {
                ((PauseFeedbackMixin) (Object) screen).oldmc$iconAnchor = OldMCModMenu.addPauseFeedbackAnchor(row);
            }
            ci.cancel();
        }
    }

    @WrapOperation(method = "addFeedbackSubscreenAndCustomDialogButtons", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 0))
    private LayoutElement oldmc$feedbackSubmenu(GridLayout.RowHelper row, LayoutElement item, Operation<LayoutElement> original) {
        return oldmc$removeFeedback(true) ? item : original.call(row, item);
    }

    @WrapOperation(method = "addFeedbackSubscreenAndCustomDialogButtons", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 1))
    private LayoutElement oldmc$customDialog(GridLayout.RowHelper row, LayoutElement item, Operation<LayoutElement> original) {
        if (oldmc$removeFeedback(true) && item instanceof Button button) {
            button.setWidth(204);
            LayoutElement dialog = row.addChild(item, 2);
            if (FabricLoader.getInstance().isModLoaded("modmenu")) {
                oldmc$iconAnchor = OldMCModMenu.addPauseFeedbackAnchor(row);
                if (oldmc$iconAnchor != null) oldmc$iconRowButton = dialog;
            }
            return dialog;
        }
        return original.call(row, item);
    }

    @WrapOperation(method = "createPauseMenu", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;"))
    private LayoutElement oldmc$pauseButton(GridLayout.RowHelper row, LayoutElement item, Operation<LayoutElement> original) {
        if (item instanceof Button button) {
            String key = WidgetLabels.key(button.getMessage());
            if (OldMCOptions.REMOVE_PLAYER_REPORTING.enabled() && key.equals("menu.playerReporting")) return item;
            if (oldmc$iconAnchor != null && oldmc$iconRowButton == null && key.equals("menu.options")) {
                oldmc$iconRowButton = original.call(row, item);
                return oldmc$iconRowButton;
            }
        }
        return original.call(row, item);
    }

    @Inject(method = "createPauseMenu", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/FrameLayout;alignInRectangle(Lnet/minecraft/client/gui/layouts/LayoutElement;IIIIFF)V",
            shift = At.Shift.AFTER))
    private void oldmc$alignModMenuIcon(CallbackInfo ci) {
        if (oldmc$iconRowButton != null && oldmc$iconAnchor != null) {
            oldmc$iconAnchor.setY(oldmc$iconRowButton.getY());
        }
        oldmc$iconAnchor = null;
        oldmc$iconRowButton = null;
    }
}
