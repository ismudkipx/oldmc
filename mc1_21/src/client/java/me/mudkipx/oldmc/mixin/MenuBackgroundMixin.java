package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.render.ClassicDirt;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Screen.class)
abstract class MenuBackgroundMixin {
    @ModifyExpressionValue(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;IIII)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/Screen;MENU_BACKGROUND:Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation oldmc$dirtTexture(ResourceLocation vanilla) {
        return OldMCOptions.DIRT_BACKGROUND.enabled() ? ClassicDirt.MENU : vanilla;
    }
}
