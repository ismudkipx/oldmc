package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import me.mudkipx.oldmc.screen.ClassicWorldView;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(CreateWorldScreen.class)
abstract class CreateWorldBackgroundMixin extends Screen {
    protected CreateWorldBackgroundMixin(Component title) {
        super(title);
    }

    @Inject(method = "extractMenuBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V", at = @At("HEAD"), cancellable = true)
    private void oldmc$drawFullBackground(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (((ClassicWorldView) this).oldmc$getWorldForm() != null) {
            super.extractMenuBackground(graphics);
            ci.cancel();
        }
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void oldmc$drawClassicForm(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        var form = ((ClassicWorldView) this).oldmc$getWorldForm();
        if (form == null) return;
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        form.drawLabels(font, getTitle(), width, (line, x, y, color) -> graphics.text(font, line, x, y, color));
        ci.cancel();
    }

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
