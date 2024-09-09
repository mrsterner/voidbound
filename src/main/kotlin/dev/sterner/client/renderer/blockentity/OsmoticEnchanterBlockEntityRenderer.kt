package dev.sterner.client.renderer.blockentity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.client.model.EnchanterGrimcultBookModel
import dev.sterner.client.renderer.GrimcultRitesRenderer.texture
import dev.sterner.common.blockentity.OsmoticEnchanterBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.resources.model.Material
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Quaternionf

class OsmoticEnchanterBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<OsmoticEnchanterBlockEntity> {

    private val bookModel = EnchanterGrimcultBookModel(ctx.bakeLayer(EnchanterGrimcultBookModel.LAYER_LOCATION))

    override fun render(
        blockEntity: OsmoticEnchanterBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        buffers: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        if (blockEntity.blockState.hasProperty(BlockStateProperties.HAS_BOOK) && blockEntity.blockState.getValue(
                BlockStateProperties.HAS_BOOK
            )
        ) {
            renderBook(blockEntity, partialTick, poseStack, buffers, packedLight, packedOverlay)
        }
    }

    private fun renderBook(
        blockEntity: OsmoticEnchanterBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        buffers: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5f, 0.05f, 0.5f)
        val f = blockEntity.time.toFloat() + partialTick
        var g = blockEntity.rot - blockEntity.oRot

        while (g >= Math.PI.toFloat()) {
            g -= (Math.PI * 2).toFloat()
        }

        while (g < -Math.PI.toFloat()) {
            g += (Math.PI * 2).toFloat()
        }
        val h = blockEntity.oRot + g * partialTick

        if (blockEntity.blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            val dir = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
            val yOffset = Mth.sin(f * 0.1f) * 0.02f
            val (xOffset, zOffset) = when (dir) {
                Direction.SOUTH -> Pair(0.0f, -0.3f)
                Direction.NORTH -> Pair(0.0f, -0.3f)
                Direction.EAST -> Pair(-0.0f, -0.3f)
                else -> Pair(0.0f, -0.3f)
            }

            poseStack.mulPose(Quaternionf().rotationY(-h))
            poseStack.translate(xOffset, 0.4f + yOffset, zOffset)
            poseStack.mulPose(Axis.XP.rotationDegrees(25f))
        }


        poseStack.scale(-1.0f, -1.0f, 1.0f)
        poseStack.translate(0.0f, -2.5f, 0.0f)

        val mat: Material = texture
        val buffer = mat.buffer(buffers, RenderType::entitySolid)
        this.bookModel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, 1.0f)

        poseStack.popPose()
    }
}