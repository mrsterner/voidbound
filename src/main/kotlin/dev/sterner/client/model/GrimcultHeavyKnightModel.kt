package dev.sterner.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.VoidBound
import dev.sterner.common.entity.AbstractCultistEntity
import dev.sterner.common.entity.GrimcultHeavyKnightEntity
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.entity.HumanoidArm


class GrimcultHeavyKnightModel(root: ModelPart) : AbstractGrimcultModel<GrimcultHeavyKnightEntity>(root) {
    private val left_arm: ModelPart = root.getChild("left_arm")
    private val right_leg: ModelPart = root.getChild("right_leg")
    private val right_arm: ModelPart = root.getChild("right_arm")
    private val left_leg: ModelPart = root.getChild("left_leg")
    private val hat: ModelPart = root.getChild("hat")
    private val head: ModelPart = root.getChild("head")
    private val body: ModelPart = root.getChild("body")

    override fun setupAnim(
        entity: GrimcultHeavyKnightEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)

        val armPose = (entity as AbstractCultistEntity).getArmPose()

        if (armPose == AbstractCultistEntity.GrimcultArmPose.BLOCK) {
            this.rightArm.xRot = this.rightArm.xRot * 0.5f - 0.9424779f
            this.rightArm.yRot = -0.5235988f
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
        entity: GrimcultHeavyKnightEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTick: Float
    ) {
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
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(VoidBound.id("heavy_cultist_knight"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val left_arm = partdefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(32, 48)
                    .addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(5.0f, 2.0f, 0.0f)
            )

            val SwordBlade = left_arm.addOrReplaceChild(
                "SwordBlade",
                CubeListBuilder.create().texOffs(66, 65)
                    .addBox(0.5f, 5.2f, -16.1f, 1.0f, 6.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val LeftArmArmor = left_arm.addOrReplaceChild(
                "LeftArmArmor",
                CubeListBuilder.create().texOffs(46, 0).mirror()
                    .addBox(0.5f, 0.0f, -2.5f, 3.0f, 7.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.0524f)
            )

            val SwordHandle = left_arm.addOrReplaceChild(
                "SwordHandle",
                CubeListBuilder.create().texOffs(49, 64)
                    .addBox(0.5f, 7.8f, -4.0f, 1.0f, 1.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val SwordHilt = left_arm.addOrReplaceChild(
                "SwordHilt",
                CubeListBuilder.create().texOffs(66, 65)
                    .addBox(0.0f, 4.8f, -6.3f, 2.0f, 7.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val SwordBladeII = left_arm.addOrReplaceChild(
                "SwordBladeII",
                CubeListBuilder.create().texOffs(78, 63)
                    .addBox(0.5f, 6.2f, -24.1f, 1.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val LeftShoulderPlate = left_arm.addOrReplaceChild(
                "LeftShoulderPlate",
                CubeListBuilder.create().texOffs(24, 0)
                    .addBox(-0.5f, -3.2f, -2.6f, 5.0f, 3.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.3491f)
            )

            val right_leg = partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.9f, 12.0f, 0.0f)
            )

            val RightLegArmor = right_leg.addOrReplaceChild(
                "RightLegArmor",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.5f, 0.5f, -2.5f, 3.0f, 6.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val RightLegSideArmor = right_leg.addOrReplaceChild(
                "RightLegSideArmor",
                CubeListBuilder.create().texOffs(34, 32)
                    .addBox(-3.3f, 0.1f, -2.2f, 2.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.1396f)
            )

            val RightBoot = right_leg.addOrReplaceChild(
                "RightBoot",
                CubeListBuilder.create().texOffs(12, 16)
                    .addBox(-1.5f, 10.0f, -2.7f, 3.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val right_arm = partdefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(40, 16)
                    .addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, 2.0f, 0.0f)
            )

            val RightShoulderPlate = right_arm.addOrReplaceChild(
                "RightShoulderPlate",
                CubeListBuilder.create().texOffs(24, 0).mirror()
                    .addBox(-4.4f, -3.2f, -2.6f, 5.0f, 3.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.3491f)
            )

            val ShieldStrapTop = right_arm.addOrReplaceChild(
                "ShieldStrapTop",
                CubeListBuilder.create().texOffs(65, 34)
                    .addBox(-3.3f, 0.4f, -3.2f, 5.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val ShieldStrapBottom = right_arm.addOrReplaceChild(
                "ShieldStrapBottom",
                CubeListBuilder.create().texOffs(65, 34)
                    .addBox(-3.3f, 7.2f, -3.2f, 5.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val TowerShield = right_arm.addOrReplaceChild(
                "TowerShield",
                CubeListBuilder.create().texOffs(48, 29)
                    .addBox(-5.3f, -6.0f, -6.5f, 2.0f, 22.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val RightArmArmor = right_arm.addOrReplaceChild(
                "RightArmArmor",
                CubeListBuilder.create().texOffs(46, 0)
                    .addBox(-3.5f, 0.0f, -2.5f, 3.0f, 7.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0524f)
            )

            val left_leg = partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(16, 48)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.9f, 12.0f, 0.0f)
            )

            val LeftLegSideArmor = left_leg.addOrReplaceChild(
                "LeftLegSideArmor",
                CubeListBuilder.create().texOffs(34, 32).mirror()
                    .addBox(1.3f, 0.1f, -2.2f, 2.0f, 5.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.1396f)
            )

            val LeftBoot = left_leg.addOrReplaceChild(
                "LeftBoot",
                CubeListBuilder.create().texOffs(12, 16)
                    .addBox(-1.5f, 10.0f, -2.7f, 3.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val LeftLegArmor = left_leg.addOrReplaceChild(
                "LeftLegArmor",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.5f, 0.5f, -2.5f, 3.0f, 6.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            ).addOrReplaceChild(
                "helm",
                CubeListBuilder.create().texOffs(64, 0)
                    .addBox(-4.5f, -8.7f, -4.5f, 9.0f, 9.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val NeckArmorL = head.addOrReplaceChild(
                "NeckArmorL",
                CubeListBuilder.create().texOffs(0, 32)
                    .addBox(0.1f, 0.0f, -2.5f, 6.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.2705f)
            )

            val NeckArmorR = head.addOrReplaceChild(
                "NeckArmorR",
                CubeListBuilder.create().texOffs(0, 32).mirror()
                    .addBox(-6.1f, 0.0f, -2.5f, 6.0f, 2.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.2705f)
            )

            val hat =
                partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))


            val body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(16, 16)
                    .addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val ChestPlateBack = body.addOrReplaceChild(
                "ChestPlateBack",
                CubeListBuilder.create().texOffs(18, 39)
                    .addBox(-3.5f, 0.4f, 2.3f, 7.0f, 6.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.1396f, 0.0f, 0.0f)
            )

            val ChestPlate = body.addOrReplaceChild(
                "ChestPlate",
                CubeListBuilder.create().texOffs(0, 39)
                    .addBox(-3.5f, 0.7f, -3.5f, 7.0f, 9.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0716f, 0.0f, 0.0f)
            )

            val AssArmor = body.addOrReplaceChild(
                "AssArmor",
                CubeListBuilder.create().texOffs(39, 0)
                    .addBox(-2.5f, 12.5f, -1.5f, 5.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.0873f, 0.0f, 0.0f)
            )

            val AssArmorBack = body.addOrReplaceChild(
                "AssArmorBack",
                CubeListBuilder.create().texOffs(52, 12)
                    .addBox(-2.5f, 12.2f, 0.9f, 5.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0873f, 0.0f, 0.0f)
            )

            val ChestPlateLower = body.addOrReplaceChild(
                "ChestPlateLower",
                CubeListBuilder.create().texOffs(17, 32)
                    .addBox(-3.0f, 6.7f, 1.2f, 6.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val Belt = body.addOrReplaceChild(
                "Belt",
                CubeListBuilder.create().texOffs(0, 64)
                    .addBox(-4.5f, 10.0f, -2.5f, 9.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val BeltBuckle = body.addOrReplaceChild(
                "BeltBuckle",
                CubeListBuilder.create().texOffs(32, 8)
                    .addBox(-2.5f, 9.5f, -3.0f, 5.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}