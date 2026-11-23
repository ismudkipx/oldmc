package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.gui.ClassicPauseMenu;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PauseScreen.class)
abstract class PauseMenuMixin {
    @WrapOperation(method = "createPauseMenu", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;ILnet/minecraft/client/gui/layouts/LayoutSettings;)Lnet/minecraft/client/gui/layouts/LayoutElement;"))
    private LayoutElement oldmc$shortcutRow(GridLayout.RowHelper row, LayoutElement item, int span,
                                            LayoutSettings settings, Operation<LayoutElement> original) {
        if (OldMCOptions.HAS_PAUSE_MENU_ICONS && item instanceof LinearLayout shortcuts) {
            var layout = ClassicPauseMenu.layout(shortcuts);
            if (layout == null) return item;
            item = layout;
        }
        return original.call(row, item, span, settings);
    }
}
