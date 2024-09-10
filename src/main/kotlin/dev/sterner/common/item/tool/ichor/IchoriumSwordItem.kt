package dev.sterner.common.item.tool.ichor

import dev.sterner.common.item.tool.GalesEdgeItem
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import java.awt.Color

class IchoriumSwordItem(
    material: Tier?,
    attackDamage: Int,
    attackSpeed: Float,
    magicDamage: Float,
    properties: Properties?
) : GalesEdgeItem(
    material, attackDamage,
    attackSpeed,
    magicDamage,
    properties
) {



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