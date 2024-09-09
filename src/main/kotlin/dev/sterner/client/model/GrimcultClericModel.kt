package dev.sterner.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.VoidBound
import dev.sterner.common.entity.AbstractCultistEntity
import dev.sterner.common.entity.GrimcultClericEntity
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.util.Mth
import net.minecraft.world.entity.HumanoidArm


class GrimcultClericModel(root: ModelPart) : AbstractGrimcultModel<GrimcultClericEntity>(root) {
    private val left_leg: ModelPart = root.getChild("left_leg")
    private val right_leg: ModelPart = root.getChild("right_leg")
    private val body: ModelPart = root.getChild("body")
    private val hat: ModelPart = root.getChild("hat")
    private val left_arm: ModelPart = root.getChild("left_arm")
    private val right_arm: ModelPart = root.getChild("right_arm")
    private val head: ModelPart = root.getChild("head")

    private val LeftArm = left_arm.getChild("LeftArm")
    private val ShoulderLBottom = left_arm.getChild("ShoulderLBottom")
    private val ShoulderLBottomBack = left_arm.getChild("ShoulderLBottomBack")
    private val ShoulderL = left_arm.getChild("ShoulderL")

    private val RightArm = right_arm.getChild("RightArm")
    private val ShoulderRBottom = right_arm.getChild("ShoulderRBottom")
    private val ShoulderRBottomBack = right_arm.getChild("ShoulderRBottomBack")
    private val ShoulderR = right_arm.getChild("ShoulderR")


    override fun setupAnim(
        entity: GrimcultClericEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)

