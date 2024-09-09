package dev.sterner

import com.sammy.malum.MalumMod
import com.sammy.malum.MalumModClient
import dev.emi.trinkets.api.client.TrinketRendererRegistry
import dev.sterner.client.VoidBoundModelLoaderPlugin
import dev.sterner.client.renderer.HallowedMonocleRenderer
import dev.sterner.client.renderer.WandItemRenderer
import dev.sterner.client.screen.OsmoticEnchanterScreen
import dev.sterner.listener.EnchantSpiritDataReloadListenerFabricImpl
import dev.sterner.registry.*
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import org.slf4j.LoggerFactory
import team.lodestar.lodestone.LodestoneLib


object VoidBound : ModInitializer, ClientModInitializer {

    const val modid: String = "voidbound"
    private val logger = LoggerFactory.getLogger(modid)

    override fun onInitialize() {
        LodestoneLib()
        MalumMod()

        VoidBoundTags.init()
        VoidBoundPacketRegistry.registerVoidBoundPackets()

        VoidBoundItemRegistry.ITEMS.register()
        VoidBoundBlockRegistry.BLOCKS.register()
        VoidBoundBlockEntityTypeRegistry.BLOCK_ENTITY_TYPES.register()
        VoidBoundEntityTypeRegistry.ENTITY_TYPES.register()
        VoidBoundParticleTypeRegistry.PARTICLES.register()
        VoidBoundMemoryTypeRegistry.MEMORY_TYPES.register()
        VoidBoundSensorTypeRegistry.SENSOR_TYPES.register()
        VoidBoundWandFocusRegistry.WAND_FOCI.register()
        VoidBoundRiftTypeRegistry.RIFT_TYPES.register()
        VoidBoundMenuTypeRegistry.MENU_TYPES.register()
        VoidBoundStructureRegistry.STRUCTURES.register()
        VoidBoundSoundEvents.SOUNDS.register()

        VoidBoundCreativeTabRegistry.init()
        VoidBoundEvents.init()
        VoidBoundWorldGenerations.init()

        ResourceManagerHelper.get(PackType.SERVER_DATA)
            .registerReloadListener(EnchantSpiritDataReloadListenerFabricImpl())
    }

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

        BuiltinItemRendererRegistry.INSTANCE.register(
            VoidBoundItemRegistry.HALLOWED_GOLD_CAPPED_RUNEWOOD_WAND.get(),
            WandItemRenderer("hallowed_gold_capped_runewood_wand")
        )
        BuiltinItemRendererRegistry.INSTANCE.register(
            VoidBoundItemRegistry.SOUL_STAINED_STEEL_CAPPED_SOULWOOD_WAND.get(),
            WandItemRenderer("soul_stained_steel_capped_soulwood_wand")
        )

        BlockRenderLayerMap.INSTANCE.putBlocks(
            RenderType.cutout(),
            VoidBoundBlockRegistry.TEAR_OF_ENDER.get(),
            VoidBoundBlockRegistry.TEAR_OF_BRIMSTONE.get(),
            VoidBoundBlockRegistry.OSMOTIC_ENCHANTER.get()
        )

        ModelLoadingPlugin.register(VoidBoundModelLoaderPlugin)

        MenuScreens.register(VoidBoundMenuTypeRegistry.OSMOTIC_ENCHANTER.get(), ::OsmoticEnchanterScreen)
    }

    fun id(name: String): ResourceLocation {
        return ResourceLocation(modid, name)
    }
}