package dev.sterner.common.item.tool.ichor

import com.sammy.malum.common.item.curiosities.weapons.scythe.MagicScytheItem
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import java.awt.Color

class IchoriumScytheItem(tier: Tier?, attackDamageIn: Float, attackSpeedIn: Float, builderIn: Properties?) :
    MagicScytheItem(
        tier,
        attackDamageIn + 3 + tier!!.attackDamageBonus,
        attackSpeedIn - 1.2f,
        4f,
        builderIn
    )