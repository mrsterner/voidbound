package dev.sterner.common.item.tool.ichor

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.level.block.state.BlockState
import team.lodestar.lodestone.registry.common.LodestoneAttributeRegistry

class IchoriumTerraformer(tier: Tier, attackDamageModifier: Float, attackSpeedModifier: Float,
                          properties: Properties
) : AxeItem(tier, attackDamageModifier, attackSpeedModifier, properties
) {

    override fun getDestroySpeed(stack: ItemStack, state: BlockState): Float {
        return this.speed
    }

    override fun isCorrectToolForDrops(block: BlockState): Boolean {
        return true
    }

    override fun getDefaultAttributeModifiers(equipmentSlot: EquipmentSlot): Multimap<Attribute, AttributeModifier> {

        val attributeBuilder = ImmutableMultimap.Builder<Attribute, AttributeModifier>()
        attributeBuilder.putAll(defaultModifiers)
        attributeBuilder.put(
            LodestoneAttributeRegistry.MAGIC_DAMAGE.get(),
            AttributeModifier(
                LodestoneAttributeRegistry.UUIDS[LodestoneAttributeRegistry.MAGIC_DAMAGE],
                "Weapon magic damage",
                5.0,
                AttributeModifier.Operation.ADDITION
            )
        )

        val attributes = attributeBuilder.build()

        return if (equipmentSlot == EquipmentSlot.MAINHAND) attributes else super.getDefaultAttributeModifiers(
            equipmentSlot
        )
    }
}