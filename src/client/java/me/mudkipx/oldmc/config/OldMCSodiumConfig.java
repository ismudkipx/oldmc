package me.mudkipx.oldmc.config;

import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionPageBuilder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        for (var group : OptionCatalog.SECTIONS) {
            var page = builder.createOptionPage().setName(Component.translatable("oldmc.category." + group.key()));
            for (var setting : group.settings()) {
                switch (setting) {
                    case OldMCOptions toggle -> addToggle(builder, page, toggle);
                    case OptionCatalog.Choice<?> choice -> addChoice(builder, page, choice);
                }
            }
            mod.addPage(page);
        }
    }

    private void addToggle(ConfigBuilder builder, OptionPageBuilder page, OldMCOptions option) {
        String key = option.nameKey();
        page.addOption(builder.createBooleanOption(SodiumResources.id(option.name().toLowerCase(Locale.ROOT)))
                .setName(Component.translatable(key))
                .setTooltip(Component.translatable(key + ".tooltip"))
                .setDefaultValue(option.defaultValue)
                .setBinding(value -> edits.add(() -> option.set(value)), option::enabled)
                .setStorageHandler(storage));
    }

    private <E extends Enum<E>> void addChoice(ConfigBuilder builder, OptionPageBuilder page, OptionCatalog.Choice<E> choice) {
        page.addOption(builder.createEnumOption(SodiumResources.id(choice.sodiumId()), choice.defaultValue().getDeclaringClass())
                .setName(Component.translatable(choice.nameKey()))
                .setTooltip(Component.translatable(choice.tooltipKey()))
                .setElementNameProvider(value -> Component.translatable(choice.labelKey().apply(value)))
                .setDefaultValue(choice.defaultValue())
                .setBinding(value -> edits.add(() -> choice.write().accept(value)), choice.read())
                .setStorageHandler(storage));
    }
}
