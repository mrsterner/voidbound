package dev.sterner.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.VoidBound
import dev.sterner.common.entity.AbstractCultistEntity
import dev.sterner.common.entity.GrimcultArcherEntity
import net.minecraft.client.model.AnimationUtils
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.entity.HumanoidArm


class GrimcultArcherModel(root: ModelPart) : AbstractGrimcultModel<GrimcultArcherEntity>(root) {
    private val leftArm: ModelPart = root.getChild("left_arm")
    private val rightArm: ModelPart = root.getChild("right_arm")
    private val body: ModelPart = root.getChild("body")
    private val head: ModelPart = root.getChild("head")
    private val hat: ModelPart = root.getChild("hat")
    private val rightLeg: ModelPart = root.getChild("right_leg")
    private val leftLeg: ModelPart = root.getChild("left_leg")

    override fun setupAnim(
        entity: GrimcultArcherEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)

        val armPose = (entity as AbstractCultistEntity).getArmPose()

        when (armPose) {
            AbstractCultistEntity.GrimcultArmPose.BOW_AND_ARROW -> {
                rightArm.yRot = -0.1f + head.yRot
                rightArm.xRot = -1.5707964f + head.xRot
                leftArm.xRot = -0.9424779f + head.xRot
                leftArm.yRot = head.yRot - 0.4f
                leftArm.zRot = 1.5707964f
            }

            AbstractCultistEntity.GrimcultArmPose.CROSSBOW_HOLD -> {
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true)
            }

            AbstractCultistEntity.GrimcultArmPose.CROSSBOW_CHARGE -> {
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, true)
            }

            AbstractCultistEntity.GrimcultArmPose.ATTACKING -> {}
            AbstractCultistEntity.GrimcultArmPose.SPELLCASTING -> {}
            AbstractCultistEntity.GrimcultArmPose.NEUTRAL -> {}
            AbstractCultistEntity.GrimcultArmPose.BLOCK -> {}
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
        entity: GrimcultArcherEntity,
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
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(VoidBound.id("grimcult_archer"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root

            val leftArm = partDefinition.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(32, 48)
                    .addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(5.0f, 2.0f, 0.0f)
            )

            leftArm.addOrReplaceChild(
                "LeftElbowPad",
                CubeListBuilder.create().texOffs(59, 47).mirror()
                    .addBox(2.5f, 0.5f, -2.5f, 1.0f, 4.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            leftArm.addOrReplaceChild(
                "LeftShoulderPad",
                CubeListBuilder.create().texOffs(59, 40).mirror()
                    .addBox(-1.5f, -2.5f, -2.5f, 5.0f, 2.0f, 5.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val rightArm = partDefinition.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(40, 16)
                    .addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, 2.0f, 0.0f)
            )

            rightArm.addOrReplaceChild(
                "RightShoulderPad",
                CubeListBuilder.create().texOffs(59, 40)
                    .addBox(-3.5f, -2.5f, -2.5f, 5.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            rightArm.addOrReplaceChild(
                "RightElbowPad",
                CubeListBuilder.create().texOffs(59, 47)
                    .addBox(-3.5f, 0.5f, -2.5f, 1.0f, 4.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val body = partDefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(16, 16)
                    .addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            body.addOrReplaceChild(
                "ChestArmor",
                CubeListBuilder.create().texOffs(59, 27)
                    .addBox(-4.5f, -0.4f, -2.4f, 9.0f, 8.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val leggings = body.addOrReplaceChild(
                "Leggings",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.5f, 9.6f, -2.8f, 3.0f, 2.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            leggings.addOrReplaceChild(
                "ClothThing",
                CubeListBuilder.create().texOffs(0, 32)
                    .addBox(-5.0f, -0.5f, -2.5f, 8.0f, 3.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.0f, 11.5f, 0.0f, 0.0f, 0.0f, -0.5236f)
            )

            body.addOrReplaceChild(
                "Cape",
                CubeListBuilder.create().texOffs(0, 50)
                    .addBox(-3.5f, 0.0f, 0.0f, 7.0f, 14.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 2.0f, 0.0873f, 0.0f, 0.0f)
            )

            val head = partDefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                    .texOffs(0, 0)
                    .addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            ).addOrReplaceChild(
                "hood",
                CubeListBuilder.create().texOffs(64, 0)
                    .addBox(-4.5f, -8.5f, -3.7f, 9.0f, 9.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0873f, 0.0f, 0.0f)
            )

            partDefinition.addOrReplaceChild(
                "hat",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            head.addOrReplaceChild(
                "HoodBack",
                CubeListBuilder.create().texOffs(76, 17)
                    .addBox(-4.0f, -8.5f, 2.3f, 8.0f, 7.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.1396f, 0.0f, 0.0f)
            )

            val rightLeg = partDefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.9f, 12.0f, 0.0f)
            )

            rightLeg.addOrReplaceChild(
                "RightKneePad",
                CubeListBuilder.create().texOffs(0, 4)
                    .addBox(-1.5f, 3.5f, -2.5f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            rightLeg.addOrReplaceChild(
                "RightBoot",
                CubeListBuilder.create().texOffs(0, 40)
                    .addBox(-2.5f, 7.0f, -2.5f, 5.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val leftLeg = partDefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(0, 16).mirror()
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(1.9f, 12.0f, 0.0f)
            )

            leftLeg.addOrReplaceChild(
                "LEftBoot",
                CubeListBuilder.create().texOffs(0, 40)
                    .addBox(-2.5f, 7.0f, -2.5f, 5.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            leftLeg.addOrReplaceChild(
                "RightKneePad_1",
                CubeListBuilder.create().texOffs(0, 4)
                    .addBox(-1.5f, 3.5f, -2.5f, 3.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshDefinition, 128, 128)
        }
    }
}