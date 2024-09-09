package dev.sterner.common.item

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.sammy.malum.common.item.curiosities.armor.MalumArmorItem
import dev.sterner.registry.VoidBoundMaterials
import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import java.util.*

class NomadsStriderItem(properties: Properties) : MalumArmorItem(
    VoidBoundMaterials.TRAVELLER, Type.BOOTS,
    properties
) {

    override fun createExtraAttributes(type: Type?): Multimap<Attribute, AttributeModifier> {
        val attributes: Multimap<Attribute, AttributeModifier> = ArrayListMultimap.create()
        attributes.put(
            PortingLibAttributes.STEP_HEIGHT_ADDITION,
            AttributeModifier(
                UUID.fromString("745DB17C-C623-495F-8C9F-6020A9A58B5B"),
                "StepHeight",
                0.5,
                AttributeModifier.Operation.ADDITION
            )
        )
        attributes.put(
            Attributes.MOVEMENT_SPEED,
            AttributeModifier(
                UUID.fromString("645DB17C-C623-495F-8C9F-6010A9A58B5B"),
                "Speed",
                0.4,
                AttributeModifier.Operation.MULTIPLY_BASE
            )
        )
        return attributes
    }

    override fun getTexture(): String {
        return "traveller_layer_1"
    }

    override fun getTextureLocation(): String {
        return "voidbound:textures/models/armor/"
    }
}