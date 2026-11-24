package me.mudkipx.oldmc.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

/** The two pages share the current game's world creation state and vanilla action buttons. */
public final class ClassicWorldForm {
    private final WorldCreationUiState state;
    private final EditBox name, seed;
    private final AbstractWidget mode, difficulty, commands, type, customize, packs, rules, experiments;
    private final CycleButton<Boolean> structures, chest;
    private final Button page, create, cancel;
    private boolean more;

    private ClassicWorldForm(WorldCreationUiState state, EditBox name, EditBox seed, AbstractWidget mode,
                            AbstractWidget difficulty, AbstractWidget commands, AbstractWidget type,
                            AbstractWidget customize, AbstractWidget packs, AbstractWidget rules,
                            AbstractWidget experiments, Button create, Button cancel, Runnable switchPage) {
        this.state = state;
        this.name = name;
        this.seed = seed;
        this.mode = mode;
        this.difficulty = difficulty;
        this.commands = commands;
        this.type = type;
        this.customize = customize;
        this.packs = packs;
        this.rules = rules;
        this.experiments = experiments;
        this.create = create;
        this.cancel = cancel;
        structures = WorldFormButtons.onOffBuilder(state.isGenerateStructures()).create(
                Component.translatable("selectWorld.mapFeatures"), (button, value) -> state.setGenerateStructures(value));
        chest = WorldFormButtons.onOffBuilder(state.isBonusChest()).create(
                Component.translatable("selectWorld.bonusItems"), (button, value) -> state.setBonusChest(value));
        page = Button.builder(Component.empty(), b -> switchPage.run()).build();
        seed.setHint(Component.empty());
    }

