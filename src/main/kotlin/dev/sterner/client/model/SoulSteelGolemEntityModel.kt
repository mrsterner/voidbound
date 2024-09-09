package dev.sterner.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.VoidBound
import dev.sterner.common.entity.SoulSteelGolemEntity
import net.minecraft.client.model.ArmedModel
import net.minecraft.client.model.HierarchicalModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.util.Mth
import net.minecraft.world.entity.HumanoidArm


class SoulSteelGolemEntityModel(root: ModelPart) : HierarchicalModel<SoulSteelGolemEntity>(), ArmedModel {
    //<T extends IronGolem> extends HierarchicalModel<T>
    private val core: ModelPart = root.getChild("core")
    private val head: ModelPart = core.getChild("head")
    private val torso: ModelPart = core.getChild("torso")
    private val rightArm: ModelPart = core.getChild("rightArm")
    private val leftArm: ModelPart = core.getChild("leftArm")
    private val rightLeg: ModelPart = core.getChild("rightLeg")
    private val leftLeg: ModelPart = core.getChild("leftLeg")

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        core.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    override fun root(): ModelPart {
        return core
    }

    override fun setupAnim(
        entity: SoulSteelGolemEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        this.head.yRot = netHeadYaw * (Math.PI / 180.0).toFloat()
        this.head.xRot = headPitch * (Math.PI / 180.0).toFloat()
        this.rightLeg.xRot = -1.5f * Mth.triangleWave(limbSwing, 13.0f) * limbSwingAmount
        this.leftLeg.xRot = 1.5f * Mth.triangleWave(limbSwing, 13.0f) * limbSwingAmount
        this.rightLeg.yRot = 0.0f
        this.leftLeg.yRot = 0.0f
    }


    override fun prepareMobModel(
        entity: SoulSteelGolemEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTick: Float
    ) {
        val i: Int = entity.attackAnimationTick
        if (i > 0) {
            rightArm.xRot = -2.0f + 1.5f * Mth.triangleWave(i.toFloat() - partialTick, 10.0f)
            leftArm.xRot = -2.0f + 1.5f * Mth.triangleWave(i.toFloat() - partialTick, 10.0f)
        } else {
            rightArm.xRot = (-0.2f + 1.5f * Mth.triangleWave(limbSwing, 13.0f)) * limbSwingAmount
            leftArm.xRot = (-0.2f - 1.5f * Mth.triangleWave(limbSwing, 13.0f)) * limbSwingAmount
        }
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(VoidBound.id("malum_golem"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val core =
                partdefinition.addOrReplaceChild("core", CubeListBuilder.create(), PartPose.offset(0.0f, 15.0f, 0.0f))

            core.addOrReplaceChild(
                "rightLeg",
                CubeListBuilder.create().texOffs(40, 0)
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.0f, 3.0f, 0.0f)
            )

            core.addOrReplaceChild(
                "leftArm",
                CubeListBuilder.create().texOffs(24, 23).mirror()
                    .addBox(-3.0f, -2.0f, -2.0f, 3.0f, 13.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(-4.0f, -2.0f, 0.0f)
            )

            core.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -6.0f, -4.0f, 8.0f, 6.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(22, 14).addBox(-1.0f, -3.0f, -6.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 27).addBox(-1.0f, -8.0f, -3.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).mirror().addBox(-3.0f, -12.0f, -3.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(0, 0).addBox(1.0f, -12.0f, -3.0f, 2.0f, 5.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -4.0f, 0.0f)
            )

            core.addOrReplaceChild(
                "torso",
                CubeListBuilder.create().texOffs(0, 14)
                    .addBox(-4.0f, -13.0f, -3.0f, 8.0f, 7.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 9.0f, 0.0f)
            )

            core.addOrReplaceChild(
                "rightArm",
                CubeListBuilder.create().texOffs(24, 23)
                    .addBox(0.0f, -2.0f, -2.0f, 3.0f, 13.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(4.0f, -2.0f, 0.0f)
            )

            core.addOrReplaceChild(
                "leftLeg",
                CubeListBuilder.create().texOffs(40, 0).mirror()
                    .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(-2.0f, 3.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }


    override fun translateToHand(side: HumanoidArm, poseStack: PoseStack) {
        val bl = side == HumanoidArm.RIGHT
        val modelPart = if (bl) this.rightArm else this.leftArm
        core.translateAndRotate(poseStack)
        modelPart.translateAndRotate(poseStack)
        poseStack.scale(0.75f, 0.75f, 0.75f)
        poseStack.translate(-0.2f, 0.3f, 0f)
        this.offsetStackPosition(poseStack, bl)
    }

    private fun offsetStackPosition(poseStack: PoseStack, rightSide: Boolean) {
        if (rightSide) {
            poseStack.translate(0.046875, -0.15625, 0.078125)
        } else {
            poseStack.translate(-0.046875, -0.15625, 0.078125)
        }
    }
}