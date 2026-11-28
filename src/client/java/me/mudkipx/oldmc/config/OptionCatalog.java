package me.mudkipx.oldmc.config;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.mudkipx.oldmc.config.OldMCOptions.*;

/** UI order and availability shared by the vanilla, Sodium and OneConfig screens. */
final class OptionCatalog {
    sealed interface Setting permits OldMCOptions, Choice {
        boolean available();
    }

    record Choice<E extends Enum<E>>(BooleanSupplier availableWhen, String key, String sodiumId, String nameKey,
                                     E defaultValue, Supplier<E> read, Consumer<E> write,
                                     Function<E, String> labelKey) implements Setting {
        @Override
        public boolean available() {
            return availableWhen.getAsBoolean();
        }

        String tooltipKey() {
            return "oldmc.option." + key + ".tooltip";
        }

        E[] values() {
            return defaultValue.getDeclaringClass().getEnumConstants();
        }
    }

    static final Choice<VersionFormat> FORMAT = new Choice<>(VERSION::available, "versionFormat", "version_format",
            "oldmc.versionFormat.name", VersionFormat.DEFAULT, OldMCOptions::versionFormat,
            OldMCOptions::setVersionFormat, VersionFormat::labelKey);
    private static final Choice<TitleFriendPlacement> TITLE_FRIEND = new Choice<>(TITLE_SIDE_BUTTONS::available,
            "titleFriendButtonPlacement", "title_friend_button_placement", "oldmc.option.titleFriendButtonPlacement",
            TitleFriendPlacement.DEFAULT, OldMCOptions::titleFriendPlacement, OldMCOptions::setTitleFriendPlacement,
            value -> "oldmc.placement." + value.key());
    private static final Choice<FriendButtonPlacement> PAUSE_FRIEND = new Choice<>(PAUSE_MENU_TEXT::available,
            "friendButtonPlacement", "friend_button_placement", "oldmc.option.friendButtonPlacement",
            FriendButtonPlacement.DEFAULT, OldMCOptions::friendPlacement, OldMCOptions::setFriendPlacement,
            value -> "oldmc.placement." + value.key());

    private static final Choice<FallingLeafMode> FALLING_LEAVES = new Choice<>(() -> true,
            "hideFallingLeaves", "hide_falling_leaves", "oldmc.option.hideFallingLeaves",
            FallingLeafMode.DEFAULT, OldMCOptions::fallingLeafMode, OldMCOptions::setFallingLeafMode,
            value -> "oldmc.fallingLeaves." + value.key());

    private static final Choice<FlowerBedMode> FLOWER_BEDS = new Choice<>(() -> true,
            "hideFlowerBeds", "hide_flower_beds", "oldmc.option.hideFlowerBeds",
            FlowerBedMode.DEFAULT, OldMCOptions::flowerBedMode, OldMCOptions::setFlowerBedMode,
            value -> "oldmc.flowerBeds." + value.key());

    record Section(String key, List<Setting> settings) {
        Section(String key, Setting... settings) {
            this(key, Arrays.stream(settings).filter(Setting::available).toList());
        }
    }

    static final List<Section> SECTIONS = List.of(
            new Section("general", DIRT_BACKGROUND, CLASSIC_MAP_TEXTURES, DEBUG, SODIUM_F3_RIGHT, SODIUM_F3_VERSION,
                    IMMEDIATELY_FAST_F3_RIGHT, IMMEDIATELY_FAST_F3_VERSION, IRIS_F3_RIGHT, IRIS_F3_VERSION, VERSION, FORMAT),
            new Section("world", PERCENTAGE, GRID, FALLING_LEAVES, FLOWER_BEDS, HIDE_LEAF_LITTER),
            new Section("mobs", CLASSIC_BABY_MOBS, CLASSIC_BABY_SOUNDS, CLASSIC_BIOME_MOBS, CLASSIC_BIOME_SOUNDS, CLASSIC_BAT),
            new Section("menus", CLASSIC_CREATE_WORLD, TITLE_SIDE_BUTTONS, TITLE_FRIEND,
                    PAUSE_MENU_TEXT, PAUSE_FRIEND, REMOVE_FEEDBACK, REMOVE_PLAYER_REPORTING)
    ).stream().filter(section -> !section.settings().isEmpty()).toList();

    private OptionCatalog() {}
}
