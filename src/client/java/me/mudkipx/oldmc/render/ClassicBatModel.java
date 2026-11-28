package me.mudkipx.oldmc.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.state.BatRenderState;

public final class ClassicBatModel extends EntityModel<BatRenderState> {
    private final ModelPart head, body, rightWing, leftWing, rightTip, leftTip;

    public ClassicBatModel() {
        this(bakeRoot());
    }

    private ClassicBatModel(ModelPart root) {
        super(root);
        head = root.getChild("head");
        body = root.getChild("body");
        rightWing = body.getChild("right_wing");
        leftWing = body.getChild("left_wing");
        rightTip = rightWing.getChild("right_wing_tip");
        leftTip = leftWing.getChild("left_wing_tip");
    }

    private static ModelPart bakeRoot() {
        var mesh = new MeshDefinition();
        var root = mesh.getRoot();
        var head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3, -3, -3, 6, 6, 6), PartPose.ZERO);
        head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(24, 0)
                .addBox(-4, -6, -2, 3, 4, 1), PartPose.ZERO);
        head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(24, 0).mirror()
                .addBox(1, -6, -2, 3, 4, 1), PartPose.ZERO);
        var body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-3, 4, -3, 6, 12, 6)
                .texOffs(0, 34).addBox(-5, 16, 0, 10, 6, 1), PartPose.ZERO);
        var right = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(42, 0)
                .addBox(-12, 1, 1.5F, 10, 16, 1), PartPose.ZERO);
        right.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(24, 16)
                .addBox(-8, 1, 0, 8, 12, 1), PartPose.offset(-12, 1, 1.5F));
        var left = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(42, 0).mirror()
                .addBox(2, 1, 1.5F, 10, 16, 1), PartPose.ZERO);
        left.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(24, 16).mirror()
                .addBox(0, 1, 0, 8, 12, 1), PartPose.offset(12, 1, 1.5F));
        return LayerDefinition.create(mesh, 64, 64).bakeRoot();
    }

    @Override
    public void setupAnim(BatRenderState state) {
        super.setupAnim(state);
        head.xRot = (float) Math.toRadians(state.xRot);
        head.yRot = (float) Math.toRadians(state.yRot);
        if (state.isResting) {
            head.yRot = (float) Math.PI - head.yRot;
            head.zRot = (float) Math.PI;
            head.y = -2;
            rightWing.setPos(-3, 0, 3);
            leftWing.setPos(3, 0, 3);
            body.xRot = (float) Math.PI;
            rightWing.xRot = leftWing.xRot = -0.15707964F;
            rightWing.yRot = -1.2566371F;
            rightTip.yRot = -1.7278761F;
        } else {
            body.xRot = (float) Math.PI / 4 + (float) Math.cos(state.ageInTicks * 0.1F) * 0.15F;
            rightWing.yRot = (float) Math.cos(Math.toRadians(state.ageInTicks * 74.48451F)) * (float) Math.PI / 4;
            rightTip.yRot = rightWing.yRot * 0.5F;
        }
        leftWing.yRot = -rightWing.yRot;
        leftTip.yRot = -rightTip.yRot;
    }
}
