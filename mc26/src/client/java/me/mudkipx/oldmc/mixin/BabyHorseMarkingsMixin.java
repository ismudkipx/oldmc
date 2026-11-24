package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.mudkipx.oldmc.render.ClassicBabySubmission;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HorseMarkingLayer.class)
abstract class BabyHorseMarkingsMixin {
    @Unique private static final String OLDMC_BLACK_DOTS = "textures/entity/horse/horse_markings_blackdots.png";
    @Unique private static final Identifier OLDMC_CLASSIC_DOTS = Identifier.fromNamespaceAndPath("oldmc", OLDMC_BLACK_DOTS);

    @ModifyExpressionValue(method = "submit", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/renderer/entity/layers/HorseMarkingLayer$HorseMarkingTextures;adult:Lnet/minecraft/resources/Identifier;"))
    private Identifier oldmc$classicMarkings(Identifier texture, @Local(argsOnly = true) HorseRenderState state) {
        return ClassicBabySubmission.active(state) && texture.getNamespace().equals("minecraft")
                && texture.getPath().equals(OLDMC_BLACK_DOTS) ? OLDMC_CLASSIC_DOTS : texture;
    }
}
