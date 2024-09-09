package dev.sterner.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.VoidBound
import dev.sterner.api.ClientTickHandler
import dev.sterner.api.ClientTickHandler.total
import dev.sterner.client.model.GrimcultBookModel
import dev.sterner.common.item.GrimBookItem
import dev.sterner.registry.VoidBoundItemRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.Material
import net.minecraft.util.Mth
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf
import team.lodestar.lodestone.systems.client.ClientTickCounter


object GrimcultRitesRenderer {
    private var model: GrimcultBookModel? = null
    val texture: Material = Material(InventoryMenu.BLOCK_ATLAS, VoidBound.id("item/grimcult_rites"))

    @JvmStatic
    fun renderHand(
        stack: ItemStack, type: ItemDisplayContext,
        leftHanded: Boolean, ms: PoseStack, buffers: MultiBufferSource, light: Int
    ): Boolean {
        if (!type.firstPerson() || stack.isEmpty || !stack.`is`(VoidBoundItemRegistry.GRIMCULT_RITES.get())) {
            return false
        }

        try {
            doRender(leftHanded, ms, buffers, light)
            return true
        } catch (throwable: Throwable) {
            return false
        }
    }

    private fun getModel(): GrimcultBookModel {
        if (model == null) {
            model = GrimcultBookModel(Minecraft.getInstance().entityModels.bakeLayer(GrimcultBookModel.LAYER_LOCATION))
        }
        return model as GrimcultBookModel
    }

    fun toRadians(degrees: Float): Float {
        return (degrees / 180f * Math.PI).toFloat()
    }

    private fun doRender(leftHanded: Boolean, poseStack: PoseStack, buffers: MultiBufferSource, light: Int) {
        poseStack.pushPose()

        var ticks = ClientTickHandler.ticksWithGrimcultOpen.toFloat()
        if (ticks > 0 && ticks < 10) {
            if (GrimBookItem.isOpen()) {
                ticks += ClientTickCounter.partialTicks
            } else {
                ticks -= ClientTickCounter.partialTicks
            }
        }

        if (!leftHanded) {
            poseStack.translate(0.1f, 0.125f - 0.02f * ticks - 1.4f, -0.2f - 0.025f * ticks + 0.2f)
            poseStack.mulPose(Quaternionf().rotateX(toRadians(-ticks * 1.5f)))
        } else {
            poseStack.translate(-0.1f, 0.125f - 0.02f * ticks - 1.4f, -0.2f - 0.025f * ticks + 0.2f)
            poseStack.mulPose(Quaternionf().rotateX(toRadians(-ticks * 1.5f)))
        }
        val opening = Mth.clamp(ticks / 12f, 0f, 1f)


        val leftPageAngle = Mth.frac(0 + 0.25f) * 1.6f - 0.3f
        val rightPageAngle = Mth.frac(0 + 0.75f) * 1.6f - 0.3f
        val model = getModel()
        model.setupAnim(
            total(),
            Mth.clamp(leftPageAngle, 0.0f, 1.0f),
            Mth.clamp(rightPageAngle, 0.0f, 1.0f),
            opening,
            true
        )

        val mat: Material = texture
        val buffer = mat.buffer(buffers, RenderType::entitySolid)
        model.renderToBuffer(poseStack, buffer, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f)

        poseStack.popPose()
    }
}