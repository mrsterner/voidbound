package dev.sterner.client

import dev.sterner.VoidBound
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier.AfterBake
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * This is only necessary because malum doesn't allow additions, could make that one better and impl that instead.
 * Used for the Ichorium Scythe
 */
object VoidBoundModelLoaderPlugin : ModelLoadingPlugin {


    override fun onInitializeModelLoader(pluginContext: ModelLoadingPlugin.Context?) {
        modelPairs.values.forEach(Consumer { ids: ModelResourceLocation? ->
            pluginContext!!.addModels(
                ids
            )
        })

        pluginContext!!.modifyModelAfterBake()
            .register(AfterBake register@{ original: BakedModel?, context: AfterBake.Context ->
                val guiModelLocation = modelPairs[context.id()]
                if (guiModelLocation != null) {
                    val guiModel = context.baker().bake(guiModelLocation, context.settings())
                    return@register VoidBoundBakedModel(original, guiModel!!)
                }
                original
            })
    }

    class VoidBoundBakedModel(heldModel: BakedModel?, guiModel: BakedModel) : ForwardingBakedModel() {
        private val guiModel: BakedModel

        init {
            this.wrapped = heldModel
            this.guiModel = guiModel
        }

        override fun emitItemQuads(stack: ItemStack, randomSupplier: Supplier<RandomSource>, context: RenderContext) {
            if (ITEM_GUI_CONTEXTS.contains(context.itemTransformationMode())) {
                guiModel.emitItemQuads(stack, randomSupplier, context)
                return
            }
            super.emitItemQuads(stack, randomSupplier, context)
        }

        override fun isVanillaAdapter(): Boolean {
            return false
        }

        companion object {
            private val ITEM_GUI_CONTEXTS: Set<ItemDisplayContext> =
                EnumSet.of(ItemDisplayContext.GUI, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED)
        }
    }

    fun createModel(baseName: String): ModelResourceLocation {
        return ModelResourceLocation(VoidBound.id(baseName), "inventory")
    }

    fun createGuiModel(baseName: String): ModelResourceLocation {
        return ModelResourceLocation(VoidBound.id(baseName + "_gui"), "inventory")
    }

    val ICHOR_BASE_MODEL: ModelResourceLocation = createModel("ichorium_scythe")
    val ICHOR_GUI_MODEL: ModelResourceLocation = createGuiModel("ichorium_scythe")

    val modelPairs: Map<ModelResourceLocation, ModelResourceLocation> = java.util.Map.of(
        ICHOR_BASE_MODEL, ICHOR_GUI_MODEL
    )
}

