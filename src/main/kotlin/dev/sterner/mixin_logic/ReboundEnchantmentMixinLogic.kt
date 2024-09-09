package dev.sterner.mixin_logic

import com.sammy.malum.common.entity.boomerang.ScytheBoomerangEntity
import com.sammy.malum.registry.common.AttributeRegistry
import dev.sterner.api.entity.IchoriumScytheGhost
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

object ReboundEnchantmentMixinLogic {

    fun logic(player: ServerPlayer, stack: ItemStack, baseDamage: Float, magicDamage: Float, yOffset: Int) {
        val entity = ScytheBoomerangEntity(
            player.level(),
            player.position().x,
            player.position().y + player.bbHeight / 2f,
            player.position().z
        )
        entity.setData(player, baseDamage, magicDamage, 0)
        entity.item = stack

        @Suppress("KotlinConstantConditions")
        (entity as IchoriumScytheGhost).setGhost(true)

        entity.shootFromRotation(
            player, player.xRot, player.yRot + yOffset, 0.0f,
            (1.5f + player.getAttributeValue(AttributeRegistry.SCYTHE_PROFICIENCY.get()) * 0.125f).toFloat(), 0f
        )
        player.level().addFreshEntity(entity)
    }
}