package dev.sterner.client.renderer.entity

import dev.sterner.VoidBound
import dev.sterner.client.model.GrimcultClericModel
import dev.sterner.common.entity.GrimcultClericEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class GrimcultClericEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<GrimcultClericEntity, GrimcultClericModel>(
        context, GrimcultClericModel(context.bakeLayer(GrimcultClericModel.LAYER_LOCATION)), 0.5f
    ) {

    override fun getTextureLocation(entity: GrimcultClericEntity): ResourceLocation {
        return VoidBound.id("textures/entity/grimcult_cleric.png")
    }
}