package dev.sterner.client.renderer.blockentity

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.common.blockentity.SpiritStabilizerBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider

class SpiritStabilizerBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<SpiritStabilizerBlockEntity> {

    override fun render(
        blockEntity: SpiritStabilizerBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {

    }
}