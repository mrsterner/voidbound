package dev.sterner.client.feature

import com.google.common.collect.ImmutableList
import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.VoidBound
import dev.sterner.client.model.SoulSteelGolemEntityModel
import dev.sterner.client.renderer.entity.SoulSteelGolemEntityRenderer
import dev.sterner.common.entity.SoulSteelGolemEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.resources.ResourceLocation

class GolemCrackRenderLayer(
    ctx: EntityRendererProvider.Context,
    soulSteelGolemEntityRenderer: SoulSteelGolemEntityRenderer
) : RenderLayer<SoulSteelGolemEntity, SoulSteelGolemEntityModel>(
    soulSteelGolemEntityRenderer
) {

    private val resourceLocations: List<ResourceLocation> = ImmutableList.of(
        VoidBound.id("textures/entity/soul_steel_golem_crack_low.png"),
        VoidBound.id("textures/entity/soul_steel_golem_crack_medium.png"),
        VoidBound.id("textures/entity/soul_steel_golem_crack_high.png")
    )

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
        if (!livingEntity.isInvisible) {
            val healthPercentage = livingEntity.health / livingEntity.maxHealth * 100
            val texture = when {
                healthPercentage <= 30 -> resourceLocations[2]
                healthPercentage <= 60 -> resourceLocations[1]
                healthPercentage <= 90 -> resourceLocations[0]
                else -> null
            }

            if (texture != null) {

                val vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(texture))
                this.parentModel.renderToBuffer(
                    poseStack,
                    vertexConsumer,
                    packedLight,
                    LivingEntityRenderer.getOverlayCoords(livingEntity, 0.0f),
                    1.0f,
                    1.0f,
                    1.0f,
                    0.5f
                )
            }
        }
    }
}