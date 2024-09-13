package dev.sterner.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.sammy.malum.common.item.curiosities.armor.MalumArmorItem
import dev.sterner.registry.VoidBoundBlockRegistry
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

class IchoriumCircletRenderer : ArmorRenderer {

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
            val state = VoidBoundBlockRegistry.ICHORIUM_CIRCLET.get().defaultBlockState()
            Minecraft.getInstance().blockRenderer.renderSingleBlock(state, matrices, vertexConsumers, light, OverlayTexture.NO_OVERLAY)
        }
    }
}