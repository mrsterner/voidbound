package dev.sterner.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import dev.sterner.common.entity.BoltEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import org.joml.Matrix4f
import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin


class BoltEntityRenderer(context: EntityRendererProvider.Context?) : EntityRenderer<BoltEntity>(context) {

    override fun getTextureLocation(entity: BoltEntity): ResourceLocation {
        return TextureAtlas.LOCATION_BLOCKS
    }

    override fun render(
        entity: BoltEntity,
        yaw: Float,
        tickDelta: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        light: Int
    ) {
        val divisionAngle = 50
        val segmentNumber = 7
        val random = entity.level().random
        val sizeMod = min((entity.tickCount + tickDelta) / 2, 1f)
        val currentLength: Float = entity.length * sizeMod
        val maxDistancePerSegment: Float = entity.length / segmentNumber.toFloat()

        val targetNumber = (segmentNumber * sizeMod).toInt()
        val size = 0.01f

        fun unpackRgb(color: Int): Vec3 {
            val r = ((color shr 16) and 0xFF) / 255.0
            val g = ((color shr 8) and 0xFF) / 255.0
            val b = (color and 0xFF) / 255.0

            return Vec3(r, g, b)
        }

        val rgb: Vec3 = unpackRgb(Color(190, 196, 250).rgb)
        val alpha =
            (if (entity.tickCount < 3) 0.3f else max(0.3f * (1 - (entity.tickCount - 3f + tickDelta) / 3f), 0f)) * 2

        val segmentLengths = FloatArray(segmentNumber)
        for (segment in segmentLengths.indices) {
            if (segment == targetNumber) {
                segmentLengths[segment] = currentLength - maxDistancePerSegment * segment
            } else {
                segmentLengths[segment] = maxDistancePerSegment
            }
        }
        val offsetsY = FloatArray(segmentNumber)
        val offsetsX = FloatArray(segmentNumber)
        poseStack.pushPose()

        val vertexConsumer: VertexConsumer = buffer.getBuffer(RenderType.lightning())
        val matrix4f: Matrix4f = poseStack.last().pose()

        poseStack.mulPose(Axis.YN.rotationDegrees(entity.getViewYRot(tickDelta)))
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getViewXRot(tickDelta)))
        poseStack.translate(0.0, 0.0, 0.1)
        for (branches in 0..1) {
            var lastOffsetY = 0.0f
            var lastOffsetX = 0.0f
            for (i in 0 until segmentNumber) {
                offsetsY[i] = lastOffsetY
                offsetsX[i] = lastOffsetX
                lastOffsetY += sin(Math.toRadians(random.nextInt(divisionAngle) - divisionAngle / 2.0).toFloat())
                lastOffsetX += sin(Math.toRadians(random.nextInt(divisionAngle) - divisionAngle / 2.0).toFloat())
            }

            for (segment in 0 until segmentNumber) {
                val startY = offsetsY[segment]
                val startX = offsetsX[segment]

                for (depth in 1..3) {
                    val depthY = (size / 2f) + depth.toFloat() * size
                    val depthZ = (size / 2f) + depth.toFloat() * size

                    val endY = (if ((segment == segmentNumber - 1)) startY else offsetsY[segment + 1])
                    val endX = (if ((segment == segmentNumber - 1)) startX else offsetsX[segment + 1])
                    if (segment <= targetNumber) {
                        drawQuad(
                            matrix4f,
                            vertexConsumer,
                            startY,
                            startX,
                            segment,
                            endY,
                            endX,
                            rgb.x.toFloat(),
                            rgb.y.toFloat(),
                            rgb.z.toFloat(),
                            alpha,
                            depthY,
                            depthZ,
                            false,
                            false,
                            true,
                            false,
                            maxDistancePerSegment,
                            segmentLengths[segment]
                        )
                        drawQuad(
                            matrix4f,
                            vertexConsumer,
                            startY,
                            startX,
                            segment,
                            endY,
                            endX,
                            rgb.x.toFloat(),
                            rgb.y.toFloat(),
                            rgb.z.toFloat(),
                            alpha,
                            depthY,
                            depthZ,
                            true,
                            false,
                            true,
                            true,
                            maxDistancePerSegment,
                            segmentLengths[segment]
                        )
                        drawQuad(
                            matrix4f,
                            vertexConsumer,
                            startY,
                            startX,
                            segment,
                            endY,
                            endX,
                            rgb.x.toFloat(),
                            rgb.y.toFloat(),
                            rgb.z.toFloat(),
                            alpha,
                            depthY,
                            depthZ,
                            true,
                            true,
                            false,
                            true,
                            maxDistancePerSegment,
                            segmentLengths[segment]
                        )
                        drawQuad(
                            matrix4f,
                            vertexConsumer,
                            startY,
                            startX,
                            segment,
                            endY,
                            endX,
                            rgb.x.toFloat(),
                            rgb.y.toFloat(),
                            rgb.z.toFloat(),
                            alpha,
                            depthY,
                            depthZ,
                            false,
                            true,
                            false,
                            false,
                            maxDistancePerSegment,
                            segmentLengths[segment]
                        )
                    }
                }
            }
        }
        poseStack.popPose()
    }

    companion object {
        private fun drawQuad(
            matrix4f: Matrix4f,
            vertexConsumer: VertexConsumer,
            startY: Float,
            startX: Float,
            segmentIndex: Int,
            endY: Float,
            endX: Float,
            red: Float,
            green: Float,
            blue: Float,
            alpha: Float,
            firstOffset: Float,
            secondOffset: Float,
            negativeOffset: Boolean,
            bl2: Boolean,
            bl3: Boolean,
            bl4: Boolean,
            segmentLength: Float,
            segmentLengthAdded: Float
        ) {
            vertexConsumer.vertex(
                matrix4f,
                startX + (if (bl2) secondOffset else -secondOffset),
                startY + (if (negativeOffset) secondOffset else -secondOffset),
                (segmentIndex * segmentLength)
            ).color(red, green, blue, alpha).endVertex()
            vertexConsumer.vertex(
                matrix4f,
                endX + (if (bl2) firstOffset else -firstOffset),
                endY + (if (negativeOffset) firstOffset else -firstOffset),
                (segmentIndex) * segmentLength + segmentLengthAdded
            ).color(red, green, blue, alpha).endVertex()
            vertexConsumer.vertex(
                matrix4f,
                endX + (if (bl4) firstOffset else -firstOffset),
                endY + (if (bl3) firstOffset else -firstOffset),
                ((segmentIndex) * segmentLength + segmentLengthAdded)
            ).color(red, green, blue, alpha).endVertex()
            vertexConsumer.vertex(
                matrix4f,
                startX + (if (bl4) secondOffset else -secondOffset),
                startY + (if (bl3) secondOffset else -secondOffset),
                (segmentIndex * segmentLength)
            ).color(red, green, blue, alpha).endVertex()
        }
    }
}