package me.mudkipx.oldmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.PoseStack;
import me.mudkipx.oldmc.config.OldMCOptions;
import me.mudkipx.oldmc.render.ClassicBatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.BatRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ambient.Bat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BatRenderer.class)
abstract class ClassicBatRendererMixin extends MobRenderer<Bat, BatRenderState, EntityModel<BatRenderState>> {
    @Unique private static final ResourceLocation OLDMC_BAT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("oldmc", "textures/entity/bat.png");
    @Unique private ClassicBatModel oldmc$batModel;

    protected ClassicBatRendererMixin(EntityRendererProvider.Context context, EntityModel<BatRenderState> model, float shadow) {
        super(context, model, shadow);
    }

    @ModifyReturnValue(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/BatRenderState;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"))
    private ResourceLocation oldmc$batTexture(ResourceLocation original) {
        return OldMCOptions.CLASSIC_BAT.enabled() ? OLDMC_BAT_TEXTURE : original;
    }

    @Override
    public void submit(BatRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!OldMCOptions.CLASSIC_BAT.enabled()) {
            super.submit(state, poses, collector, camera);
            return;
        }
        if (oldmc$batModel == null) oldmc$batModel = new ClassicBatModel();
        var original = this.model;
        this.model = oldmc$batModel;
        try {
            super.submit(state, poses, collector, camera);
        } finally {
            this.model = original;
        }
    }
}
