package me.mudkipx.oldmc.config;

import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.ConfigEntrypoint;
import org.polyfrost.oneconfig.api.config.v1.Properties;
import org.polyfrost.oneconfig.api.config.v1.Property;
import org.polyfrost.oneconfig.api.config.v1.Tree;
import org.polyfrost.oneconfig.api.config.v1.Visualizer;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class OldMCOneConfig implements ConfigEntrypoint {
    @Override
    public void registerConfigs(Context context) {
        context.register(new Settings());
    }

    private static final class Settings extends Config {
        Settings() {
            super("oldmc.json", "assets/oldmc/textures/gui/icon.png", "OldMC", Category.QOL);
        }

        @Override
        protected void restoreDefaults() {
            // OneConfig calls this when creating a profile. OldMC settings are global.
            // Explicit resets in the options UI use each property's "default" metadata.
        }

        @Override
        protected Tree makeTree() {
            var root = Tree.tree(id);
            // This backend hook keeps OldMC's existing JSON as the sole settings file.
            root.addMetadata("custom_save", (Runnable) OldMCOptions::saveIfDirty);
            addToggle(root, "world", OldMCOptions.PERCENTAGE);
            addToggle(root, "world", OldMCOptions.GRID);
            addToggle(root, "general", OldMCOptions.DIRT_BACKGROUND);
            addToggle(root, "general", OldMCOptions.DEBUG);
            if (OldMCOptions.SODIUM_F3_RIGHT.available()) {
                addToggle(root, "general", OldMCOptions.SODIUM_F3_RIGHT);
            }
            if (OldMCOptions.SODIUM_F3_VERSION.available()) {
                addToggle(root, "general", OldMCOptions.SODIUM_F3_VERSION);
            }
            if (OldMCOptions.IMMEDIATELY_FAST_F3_RIGHT.available()) {
                addToggle(root, "general", OldMCOptions.IMMEDIATELY_FAST_F3_RIGHT);
            }
            if (OldMCOptions.IMMEDIATELY_FAST_F3_VERSION.available()) {
                addToggle(root, "general", OldMCOptions.IMMEDIATELY_FAST_F3_VERSION);
            }
            if (OldMCOptions.IRIS_F3_RIGHT.available()) {
                addToggle(root, "general", OldMCOptions.IRIS_F3_RIGHT);
            }
            if (OldMCOptions.IRIS_F3_VERSION.available()) {
                addToggle(root, "general", OldMCOptions.IRIS_F3_VERSION);
            }
            if (OldMCOptions.VERSION.available()) {
                addToggle(root, "general", OldMCOptions.VERSION);
                var format = option("majorVersionFormat", "oldmc.versionFormat.name", "oldmc.option.versionFormat.tooltip",
                        "general", () -> OldMCOptions.usesMajorVersionFormat() ? 1 : 0,
                        index -> OldMCOptions.setMajorVersionFormat(index == 1),
                        OldMCOptions.DEFAULT_MAJOR_VERSION_FORMAT ? 1 : 0, Integer.class);
                addChoices(root, format, new String[]{"oldmc.format.year", "oldmc.format.major"});
            }
            if (OldMCOptions.TITLE_SIDE_BUTTONS.available()) {
                addToggle(root, "menus", OldMCOptions.TITLE_SIDE_BUTTONS);
                addPlacement(root, "titleFriendButtonPlacement", OldMCOptions::titleFriendPlacement,
                        OldMCOptions::setTitleFriendPlacement, TitleFriendPlacement.DEFAULT,
                        TitleFriendPlacement::key);
            }
            if (OldMCOptions.PAUSE_MENU_TEXT.available()) {
                addToggle(root, "menus", OldMCOptions.PAUSE_MENU_TEXT);
                addPlacement(root, "friendButtonPlacement", OldMCOptions::friendPlacement,
                        OldMCOptions::setFriendPlacement, FriendButtonPlacement.DEFAULT,
                        FriendButtonPlacement::key);
            }
            addToggle(root, "menus", OldMCOptions.REMOVE_FEEDBACK);
            addToggle(root, "menus", OldMCOptions.REMOVE_PLAYER_REPORTING);
            return root;
        }

        private static void addToggle(Tree tree, String category, OldMCOptions toggle) {
            String name = "oldmc.option." + toggle.key;
            var property = option(toggle.key, name, name + ".tooltip", category, toggle::enabled, toggle::set,
                    toggle.defaultValue, Boolean.class);
            property.addMetadata("visualizer", Visualizer.SwitchVisualizer.class);
            tree.put(property);
        }

        private static <E extends Enum<E>> void addPlacement(Tree tree, String id, Supplier<E> read, Consumer<E> write,
                                                              E defaultValue, Function<E, String> key) {
            String name = "oldmc.option." + id;
            var property = option(id, name, name + ".tooltip", "menus", read, write, defaultValue, defaultValue.getDeclaringClass());
            String[] labels = Arrays.stream(defaultValue.getDeclaringClass().getEnumConstants())
                    .map(value -> "oldmc.placement." + key.apply(value)).toArray(String[]::new);
            addChoices(tree, property, labels);
        }

        private static void addChoices(Tree tree, Property<?> property, String[] labelKeys) {
            property.addMetadata("visualizer", Visualizer.DropdownVisualizer.class);
            property.addMetadata("options", labelKeys);
            property.addMetadata("optionsKey", labelKeys);
            tree.put(property);
        }

        private static <T> Property<T> option(String id, String name, String tooltip, String category,
                                              Supplier<T> read, Consumer<T> write, T defaultValue, Class<T> type) {
            var property = Properties.functional(read, value -> OldMCOptions.applyChanges(() -> write.accept(value)),
                    id, name, tooltip, type);
            property.addMetadata("default", defaultValue);
            property.addMetadata("category", "oldmc.category." + category);
            return property;
        }
    }
}
