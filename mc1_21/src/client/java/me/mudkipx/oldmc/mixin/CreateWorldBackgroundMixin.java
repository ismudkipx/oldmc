package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CreateWorldScreen.class)
abstract class CreateWorldBackgroundMixin {
    @WrapWithCondition(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"), allow = 1)
    private boolean oldmc$drawTabHeader(GuiGraphics graphics, RenderPipeline pipeline, ResourceLocation texture,
                                       int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        if (!OldMCOptions.DIRT_BACKGROUND.enabled()) return true;
        graphics.fill(x, y, x + width, y + height, 0xFF000000);
        return false;
    }
}
