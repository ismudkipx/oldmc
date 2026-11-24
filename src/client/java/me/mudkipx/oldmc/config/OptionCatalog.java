package me.mudkipx.oldmc.config;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.mudkipx.oldmc.config.OldMCOptions.*;

/** UI order and availability shared by the vanilla, Sodium and OneConfig screens. */
final class OptionCatalog {
    sealed interface Setting permits OldMCOptions, Choice {
        boolean available();
    }

    record Choice<E extends Enum<E>>(OldMCOptions parent, String key, String sodiumId, String nameKey,
                                     E defaultValue, Supplier<E> read, Consumer<E> write,
                                     Function<E, String> labelKey) implements Setting {
        @Override
        public boolean available() {
            return parent.available();
        }

        String tooltipKey() {
            return "oldmc.option." + key + ".tooltip";
        }

        E[] values() {
            return defaultValue.getDeclaringClass().getEnumConstants();
        }
    }

    static final Choice<VersionFormat> FORMAT = new Choice<>(VERSION, "versionFormat", "version_format",
            "oldmc.versionFormat.name", VersionFormat.DEFAULT, OldMCOptions::versionFormat,
            OldMCOptions::setVersionFormat, VersionFormat::labelKey);
    private static final Choice<TitleFriendPlacement> TITLE_FRIEND = new Choice<>(TITLE_SIDE_BUTTONS,
            "titleFriendButtonPlacement", "title_friend_button_placement", "oldmc.option.titleFriendButtonPlacement",
            TitleFriendPlacement.DEFAULT, OldMCOptions::titleFriendPlacement, OldMCOptions::setTitleFriendPlacement,
            value -> "oldmc.placement." + value.key());
    private static final Choice<FriendButtonPlacement> PAUSE_FRIEND = new Choice<>(PAUSE_MENU_TEXT,
            "friendButtonPlacement", "friend_button_placement", "oldmc.option.friendButtonPlacement",
            FriendButtonPlacement.DEFAULT, OldMCOptions::friendPlacement, OldMCOptions::setFriendPlacement,
            value -> "oldmc.placement." + value.key());

    record Section(String key, List<Setting> settings) {
        Section(String key, Setting... settings) {
            this(key, Arrays.stream(settings).filter(Setting::available).toList());
        }
    }

    static final List<Section> SECTIONS = List.of(
            new Section("general", DIRT_BACKGROUND, DEBUG, SODIUM_F3_RIGHT, SODIUM_F3_VERSION,
                    IMMEDIATELY_FAST_F3_RIGHT, IMMEDIATELY_FAST_F3_VERSION, IRIS_F3_RIGHT, IRIS_F3_VERSION, VERSION, FORMAT),
            new Section("world", PERCENTAGE, GRID),
            new Section("mobs", CLASSIC_BABY_MOBS, CLASSIC_BABY_SOUNDS),
            new Section("menus", CLASSIC_CREATE_WORLD, TITLE_SIDE_BUTTONS, TITLE_FRIEND,
                    PAUSE_MENU_TEXT, PAUSE_FRIEND, REMOVE_FEEDBACK, REMOVE_PLAYER_REPORTING)
    ).stream().filter(section -> !section.settings().isEmpty()).toList();

    private OptionCatalog() {}
}
