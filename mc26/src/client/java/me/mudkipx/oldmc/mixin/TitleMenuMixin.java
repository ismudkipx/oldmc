package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.config.TitleFriendPlacement;
import me.mudkipx.oldmc.gui.PauseMenuTextButton;
import me.mudkipx.oldmc.screen.WidgetLabels;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
abstract class TitleMenuMixin extends Screen {
    protected TitleMenuMixin(Component title) {
        super(title);
    }

    @Unique
    private AbstractButton oldmc$findButton(String key) {
        for (var child : children()) {
            if (child instanceof AbstractButton button
                    && key.equals(WidgetLabels.key(button.getMessage()))) return button;
        }
        return null;
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void oldmc$positionTitleButtons(CallbackInfo ci) {
        if (!OldMCOptions.IS_26_2_OR_LATER || !OldMCOptions.TITLE_SIDE_BUTTONS.enabled()) return;
        var language = oldmc$findButton("options.language");
        var accessibility = oldmc$findButton("options.accessibility");
        var options = oldmc$findButton("menu.options");
        var quit = oldmc$findButton("menu.quit");
        var friends = oldmc$findButton("gui.friends.open");
        if (language == null || accessibility == null || options == null || quit == null) return;

        var realms = oldmc$findButton("menu.online");
        var placement = OldMCOptions.titleFriendPlacement();
        int left = options.getX();
        int right = quit.getRight();
        int middle = (left + right) / 2;
        int shortcutY = language.getY();
        boolean separateRow = friends != null && placement == TitleFriendPlacement.FULL_ROW;
        // Demo menus have no Realms button to anchor the two Realms-relative placements.
        boolean keepFriendIcon = friends != null && realms == null
                && (placement == TitleFriendPlacement.ICON_RIGHT || placement == TitleFriendPlacement.ICON_LEFT);

        if (friends != null) {
            if (placement == TitleFriendPlacement.HIDDEN) {
                removeWidget(friends);
            } else if (separateRow) {
                ((PauseMenuTextButton) friends).oldmc$useTextLabel();
                friends.setWidth(right - left);
                friends.setPosition(left, shortcutY);
            } else if (keepFriendIcon) {
                friends.setX(middle - friends.getWidth() / 2);
            } else if (placement == TitleFriendPlacement.ICON_RIGHT) {
                friends.setPosition(realms.getRight() + 4, realms.getY());
            } else {
                friends.setPosition(realms.getX() - friends.getWidth() - 4, realms.getY());
            }
        }

        // 26.1 leaves 36 pixels between the last game-mode row and the Options row.
        int optionsY = separateRow || keepFriendIcon ? options.getY() : shortcutY + 12;
        language.setPosition(left - language.getWidth() - 4, optionsY);
        accessibility.setPosition(right + 4, optionsY);
        options.setY(optionsY);
        quit.setY(optionsY);

        var anchor = realms != null ? realms : oldmc$findButton("menu.resetdemo");
        if (anchor != null) {
            if (realms != null && friends != null && placement == TitleFriendPlacement.ICON_RIGHT) {
                anchor = friends;
            }
            oldmc$positionModMenuIcon(anchor);
        }
    }

    @Unique
    private void oldmc$positionModMenuIcon(AbstractButton anchor) {
        // Mod Menu adds this widget during TitleScreen.init only for its icon style.
        // Match its class by name so Mod Menu remains optional.
        for (var child : children()) {
            if (child instanceof AbstractButton button && button.getClass().getName().equals(
                    "com.terraformersmc.modmenu.gui.widget.SmallModMenuButtonWidget")) {
                button.setPosition(anchor.getRight() + 4, anchor.getY());
                return;
            }
        }
    }
}
