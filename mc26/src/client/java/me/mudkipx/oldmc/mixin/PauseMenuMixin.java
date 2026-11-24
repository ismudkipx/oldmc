package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.gui.ClassicPauseMenu;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@Mixin(PauseScreen.class)
abstract class PauseMenuMixin {
    // Only the addChild calls in vanilla's createPauseMenu are targeted. Mod Menu adds its
    // icon to the same row from its own handler, even though it subclasses a vanilla icon button.
    @ModifyArg(method = "createPauseMenu", index = 0, require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/LinearLayout;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;"))
    private LayoutElement oldmc$rememberShortcut(LayoutElement item,
            @Share("vanillaShortcuts") LocalRef<Set<LayoutElement>> vanilla) {
        if (vanilla.get() == null) vanilla.set(Collections.newSetFromMap(new IdentityHashMap<>()));
        vanilla.get().add(item);
        return item;
    }

    @WrapOperation(method = "createPauseMenu", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;ILnet/minecraft/client/gui/layouts/LayoutSettings;)Lnet/minecraft/client/gui/layouts/LayoutElement;"))
    private LayoutElement oldmc$shortcutRow(GridLayout.RowHelper row, LayoutElement item, int span,
                                            LayoutSettings settings, Operation<LayoutElement> original,
                                            @Share("vanillaShortcuts") LocalRef<Set<LayoutElement>> vanilla) {
        if (OldMCOptions.HAS_PAUSE_MENU_ICONS && vanilla.get() != null && item instanceof LinearLayout shortcuts) {
            var layout = ClassicPauseMenu.layout(shortcuts, vanilla.get());
            if (layout == null) return item;
            item = layout;
        }
        return original.call(row, item, span, settings);
    }
}
