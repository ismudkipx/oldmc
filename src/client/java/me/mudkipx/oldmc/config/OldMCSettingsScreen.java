package me.mudkipx.oldmc.config;

import me.mudkipx.oldmc.mixin.DebugProfileAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;

public final class OldMCSettingsScreen extends OptionsSubScreen {
    private final EnumMap<OldMCOptions, OptionInstance<Boolean>> controls = new EnumMap<>(OldMCOptions.class);

    private OldMCOptionList rows;
    private boolean majorVersionFormat = OldMCOptions.usesMajorVersionFormat();

    public OldMCSettingsScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable("oldmc.settings"));
        String gameVersion = FabricLoader.getInstance().getModContainer("minecraft").orElseThrow()
                .getMetadata().getVersion().getFriendlyString();
        for (var option : OldMCOptions.values()) {
            if (option == OldMCOptions.VERSION && !gameVersion.startsWith("26.")) continue;
            String key = "oldmc.option." + option.key;
            var control = OptionInstance.createBoolean(key,
                    OptionInstance.cachedConstantTooltip(Component.translatable(key + ".tooltip")), true);
            control.set(option.enabled());
            controls.put(option, control);
        }
    }

    @Override
    protected void addContents() {
        rows = layout.addToContents(new OldMCOptionList(minecraft, width, layout));
        addOptions();
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        if (rows != null) rows.updateSize(width, layout);
    }

    @Override
    protected void addOptions() {
        controls.forEach((option, control) -> {
            // Boolean OptionInstances create vanilla on/off cycle buttons.
            @SuppressWarnings("unchecked")
            var toggle = (CycleButton<Boolean>) control.createButton(options, 0, 0, OldMCOptionList.VALUE_WIDTH);
            rows.addOption(toggle, Component.translatable("oldmc.option." + option.key),
                    () -> !control.get(), () -> {
                        control.set(true);
                        toggle.setValue(true);
                    });
        });
        if (controls.containsKey(OldMCOptions.VERSION)) {
            var button = Button.builder(formatLabel(), clicked -> {
                majorVersionFormat = !majorVersionFormat;
                clicked.setMessage(formatLabel());
            }).width(OldMCOptionList.VALUE_WIDTH).build();
            button.setTooltip(Tooltip.create(Component.translatable("oldmc.option.versionFormat.tooltip")));
            rows.addOption(button, Component.translatable("oldmc.versionFormat.name"),
                    () -> majorVersionFormat, () -> {
                        majorVersionFormat = false;
                        button.setMessage(formatLabel());
                    });
        }
    }

    private Component formatLabel() {
        return Component.translatable("oldmc.option.versionFormat",
                Component.translatable(majorVersionFormat ? "oldmc.format.major" : "oldmc.format.year"));
    }

    @Override
    public void removed() {
        super.removed();
        boolean debugBefore = OldMCOptions.DEBUG.enabled();
        boolean versionBefore = OldMCOptions.VERSION.enabled();
        boolean changed = false;
        for (var entry : controls.entrySet()) {
            boolean value = entry.getValue().get();
            if (entry.getKey().enabled() != value) {
                entry.getKey().set(value);
                changed = true;
            }
        }
        boolean formatChanged = controls.containsKey(OldMCOptions.VERSION)
                && majorVersionFormat != OldMCOptions.usesMajorVersionFormat();
        if (formatChanged) OldMCOptions.setMajorVersionFormat(majorVersionFormat);
        if (changed || formatChanged) OldMCOptions.save();
        var client = Minecraft.getInstance();
        if (debugBefore != OldMCOptions.DEBUG.enabled()) {
            var entries = client.debugEntries;
            if (entries.isUsingProfile(DebugScreenProfile.DEFAULT) && !DefaultDebugProfile.matches(entries)) {
                // Vanilla saves named profiles by name only. Preserve these exact entries on restart.
                ((DebugProfileAccessor) entries).oldmc$setProfile(null);
                entries.save();
            }
        }
        if (versionBefore != OldMCOptions.VERSION.enabled() || (formatChanged && versionBefore)) client.updateTitle();
    }
}
