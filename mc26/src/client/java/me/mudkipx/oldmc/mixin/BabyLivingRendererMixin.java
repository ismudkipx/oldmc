package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.render.ClassicBabyRenderers;
import me.mudkipx.oldmc.render.ClassicBabySubmission;
import me.mudkipx.oldmc.render.ClassicCoat;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
abstract class BabyLivingRendererMixin {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"))
    private void oldmc$restoreAge(LivingEntityRenderState state, PoseStack poses, SubmitNodeCollector collector,
                                  CameraRenderState camera, CallbackInfo ci) {
        // The species-specific renderer has selected its classic model. Keep real age for poses and held items.
        if (ClassicBabySubmission.active(state)) state.isBaby = true;
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("RETURN"))
    private void oldmc$extractCoat(LivingEntity entity, LivingEntityRenderState state, float partialTick, CallbackInfo ci) {
        if (!(state instanceof ClassicCoat coat) || !state.isBaby) return;
        if (!OldMCOptions.CLASSIC_BABY_MOBS.enabled()) {
            coat.oldmc$setClassicCoat(null);
            return;
        }
        if (entity instanceof Cat cat) {
            coat.oldmc$setClassicCoat(cat.getVariant().value().assetInfo(false).texturePath());
        } else if (entity instanceof Wolf wolf) {
            var variant = wolf.get(DataComponents.WOLF_VARIANT);
            if (variant == null) {
                coat.oldmc$setClassicCoat(null);
                return;
            }
            var assets = variant.value().adultInfo();
            var texture = wolf.isTame() ? assets.tame() : wolf.isAngry() ? assets.angry() : assets.wild();
            coat.oldmc$setClassicCoat(texture.texturePath());
        }
    }

    @WrapOperation(method = "getRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)Lnet/minecraft/resources/Identifier;"))
    private Identifier oldmc$classicTexture(LivingEntityRenderer<?, ?, ?> renderer, LivingEntityRenderState state,
                                            Operation<Identifier> original) {
        if (!ClassicBabySubmission.active(state)) return original.call(renderer, state);
        if (state instanceof ClassicCoat coat && coat.oldmc$classicCoat() != null) return coat.oldmc$classicCoat();
        try {
            state.isBaby = false;
            return ClassicBabyRenderers.texture(original.call(renderer, state));
        } finally {
            state.isBaby = true;
        }
    }

    @WrapOperation(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/EntityRenderState;FF)V"))
    private void oldmc$classicOverlay(RenderLayer<?, ?> layer, PoseStack poses, SubmitNodeCollector collector,
                                      int light, EntityRenderState state, float yaw, float pitch, Operation<Void> original) {
        if (ClassicBabySubmission.active(state) && state instanceof LivingEntityRenderState living
                && (layer instanceof CatCollarLayer || layer instanceof WolfCollarLayer || layer instanceof SheepWoolLayer
                || layer instanceof SheepWoolUndercoatLayer || layer instanceof DrownedOuterLayer || layer instanceof HorseMarkingLayer
                || layer instanceof HumanoidArmorLayer || layer instanceof SimpleEquipmentLayer || layer instanceof WingsLayer
                || layer instanceof LlamaDecorLayer || layer instanceof WolfArmorLayer)) {
            try {
                living.isBaby = false;
                original.call(layer, poses, collector, light, state, yaw, pitch);
            } finally {
                living.isBaby = true;
            }
        } else {
            original.call(layer, poses, collector, light, state, yaw, pitch);
        }
    }
}
