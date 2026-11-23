package me.mudkipx.oldmc.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.Identifier;

import java.util.function.BooleanSupplier;

final class OptionRow extends OldMCOptionList.Row {
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
}