    public static ClassicWorldForm fromTabs(List<Tab> tabs, WorldCreationUiState state,
                                            Button create, Button cancel, Runnable switchPage) {
        if (tabs.size() != 3 || create == null || cancel == null) return null;
        var controls = new HashMap<String, AbstractWidget>();
        for (Tab tab : tabs) {
            List<AbstractWidget> widgets = new ArrayList<>();
            tab.visitChildren(widgets::add);
            String previousLabel = "";
            for (AbstractWidget widget : widgets) {
                String key = WidgetLabels.optionKey(widget.getMessage());
                if (widget instanceof StringWidget && (key.equals("selectWorld.enterName")
                        || key.equals("selectWorld.enterSeed") || key.equals("selectWorld.mapFeatures")
                        || key.equals("selectWorld.bonusItems"))) {
                    previousLabel = key;
                    continue;
                }
                // Vanilla's two World-tab switches display only On/Off after a separate text label.
                if (widget instanceof CycleButton<?> cycle && cycle.getValue() instanceof Boolean
                        && (key.equals("options.on") || key.equals("options.off"))
                        && (previousLabel.equals("selectWorld.mapFeatures") || previousLabel.equals("selectWorld.bonusItems"))) {
                    key = previousLabel;
                }
                previousLabel = "";
                if (controls.putIfAbsent(key, widget) != null) return null;
            }
        }
        // Preserve the original tabs when another mod adds controls we cannot represent.
        if (!(controls.remove("selectWorld.enterName") instanceof EditBox name)
                || !(controls.remove("selectWorld.enterSeed") instanceof EditBox seed)
                || !(controls.remove("selectWorld.gameMode") instanceof CycleButton<?> mode)
                || !(controls.remove("options.difficulty") instanceof CycleButton<?> difficulty)
                || !(controls.remove("selectWorld.allowCommands") instanceof CycleButton<?> commands)
                || !(commands.getValue() instanceof Boolean)
                || !(controls.remove("selectWorld.mapType") instanceof CycleButton<?> type)
                || !(controls.remove("selectWorld.customizeType") instanceof Button customize)
                || !(controls.remove("selectWorld.dataPacks") instanceof Button packs)
                || !(controls.remove("selectWorld.gameRules") instanceof Button rules)
                || !(controls.remove("selectWorld.experiments") instanceof Button experiments)
                || !(controls.remove("selectWorld.mapFeatures") instanceof CycleButton<?>)
                || !(controls.remove("selectWorld.bonusItems") instanceof CycleButton<?>)
                || !controls.isEmpty()) return null;
        return new ClassicWorldForm(state, name, seed, mode, difficulty, commands, type, customize,
                packs, rules, experiments, create, cancel, switchPage);
    }

    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        List.of(name, seed, mode, difficulty, structures, type, customize, commands, packs, chest,
                rules, experiments, page, create, cancel).forEach(consumer);
    }

    public boolean more() {
        return more;
    }

    public EditBox show(boolean advanced) {
        more = advanced;
        name.visible = mode.visible = difficulty.visible = rules.visible = !more;
        type.visible = seed.visible = experiments.visible = more;
        page.setMessage(more ? CommonComponents.GUI_DONE : Component.translatable("oldmc.createWorld.moreOptions"));
        refresh();
        return more ? seed : name;
    }

    public void refresh() {
        boolean debug = state.isDebug();
        commands.visible = packs.visible = !more && !debug;
        structures.visible = chest.visible = more && !debug;
        customize.visible = more && !debug && state.getPresetEditor() != null;
        structures.setValue(state.isGenerateStructures());
        chest.setValue(state.isBonusChest());
        chest.active = !state.isHardcore();
        create.active = !name.getValue().isEmpty();
    }

    public boolean canCreate() {
        return create.active;
    }

    public void position(int width, int height) {
        int center = width / 2, left = center - 155, right = center + 5;
        int footer = height - 28;
        // Vanilla can use a scaled height as low as 240. Keep the footer clear on smaller windows too.
        int lift = Math.min(0, footer - 209);
        place(name, center - 100, 60 + lift, 200);
        place(seed, center - 100, 60 + lift, 200);
        place(mode, left, 100 + lift, 150);
        place(difficulty, right, 100 + lift, 150);
        place(structures, left, 100 + lift, 150);
        place(type, right, 100 + lift, 150);
        place(customize, right, 120 + lift, 150);
        place(commands, left, 151 + lift, 150);
        place(packs, right, 151 + lift, 150);
        place(chest, left, 151 + lift, 150);
        place(rules, left, 185 + lift, 150);
        place(experiments, left, 185 + lift, 150);
        place(page, right, 185 + lift, 150);
        place(create, left, footer, 150);
        place(cancel, right, footer, 150);
    }

    private static void place(AbstractWidget widget, int x, int y, int width) {
        widget.setPosition(x, y);
        widget.setWidth(width);
    }

    @FunctionalInterface
    public interface Text {
        void draw(FormattedCharSequence line, int x, int y, int color);

        default void draw(Component text, int x, int y, int color) {
            draw(text.getVisualOrderText(), x, y, color);
        }
    }

    public void drawLabels(Font font, Component title, int width, Text text) {
        int center = width / 2, lift = name.getY() - 60, gray = 0xffa0a0a0;
        text.draw(title, center - font.width(title) / 2, 20 + lift, 0xffffffff);
        text.draw(Component.translatable(more ? "selectWorld.enterSeed" : "selectWorld.enterName"), center - 100, 47 + lift, gray);
        if (more) {
            text.draw(Component.translatable("selectWorld.seedInfo"), center - 100, 85 + lift, gray);
            if (structures.visible) text.draw(Component.translatable("selectWorld.mapFeatures.info"), center - 150, 122 + lift, gray);
            if (state.getWorldType().isAmplified()) {
                int y = 122 + lift;
                for (var line : font.split(Component.translatable("generator.minecraft.amplified.info"), 150)) {
                    text.draw(line, type.getX() + 2, y, gray);
                    y += font.lineHeight;
                }
            }
        } else {
            text.draw(Component.translatable("oldmc.createWorld.saveFolder", state.getTargetFolder()),
                    center - 100, 85 + lift, gray);
            String modeKey = state.isDebug() ? "spectator" : state.getGameMode().name().toLowerCase(Locale.ROOT);
            String prefix = "oldmc.createWorld." + modeKey;
            text.draw(Component.translatable(prefix + ".line1"), center - 150, 122 + lift, gray);
            text.draw(Component.translatable(prefix + ".line2"), center - 150, 134 + lift, gray);
            if (commands.visible) text.draw(Component.translatable("selectWorld.allowCommands.info"), center - 150, 172 + lift, gray);
        }
    }
}
