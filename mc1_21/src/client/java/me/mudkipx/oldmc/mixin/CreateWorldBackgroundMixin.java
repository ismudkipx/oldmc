package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.gui.GuiGraphics;
import me.mudkipx.oldmc.screen.ClassicWorldView;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CreateWorldScreen.class)
abstract class CreateWorldBackgroundMixin extends Screen {
    protected CreateWorldBackgroundMixin(Component title) {
        super(title);
    }

    @Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"), cancellable = true)
    private void oldmc$drawFullBackground(GuiGraphics graphics, CallbackInfo ci) {
        if (((ClassicWorldView) this).oldmc$getWorldForm() != null) {
            super.renderMenuBackground(graphics);
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void oldmc$drawClassicForm(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        var form = ((ClassicWorldView) this).oldmc$getWorldForm();
        if (form == null) return;
        super.render(graphics, mouseX, mouseY, delta);
        form.drawLabels(font, getTitle(), width, (line, x, y, color) -> graphics.drawString(font, line, x, y, color));
        ci.cancel();
    }

    @WrapWithCondition(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"), allow = 1)
    private boolean oldmc$drawTabHeader(GuiGraphics graphics, RenderPipeline pipeline, ResourceLocation texture,
                                       int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        if (!OldMCOptions.DIRT_BACKGROUND.enabled()) return true;
        graphics.fill(x, y, x + width, y + height, 0xFF000000);
        return false;
    }
}
