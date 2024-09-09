package dev.sterner.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.VoidBound
import dev.sterner.common.entity.GrimcultNecromancerEntity
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.entity.HumanoidArm


class GrimcultNecromancerModel(root: ModelPart) : AbstractGrimcultModel<GrimcultNecromancerEntity>(root) {
    private val left_leg: ModelPart = root.getChild("left_leg")
    private val right_leg: ModelPart = root.getChild("right_leg")
    private val right_arm: ModelPart = root.getChild("right_arm")
    private val left_arm: ModelPart = root.getChild("left_arm")
    private val head: ModelPart = root.getChild("head")
    private val hat: ModelPart = root.getChild("hat")
    private val body: ModelPart = root.getChild("body")

    override fun translateToHand(side: HumanoidArm, poseStack: PoseStack) {
        val f = if (side == HumanoidArm.RIGHT) 1.0f else -1.0f
        val modelPart = this.getArm(side)
        modelPart.x += f
        modelPart.translateAndRotate(poseStack)
        modelPart.x -= f
    }

    override fun prepareMobModel(
        entity: GrimcultNecromancerEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTick: Float
    ) {
        this.rightArmPose = ArmPose.EMPTY
        this.leftArmPose = ArmPose.EMPTY
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick)
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(VoidBound.id("grimcult_necromancer"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val left_leg = partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(16, 48)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.9f, 12.0f, 0.0f)
            )

            val RobeLeft = left_leg.addOrReplaceChild(
                "RobeLeft",
                CubeListBuilder.create().texOffs(0, 81)
                    .addBox(-1.5f, 0.0f, -2.5f, 4.0f, 11.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.1222f)
            )

            val right_leg = partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.9f, 12.0f, 0.0f)
            )

            val RobeRight = right_leg.addOrReplaceChild(
                "RobeRight",
                CubeListBuilder.create().texOffs(0, 81).mirror()
                    .addBox(-2.5f, 0.0f, -2.5f, 4.0f, 11.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.1222f)
            )

            val right_arm = partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(40, 16)
                    .addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, 2.0f, 0.0f)
            )

            val RightShoulder = right_arm.addOrReplaceChild(
                "RightShoulder",
                CubeListBuilder.create().texOffs(56, 28)
                    .addBox(-3.5f, -2.0f, -2.5f, 5.0f, 3.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2269f)
            )

            val left_arm = partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(32, 48)
                    .addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(5.0f, 2.0f, 0.0f)
            )

            val LeftShoulder = left_arm.addOrReplaceChild(
                "LeftShoulder",
                CubeListBuilder.create().texOffs(56, 28).mirror()
                    .addBox(-1.5f, -2.0f, -2.5f, 5.0f, 3.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.2269f)
            )

            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            ).addOrReplaceChild(
                "hood",
                CubeListBuilder.create().texOffs(64, 0)
                    .addBox(-4.5f, -8.4f, -4.0f, 9.0f, 9.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0873f, 0.0f, 0.0f)
            )

            val hat =
                partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))

            val HoodBuckle = hat.addOrReplaceChild(
                "HoodBuckle",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.5f, -0.6f, -4.1f, 3.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val HoodBack = head.addOrReplaceChild(
                "HoodBack",
                CubeListBuilder.create().texOffs(64, 16)
                    .addBox(-4.0f, -8.1f, 0.3f, 8.0f, 8.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.0436f, 0.0f, 0.0f)
            )

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(16, 16)
                    .addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val ScarfFront = body.addOrReplaceChild(
                "ScarfFront",
                CubeListBuilder.create().texOffs(6, 68)
                    .addBox(-6.0f, 0.0f, -2.2f, 12.0f, 12.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.0873f, 0.0f, 0.0f)
            )

            val ScarfBack = body.addOrReplaceChild(
                "ScarfBack",
                CubeListBuilder.create().texOffs(0, 32)
                    .addBox(-4.0f, 0.6f, 1.7f, 8.0f, 12.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0873f, 0.0f, 0.0f)
            )

            val GoldScarfRight = body.addOrReplaceChild(
                "GoldScarfRight",
                CubeListBuilder.create().texOffs(0, 47).mirror()
                    .addBox(-4.0f, 0.2f, -2.5f, 2.0f, 12.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.1309f, 0.0f, 0.0f)
            )

            val RobeFront = body.addOrReplaceChild(
                "RobeFront",
                CubeListBuilder.create().texOffs(18, 32)
                    .addBox(-2.0f, 0.0f, -2.4f, 4.0f, 12.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 10.0f, 0.0f, -0.0524f, 0.0f, 0.0f)
            )

            val GoldScarfLeft = body.addOrReplaceChild(
                "GoldScarfLeft",
                CubeListBuilder.create().texOffs(0, 47)
                    .addBox(2.0f, 0.2f, -2.5f, 2.0f, 12.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.1309f, 0.0f, 0.0f)
            )

            val Cape = body.addOrReplaceChild(
                "Cape",
                CubeListBuilder.create().texOffs(28, 32)
                    .addBox(-4.5f, 0.0f, 0.0f, 9.0f, 14.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 1.8f, 0.0873f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}