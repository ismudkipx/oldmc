package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.mudkipx.oldmc.render.ClassicBabyModels;
import me.mudkipx.oldmc.render.ClassicBabyRenderers;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityModelSet.class)
abstract class BabyModelSetMixin {
    @ModifyReturnValue(method = "bakeLayer", at = @At("RETURN"))
    private ModelPart oldmc$classicMesh(ModelPart root, ModelLayerLocation layer) {
        return ClassicBabyRenderers.baking() ? ClassicBabyModels.apply(layer, root) : root;
    }
}
