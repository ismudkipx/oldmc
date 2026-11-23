package me.mudkipx.oldmc.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;

final class OldMCOptionList extends ContainerObjectSelectionList<OldMCOptionList.ListEntry> {
    private static final int ROW_WIDTH = 310;
    private static final int RESET_SIZE = 20;
    private static final int GAP = 4;
    static final int VALUE_WIDTH = ROW_WIDTH - RESET_SIZE - GAP;

    private final List<Section> sections = new ArrayList<>();

    OldMCOptionList(Minecraft client, int width, HeaderAndFooterLayout layout) {
        super(client, width, layout.getContentHeight(), layout.getHeaderHeight(), 25);
        centerListVertically = false;
    }

    @Override
    public int getRowWidth() {
        return ROW_WIDTH;
    }

    Section addSection(String key) {
        var section = new Section(OptionRow.createHeading(minecraft.font, Component.translatable("oldmc.category." + key)));
        sections.add(section);
        return section;
    }

    void updateSearch(String query) {
        String needle = query.strip().toLowerCase(Locale.ROOT);
        setFocused(null);
        clearEntries();
        for (var section : sections) {
            boolean showHeading = true;
            for (var option : section.options) {
                if (!option.searchText().contains(needle)) continue;
                if (showHeading) {
                    addEntry(section.heading);
                    showHeading = false;
                }
                addEntry(option.row());
            }
        }
        setScrollAmount(0);
    }

    private record SearchOption(String searchText, OptionRow row) {}

    static final class Section {
        private final ListEntry heading;
        private final List<SearchOption> options = new ArrayList<>();

        Section(ListEntry heading) {
            this.heading = heading;
        }

        void addOption(AbstractWidget value, String key, BooleanSupplier changed, Runnable restore) {
            var name = Component.translatable(key);
            var label = Component.translatable("oldmc.resetOption", name);
            var reset = SpriteIconButton.builder(label, button -> {
                restore.run();
                button.active = changed.getAsBoolean();
            }, true).size(RESET_SIZE, RESET_SIZE).sprite(OptionRow.RESET_SPRITES, 12, 12).build();
            reset.setTooltip(Tooltip.create(label));
            reset.active = changed.getAsBoolean();
            String searchText = (name.getString() + "\n" + key).toLowerCase(Locale.ROOT);
            options.add(new SearchOption(searchText, new OptionRow(value, reset, changed)));
        }
    }

    abstract static class ListEntry extends ContainerObjectSelectionList.Entry<ListEntry> {}

    abstract static class HeadingRow extends ListEntry {
        final Font font;
        final Component name;

        HeadingRow(Font font, Component name) {
            this.font = font;
            this.name = name;
        }

        @Override
        public List<AbstractWidget> children() {
            return List.of();
        }

        @Override
        public List<NarratableEntry> narratables() {
            return List.of(new HeadingNarration(name));
        }
    }

    // In 1.21.x the row and the narration interface map updateNarration to different methods.
    private record HeadingNarration(Component name) implements NarratableEntry {
        @Override
        public NarrationPriority narrationPriority() {
            return NarrationPriority.HOVERED;
        }

        @Override
        public void updateNarration(NarrationElementOutput output) {
            output.add(NarratedElementType.TITLE, name);
        }
    }

    abstract static class Row extends ListEntry {
        final AbstractWidget value;
        final AbstractWidget reset;
        private final BooleanSupplier changed;
        private final List<AbstractWidget> widgets;

        Row(AbstractWidget value, AbstractWidget reset, BooleanSupplier changed) {
            this.value = value;
            this.reset = reset;
            this.changed = changed;
            widgets = List.of(value, reset);
        }

        final void positionButtons() {
            int left = getContentXMiddle() - ROW_WIDTH / 2;
            value.setPosition(left, getContentY());
            reset.setPosition(left + VALUE_WIDTH + GAP, getContentY());
            reset.active = changed.getAsBoolean();
        }

        @Override
        public List<AbstractWidget> children() {
            return widgets;
        }

        @Override
        public List<AbstractWidget> narratables() {
            return widgets;
        }
    }
}
