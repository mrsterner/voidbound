package dev.sterner.common.item.equipment.ichor

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import dev.sterner.api.item.HammerLikeItem
import dev.sterner.registry.VoidBoundTiers
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import team.lodestar.lodestone.registry.common.LodestoneAttributeRegistry

class IchoriumTerraformer(tier: Tier, attackDamageModifier: Float, attackSpeedModifier: Float,
                          properties: Properties
) : AxeItem(tier, attackDamageModifier, attackSpeedModifier, properties
), HammerLikeItem {

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
                2.0,
                AttributeModifier.Operation.ADDITION
            )
        )

        val attributes = attributeBuilder.build()

        return if (equipmentSlot == EquipmentSlot.MAINHAND) attributes else super.getDefaultAttributeModifiers(
            equipmentSlot
        )
    }

    override fun isIchor(): Boolean {
        return true
    }

    override fun getRadius(): Int {
        return 3
    }

    override fun getDepth(): Int {
        return 2
    }

    override fun getHammerTier(): Tier {
        return VoidBoundTiers.ICHORIUM
    }

    override fun getBlockTags(): TagKey<Block> {
        return BlockTags.MINEABLE_WITH_PICKAXE //Doesnt matter
    }
}