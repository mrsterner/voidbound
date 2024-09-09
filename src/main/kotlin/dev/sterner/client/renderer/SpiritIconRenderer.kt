package dev.sterner.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.api.VoidBoundApi
import dev.sterner.api.util.VoidBoundRenderUtils
import dev.sterner.api.util.VoidBoundUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import org.joml.Quaternionf
import kotlin.math.sqrt


object SpiritIconRenderer {

    @JvmStatic
    fun render(entity: Entity, poseStack: PoseStack, buffers: MultiBufferSource, camera: Quaternionf) {
        val minecraft = Minecraft.getInstance()

        if (minecraft.player == null) {
            return
        }

        if (entity !is LivingEntity) {
            return
        }

        if (!VoidBoundApi.hasGoggles()) {
            return
        }

        val squareDistance: Double = minecraft.player!!.distanceToSqr(entity)
        val maxDistance = 12.0
        if (squareDistance > maxDistance * maxDistance) {
            return
        }

        val startFade: Double = ((1.0 - (25.0 / 100.0)) * maxDistance)
        val currentAlpha =
            Mth.clamp(1.0 - ((sqrt(squareDistance) - startFade) / (maxDistance - startFade)), 0.0, 0.85).toFloat()

        val entityHeight = entity.nameTagOffsetY

        VoidBoundRenderUtils.renderWobblyOrientedWorldIcon(
            poseStack,
            buffers,
            camera,
            entityHeight,
            currentAlpha,
            VoidBoundUtils.getSpiritData(entity)
        )
    }
}