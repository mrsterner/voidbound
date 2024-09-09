package dev.sterner.client.renderer.entity

import dev.sterner.VoidBound
import dev.sterner.client.model.GrimcultKnightModel
import dev.sterner.common.entity.GrimcultKnightEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer
import net.minecraft.resources.ResourceLocation

class GrimcultKnightEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<GrimcultKnightEntity, GrimcultKnightModel>(
        context, GrimcultKnightModel(context.bakeLayer(GrimcultKnightModel.LAYER_LOCATION)), 0.5f
    ) {

    init {
        this.addLayer(ItemInHandLayer(this, context.itemInHandRenderer))
    }

    override fun getTextureLocation(entity: GrimcultKnightEntity): ResourceLocation {
        return VoidBound.id("textures/entity/grimcult_knight.png")
    }
}