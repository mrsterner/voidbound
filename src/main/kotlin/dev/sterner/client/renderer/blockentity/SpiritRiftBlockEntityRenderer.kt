package dev.sterner.client.renderer.blockentity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.api.VoidBoundApi
import dev.sterner.client.renderer.blockentity.SpiritBinderBlockEntityRenderer.Companion.TOKEN
import dev.sterner.common.blockentity.SpiritRiftBlockEntity
import dev.sterner.registry.VoidBoundRenderTypes
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry
import team.lodestar.lodestone.systems.rendering.VFXBuilders


class SpiritRiftBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<SpiritRiftBlockEntity> {

    var renderType = VoidBoundRenderTypes.GRAVITY_VORTEX.apply(TOKEN)
    val copy = LodestoneRenderTypeRegistry.copy(renderType)
    val copy2 = LodestoneRenderTypeRegistry.copy(renderType)

    override fun render(
        blockEntity: SpiritRiftBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {


        var alpha = 0.1f

        if (VoidBoundApi.hasGoggles()) {
            alpha = 1f
            renderType = VoidBoundRenderTypes.GRAVITY_VORTEX_DEPTH.apply(TOKEN)
        }

        var builder = VFXBuilders.createWorld()
            .setRenderType(LodestoneRenderTypeRegistry.applyUniformChanges(
                renderType
            ) { s: ShaderInstance ->
                s.safeGetUniform("RingCount").set(10f)
                s.safeGetUniform("RingSpeed").set(30f)
                s.safeGetUniform("CycleDuration").set(1f)
                s.safeGetUniform("TunnelElongation").set(0.25f)
                s.safeGetUniform("RotationSpeed").set(0f)
                s.safeGetUniform("Alpha").set(alpha)
            })
        val cam = Minecraft.getInstance().gameRenderer.mainCamera
        poseStack.pushPose()

        poseStack.popPose()

        poseStack.pushPose()
        poseStack.translate(0.5, 0.5, 0.5)


        poseStack.mulPose(cam.rotation())
        poseStack.mulPose(Axis.XP.rotationDegrees(180f))
        poseStack.scale(2f, 2f, 2f)
        builder.renderQuad(poseStack, 0.5f)

        builder = VFXBuilders.createWorld()
            .setRenderType(LodestoneRenderTypeRegistry.applyUniformChanges(
                copy
            ) { s: ShaderInstance ->
                s.safeGetUniform("RotationSpeed").set(1000f)
                s.safeGetUniform("Alpha").set(alpha)
            })

        builder.renderQuad(poseStack, 0.4f)

        builder = VFXBuilders.createWorld()
            .setRenderType(LodestoneRenderTypeRegistry.applyUniformChanges(
                copy2
            ) { s: ShaderInstance ->
                s.safeGetUniform("RotationSpeed").set(2000f)
                s.safeGetUniform("Alpha").set(alpha)
            })

        builder.renderQuad(poseStack, 0.3f)



        poseStack.popPose()
    }
}