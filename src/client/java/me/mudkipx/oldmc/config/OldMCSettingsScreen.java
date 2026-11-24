package me.mudkipx.oldmc.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class OldMCSettingsScreen extends OptionsSubScreen {
    private final EnumMap<OldMCOptions, OptionInstance<Boolean>> controls = new EnumMap<>(OldMCOptions.class);
    private final Map<OptionCatalog.Choice<?>, Integer> choices = new HashMap<>();

    private OldMCOptionList rows;
    private EditBox searchBox;

    public OldMCSettingsScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable("oldmc.settings"));
    }

    @Override
    protected void addTitle() {
        var header = layout.addToHeader(LinearLayout.vertical().spacing(4));
        header.defaultCellSetting().alignHorizontallyCenter();
        header.addChild(new StringWidget(title, font));
        var searchLabel = Component.translatable("oldmc.search");
        searchBox = header.addChild(new EditBox(font, 0, 0, 200, 15, searchBox, searchLabel));
        searchBox.setHint(searchLabel.copy().withStyle(EditBox.SEARCH_HINT_STYLE));
        searchBox.setResponder(query -> {
            if (rows != null) rows.updateSearch(query);
        });
        layout.setHeaderHeight(36);
    }

    @Override
    protected void setInitialFocus() {
        setInitialFocus(searchBox);
    }

    @Override
    protected void addContents() {
        rows = layout.addToContents(new OldMCOptionList(minecraft, width, layout));
        addOptions();
        rows.updateSearch(searchBox.getValue());
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        if (rows != null) rows.updateSize(width, layout);
    }

    @Override
    protected void addOptions() {
        for (var group : OptionCatalog.SECTIONS) {
            var section = rows.addSection(group.key());
            for (var setting : group.settings()) {
                switch (setting) {
                    case OldMCOptions toggle -> addToggle(section, toggle);
                    case OptionCatalog.Choice<?> choice -> addChoice(section, choice);
                }
            }
        }
    }

    private void addToggle(OldMCOptionList.Section section, OldMCOptions option) {
        var control = controls.computeIfAbsent(option, key -> {
            var instance = OptionInstance.createBoolean(key.nameKey(),
                    OptionInstance.cachedConstantTooltip(Component.translatable(key.nameKey() + ".tooltip")), key.defaultValue);
            instance.set(key.enabled());
            return instance;
        });
        @SuppressWarnings("unchecked")
        var toggle = (CycleButton<Boolean>) control.createButton(options, 0, 0, OldMCOptionList.VALUE_WIDTH);
        section.addOption(toggle, option.nameKey(), () -> control.get() != option.defaultValue, () -> {
            control.set(option.defaultValue);
            toggle.setValue(option.defaultValue);
        });
    }

    private <E extends Enum<E>> void addChoice(OldMCOptionList.Section section, OptionCatalog.Choice<E> choice) {
        E[] values = choice.values();
        choices.putIfAbsent(choice, choice.read().get().ordinal());
        Supplier<Component> label = () -> {
            E value = values[choices.get(choice)];
            var text = Component.translatable(choice.labelKey().apply(value));
            if (choice == OptionCatalog.FORMAT) return Component.translatable("oldmc.option.versionFormat", text);
            return Component.translatable("options.generic_value", Component.translatable(choice.nameKey()), text);
        };
        var button = Button.builder(label.get(), clicked -> {
            choices.put(choice, (choices.get(choice) + 1) % values.length);
            clicked.setMessage(label.get());
        }).width(OldMCOptionList.VALUE_WIDTH).build();
        button.setTooltip(Tooltip.create(Component.translatable(choice.tooltipKey())));
        section.addOption(button, choice.nameKey(), () -> choices.get(choice) != choice.defaultValue().ordinal(), () -> {
            choices.put(choice, choice.defaultValue().ordinal());
            button.setMessage(label.get());
        });
    }

    private static <E extends Enum<E>> void applyChoice(OptionCatalog.Choice<E> choice, int index) {
        choice.write().accept(choice.values()[index]);
    }

    @Override
    public void removed() {
        super.removed();
        OldMCOptions.applyChanges(() -> {
            controls.forEach((option, control) -> option.set(control.get()));
            choices.forEach(OldMCSettingsScreen::applyChoice);
        });
    }
}
