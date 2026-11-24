package me.mudkipx.oldmc.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

public final class OldMCModMenu implements ModMenuApi {
    public static boolean replacesPauseFeedback() {
        return ModMenuConfig.MODIFY_GAME_MENU.getValue()
                && ModMenuConfig.GAME_MENU_BUTTON_STYLE.getValue() == ModMenuConfig.GameMenuButtonStyle.REPLACE;
    }

    public static Button addPauseFeedbackAnchor(GridLayout.RowHelper row) {
        if (!ModMenuConfig.MODIFY_GAME_MENU.getValue()) return null;
        var style = ModMenuConfig.GAME_MENU_BUTTON_STYLE.getValue();
        if (style != ModMenuConfig.GameMenuButtonStyle.INSERT && style != ModMenuConfig.GameMenuButtonStyle.ICON) return null;

        // Mod Menu scans the grid in insertion order. Register its marker without
        // reserving a cell in the row helper. Only ICON reads the marker's Y.
        Button anchor = Button.builder(Component.translatable("menu.sendFeedback"), button -> {})
                .size(0, 0).build();
        anchor.visible = false;
        anchor.active = false;
        row.getGrid().addChild(anchor, 0, 0);
        return style == ModMenuConfig.GameMenuButtonStyle.ICON ? anchor : null;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return OldMCSettingsScreen::new;
    }
}
