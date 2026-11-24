package me.mudkipx.oldmc.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class ClassicDirt {
    public static final Identifier MENU = Identifier.fromNamespaceAndPath("oldmc", "textures/gui/dirt_background.png");
    public static final Identifier LIST = Identifier.fromNamespaceAndPath("oldmc", "textures/gui/dirt_list_background.png");
    public static final Identifier TAB_HEADER = Identifier.fromNamespaceAndPath("oldmc", "textures/gui/tab_header_background.png");

    public static void drawBackground(GuiGraphicsExtractor graphics) {
        Screen.extractMenuBackgroundTexture(graphics, MENU, 0, 0, 0, 0, graphics.guiWidth(), graphics.guiHeight());
    }

    public static void drawMessage(GuiGraphicsExtractor graphics, Font font, Component title, int centerX) {
        graphics.centeredText(font, title, centerX, 70, 0xFFFFFFFF);
    }

    private ClassicDirt() {}
}
