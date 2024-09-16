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
    private var wrathStackKiller: Int = 0
    private val wrathStackKillerMax = 20 * 60
    private var finalStrike = false
    private val finalStrikeCooldownMax = 20 * 20
    private var finalStrikeDuration = 0
    private var wrathCounter = 0

    //VAMPIRISM start
    /**
     * If the cooldown is 0, perform Vamp, if vamp is successful, heal and play sound
     */
    fun tryUseVampirism(target: LivingEntity, amount: Float) : Float{
        if (vampirismCooldown <= 0) {
            val healing = performVampirism(target)
            if (healing > 0) {
                player.heal(healing.toFloat())
                player.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE)
                vampirismCooldown = 10 * healing
                sync()
            }
        }
        return amount
    }

    /**
     * Get amount of health to heal, healing is dependent on amount of spirits or a players armor value. Defaults at half a heart
     */
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
                healing += 1
            }
        }

        return healing
    }

    //VAMPIRISM end

    //OPENER - FINALE start

    /**
     * If final strike is not active, increase the wrath counter and start the cooldown
     */
    fun increaseWrath() {
        if (!finalStrike) {
            wrathCounter++
            wrathStackKiller = wrathStackKillerMax
        }
        sync()
    }

    /**
     * if wrath is not maxed out and target has more than 95% health, increase wrath and increase the damage
     */
    fun tryUseOpener(target: LivingEntity, amount: Float) : Float {
        var outDamage = amount
        if (wrathCounter < 10 && target.health / target.maxHealth > 0.95) {
            increaseWrath()
            outDamage *= 1.2f
        }
        return outDamage
    }

    /**
     * if final strike is active, multiply output damage with wrath and reset
     */
    fun tryUseFinale(entity: LivingEntity?, amount: Float): Float {
        var outAmount = amount
        if (finalStrike) {
            finalStrike = false
            outAmount *= (wrathCounter / 2)
            wrathCounter = 0
        }
        return outAmount
    }

    //OPENER - FINALE end

    override fun tick() {
        //Count down vampirism only when vampirism ability is selected
        if (vampirismCooldown > 0 && VoidBoundItemUtils.getActiveAbility(player.mainHandItem) == ItemAbility.VAMPIRISM) {
            vampirismCooldown--
            sync()
        }

        if (VoidBoundItemUtils.getActiveAbility(player.mainHandItem) != ItemAbility.OPENER) {
            if (finalStrike) {
                finalStrike = false
                sync()
            }
            if (wrathCounter > 0) {
                wrathCounter = 0
                sync()
            }
        }

        if (!finalStrike) {
            // Wrath logic
            if (wrathCounter in 1..9) {
                //if cooldown hits 0
                if (wrathStackKiller > 0) {
                    wrathStackKiller--
                } else {
                    // If cooldown hits 0, reset the wrath counter
                    wrathCounter = 0
                }
                sync()
            }

            // FinalStrike logic when wrathCounter reaches 10
            if (wrathCounter == 10) {
                finalStrike = true
                wrathStackKiller = 0
                finalStrikeDuration = finalStrikeCooldownMax
                sync()
            }

        } else {
            // Handle ticking down the finalStrike duration
            if (finalStrikeDuration > 0) {
                finalStrikeDuration--
            } else {
                wrathCounter = 0
                finalStrike = false
            }
            sync()
        }
    }

    override fun readFromNbt(tag: CompoundTag) {
        vampirismCooldown = tag.getInt("vampirismCooldown")
        wrathStackKiller = tag.getInt("wrathCooldown")
        wrathCounter = tag.getInt("wrathCounter")
        finalStrike = tag.getBoolean("finalStrike")
        finalStrikeDuration = tag.getInt("finalStrikeDuration")
    }

    override fun writeToNbt(tag: CompoundTag) {
        tag.putInt("vampirismCooldown", vampirismCooldown)
        tag.putInt("wrathCooldown", wrathStackKiller)
        tag.putInt("wrathCounter", wrathCounter)
        tag.putBoolean("finalStrike", finalStrike)
        tag.putInt("finalStrikeDuration", finalStrikeDuration)
    }


    private fun sync(){
        VoidBoundComponentRegistry.VOID_BOUND_PLAYER_ITEM_ABILITY_COMPONENT.sync(player)
    }

    companion object {
       fun onRightClickItem(player: ServerPlayer, interactionHand: InteractionHand, stack: ItemStack): Boolean {
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