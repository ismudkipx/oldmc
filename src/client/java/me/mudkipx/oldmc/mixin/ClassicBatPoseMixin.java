package me.mudkipx.oldmc.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.mudkipx.oldmc.config.OldMCOptions;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.BatRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.ambient.Bat;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BatRenderer.class)
abstract class ClassicBatPoseMixin extends MobRenderer<Bat, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> {
    protected ClassicBatPoseMixin(EntityRendererProvider.Context context, EntityModel<LivingEntityRenderState> model, float shadow) {
        super(context, model, shadow);
    }

    @Override
    protected void setupRotations(LivingEntityRenderState state, PoseStack poses, float bodyRot, float scale) {
        if (OldMCOptions.CLASSIC_BAT.enabled()) {
            float offset = ((BatRenderState) state).isResting ? -0.1F : (float) Math.cos(state.ageInTicks * 0.3F) * 0.1F;
            poses.translate(0, offset, 0);
        }
        super.setupRotations(state, poses, bodyRot, scale);
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack poses) {
        super.scale(state, poses);
        if (OldMCOptions.CLASSIC_BAT.enabled()) poses.scale(0.35F, 0.35F, 0.35F);
    }
}
