package dev.sterner.common.components

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent
import dev.sterner.api.item.ItemAbility
import dev.sterner.api.util.VoidBoundItemUtils
import dev.sterner.api.util.VoidBoundPlayerUtils
import dev.sterner.api.util.VoidBoundUtils
import dev.sterner.registry.VoidBoundComponentRegistry
import net.minecraft.nbt.CompoundTag
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

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
}