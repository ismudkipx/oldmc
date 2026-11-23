package me.mudkipx.oldmc.config;

import java.util.Locale;

public enum FriendButtonPlacement {
    FULL_ROW, REPLACE_BUGS, HIDDEN, UNCHANGED;

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public FriendButtonPlacement next() {
        var choices = values();
        return choices[(ordinal() + 1) % choices.length];
    }

    static FriendButtonPlacement fromKey(String key) {
        for (var placement : values()) {
            if (placement.key().equals(key)) return placement;
        }
        return FULL_ROW;
    }
}
