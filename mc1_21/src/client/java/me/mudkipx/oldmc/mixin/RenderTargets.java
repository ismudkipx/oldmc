package me.mudkipx.oldmc.mixin;

final class RenderTargets {
    static final String SCREEN_RENDER = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V";
    static final String CENTERED_TEXT = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString"
        + "(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V";
    static final String PROGRESS_BAR = "drawProgressBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIF)V";

    static final String RESET_PROFILE = "loadDefaultProfile";
    static final String DEBUG_RENDER = "render(Lnet/minecraft/client/gui/GuiGraphics;)V";
    static final String DEBUG_LINES = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;"
        + "renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V";

    private RenderTargets() {}
}
