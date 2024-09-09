package dev.sterner.client.renderer.entity

import dev.sterner.VoidBound
import dev.sterner.client.model.GrimcultJesterModel
import dev.sterner.common.entity.GrimcultJesterEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class GrimcultJesterEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<GrimcultJesterEntity, GrimcultJesterModel>(
        context, GrimcultJesterModel(context.bakeLayer(GrimcultJesterModel.LAYER_LOCATION)), 0.5f
    ) {

    override fun getTextureLocation(entity: GrimcultJesterEntity): ResourceLocation {
        return VoidBound.id("textures/entity/grimcult_jester.png")
    }
}