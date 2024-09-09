package dev.sterner.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.VoidBound
import dev.sterner.common.entity.GrimcultJesterEntity
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.entity.HumanoidArm


class GrimcultJesterModel(root: ModelPart) : AbstractGrimcultModel<GrimcultJesterEntity>(root) {
    private val left_arm: ModelPart = root.getChild("left_arm")
    private val right_arm: ModelPart = root.getChild("right_arm")
    private val head: ModelPart = root.getChild("head")
    private val left_leg: ModelPart = root.getChild("left_leg")
    private val right_leg: ModelPart = root.getChild("right_leg")
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
        entity: GrimcultJesterEntity,
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
        left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(VoidBound.id("grimcult_jester"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val left_arm = partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(32, 48)
                    .addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(5.0f, 2.0f, 0.0f)
            )

            val LeftSleeve = left_arm.addOrReplaceChild(
                "LeftSleeve",
                CubeListBuilder.create().texOffs(0, 75).mirror()
                    .addBox(-1.3f, 5.0f, -2.5f, 5.0f, 6.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val LeftShoulderPad = left_arm.addOrReplaceChild(
                "LeftShoulderPad",
                CubeListBuilder.create().texOffs(0, 64).mirror()
                    .addBox(-1.3f, -3.2f, -2.5f, 5.0f, 5.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val right_arm = partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(40, 16)
                    .addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, 2.0f, 0.0f)
            )

            val RightShoulderPad = right_arm.addOrReplaceChild(
                "RightShoulderPad",
                CubeListBuilder.create().texOffs(0, 64)
                    .addBox(-3.7f, -3.2f, -2.5f, 5.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val RightSleeve = right_arm.addOrReplaceChild(
                "RightSleeve",
                CubeListBuilder.create().texOffs(0, 75)
                    .addBox(-3.7f, 5.0f, -2.5f, 5.0f, 6.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            ).addOrReplaceChild(
                "hood",
                CubeListBuilder.create().texOffs(56, 0)
                    .addBox(-4.5f, -8.5f, -4.5f, 9.0f, 9.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val MaskBottom = head.addOrReplaceChild(
                "MaskBottom",
                CubeListBuilder.create().texOffs(74, 18)
                    .addBox(-3.5f, -3.5f, -4.6f, 7.0f, 4.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0436f, 0.0f, 0.0f)
            )

            val HornRightBase = head.addOrReplaceChild(
                "HornRightBase",
                CubeListBuilder.create().texOffs(24, 0)
                    .addBox(-2.5f, -12.5f, -3.0f, 5.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.2618f, 2.0944f, 0.0f)
            )

            val HornRightTop = HornRightBase.addOrReplaceChild(
                "HornRightTop",
                CubeListBuilder.create().texOffs(92, 0)
                    .addBox(-2.0f, -15.1f, -7.6f, 4.0f, 3.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val HornRightBell = HornRightBase.addOrReplaceChild(
                "HornRightBell",
                CubeListBuilder.create().texOffs(0, 44)
                    .addBox(-1.5f, -10.4f, -11.5f, 3.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.2618f, 0.0f, 0.0f)
            )

            val HatRing = head.addOrReplaceChild(
                "HatRing",
                CubeListBuilder.create().texOffs(0, 32)
                    .addBox(-5.0f, -8.1f, -5.0f, 10.0f, 2.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val Ruff = head.addOrReplaceChild(
                "Ruff",
                CubeListBuilder.create().texOffs(12, 44)
                    .addBox(-4.0f, -0.1f, -4.0f, 8.0f, 1.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.2731f, 0.0f, 0.0f)
            )
            val hat =
                partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))


            val HatRingBuckle = head.addOrReplaceChild(
                "HatRingBuckle",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.5f, -8.1f, -5.3f, 3.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val HornFrontBase = head.addOrReplaceChild(
                "HornFrontBase",
                CubeListBuilder.create().texOffs(24, 0)
                    .addBox(-2.5f, -12.5f, -2.7f, 5.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.2618f, 0.0f, 0.0f)
            )

            val HornFrontBell = HornFrontBase.addOrReplaceChild(
                "HornFrontBell",
                CubeListBuilder.create().texOffs(0, 44)
                    .addBox(-1.5f, -9.9f, -10.9f, 3.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.2618f, 0.0f, 0.0f)
            )

            val HornFrontTop = HornFrontBase.addOrReplaceChild(
                "HornFrontTop",
                CubeListBuilder.create().texOffs(92, 0)
                    .addBox(-2.0f, -15.1f, -7.4f, 4.0f, 3.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val MaskTop = head.addOrReplaceChild(
                "MaskTop",
                CubeListBuilder.create().texOffs(56, 18)
                    .addBox(-4.0f, -7.1f, -5.1f, 8.0f, 6.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.0436f, 0.0f, 0.0f)
            )

            val HornLeftBase = head.addOrReplaceChild(
                "HornLeftBase",
                CubeListBuilder.create().texOffs(24, 0)
                    .addBox(-2.5f, -12.5f, -3.0f, 5.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.2618f, -2.0944f, 0.0f)
            )

            val HornLeftBell = HornLeftBase.addOrReplaceChild(
                "HornLeftBell",
                CubeListBuilder.create().texOffs(0, 44)
                    .addBox(-1.5f, -10.4f, -11.5f, 3.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.2618f, 0.0f, 0.0f)
            )

            val HornLeftTop = HornLeftBase.addOrReplaceChild(
                "HornLeftTop",
                CubeListBuilder.create().texOffs(92, 0)
                    .addBox(-2.0f, -15.1f, -7.6f, 4.0f, 3.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val left_leg = partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(16, 48)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.9f, 12.0f, 0.0f)
            )

            val BootLeft = left_leg.addOrReplaceChild(
                "BootLeft",
                CubeListBuilder.create().texOffs(20, 64)
                    .addBox(-2.5f, 8.0f, -2.5f, 5.0f, 4.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val right_leg = partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.9f, 12.0f, 0.0f)
            )

            val BootRight = right_leg.addOrReplaceChild(
                "BootRight",
                CubeListBuilder.create().texOffs(20, 64)
                    .addBox(-2.5f, 8.0f, -2.5f, 5.0f, 4.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(16, 16)
                    .addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val Belt = body.addOrReplaceChild(
                "Belt",
                CubeListBuilder.create().texOffs(30, 32)
                    .addBox(-4.0f, 11.6f, -2.5f, 8.0f, 1.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val CapeFrontBottom = body.addOrReplaceChild(
                "CapeFrontBottom",
                CubeListBuilder.create().texOffs(56, 37)
                    .addBox(-3.0f, 12.0f, -1.9f, 6.0f, 4.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.0436f, 0.0f, 0.0f)
            )

            val BeltBuckle = body.addOrReplaceChild(
                "BeltBuckle",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.5f, 11.0f, -2.7f, 3.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val CapeBackBottom = body.addOrReplaceChild(
                "CapeBackBottom",
                CubeListBuilder.create().texOffs(56, 42)
                    .addBox(-3.6f, 12.0f, 0.8f, 7.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0436f, 0.0f, 0.0f)
            )

            val CapeBackTop = body.addOrReplaceChild(
                "CapeBackTop",
                CubeListBuilder.create().texOffs(56, 25)
                    .addBox(-3.5f, 0.8f, 1.8f, 7.0f, 11.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.0436f, 0.0f, 0.0f)
            )

            val CapeFrontTop = body.addOrReplaceChild(
                "CapeFrontTop",
                CubeListBuilder.create().texOffs(56, 25)
                    .addBox(-3.5f, 0.5f, -2.7f, 7.0f, 11.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0436f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}