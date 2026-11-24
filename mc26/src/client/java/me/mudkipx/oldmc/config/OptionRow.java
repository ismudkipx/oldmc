package me.mudkipx.oldmc.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.BooleanSupplier;

final class OptionRow extends OldMCOptionList.Row {
    private static final boolean FOCUSABLE_HEADINGS = OldMCOptions.GAME_VERSION.equals("26.3");
    private static final Identifier RESET = Identifier.fromNamespaceAndPath("oldmc", "widget/reset");
    private static final Identifier DISABLED = Identifier.fromNamespaceAndPath("oldmc", "widget/reset_disabled");
    static final WidgetSprites RESET_SPRITES = new WidgetSprites(RESET, DISABLED, RESET);

    OptionRow(AbstractWidget value, AbstractWidget reset, BooleanSupplier changed) {
        super(value, reset, changed);
    }

    @Override
    public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float delta) {
        positionButtons();
        value.extractRenderState(graphics, mouseX, mouseY, delta);
        reset.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    static OldMCOptionList.ListEntry createHeading(Font font, Component name) {
        return FOCUSABLE_HEADINGS ? new FocusableHeading(font, name) : new Heading(font, name);
    }

    private static final class Heading extends OldMCOptionList.HeadingRow {
        Heading(Font font, Component name) {
            super(font, name);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float delta) {
            graphics.centeredText(font, name, getContentXMiddle(), getContentY() + 5, 0xFFFFFFFF);
        }
    }

    private static final class FocusableHeading extends OldMCOptionList.ListEntry {
        private final FocusableTextWidget widget;

        FocusableHeading(Font font, Component name) {
            widget = FocusableTextWidget.builder(name, font)
                    .alwaysShowBorder(false)
                    .backgroundFill(FocusableTextWidget.BackgroundFill.ON_FOCUS)
                    .build();
        }

        @Override
        public List<FocusableTextWidget> children() {
            return List.of(widget);
        }

        @Override
        public List<FocusableTextWidget> narratables() {
            return List.of(widget);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float delta) {
            widget.setPosition(getContentX() + getContentWidth() / 2 - widget.getWidth() / 2,
                    getContentYMiddle() - widget.getHeight() / 2);
            widget.extractRenderState(graphics, mouseX, mouseY, delta);
        }
    }
}
