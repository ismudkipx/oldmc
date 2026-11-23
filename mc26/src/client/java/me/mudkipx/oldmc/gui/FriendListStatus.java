package me.mudkipx.oldmc.gui;

/** The vanilla social manager implements this method on 26.2 and later. */
public interface FriendListStatus {
    default boolean isFriendListEnabled() {
        return false;
    }
}
