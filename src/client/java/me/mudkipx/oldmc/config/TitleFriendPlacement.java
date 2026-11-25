package me.mudkipx.oldmc.config;

import java.util.Locale;

public enum TitleFriendPlacement {
    ICON_RIGHT, ICON_LEFT, FULL_ROW, HIDDEN;

    public static final TitleFriendPlacement DEFAULT = ICON_LEFT;

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    static TitleFriendPlacement fromKey(String key) {
        if ("text_left".equals(key)) return ICON_LEFT;
        for (var placement : values()) {
            if (placement.key().equals(key)) return placement;
        }
        return null;
    }
}
