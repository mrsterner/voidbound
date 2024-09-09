package dev.sterner.client.renderer.entity

import dev.sterner.VoidBound
import dev.sterner.client.model.GrimcultHeavyKnightModel
import dev.sterner.common.entity.GrimcultHeavyKnightEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class GrimcultHeavyKnightEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<GrimcultHeavyKnightEntity, GrimcultHeavyKnightModel>(
        context, GrimcultHeavyKnightModel(context.bakeLayer(GrimcultHeavyKnightModel.LAYER_LOCATION)), 0.5f
    ) {

    override fun getTextureLocation(entity: GrimcultHeavyKnightEntity): ResourceLocation {
        return VoidBound.id("textures/entity/grimcult_heavy_knight.png")
    }
}