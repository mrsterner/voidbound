package dev.sterner

import com.sammy.malum.MalumMod
import dev.sterner.common.VoidBoundLootModifier
import dev.sterner.listener.EnchantSpiritDataReloadListenerFabricImpl
import dev.sterner.registry.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import org.slf4j.LoggerFactory
import team.lodestar.lodestone.LodestoneLib


object VoidBound : ModInitializer {

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
        VoidBoundRiftTypeRegistry.RIFT_TYPES.register()
        VoidBoundMenuTypeRegistry.MENU_TYPES.register()
        VoidBoundStructureRegistry.STRUCTURES.register()
        VoidBoundSoundEvents.SOUNDS.register()
        VoidBoundLootModifier.MODIFIERS.register()

        VoidBoundCreativeTabRegistry.init()
        VoidBoundEvents.init()
        VoidBoundWorldGenerations.init()

        ResourceManagerHelper.get(PackType.SERVER_DATA)
            .registerReloadListener(EnchantSpiritDataReloadListenerFabricImpl())
    }

    fun id(name: String): ResourceLocation {
        return ResourceLocation(modid, name)
    }
}