        val armPose = (entity as AbstractCultistEntity).getArmPose()
        if (armPose == AbstractCultistEntity.GrimcultArmPose.SPELLCASTING) {
            rightArm.z = 0.0f
            rightArm.x = -5.0f
            leftArm.z = 0.0f
            leftArm.x = 5.0f
            rightArm.xRot = Mth.cos(ageInTicks * 0.6662f) * 0.25f
            leftArm.xRot = Mth.cos(ageInTicks * 0.6662f) * 0.25f

            RightArm.zRot = 2.3561945f
            ShoulderRBottom.visible = false
            ShoulderRBottomBack.visible = false
            ShoulderR.visible = false

            LeftArm.zRot = -2.3561945f
            ShoulderLBottom.visible = false
            ShoulderLBottomBack.visible = false
            ShoulderL.visible = false

            rightArm.yRot = 0.0f
            leftArm.yRot = 0.0f
        } else {

            ShoulderRBottom.visible = true
            ShoulderRBottomBack.visible = true
            ShoulderR.visible = true

            ShoulderLBottom.visible = true
            ShoulderLBottomBack.visible = true
            ShoulderL.visible = true

            RightArm.zRot = 0f
            LeftArm.zRot = 0f
        }
    }

    override fun translateToHand(side: HumanoidArm, poseStack: PoseStack) {
        val f = if (side == HumanoidArm.RIGHT) 1.0f else -1.0f
        val modelPart = this.getArm(side)
        modelPart.x += f
        modelPart.translateAndRotate(poseStack)
        modelPart.x -= f
    }

    override fun prepareMobModel(
        entity: GrimcultClericEntity,
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
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(VoidBound.id("grimcult_cleric"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val left_leg = partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(16, 48)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.9f, 12.0f, 0.0f)
            )

            val LeftShoe = left_leg.addOrReplaceChild(
                "LeftShoe",
                CubeListBuilder.create().texOffs(30, 39)
                    .addBox(-2.5f, 9.0f, -2.4f, 5.0f, 3.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val LegL = left_leg.addOrReplaceChild(
                "LegL",
                CubeListBuilder.create().texOffs(0, 59).mirror()
                    .addBox(-1.4f, -0.8f, -2.5f, 4.0f, 12.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.2094f)
            )

            val right_leg = partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.9f, 12.0f, 0.0f)
            )

            val RightShoe = right_leg.addOrReplaceChild(
                "RightShoe",
                CubeListBuilder.create().texOffs(30, 39)
                    .addBox(-2.5f, 9.0f, -2.4f, 5.0f, 3.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val LegR = right_leg.addOrReplaceChild(
                "LegR",
                CubeListBuilder.create().texOffs(0, 59)
                    .addBox(-2.5f, -0.8f, -2.5f, 4.0f, 12.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2094f)
            )

            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(16, 16)
                    .addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val CapeBack = body.addOrReplaceChild(
                "CapeBack",
                CubeListBuilder.create().texOffs(18, 64)
                    .addBox(-3.5f, 1.9f, 1.3f, 7.0f, 9.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0646f, 0.0f, 0.0f)
            )

            val CapeBuckle = body.addOrReplaceChild(
                "CapeBuckle",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.5f, 0.4f, -3.2f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val CapeIcon = body.addOrReplaceChild(
                "CapeIcon",
                CubeListBuilder.create().texOffs(10, 40)
                    .addBox(-1.5f, 18.5f, -2.2f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.0873f, 0.0f, 0.0f)
            )

            val Belt = body.addOrReplaceChild(
                "Belt",
                CubeListBuilder.create().texOffs(28, 32)
                    .addBox(-4.5f, 10.6f, -2.8f, 9.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val CapeFront = body.addOrReplaceChild(
                "CapeFront",
                CubeListBuilder.create().texOffs(0, 40)
                    .addBox(-2.0f, 12.0f, -1.9f, 4.0f, 11.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.0873f, 0.0f, 0.0f)
            )

            val CapeBackBottom = body.addOrReplaceChild(
                "CapeBackBottom",
                CubeListBuilder.create().texOffs(34, 64)
                    .addBox(-3.5f, 11.2f, 0.5f, 7.0f, 12.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.1396f, 0.0f, 0.0f)
            )

            val CapeFrontTop = body.addOrReplaceChild(
                "CapeFrontTop",
                CubeListBuilder.create().texOffs(56, 0)
                    .addBox(-0.6f, -0.6f, -1.3f, 5.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.2513f, 0.2513f, 0.7854f)
            )

            val BeltBuckle = body.addOrReplaceChild(
                "BeltBuckle",
                CubeListBuilder.create().texOffs(24, 0)
                    .addBox(-2.0f, 10.0f, -3.2f, 4.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val left_arm = partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create(),
                PartPose.offset(5.0f, 3.0f, 0.0f)
            )

            val LeftArm = left_arm.addOrReplaceChild(
                "LeftArm",
                CubeListBuilder.create().texOffs(32, 48)
                    .addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -1.0f, 0.0f)
            )

            val ShoulderLBottom = left_arm.addOrReplaceChild(
                "ShoulderLBottom",
                CubeListBuilder.create().texOffs(18, 40).mirror()
                    .addBox(5.5f, 2.5f, -2.2f, 3.0f, 2.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-5.0f, -3.0f, 0.0f, 0.0f, 0.0f, -0.1396f)
            )

            val ShoulderLBottomBack = left_arm.addOrReplaceChild(
                "ShoulderLBottomBack",
                CubeListBuilder.create().texOffs(18, 40).mirror()
                    .addBox(5.5f, 2.5f, -0.2f, 3.0f, 2.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-5.0f, -3.0f, 0.0f, 0.0f, 0.0f, -0.1396f)
            )

            val ShoulderL = left_arm.addOrReplaceChild(
                "ShoulderL",
                CubeListBuilder.create().texOffs(0, 32)
                    .addBox(0.0f, 0.0f, -2.5f, 9.0f, 3.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-5.0f, -3.0f, 0.0f, 0.0f, 0.0f, -0.1396f)
            )

            val right_arm = partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create(),
                PartPose.offset(-5.0f, 3.0f, 0.0f)
            )

            val ShoulderRBottomBack = right_arm.addOrReplaceChild(
                "ShoulderRBottomBack",
                CubeListBuilder.create().texOffs(18, 41)
                    .addBox(-8.3f, 2.5f, 0.2f, 3.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.0f, -3.0f, 0.0f, 0.0f, 0.0f, 0.1396f)
            )

            val RightArm = right_arm.addOrReplaceChild(
                "RightArm",
                CubeListBuilder.create().texOffs(40, 16)
                    .addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -1.0f, 0.0f)
            )

            val ShoulderR = right_arm.addOrReplaceChild(
                "ShoulderR",
                CubeListBuilder.create().texOffs(0, 32).mirror()
                    .addBox(-9.0f, 0.0f, -2.5f, 9.0f, 3.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(5.0f, -3.0f, 0.0f, 0.0f, 0.0f, 0.1396f)
            )

            val ShoulderRBottom = right_arm.addOrReplaceChild(
                "ShoulderRBottom",
                CubeListBuilder.create().texOffs(18, 40)
                    .addBox(-8.3f, 2.5f, -2.2f, 3.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.0f, -3.0f, 0.0f, 0.0f, 0.0f, 0.1396f)
            )

            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 80)
                    .addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val HoodBackest = head.addOrReplaceChild(
                "HoodBackest",
                CubeListBuilder.create().texOffs(96, 17)
                    .addBox(-3.0f, -7.3f, 4.7f, 6.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.2443f, 0.0f, 0.0f)
            )

            val HoodFronter = head.addOrReplaceChild(
                "HoodFronter",
                CubeListBuilder.create().texOffs(64, 17)
                    .addBox(-2.0f, -7.1f, -6.1f, 4.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.0524f, 0.0f, 0.0f)
            )

            val HoodMain = head.addOrReplaceChild(
                "HoodMain",
                CubeListBuilder.create().texOffs(64, 0)
                    .addBox(-4.5f, -8.5f, -3.7f, 9.0f, 9.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.0524f, 0.0f, 0.0f)
            )

            val Hair = head.addOrReplaceChild(
                "Hair",
                CubeListBuilder.create().texOffs(32, 80)
                    .addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val HoodBack = head.addOrReplaceChild(
                "HoodBack",
                CubeListBuilder.create().texOffs(76, 17)
                    .addBox(-4.0f, -8.3f, 3.5f, 8.0f, 7.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.1396f, 0.0f, 0.0f)
            )

            val HoodFrontest = head.addOrReplaceChild(
                "HoodFrontest",
                CubeListBuilder.create().texOffs(64, 22)
                    .addBox(-1.0f, -7.7f, -6.4f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.0524f, 0.0f, 0.0f)
            )

            val HoodFront = head.addOrReplaceChild(
                "HoodFront",
                CubeListBuilder.create().texOffs(90, 0)
                    .addBox(-4.5f, -8.5f, -4.4f, 9.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.096f, 0.0f, 0.0f)
            )

            val hat =
                partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(4.0f, 3.0f, 0.0f))

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}