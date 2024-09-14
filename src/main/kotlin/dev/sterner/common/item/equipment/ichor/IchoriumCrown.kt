package dev.sterner.common.item.equipment.ichor

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.sammy.malum.common.item.curiosities.armor.MalumArmorItem
import com.sammy.malum.registry.common.AttributeRegistry
import dev.sterner.registry.VoidBoundMaterials
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import java.util.*

class IchoriumCrown(properties: Properties) : MalumArmorItem(
    VoidBoundMaterials.ICHORIUM, Type.HELMET,
    properties
) {

    override fun createExtraAttributes(type: Type?): Multimap<Attribute, AttributeModifier> {
        val attributes: Multimap<Attribute, AttributeModifier> = ArrayListMultimap.create()
        attributes.put(
            AttributeRegistry.SOUL_WARD_RECOVERY_RATE.get(),
            AttributeModifier(
                UUID.fromString("745DB17C-C613-495F-8C9F-6020A9A58B5B"),
                "Soul Ward Recovery Speed",
                5.0,
                AttributeModifier.Operation.ADDITION
            )
        )
        attributes.put(
            AttributeRegistry.SOUL_WARD_CAP.get(),
            AttributeModifier(
                UUID.fromString("545DB17C-C623-495F-8C9F-6010A9A58B5B"),
                "Speed",
                9.0,
                AttributeModifier.Operation.ADDITION
            )
        )
        return attributes
    }

    override fun getTexture(): String {
        return "ichorium_layer_1"
    }

    override fun getTextureLocation(): String {
        return "voidbound:textures/models/armor/"
    }
}