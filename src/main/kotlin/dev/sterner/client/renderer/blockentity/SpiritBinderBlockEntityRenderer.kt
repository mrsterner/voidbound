package dev.sterner.client.renderer.blockentity

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.common.blockentity.SpiritBinderBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import team.lodestar.lodestone.LodestoneLib
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken


class SpiritBinderBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<SpiritBinderBlockEntity> {

    override fun render(
        blockEntity: SpiritBinderBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5, 1.5, 0.5)
        /*
                val interpolatedAlpha = Mth.lerp(partialTick, blockEntity.previousAlpha!!, blockEntity.alpha!!)

                val rgb: Vector3f = blockEntity.color

                // Obtain the render type
                val renderType = VoidBoundRenderTypes.TRANSPARENT_GLOW_TEXTURE.apply(
                    TOKEN
                )

                // Create the builder and bind the shader
                val builder = VFXBuilders.createWorld()
                    .setRenderType(LodestoneRenderTypeRegistry.applyUniformChanges(
                        renderType
                    ) { s: ShaderInstance ->
                        s.safeGetUniform("Alpha").set(interpolatedAlpha)
                        s.safeGetUniform("SphereColor").set(rgb)
                    })

                // Render the sphere
                builder.renderSphere(poseStack, 1f, 20, 20)


         */

        // Render the spirit glimmer
        /*
        FloatingItemEntityRenderer.renderSpiritGlimmer(
            poseStack,
            Color(200, 200, 255, 200),
            Color(100, 100, 255, 200),
            partialTick
        )

         */

        poseStack.popPose()
    }

    companion object {
        var TOKEN: RenderTypeToken = RenderTypeToken.createCachedToken(
            LodestoneLib.lodestonePath("textures/particle/wisp.png")
        )
    }
}