package dev.sterner.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.VoidBound
import dev.sterner.client.model.IchoriumCircletModel
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

class IchoriumCircletRenderer : ArmorRenderer {

    var model: IchoriumCircletModel<LivingEntity>? = null

    override fun render(
        matrices: PoseStack,
        vertexConsumers: MultiBufferSource,
        stack: ItemStack,
        entity: LivingEntity,
        slot: EquipmentSlot?,
        light: Int,
        contextModel: HumanoidModel<LivingEntity>?
    ) {
        if (slot == EquipmentSlot.HEAD) {
            matrices.pushPose()

            if (model == null) {
                model = IchoriumCircletModel(
                    Minecraft.getInstance().entityModels.bakeLayer(IchoriumCircletModel.LAYER_LOCATION)
                )
            } else {
                contextModel!!.head.translateAndRotate(matrices)
                matrices.translate(0.0, -1.7, 0.0)
                ArmorRenderer.renderPart(
                    matrices,
                    vertexConsumers,
                    light,
                    stack,
                    model,
                    VoidBound.id("textures/entity/ichorium_circlet.png")
                )
            }

            matrices.popPose()
        }
    }
}