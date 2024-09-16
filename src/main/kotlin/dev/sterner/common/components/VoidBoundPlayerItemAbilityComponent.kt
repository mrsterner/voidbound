package dev.sterner.common.components

import com.sammy.malum.common.entity.boomerang.ScytheBoomerangEntity
import com.sammy.malum.registry.common.AttributeRegistry
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent
import dev.sterner.api.item.ItemAbility
import dev.sterner.api.util.VoidBoundItemUtils
import dev.sterner.api.util.VoidBoundUtils
import dev.sterner.mixin_logic.ReboundEnchantmentMixinLogic.logic
import dev.sterner.registry.VoidBoundComponentRegistry
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import team.lodestar.lodestone.registry.common.LodestoneAttributeRegistry

class VoidBoundPlayerItemAbilityComponent(private val player: Player) : AutoSyncedComponent, CommonTickingComponent {

    private var vampirismCooldown: Int = 0

    fun tryUseVampirism(target: LivingEntity){
        if (vampirismCooldown <= 0) {
            val healing = performVampirism(target)
            if (healing > 0) {
                player.heal(healing.toFloat())
                player.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE)
                vampirismCooldown = 10 * healing
                sync()
            }
        }
    }

    private fun performVampirism(target: LivingEntity): Int {
        var healing = 0
        if (target is Player) {
           healing += target.armorValue
        } else {
            val data = VoidBoundUtils.getSpiritData(target)
            if (data.isPresent) {
                val d = data.get()
                d.forEach {
                    healing += it.count
                }
            } else {
                healing += 2
            }
        }

        return healing
    }

    override fun readFromNbt(tag: CompoundTag) {
        vampirismCooldown = tag.getInt("vampirm")
    }

    override fun writeToNbt(tag: CompoundTag) {
       tag.putInt("vampirm", vampirismCooldown)
    }

    override fun tick() {
        if (vampirismCooldown > 0 && VoidBoundItemUtils.getActiveAbility(player.mainHandItem) == ItemAbility.VAMPIRISM) {
            vampirismCooldown--
            sync()
        }
    }

    private fun sync(){
        VoidBoundComponentRegistry.VOID_BOUND_PLAYER_ITEM_ABILITY_COMPONENT.sync(player)
    }

    companion object {
       fun onRightClickItem(player: ServerPlayer, interactionHand: InteractionHand?, stack: ItemStack): Boolean {
           if (VoidBoundItemUtils.getActiveAbility(stack) == ItemAbility.TRIPLE_REBOUND) {
               val level = player.level()
               if (!level.isClientSide) {

                   player.setItemInHand(interactionHand, ItemStack.EMPTY)
                   val baseDamage = player.attributes.getValue(Attributes.ATTACK_DAMAGE).toFloat()
                   val magicDamage = player.attributes.getValue(LodestoneAttributeRegistry.MAGIC_DAMAGE.get()).toFloat()
                   val slot =
                       if (interactionHand == InteractionHand.OFF_HAND) player.inventory.containerSize - 1 else player.inventory.selected
                   val entity = ScytheBoomerangEntity(
                       level,
                       player.position().x,
                       player.position().y + (player.bbHeight / 2.0f).toDouble(),
                       player.position().z
                   )
                   entity.setData(player, baseDamage, magicDamage, slot)
                   entity.item = stack
                   entity.shootFromRotation(
                       player, player.xRot, player.yRot, 0.0f,
                       (1.5 + player.getAttributeValue(AttributeRegistry.SCYTHE_PROFICIENCY.get()) * 0.125).toFloat(), 0.0f
                   )
                   level.addFreshEntity(entity)

                   logic(player, stack, baseDamage, magicDamage)
               }

               player.awardStat(Stats.ITEM_USED[stack.item])
           }

           return false
       }
    }
}