package me.mudkipx.oldmc.mixin;

import me.mudkipx.oldmc.render.ClassicBabyRenderers;
import net.minecraft.client.model.animal.rabbit.RabbitModel;
import net.minecraft.client.renderer.entity.state.RabbitRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RabbitModel.class)
abstract class BabyRabbitModelMixin {
    @Unique private boolean oldmc$classicRabbit;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void oldmc$rememberModel(CallbackInfo ci) {
        oldmc$classicRabbit = ClassicBabyRenderers.baking() && ((RabbitModel)(Object)this).root().hasChild("left_haunch");
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/RabbitRenderState;)V", at = @At("HEAD"), cancellable = true)
    private void oldmc$animateClassicRabbit(RabbitRenderState state, CallbackInfo ci) {
        if (!oldmc$classicRabbit) return;
        var model = (RabbitModel)(Object)this;
        var root = model.root();
        model.resetPose();
        var head = root.getChild("head");
        head.xRot = (float)Math.toRadians(state.xRot);
        head.yRot = (float)Math.toRadians(state.yRot);
        float jump = (float)Math.sin(state.jumpCompletion * Math.PI);
        float rear = jump * (float)Math.toRadians(50);
        float front = jump * (float)Math.toRadians(40);
        root.getChild("left_haunch").xRot += rear;
        root.getChild("right_haunch").xRot += rear;
        root.getChild("left_front_leg").xRot -= front;
        root.getChild("right_front_leg").xRot -= front;
        ci.cancel();
    }
}
