package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(CreateWorldScreen.class)
abstract class CreateWorldBackgroundMixin {
    // The pipeline class changed packages in 26.3. The header uses a single blit call.
    @WrapWithCondition(method = "extractMenuBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit"), allow = 1)
    private boolean oldmc$drawTabHeader(GuiGraphicsExtractor graphics, @Coerce Object pipeline, Identifier texture,
                                       int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        if (!OldMCOptions.DIRT_BACKGROUND.enabled()) return true;
        graphics.fill(x, y, x + width, y + height, 0xFF000000);
        return false;
    }
}
