package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.mudkipx.oldmc.render.ClassicBabySubmission;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerProfessionLayer.class)
abstract class BabyVillagerClothingMixin {
    // Use the classic biome outfit and hat model. The third read controls profession badges by actual age.
    @ModifyExpressionValue(method = "submit", at = {
        @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;isBaby:Z", ordinal = 0),
        @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;isBaby:Z", ordinal = 1)
    })
    private boolean oldmc$classicOutfit(boolean baby, @Local(argsOnly = true) LivingEntityRenderState state) {
        return baby && !ClassicBabySubmission.active(state);
    }
}
