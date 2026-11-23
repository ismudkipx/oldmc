package me.mudkipx.oldmc.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;

final class OptionRow extends OldMCOptionList.Row {
    private static final ResourceLocation RESET = ResourceLocation.fromNamespaceAndPath("oldmc", "widget/reset");
    private static final ResourceLocation DISABLED = ResourceLocation.fromNamespaceAndPath("oldmc", "widget/reset_disabled");
    static final WidgetSprites RESET_SPRITES = new WidgetSprites(RESET, DISABLED, RESET);

    OptionRow(AbstractWidget value, AbstractWidget reset, BooleanSupplier changed) {
        super(value, reset, changed);
    }

    @Override
    public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float delta) {
        positionButtons();
        value.render(graphics, mouseX, mouseY, delta);
        reset.render(graphics, mouseX, mouseY, delta);
    }

    static OldMCOptionList.ListEntry createHeading(Font font, Component name) {
        return new Heading(font, name);
    }

    private static final class Heading extends OldMCOptionList.HeadingRow {
        Heading(Font font, Component name) {
            super(font, name);
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float delta) {
            graphics.drawCenteredString(font, name, getContentXMiddle(), getContentY() + 5, 0xFFFFFFFF);
        }
    }
}
