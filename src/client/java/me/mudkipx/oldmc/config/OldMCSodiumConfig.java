package me.mudkipx.oldmc.config;

import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class OldMCSodiumConfig implements ConfigEntryPoint {
    // Sodium invokes all changed bindings before flushing their shared storage handler.
    private final List<Runnable> edits = new ArrayList<>();
    private final StorageEventHandler storage = () -> {
        try {
            OldMCOptions.applyChanges(() -> edits.forEach(Runnable::run));
        } finally {
            edits.clear();
        }
    };

    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        var mod = builder.registerOwnModOptions();
        mod.formatVersion(version -> version.split("[-+]", 2)[0]);
        SodiumResources.setIcon(mod);

        var world = page(builder, "world");
        addToggle(builder, world, OldMCOptions.PERCENTAGE);
        addToggle(builder, world, OldMCOptions.GRID);
        mod.addPage(world);

        var general = page(builder, "general");
        addToggle(builder, general, OldMCOptions.DIRT_BACKGROUND);
        addToggle(builder, general, OldMCOptions.DEBUG);
        addToggle(builder, general, OldMCOptions.SODIUM_F3_RIGHT);
        if (OldMCOptions.SODIUM_F3_VERSION.available()) {
            addToggle(builder, general, OldMCOptions.SODIUM_F3_VERSION);
        }
        if (OldMCOptions.IMMEDIATELY_FAST_F3_RIGHT.available()) {
            addToggle(builder, general, OldMCOptions.IMMEDIATELY_FAST_F3_RIGHT);
        }
        if (OldMCOptions.IMMEDIATELY_FAST_F3_VERSION.available()) {
            addToggle(builder, general, OldMCOptions.IMMEDIATELY_FAST_F3_VERSION);
        }
        if (OldMCOptions.IRIS_F3_RIGHT.available()) {
            addToggle(builder, general, OldMCOptions.IRIS_F3_RIGHT);
        }
        if (OldMCOptions.IRIS_F3_VERSION.available()) {
            addToggle(builder, general, OldMCOptions.IRIS_F3_VERSION);
        }
        if (OldMCOptions.VERSION.available()) {
            addToggle(builder, general, OldMCOptions.VERSION);
            addChoice(builder, general, "version_format", "oldmc.versionFormat.name", "oldmc.option.versionFormat.tooltip",
                    VersionFormat.of(OldMCOptions.DEFAULT_MAJOR_VERSION_FORMAT),
                    () -> VersionFormat.of(OldMCOptions.usesMajorVersionFormat()),
                    value -> OldMCOptions.setMajorVersionFormat(value == VersionFormat.MAJOR),
                    value -> value == VersionFormat.MAJOR ? "oldmc.format.major" : "oldmc.format.year");
        }
        mod.addPage(general);

        var menus = page(builder, "menus");
        if (OldMCOptions.TITLE_SIDE_BUTTONS.available()) {
            addToggle(builder, menus, OldMCOptions.TITLE_SIDE_BUTTONS);
            String key = "oldmc.option.titleFriendButtonPlacement";
            addChoice(builder, menus, "title_friend_button_placement", key, key + ".tooltip",
                    TitleFriendPlacement.DEFAULT, OldMCOptions::titleFriendPlacement,
                    OldMCOptions::setTitleFriendPlacement, value -> "oldmc.placement." + value.key());
        }
        if (OldMCOptions.PAUSE_MENU_TEXT.available()) {
            addToggle(builder, menus, OldMCOptions.PAUSE_MENU_TEXT);
            String key = "oldmc.option.friendButtonPlacement";
            addChoice(builder, menus, "friend_button_placement", key, key + ".tooltip",
                    FriendButtonPlacement.DEFAULT, OldMCOptions::friendPlacement,
                    OldMCOptions::setFriendPlacement, value -> "oldmc.placement." + value.key());
        }
        addToggle(builder, menus, OldMCOptions.REMOVE_FEEDBACK);
        addToggle(builder, menus, OldMCOptions.REMOVE_PLAYER_REPORTING);
        mod.addPage(menus);
    }

    private static OptionPageBuilder page(ConfigBuilder builder, String category) {
        return builder.createOptionPage().setName(Component.translatable("oldmc.category." + category));
    }

    private void addToggle(ConfigBuilder builder, OptionPageBuilder page, OldMCOptions option) {
        String key = "oldmc.option." + option.key;
        page.addOption(builder.createBooleanOption(SodiumResources.id(option.name().toLowerCase(Locale.ROOT)))
                .setName(Component.translatable(key))
                .setTooltip(Component.translatable(key + ".tooltip"))
                .setDefaultValue(option.defaultValue)
                .setBinding(value -> edits.add(() -> option.set(value)), option::enabled)
                .setStorageHandler(storage));
    }

    private <E extends Enum<E>> void addChoice(ConfigBuilder builder, OptionPageBuilder page, String id,
                                                String name, String tooltip, E defaultValue, Supplier<E> get,
                                                Consumer<E> set, Function<E, String> labelKey) {
        page.addOption(builder.createEnumOption(SodiumResources.id(id), defaultValue.getDeclaringClass())
                .setName(Component.translatable(name))
                .setTooltip(Component.translatable(tooltip))
                .setElementNameProvider(value -> Component.translatable(labelKey.apply(value)))
                .setDefaultValue(defaultValue)
                .setBinding(value -> edits.add(() -> set.accept(value)), get)
                .setStorageHandler(storage));
    }

    private enum VersionFormat {
        YEAR, MAJOR;

        static VersionFormat of(boolean major) {
            return major ? MAJOR : YEAR;
        }
    }
}
