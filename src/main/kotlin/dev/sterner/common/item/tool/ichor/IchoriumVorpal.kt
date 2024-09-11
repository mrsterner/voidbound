package dev.sterner.common.item.tool.ichor

import dev.sterner.common.item.tool.GalesEdgeItem.Companion.ascend
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.level.Level
import team.lodestar.lodestone.systems.item.tools.magic.MagicSwordItem

class IchoriumVorpal(tier: Tier, attackDamageModifier: Int, attackSpeedModifier: Float, magicDamage: Float, properties: Properties) : MagicSwordItem(tier, attackDamageModifier,
    attackSpeedModifier,
    magicDamage,
    properties
) {

    override fun getUseDuration(stack: ItemStack): Int {
        return 72000
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        player.startUsingItem(usedHand)
        return super.use(level, player, usedHand)
    }

    override fun onUseTick(level: Level, player: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
        ascend(player, stack, this.getUseDuration(stack) - remainingUseDuration)
        super.onUseTick(level, player, stack, remainingUseDuration)
    }

}