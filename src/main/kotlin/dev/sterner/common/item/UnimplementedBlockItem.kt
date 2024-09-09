package dev.sterner.common.item

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import java.awt.Color

/**
 * Used by any blockItem which is not fully implemented to notify the player in the tooltip
 */
class UnimplementedBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        tooltipComponents.add(
            Component.translatable("Not yet implemented").withStyle(ChatFormatting.ITALIC).withStyle(
                Style.EMPTY.withColor(Color.red.rgb)
            )
        )

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced)
    }
}