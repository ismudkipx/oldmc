package me.mudkipx.oldmc.config;

import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.ConfigEntrypoint;
import org.polyfrost.oneconfig.api.config.v1.Properties;
import org.polyfrost.oneconfig.api.config.v1.Property;
import org.polyfrost.oneconfig.api.config.v1.Tree;
import org.polyfrost.oneconfig.api.config.v1.Visualizer;

import java.util.Arrays;
import java.util.function.Consumer;
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
            for (var group : OptionCatalog.SECTIONS) {
                for (var setting : group.settings()) {
                    switch (setting) {
                        case OldMCOptions toggle -> addToggle(root, group.key(), toggle);
                        case OptionCatalog.Choice<?> choice -> addChoice(root, group.key(), choice);
                    }
                }
            }
            return root;
        }

        private static void addToggle(Tree tree, String category, OldMCOptions toggle) {
            String name = toggle.nameKey();
            var property = option(toggle.key, name, name + ".tooltip", category, toggle::enabled, toggle::set,
                    toggle.defaultValue, Boolean.class);
            property.addMetadata("visualizer", Visualizer.SwitchVisualizer.class);
            tree.put(property);
        }

        private static <E extends Enum<E>> void addChoice(Tree tree, String category, OptionCatalog.Choice<E> choice) {
            E[] values = choice.values();
            Property<?> property;
            if (choice == OptionCatalog.FORMAT) {
                // Keep the existing integer-backed OneConfig property for versionFormat.
                property = option(choice.key(), choice.nameKey(), choice.tooltipKey(), category,
                        () -> choice.read().get().ordinal(), index -> choice.write().accept(values[index]),
                        choice.defaultValue().ordinal(), Integer.class);
            } else {
                property = option(choice.key(), choice.nameKey(), choice.tooltipKey(), category,
                        choice.read(), choice.write(), choice.defaultValue(), choice.defaultValue().getDeclaringClass());
            }
            String[] labelKeys = Arrays.stream(values).map(choice.labelKey()).toArray(String[]::new);
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
