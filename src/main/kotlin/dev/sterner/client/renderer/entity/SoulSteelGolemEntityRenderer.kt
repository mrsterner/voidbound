package dev.sterner.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.VoidBound
import dev.sterner.client.feature.GolemCoreRenderLayer
import dev.sterner.client.feature.GolemCrackRenderLayer
import dev.sterner.client.model.SoulSteelGolemEntityModel
import dev.sterner.common.entity.SoulSteelGolemEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer
import net.minecraft.resources.ResourceLocation
import kotlin.math.abs

class SoulSteelGolemEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<SoulSteelGolemEntity, SoulSteelGolemEntityModel>(
        context, SoulSteelGolemEntityModel(context.bakeLayer(SoulSteelGolemEntityModel.LAYER_LOCATION)), 0.2f
    ) {

    init {
        addLayer(GolemCoreRenderLayer(context, this))
        addLayer(ItemInHandLayer(this, context.itemInHandRenderer))
        addLayer(GolemCrackRenderLayer(context, this))
    }

    override fun setupRotations(
        entityLiving: SoulSteelGolemEntity,
        poseStack: PoseStack,
        ageInTicks: Float,
        rotationYaw: Float,
        partialTicks: Float
    ) {
        super.setupRotations(entityLiving, poseStack, ageInTicks, rotationYaw, partialTicks)
        if (!(entityLiving.walkAnimation.speed().toDouble() < 0.01)) {
            val f = 13.0f
            val g = entityLiving.walkAnimation.position(partialTicks) + 6.0f
            val h = ((abs((g % f - 6.5f).toDouble()) - 3.25f) / 3.25f).toFloat()
            poseStack.mulPose(Axis.ZP.rotationDegrees(6.5f * h))
        }
    }

    override fun getTextureLocation(entity: SoulSteelGolemEntity): ResourceLocation {
        return VoidBound.id("textures/entity/soul_steel_golem.png")
    }
}