package dev.sterner.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.sammy.malum.client.RenderUtils
import com.sammy.malum.client.SpiritBasedWorldVFXBuilder
import com.sammy.malum.client.renderer.entity.FloatingItemEntityRenderer
import com.sammy.malum.core.systems.spirit.MalumSpiritType
import com.sammy.malum.registry.client.MalumRenderTypeTokens
import dev.sterner.VoidBound
import dev.sterner.common.entity.ParticleEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry
import java.awt.Color

class ParticleEntityRenderer(context: EntityRendererProvider.Context) : EntityRenderer<ParticleEntity>(context) {

    override fun render(
        entity: ParticleEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int
    ) {

        val spiritType: MalumSpiritType? = entity.spiritType
        if (spiritType != null) {
            val trailBuilder =
                SpiritBasedWorldVFXBuilder.create(spiritType).setRenderType(
                    LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE_TRIANGLE.applyAndCache(MalumRenderTypeTokens.CONCENTRATED_TRAIL)
                )
            RenderUtils.renderEntityTrail(
                poseStack,
                trailBuilder,
                entity.trailPointBuilder,
                entity,
                invertColor(spiritType.primaryColor),
                invertColor(spiritType.secondaryColor),
                1.0f,
                partialTick
            )
        }

        FloatingItemEntityRenderer.renderSpiritGlimmer(
            poseStack,
            Color(200, 200, 255, 200),
            Color(100, 100, 255, 200),
            partialTick
        )
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight)
    }

    fun invertColor(color: Color): Color {
        val red = 255 - color.red
        val green = 255 - color.green
        val blue = 255 - color.blue
        return Color(red, green, blue)
    }

    override fun shouldRender(
        livingEntity: ParticleEntity,
        camera: Frustum,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        return true
    }

    override fun getTextureLocation(entity: ParticleEntity): ResourceLocation {
        return ResourceLocation(VoidBound.modid, "particles/$entity")
    }
}