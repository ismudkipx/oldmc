package me.mudkipx.oldmc.gui;

import me.mudkipx.oldmc.config.FriendButtonPlacement;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayList;
import java.util.List;

public final class ClassicPauseMenu {
    private ClassicPauseMenu() {}

    public static LayoutElement layout(LinearLayout shortcuts) {
        boolean text = OldMCOptions.PAUSE_MENU_TEXT.enabled();
        var placement = OldMCOptions.friendPlacement();
        if (!text && placement != FriendButtonPlacement.HIDDEN
                && !OldMCOptions.REMOVE_FEEDBACK.enabled() && !OldMCOptions.REMOVE_PLAYER_REPORTING.enabled()) {
            return shortcuts;
        }

        var items = new ArrayList<LayoutElement>();
        shortcuts.visitChildren(items::add);
        LayoutElement fullRowFriend = null;
        if (placement == FriendButtonPlacement.HIDDEN) {
            items.removeIf(PauseMenuFriends.class::isInstance);
        } else if (text && placement != FriendButtonPlacement.UNCHANGED
                && Minecraft.getInstance().getPlayerSocialManager() instanceof FriendListStatus status
                && status.isFriendListEnabled()) {
            var friend = items.stream().filter(PauseMenuFriends.class::isInstance).findFirst().orElse(null);
            if (friend != null) {
                items.remove(friend);
                int slot = placement == FriendButtonPlacement.REPLACE_BUGS ? bugButtonIndex(items) : -1;
                if (slot >= 0) items.set(slot, friend);
                else fullRowFriend = friend;
            }
        }

        items.removeIf(item -> switch (buttonKey(item)) {
            case "menu.reportBugs", "menu.sendFeedback" -> OldMCOptions.REMOVE_FEEDBACK.enabled();
            case "menu.playerReporting" -> OldMCOptions.REMOVE_PLAYER_REPORTING.enabled();
            default -> false;
        });
        if (items.isEmpty() && fullRowFriend == null) return null;
        if (!text) {
            var icons = LinearLayout.horizontal().spacing(4);
            items.forEach(icons::addChild);
            return icons;
        }
        var grid = new GridLayout().columnSpacing(8).rowSpacing(4);
        var rows = grid.createRowHelper(2);
        for (int i = 0; i < items.size(); i++) {
            addTextButton(rows, items.get(i), i % 2 == 0 && i == items.size() - 1);
        }
        if (fullRowFriend != null) addTextButton(rows, fullRowFriend, true);
        return grid;
    }

    private static String buttonKey(LayoutElement item) {
        if (item instanceof AbstractButton button && button.getMessage().getContents() instanceof TranslatableContents label) {
            return label.getKey();
        }
        return "";
    }

    private static int bugButtonIndex(List<LayoutElement> items) {
        for (int i = 0; i < items.size(); i++) {
            if (buttonKey(items.get(i)).equals("menu.reportBugs")) return i;
        }
        return -1;
    }

    private static void addTextButton(GridLayout.RowHelper row, LayoutElement item, boolean fullWidth) {
        if (item instanceof AbstractButton button && !button.getMessage().getString().isEmpty()) {
            ((PauseMenuTextButton) button).oldmc$useTextLabel();
            button.setWidth(fullWidth ? 204 : 98);
        }
        row.addChild(item, fullWidth ? 2 : 1);
    }
}
