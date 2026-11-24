package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.render.ClassicDirt;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CreateWorldScreen.class)
abstract class CreateWorldBackgroundMixin {
    @ModifyExpressionValue(method = "extractMenuBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;TAB_HEADER_BACKGROUND:Lnet/minecraft/resources/Identifier;"))
    private Identifier oldmc$tabHeaderBackground(Identifier vanilla) {
        return OldMCOptions.DIRT_BACKGROUND.enabled() ? ClassicDirt.TAB_HEADER : vanilla;
    }
}
