package me.mudkipx.oldmc.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class ClassicDirt {
    public static final ResourceLocation MENU = ResourceLocation.fromNamespaceAndPath("oldmc", "textures/gui/dirt_background.png");
    public static final ResourceLocation LIST = ResourceLocation.fromNamespaceAndPath("oldmc", "textures/gui/dirt_list_background.png");
    public static final ResourceLocation TAB_HEADER = ResourceLocation.fromNamespaceAndPath("oldmc", "textures/gui/tab_header_background.png");

    public static void drawBackground(GuiGraphics graphics) {
        Screen.renderMenuBackgroundTexture(graphics, MENU, 0, 0, 0, 0, graphics.guiWidth(), graphics.guiHeight());
    }

    public static void drawMessage(GuiGraphics graphics, Font font, Component title, int centerX) {
        graphics.drawCenteredString(font, title, centerX, 70, 0xFFFFFFFF);
    }

    private ClassicDirt() {}
}
