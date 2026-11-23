package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.render.ClassicDirt;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSelectionList.class)
abstract class ListBackgroundMixin {
    @ModifyExpressionValue(method = "renderListBackground",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;MENU_LIST_BACKGROUND:Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation oldmc$dirtTexture(ResourceLocation vanilla) {
        return OldMCOptions.DIRT_BACKGROUND.enabled() ? ClassicDirt.LIST : vanilla;
    }
}
