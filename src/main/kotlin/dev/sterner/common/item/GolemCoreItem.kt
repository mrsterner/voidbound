package dev.sterner.common.item

import dev.sterner.api.entity.GolemCore
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import team.lodestar.lodestone.helpers.ColorHelper
import java.awt.Color

class GolemCoreItem(val core: GolemCore, properties: Properties) : Item(properties.rarity(Rarity.UNCOMMON)) {

    private val color = Color(-19164)
    private val txtColor: TextColor = TextColor.fromRgb(ColorHelper.darker(color, 1, 0.75f).rgb)

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        val coreName: String = core.name.lowercase()
        tooltipComponents.add(
            Component.translatable("tooltip.voidbound.$coreName").withStyle(ChatFormatting.ITALIC).withStyle(
                Style.EMPTY.withColor(txtColor)
            )
        )
        if (!core.implemented) {
            tooltipComponents.add(
                Component.translatable("Not yet implemented").withStyle(ChatFormatting.ITALIC).withStyle(
                    Style.EMPTY.withColor(Color.red.rgb)
                )
            )
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced)
    }
}