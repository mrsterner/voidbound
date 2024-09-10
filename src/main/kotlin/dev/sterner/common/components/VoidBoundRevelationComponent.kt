package dev.sterner.common.components

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent
import dev.sterner.api.VoidBoundApi
import dev.sterner.api.item.ItemAbility
import dev.sterner.registry.VoidBoundComponentRegistry
import dev.sterner.registry.VoidBoundSoundEvents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

class VoidBoundRevelationComponent(private val player: Player) : AutoSyncedComponent, CommonTickingComponent {

    data class ThoughtData(var duration: Int, var delay: Int)

    var thoughtsQueue: MutableMap<Component, ThoughtData> = mutableMapOf()

    var unlockedItemAbilities = mutableSetOf<ItemAbility>()

    fun addUnlockedItemAbility(ability: ItemAbility) {
        unlockedItemAbilities.add(ability)
        sync()
    }

    override fun tick() {
        // Ensure the queue does not exceed 16 elements
        while (thoughtsQueue.size > 16) {
            val firstEntry = thoughtsQueue.entries.firstOrNull()
            if (firstEntry != null) {
                thoughtsQueue.remove(firstEntry.key)
            }
        }

        val iterator = thoughtsQueue.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val thoughtData = entry.value

            // Handle delay countdown
            if (thoughtData.delay > 0) {
                thoughtData.delay -= 1
                if (thoughtData.delay <= 0) {
                    player.playSound(VoidBoundSoundEvents.SOUL_SPEAK.get(), 1.0f, 1.0f)
                }
            } else {
                // Handle duration countdown
                val remainingTime = thoughtData.duration - 1
                if (remainingTime <= 0) {
                    iterator.remove() // Remove the thought if the time is up
                } else {
                    thoughtData.duration = remainingTime
                }
            }
        }
    }

    var hasWellKnowledge: Boolean = false
        set(value) {
            field = value
            sync()
        }

    var hasEndKnowledge: Boolean = false
        set(value) {
            field = value
            sync()
        }

    var hasNetherKnowledge: Boolean = false
        set(value) {
            field = value
            sync()
        }

    var hasGrimcultKnowledge: Boolean = false
        set(value) {
            field = value
            sync()
        }

    var hasIchorKnowledge: Boolean = false
        set(value) {
            field = value
            sync()
        }

    var hasReceivedNetherMessage: Boolean = false
        set(value) {
            field = value
            sync()
        }

    var hasReceivedPreWellNetherMessage: Boolean = false
        set(value) {
            field = value

            sync()
        }

    var hasReceivedEndMessage: Boolean = false
        set(value) {
            field = value
            sync()
        }

    var hasReceivedPreWellEndMessage: Boolean = false
        set(value) {
            field = value
            sync()
        }

    fun isTearKnowledgeComplete(): Boolean {
        return hasWellKnowledge && hasEndKnowledge && hasNetherKnowledge
    }

    fun addThought(thought: Component, durationTicks: Int, delayTicks: Int) {
        val data = ThoughtData(durationTicks, delayTicks)
        thoughtsQueue[thought] = data
        sync()
    }

    fun sync(){
        VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.sync(player)
    }

    override fun readFromNbt(tag: CompoundTag) {
        hasWellKnowledge = tag.getBoolean("hasWellKnowledge")
        hasEndKnowledge = tag.getBoolean("hasEndKnowledge")
        hasNetherKnowledge = tag.getBoolean("hasNetherKnowledge")
        hasGrimcultKnowledge = tag.getBoolean("hasGrimcultKnowledge")
        hasIchorKnowledge = tag.getBoolean("hasIchorKnowledge")

        hasReceivedNetherMessage = tag.getBoolean("hasReceivedNetherMessage")
        hasReceivedEndMessage = tag.getBoolean("hasReceivedEndMessage")

        hasReceivedPreWellNetherMessage = tag.getBoolean("hasReceivedPreWellNetherMessage")
        hasReceivedPreWellEndMessage = tag.getBoolean("hasReceivedPreWellEndMessage")

        thoughtsQueue.clear() // Clear the existing queue
        val thoughtsList = tag.getList("ThoughtsQueue", 10) // 10 is the ID for CompoundTag
        for (i in 0 until thoughtsList.size) {
            val thoughtTag = thoughtsList.getCompound(i)
            val thought = Component.Serializer.fromJson(thoughtTag.getString("Text"))
            val duration = thoughtTag.getInt("Duration")
            val delay = thoughtTag.getInt("Delay")
            if (thought != null) {
                thoughtsQueue[thought] = ThoughtData(duration, delay)
            }
        }

        unlockedItemAbilities.clear()
        val unlockedList = tag.getList("UnlockedItems", 10)
        for (i in 0 until unlockedList.size) {
            val item = unlockedList.getCompound(i)
            val itemAbility = ItemAbility.readNbt(item)
            unlockedItemAbilities.add(itemAbility)
        }
    }

    override fun writeToNbt(tag: CompoundTag) {
        tag.putBoolean("hasWellKnowledge", hasWellKnowledge)
        tag.putBoolean("hasEndKnowledge", hasEndKnowledge)
        tag.putBoolean("hasNetherKnowledge", hasNetherKnowledge)
        tag.putBoolean("hasGrimcultKnowledge", hasGrimcultKnowledge)
        tag.putBoolean("hasIchorKnowledge", hasIchorKnowledge)

        tag.putBoolean("hasReceivedNetherMessage", hasReceivedNetherMessage)
        tag.putBoolean("hasReceivedEndMessage", hasReceivedEndMessage)

        tag.putBoolean("hasReceivedPreWellNetherMessage", hasReceivedPreWellNetherMessage)
        tag.putBoolean("hasReceivedPreWellEndMessage", hasReceivedPreWellEndMessage)

        val thoughtsList = ListTag()
        thoughtsQueue.forEach { (thought, data) ->
            val thoughtTag = CompoundTag()
            thoughtTag.putString("Text", Component.Serializer.toJson(thought))
            thoughtTag.putInt("Duration", data.duration)
            thoughtTag.putInt("Delay", data.delay)
            thoughtsList.add(thoughtTag)
        }
        tag.put("ThoughtsQueue", thoughtsList)

        val unlockedList = ListTag()
        unlockedItemAbilities.forEach { unlockedTag ->
            val abilityTag = unlockedTag.writeNbt()
            unlockedList.add(abilityTag)
        }
        tag.put("UnlockedItems", unlockedList)
    }
}