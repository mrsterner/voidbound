package dev.sterner.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.VoidBound
import dev.sterner.client.model.HallowedGogglesModel
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack


class HallowedGogglesRenderer : ArmorRenderer {

    companion object {
        private var model: HallowedGogglesModel<LivingEntity>? = null
    }

    override fun render(
        poseStack: PoseStack?,
        vertexConsumers: MultiBufferSource?,
        stack: ItemStack?,
        entity: LivingEntity?,
        slot: EquipmentSlot?,
        light: Int,
        contextModel: HumanoidModel<LivingEntity>
    ) {
        if (model == null) {
            model = HallowedGogglesModel(
                Minecraft.getInstance().entityModels.bakeLayer(HallowedGogglesModel.LAYER_LOCATION)
            )
        }
        if (slot == EquipmentSlot.HEAD) {
            contextModel.copyPropertiesTo(model!!)
            model!!.setAllVisible(true)
            ArmorRenderer.renderPart(
                poseStack,
                vertexConsumers,
                light,
                stack,
                model,
                VoidBound.id("textures/entity/armor/hallowed_goggles.png")
            )
        }
    }
}