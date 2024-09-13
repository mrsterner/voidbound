package dev.sterner.common.item.equipment.ichor

import com.sammy.malum.common.item.curiosities.weapons.scythe.MagicScytheItem
import net.minecraft.world.item.Tier

class IchoriumEdge(tier: Tier?, attackDamageIn: Float, attackSpeedIn: Float, builderIn: Properties?) :
    MagicScytheItem(
        tier,
        attackDamageIn + 3 + tier!!.attackDamageBonus,
        attackSpeedIn - 1.2f,
        4f,
        builderIn
    )