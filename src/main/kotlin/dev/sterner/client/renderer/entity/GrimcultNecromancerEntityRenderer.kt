package dev.sterner.client.renderer.entity

import dev.sterner.VoidBound
import dev.sterner.client.model.GrimcultNecromancerModel
import dev.sterner.common.entity.GrimcultNecromancerEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class GrimcultNecromancerEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<GrimcultNecromancerEntity, GrimcultNecromancerModel>(
        context, GrimcultNecromancerModel(context.bakeLayer(GrimcultNecromancerModel.LAYER_LOCATION)), 0.5f
    ) {

    override fun getTextureLocation(entity: GrimcultNecromancerEntity): ResourceLocation {
        return VoidBound.id("textures/entity/grimcult_necromancer.png")
    }
}