package dev.sterner.common.entity.ai.goal

import dev.sterner.common.entity.GrimcultHeavyKnightEntity
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ProjectileWeaponItem


class RaiseShieldGoal(private val cultist: GrimcultHeavyKnightEntity) : Goal() {

    override fun canContinueToUse(): Boolean {
        return this.canUse()
    }

    override fun canUse(): Boolean {
        return raiseShield() && cultist.shieldCoolDown <= 0
    }

    override fun start() {
        cultist.startUsingItem(InteractionHand.OFF_HAND)
        cultist.setShieldBlocking(true)
    }

    override fun stop() {
        cultist.stopUsingItem()
        cultist.setShieldBlocking(false)
        cultist.shieldCoolDown = 20
    }

    private fun raiseShield(): Boolean {
        val target: LivingEntity? = cultist.target
        if (target != null && cultist.shieldCoolDown == 0) {
            if (target is Player) {
                if (target.mainHandItem.item is ProjectileWeaponItem) {
                    return true
                }
            }
        }
        return false
    }
}