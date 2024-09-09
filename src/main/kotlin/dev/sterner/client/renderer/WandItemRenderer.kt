package dev.sterner.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import dev.sterner.VoidBound
import dev.sterner.client.model.FocusModel
import dev.sterner.client.model.WandItemModel
import dev.sterner.registry.VoidBoundWandFocusRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack


class WandItemRenderer(val texture: String) : DynamicItemRenderer {

    var model: WandItemModel? = null

    var focusModel: FocusModel? = null


    override fun render(
        stack: ItemStack,
        mode: ItemDisplayContext,
        poseStack: PoseStack,
        vertexConsumers: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {

        if (model == null) {
            model = WandItemModel(Minecraft.getInstance().entityModels.bakeLayer(WandItemModel.LAYER_LOCATION))
        }
        if (focusModel == null) {
            focusModel = FocusModel(Minecraft.getInstance().entityModels.bakeLayer(FocusModel.LAYER_LOCATION))
        }

        poseStack.pushPose()

        poseStack.translate(0f, 0.75f, 0f)

        poseStack.translate(0.5, 0.65, 0.5)

        poseStack.scale(1f, -1f, -1f)
        model?.renderToBuffer(
            poseStack,
            vertexConsumers.getBuffer(RenderType.entityTranslucent(VoidBound.id("textures/item/$texture.png"))),
            light,
            overlay,
            1f,
            1f,
            1f,
            1f
        )

        val focusName = stack.tag?.getString("FocusName")
        val focus = VoidBoundWandFocusRegistry.WAND_FOCUS.getOptional(focusName?.let { ResourceLocation.tryParse(it) })
        if (focus.isPresent) {

            focusModel?.renderToBuffer(
                poseStack,
                vertexConsumers.getBuffer(
                    RenderType.entityTranslucent(
                        VoidBound.id(
                            "textures/models/${
                                ResourceLocation.tryParse(
                                    focusName!!
                                )!!.path
                            }.png"
                        )
                    )
                ),
                light,
                overlay,
                1f,
                1f,
                1f,
                1f
            )
        }

        poseStack.popPose()
    }
}