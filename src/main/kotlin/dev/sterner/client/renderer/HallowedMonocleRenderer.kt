package dev.sterner.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.client.TrinketRenderer
import dev.sterner.VoidBound
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

class HallowedMonocleRenderer : TrinketRenderer {

    val location = VoidBound.id("textures/models/armor/hallowed_layer_1.png")

    override fun render(
        itemStack: ItemStack?,
        slotReference: SlotReference?,
        entityModel: EntityModel<out LivingEntity>?,
        poseStack: PoseStack?,
        multiBufferSource: MultiBufferSource?,
        light: Int,
        livingEntity: LivingEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {

        //ArmorRenderer.renderPart(poseStack, multiBufferSource, light, itemStack, entityModel, location)
    }
}