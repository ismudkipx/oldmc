package me.mudkipx.oldmc.mixin;

final class RenderTargets {
    static final String SCREEN_RENDER = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V";
    static final String CENTERED_TEXT = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;centeredText"
        + "(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V";
    static final String PROGRESS_BAR = "drawProgressBar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIF)V";

    static final String RESET_PROFILE = "resetToProfile";
    static final String DEBUG_RENDER = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V";
    static final String DEBUG_LINES = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;"
        + "extractLines";

    private RenderTargets() {}
}
