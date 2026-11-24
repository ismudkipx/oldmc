package me.mudkipx.oldmc.config;

import java.util.Locale;

public enum FriendButtonPlacement {
    FULL_ROW, REPLACE_BUGS, HIDDEN, UNCHANGED;

    public static final FriendButtonPlacement DEFAULT = REPLACE_BUGS;

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    static FriendButtonPlacement fromKey(String key) {
        for (var placement : values()) {
            if (placement.key().equals(key)) return placement;
        }
        OldMCOptions.warnUnknownChoice("friendButtonPlacement");
        return DEFAULT;
    }
}
