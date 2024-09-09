package dev.sterner.client.renderer.entity

import dev.sterner.VoidBound
import dev.sterner.client.model.GrimcultArcherModel
import dev.sterner.common.entity.GrimcultArcherEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer
import net.minecraft.resources.ResourceLocation

class GrimcultArcherEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<GrimcultArcherEntity, GrimcultArcherModel>(
        context, GrimcultArcherModel(context.bakeLayer(GrimcultArcherModel.LAYER_LOCATION)), 0.5f
    ) {

    init {
        this.addLayer(ItemInHandLayer(this, context.itemInHandRenderer))
    }

    override fun getTextureLocation(entity: GrimcultArcherEntity): ResourceLocation {
        return VoidBound.id("textures/entity/grimcult_archer.png")
    }
}