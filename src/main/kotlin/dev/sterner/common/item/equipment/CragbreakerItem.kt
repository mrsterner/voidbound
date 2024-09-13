package dev.sterner.common.item.equipment

import dev.sterner.api.item.HammerLikeItem
import dev.sterner.api.util.VoidBoundUtils
import net.minecraft.network.chat.Component
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import team.lodestar.lodestone.systems.item.tools.magic.MagicPickaxeItem


open class CragbreakerItem(
    val material: Tier,
    damage: Int,
    speed: Float,
    magicDamage: Float,
    properties: Properties?
) :
    MagicPickaxeItem(
        material, damage, speed,
        magicDamage,
        properties
    ), HammerLikeItem, UpgradableTool {

    override fun getHammerTier(): Tier {
        return material
    }

    override fun getDestroySpeed(stack: ItemStack, state: BlockState): Float {
        return super.getDestroySpeed(stack, state) + getExtraMiningSpeed(stack)
    }

    override fun isIchor(): Boolean {
        return false
    }

    override fun getRadius(): Int {
        return 3
    }

    override fun getDepth(): Int {
        return 1
    }

    override fun getBlockTags(): TagKey<Block> {
        return BlockTags.MINEABLE_WITH_PICKAXE
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        VoidBoundUtils.addNetheritedTooltip(stack, tooltipComponents)
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced)
    }
}