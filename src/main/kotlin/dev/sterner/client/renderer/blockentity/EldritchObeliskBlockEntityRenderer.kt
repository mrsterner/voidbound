package dev.sterner.client.renderer.blockentity

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.VoidBound
import dev.sterner.client.model.ObeliskCoreModel
import dev.sterner.client.model.ObeliskModel
import dev.sterner.common.blockentity.EldritchObeliskBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider

class EldritchObeliskBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<EldritchObeliskBlockEntity> {

    private val coreModel = ObeliskCoreModel(ctx.bakeLayer(ObeliskCoreModel.LAYER_LOCATION))
    private val mainModel = ObeliskModel(ctx.bakeLayer(ObeliskModel.LAYER_LOCATION))


    override fun render(
        blockEntity: EldritchObeliskBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5, 1.5, 0.5)
        poseStack.scale(-1.0f, -1.0f, 1.0f)

        coreModel.renderToBuffer(
            poseStack,
            buffer.getBuffer(RenderType.endPortal()),
            packedLight,
            packedOverlay,
            1f,
            1f,
            1f,
            1f
        )
        mainModel.renderToBuffer(
            poseStack,
            buffer.getBuffer(RenderType.entityTranslucent(VoidBound.id("textures/block/obelisk.png"))),
            packedLight,
            packedOverlay,
            1f,
            1f,
            1f,
            1f
        )

        poseStack.popPose()
    }
}