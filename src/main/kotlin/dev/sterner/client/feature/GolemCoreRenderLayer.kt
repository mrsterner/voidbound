package dev.sterner.client.feature

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.VoidBound
import dev.sterner.api.entity.GolemCore
import dev.sterner.client.model.GolemCoreModel
import dev.sterner.client.model.SoulSteelGolemEntityModel
import dev.sterner.client.renderer.entity.SoulSteelGolemEntityRenderer
import dev.sterner.common.entity.SoulSteelGolemEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture

class GolemCoreRenderLayer(
    ctx: EntityRendererProvider.Context,
    soulSteelGolemEntityRenderer: SoulSteelGolemEntityRenderer
) : RenderLayer<SoulSteelGolemEntity, SoulSteelGolemEntityModel>(
    soulSteelGolemEntityRenderer
) {

    val model = GolemCoreModel(ctx.bakeLayer(GolemCoreModel.LAYER_LOCATION))

    override fun render(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        livingEntity: SoulSteelGolemEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTick: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        if (livingEntity.getGolemCore() != GolemCore.NONE) {
            val texture = VoidBound.id("textures/entity/golem_core_${livingEntity.getGolemCore().serializedName}.png")
            poseStack.pushPose()
            poseStack.scale(0.75f, 0.75f, 1f)
            poseStack.translate(0.0f, 0.3f, 0f)
            model.renderToBuffer(
                poseStack,
                buffer.getBuffer(RenderType.eyes(texture)),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1f,
                1f,
                1f,
                1f
            )
            poseStack.popPose()
        }
    }
}