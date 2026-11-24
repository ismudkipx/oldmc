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

public final class OldMCSettingsScreen extends OptionsSubScreen {
    private final EnumMap<OldMCOptions, OptionInstance<Boolean>> controls = new EnumMap<>(OldMCOptions.class);

    private OldMCOptionList rows;
    private EditBox searchBox;
    private FriendButtonPlacement friendPlacement = OldMCOptions.friendPlacement();
    private TitleFriendPlacement titlePlacement = OldMCOptions.titleFriendPlacement();
    private boolean majorVersionFormat = OldMCOptions.usesMajorVersionFormat();

    public OldMCSettingsScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable("oldmc.settings"));
        for (var option : OldMCOptions.values()) {
            if (!option.available()) continue;
            String key = "oldmc.option." + option.key;
            var control = OptionInstance.createBoolean(key,
                    OptionInstance.cachedConstantTooltip(Component.translatable(key + ".tooltip")), option.defaultValue);
            control.set(option.enabled());
            controls.put(option, control);
        }
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
        var world = rows.addSection("world");
        addToggle(world, OldMCOptions.PERCENTAGE);
        addToggle(world, OldMCOptions.GRID);

        if (controls.containsKey(OldMCOptions.CLASSIC_BABY_MOBS)) {
            addToggle(rows.addSection("mobs"), OldMCOptions.CLASSIC_BABY_MOBS);
        }

        var general = rows.addSection("general");
        addToggle(general, OldMCOptions.DIRT_BACKGROUND);
        addToggle(general, OldMCOptions.DEBUG);
        if (controls.containsKey(OldMCOptions.SODIUM_F3_RIGHT)) {
            addToggle(general, OldMCOptions.SODIUM_F3_RIGHT);
        }
        if (controls.containsKey(OldMCOptions.SODIUM_F3_VERSION)) {
            addToggle(general, OldMCOptions.SODIUM_F3_VERSION);
        }
        if (controls.containsKey(OldMCOptions.IMMEDIATELY_FAST_F3_RIGHT)) {
            addToggle(general, OldMCOptions.IMMEDIATELY_FAST_F3_RIGHT);
        }
        if (controls.containsKey(OldMCOptions.IMMEDIATELY_FAST_F3_VERSION)) {
            addToggle(general, OldMCOptions.IMMEDIATELY_FAST_F3_VERSION);
        }
        if (controls.containsKey(OldMCOptions.IRIS_F3_RIGHT)) {
            addToggle(general, OldMCOptions.IRIS_F3_RIGHT);
        }
        if (controls.containsKey(OldMCOptions.IRIS_F3_VERSION)) {
            addToggle(general, OldMCOptions.IRIS_F3_VERSION);
        }
        if (controls.containsKey(OldMCOptions.VERSION)) {
            addToggle(general, OldMCOptions.VERSION);
            addVersionFormat(general);
        }

        var menus = rows.addSection("menus");
        addToggle(menus, OldMCOptions.CLASSIC_CREATE_WORLD);
        if (controls.containsKey(OldMCOptions.TITLE_SIDE_BUTTONS)) {
            addToggle(menus, OldMCOptions.TITLE_SIDE_BUTTONS);
            addTitleFriendPlacement(menus);
        }
        if (controls.containsKey(OldMCOptions.PAUSE_MENU_TEXT)) {
            addToggle(menus, OldMCOptions.PAUSE_MENU_TEXT);
            addFriendPlacement(menus);
        }
        addToggle(menus, OldMCOptions.REMOVE_FEEDBACK);
        addToggle(menus, OldMCOptions.REMOVE_PLAYER_REPORTING);
    }

    private void addToggle(OldMCOptionList.Section section, OldMCOptions option) {
        var control = controls.get(option);
        @SuppressWarnings("unchecked")
        var toggle = (CycleButton<Boolean>) control.createButton(options, 0, 0, OldMCOptionList.VALUE_WIDTH);
        section.addOption(toggle, "oldmc.option." + option.key, () -> control.get() != option.defaultValue, () -> {
            control.set(option.defaultValue);
            toggle.setValue(option.defaultValue);
        });
    }

    private void addVersionFormat(OldMCOptionList.Section section) {
        var button = Button.builder(formatLabel(), clicked -> {
            majorVersionFormat = !majorVersionFormat;
            clicked.setMessage(formatLabel());
        }).width(OldMCOptionList.VALUE_WIDTH).build();
        button.setTooltip(Tooltip.create(Component.translatable("oldmc.option.versionFormat.tooltip")));
        section.addOption(button, "oldmc.versionFormat.name",
                () -> majorVersionFormat != OldMCOptions.DEFAULT_MAJOR_VERSION_FORMAT, () -> {
                    majorVersionFormat = OldMCOptions.DEFAULT_MAJOR_VERSION_FORMAT;
                    button.setMessage(formatLabel());
                });
    }

    private void addTitleFriendPlacement(OldMCOptionList.Section section) {
        String key = "oldmc.option.titleFriendButtonPlacement";
        var name = Component.translatable(key);
        var button = Button.builder(titlePlacementLabel(name), clicked -> {
            titlePlacement = titlePlacement.next();
            clicked.setMessage(titlePlacementLabel(name));
        }).width(OldMCOptionList.VALUE_WIDTH).build();
        button.setTooltip(Tooltip.create(Component.translatable("oldmc.option.titleFriendButtonPlacement.tooltip")));
        section.addOption(button, key, () -> titlePlacement != TitleFriendPlacement.DEFAULT, () -> {
            titlePlacement = TitleFriendPlacement.DEFAULT;
            button.setMessage(titlePlacementLabel(name));
        });
    }

    private Component titlePlacementLabel(Component name) {
        return Component.translatable("options.generic_value", name,
                Component.translatable("oldmc.placement." + titlePlacement.key()));
    }

    private void addFriendPlacement(OldMCOptionList.Section section) {
        String key = "oldmc.option.friendButtonPlacement";
        var name = Component.translatable(key);
        var button = Button.builder(friendPlacementLabel(name), clicked -> {
            friendPlacement = friendPlacement.next();
            clicked.setMessage(friendPlacementLabel(name));
        }).width(OldMCOptionList.VALUE_WIDTH).build();
        button.setTooltip(Tooltip.create(Component.translatable("oldmc.option.friendButtonPlacement.tooltip")));
        section.addOption(button, key, () -> friendPlacement != FriendButtonPlacement.DEFAULT, () -> {
            friendPlacement = FriendButtonPlacement.DEFAULT;
            button.setMessage(friendPlacementLabel(name));
        });
    }

    private Component friendPlacementLabel(Component name) {
        return Component.translatable("options.generic_value", name,
                Component.translatable("oldmc.placement." + friendPlacement.key()));
    }

    private Component formatLabel() {
        return Component.translatable("oldmc.option.versionFormat",
                Component.translatable(majorVersionFormat ? "oldmc.format.major" : "oldmc.format.year"));
    }

    @Override
    public void removed() {
        super.removed();
        OldMCOptions.applyChanges(() -> {
            controls.forEach((option, control) -> option.set(control.get()));
            if (controls.containsKey(OldMCOptions.VERSION)) OldMCOptions.setMajorVersionFormat(majorVersionFormat);
            OldMCOptions.setFriendPlacement(friendPlacement);
            OldMCOptions.setTitleFriendPlacement(titlePlacement);
        });
    }
}
