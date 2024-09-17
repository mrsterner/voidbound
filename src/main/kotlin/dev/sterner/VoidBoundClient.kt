package dev.sterner

import com.sammy.malum.MalumModClient
import dev.emi.trinkets.api.client.TrinketRendererRegistry
import dev.sterner.VoidBound.id
import dev.sterner.client.VoidBoundModelLoaderPlugin
import dev.sterner.client.renderer.HallowedMonocleRenderer
import dev.sterner.client.renderer.IchoriumCircletRenderer
import dev.sterner.client.screen.OsmoticEnchanterScreen
import dev.sterner.common.ItemAbilityHandler
import dev.sterner.registry.*
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.item.ItemProperties


object VoidBoundClient : ClientModInitializer {

    val ITEM_ABILITY_HANDLER: ItemAbilityHandler = ItemAbilityHandler()

    override fun onInitializeClient() {
        MalumModClient()
        VoidBoundShaders.init()
        VoidBoundParticleTypeRegistry.init()
        VoidBoundKeyBindings.init()
        VoidBoundEntityRenderers.init()
        VoidBoundModelLayers.init()
        VoidBoundEvents.clientInit()

        TrinketRendererRegistry.registerRenderer(
            VoidBoundItemRegistry.HALLOWED_MONOCLE.get(),
            HallowedMonocleRenderer()
        )

        ItemProperties.register(
            VoidBoundItemRegistry.CALL_OF_THE_VOID.get(),
            id("glowing")
        ) { itemStack, _, _, _ ->
            val v =
                if (itemStack.tag != null && itemStack.tag!!.contains("Glowing") && itemStack.tag!!.getBoolean("Glowing")) 1f else 0f
            return@register v
        }

        BlockRenderLayerMap.INSTANCE.putBlocks(
            RenderType.cutout(),
            VoidBoundBlockRegistry.TEAR_OF_ENDER.get(),
            VoidBoundBlockRegistry.TEAR_OF_BRIMSTONE.get(),
            VoidBoundBlockRegistry.OSMOTIC_ENCHANTER.get()
        )

        ArmorRenderer.register(IchoriumCircletRenderer(), VoidBoundItemRegistry.ICHORIUM_CIRCLET.get())

        ModelLoadingPlugin.register(VoidBoundModelLoaderPlugin)

        MenuScreens.register(VoidBoundMenuTypeRegistry.OSMOTIC_ENCHANTER.get(), ::OsmoticEnchanterScreen)
    }


}