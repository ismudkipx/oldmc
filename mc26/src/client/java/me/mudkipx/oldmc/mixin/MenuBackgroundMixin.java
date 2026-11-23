package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.render.ClassicDirt;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Screen.class)
abstract class MenuBackgroundMixin {
    @ModifyExpressionValue(method = "extractMenuBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIII)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/Screen;MENU_BACKGROUND:Lnet/minecraft/resources/Identifier;"))
    private Identifier oldmc$dirtTexture(Identifier vanilla) {
        return OldMCOptions.DIRT_BACKGROUND.enabled() ? ClassicDirt.MENU : vanilla;
    }
}
