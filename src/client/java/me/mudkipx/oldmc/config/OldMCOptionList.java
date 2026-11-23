package me.mudkipx.oldmc.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.BooleanSupplier;

final class OldMCOptionList extends ContainerObjectSelectionList<OldMCOptionList.Row> {
    private static final int ROW_WIDTH = 310;
    private static final int RESET_SIZE = 20;
    private static final int GAP = 4;
    static final int VALUE_WIDTH = ROW_WIDTH - RESET_SIZE - GAP;

    OldMCOptionList(Minecraft client, int width, HeaderAndFooterLayout layout) {
        super(client, width, layout.getContentHeight(), layout.getHeaderHeight(), 25);
        centerListVertically = false;
    }

    @Override
    public int getRowWidth() {
        return ROW_WIDTH;
    }

    void addOption(AbstractWidget value, Component name, BooleanSupplier changed, Runnable restore) {
        var label = Component.translatable("oldmc.resetOption", name);
        var reset = SpriteIconButton.builder(label, button -> {
            restore.run();
            button.active = changed.getAsBoolean();
        }, true).size(RESET_SIZE, RESET_SIZE).sprite(OptionRow.RESET_SPRITES, 12, 12).build();
        reset.setTooltip(Tooltip.create(label));
        reset.active = changed.getAsBoolean();
        addEntry(new OptionRow(value, reset, changed));
    }

    abstract static class Row extends ContainerObjectSelectionList.Entry<Row> {
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
