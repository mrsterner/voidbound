package dev.sterner.listener

import dev.sterner.VoidBound.id
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resources.ResourceLocation

class EnchantSpiritDataReloadListenerFabricImpl : EnchantSpiritDataReloadListener(),
    IdentifiableResourceReloadListener {
    override fun getFabricId(): ResourceLocation {
        return id("enchanting_data")
    }
}