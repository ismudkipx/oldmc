package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import me.mudkipx.oldmc.render.ClassicBabyRenderers;
import me.mudkipx.oldmc.render.ClassicBabySubmission;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
abstract class BabyRenderDispatcherMixin {
    @WrapOperation(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"))
    private void oldmc$submitBaby(EntityRenderer<?, ?> renderer, EntityRenderState state, PoseStack poses,
                                  SubmitNodeCollector collector, CameraRenderState camera, Operation<Void> original) {
        if (state instanceof LivingEntityRenderState living) {
            var selected = ClassicBabyRenderers.select(renderer, living);
            if (selected != renderer) {
                ClassicBabySubmission.withModel(living, () -> original.call(selected, state, poses, collector, camera));
                return;
            }
        }
        original.call(renderer, state, poses, collector, camera);
    }
}
