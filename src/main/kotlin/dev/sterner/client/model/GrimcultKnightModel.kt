package dev.sterner.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.VoidBound
import dev.sterner.common.entity.GrimcultKnightEntity
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.entity.HumanoidArm


class GrimcultKnightModel(root: ModelPart) : AbstractGrimcultModel<GrimcultKnightEntity>(root) {
    private val rightArm: ModelPart = root.getChild("right_arm")
    private val rightLeg: ModelPart = root.getChild("right_leg")
    private val hat: ModelPart = root.getChild("hat")
    private val head: ModelPart = root.getChild("head")
    private val leftLeg: ModelPart = root.getChild("left_leg")
    private val leftArm: ModelPart = root.getChild("left_arm")
    private val body: ModelPart = root.getChild("body")

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
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        hat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    override fun translateToHand(side: HumanoidArm, poseStack: PoseStack) {
        val f = if (side == HumanoidArm.RIGHT) 0.5f else -0.5f
        val modelPart = this.getArm(side)
        modelPart.x += f
        modelPart.translateAndRotate(poseStack)
        modelPart.x -= f
    }

    override fun prepareMobModel(
        entity: GrimcultKnightEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTick: Float
    ) {
        this.rightArmPose = ArmPose.EMPTY
        this.leftArmPose = ArmPose.EMPTY
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick)
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(VoidBound.id("grimcult_knight"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val right_arm = partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(40, 16)
                    .addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, 2.0f, 0.0f)
            )

            val RightArmPiece = right_arm.addOrReplaceChild(
                "RightArmPiece",
                CubeListBuilder.create().texOffs(0, 56)
                    .addBox(-3.5f, 1.0f, -2.5f, 2.0f, 4.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val RightShoulderPlate = right_arm.addOrReplaceChild(
                "RightShoulderPlate",
                CubeListBuilder.create().texOffs(0, 65)
                    .addBox(-4.5f, -3.0f, -2.5f, 6.0f, 3.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.2269f)
            )

            val RightGauntlet = right_arm.addOrReplaceChild(
                "RightGauntlet",
                CubeListBuilder.create().texOffs(0, 49)
                    .addBox(-3.5f, 5.5f, -1.5f, 1.0f, 4.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val right_leg = partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.9f, 12.0f, 0.0f)
            )

            val RightFaulds = right_leg.addOrReplaceChild(
                "RightFaulds",
                CubeListBuilder.create().texOffs(28, 32)
                    .addBox(-2.1f, 0.2f, -2.5f, 3.0f, 6.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2269f)
            )

            val RightBoot = right_leg.addOrReplaceChild(
                "RightBoot",
                CubeListBuilder.create().texOffs(56, 0)
                    .addBox(-1.5f, 10.0f, -2.9f, 3.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val RightBootTop = right_leg.addOrReplaceChild(
                "RightBootTop",
                CubeListBuilder.create().texOffs(12, 16)
                    .addBox(-1.5f, 5.2f, -2.4f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )
            val hat =
                partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))


            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(32, 0)
                    .addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            ).addOrReplaceChild(
                "helm",
                CubeListBuilder.create().texOffs(64, 0)
                    .addBox(-4.5f, -8.5f, -4.5f, 9.0f, 9.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val left_leg = partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(16, 48)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.9f, 12.0f, 0.0f)
            )

            val LeftBoot = left_leg.addOrReplaceChild(
                "LeftBoot",
                CubeListBuilder.create().texOffs(56, 0).mirror()
                    .addBox(-1.5f, 10.0f, -2.9f, 3.0f, 2.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val LeftBootTop = left_leg.addOrReplaceChild(
                "LeftBootTop",
                CubeListBuilder.create().texOffs(12, 16).mirror()
                    .addBox(-1.5f, 5.2f, -2.4f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val LeftFaulds = left_leg.addOrReplaceChild(
                "LeftFaulds",
                CubeListBuilder.create().texOffs(28, 32).mirror()
                    .addBox(-0.9f, 0.2f, -2.5f, 3.0f, 6.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.2269f)
            )

            val left_arm = partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(32, 48)
                    .addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.0f, 2.0f, 0.0f, 0.0f, 0.0456f, 0.0f)
            )

            val LeftShoulderPlate = left_arm.addOrReplaceChild(
                "LeftShoulderPlate",
                CubeListBuilder.create().texOffs(0, 65).mirror()
                    .addBox(-1.5f, -3.0f, -2.5f, 6.0f, 3.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2269f)
            )

            val LeftGauntlet = left_arm.addOrReplaceChild(
                "LeftGauntlet",
                CubeListBuilder.create().texOffs(0, 49).mirror()
                    .addBox(2.5f, 5.5f, -1.5f, 1.0f, 4.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val LeftArmPiece = left_arm.addOrReplaceChild(
                "LeftArmPiece",
                CubeListBuilder.create().texOffs(0, 56).mirror()
                    .addBox(1.5f, 1.0f, -2.5f, 2.0f, 4.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(16, 16)
                    .addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val UpperChestplate = body.addOrReplaceChild(
                "UpperChestplate",
                CubeListBuilder.create().texOffs(0, 32)
                    .addBox(-4.5f, 0.5f, -2.5f, 9.0f, 7.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val ChestplateTopLeft = body.addOrReplaceChild(
                "ChestplateTopLeft",
                CubeListBuilder.create().texOffs(24, 0)
                    .addBox(1.5f, -0.2f, -2.0f, 2.0f, 1.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val BeltBuckle = body.addOrReplaceChild(
                "BeltBuckle",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.5f, 10.0f, -2.7f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val ChestplateTopRight = body.addOrReplaceChild(
                "ChestplateTopRight",
                CubeListBuilder.create().texOffs(24, 0)
                    .addBox(-3.5f, -0.2f, -2.0f, 2.0f, 1.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val Belt = body.addOrReplaceChild(
                "Belt",
                CubeListBuilder.create().texOffs(56, 18)
                    .addBox(-4.5f, 11.0f, -2.5f, 9.0f, 1.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}