package dev.sterner.common.components

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent
import dev.sterner.api.item.ItemAbility
import dev.sterner.api.revelation.KnowledgeData
import dev.sterner.api.revelation.KnowledgeType
import dev.sterner.api.revelation.ThoughtData
import dev.sterner.api.util.VoidBoundPlayerUtils
import dev.sterner.registry.VoidBoundComponentRegistry
import dev.sterner.registry.VoidBoundSoundEvents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

class VoidBoundRevelationComponent(private val player: Player) : AutoSyncedComponent, CommonTickingComponent {

    var thoughtsQueue: MutableMap<Component, ThoughtData> = mutableMapOf()

    var unlockedItemAbilities = defaultAbilities()

    private fun defaultAbilities(): MutableSet<ItemAbility> {
        return mutableSetOf(
            ItemAbility.NONE,
            ItemAbility.EXCAVATOR,
            ItemAbility.HARVEST,
            ItemAbility.SCORCHING_HEAT,
            ItemAbility.SPIRIT_VISION
        )
    }

    var knowledgeDataSet: MutableSet<KnowledgeData> = mutableSetOf()

    fun unlockKnowledge(knowledgeType: KnowledgeType, rejectionText: String = "voidbound.prerequisite.missing"): Boolean {
        val requiredKnowledge = KnowledgeType.prerequisites[knowledgeType]
        val unlockedKnowledge = knowledgeDataSet.map { it.knowledgeType }.toSet()

        if (requiredKnowledge != null && !unlockedKnowledge.containsAll(requiredKnowledge)) {
            handleThought(knowledgeType, rejectionText)
            return false // Prerequisites not met
        }

        // If already unlocked, skip
        if (unlockedKnowledge.contains(knowledgeType)) {
            return true
        }

        // Unlock knowledge
        knowledgeDataSet.add(KnowledgeData(knowledgeType))
        sync()
        return true
    }

    // Checks if a specific knowledge type is unlocked
    fun hasKnowledge(knowledgeType: KnowledgeType): Boolean {
        return knowledgeDataSet.any { it.knowledgeType == knowledgeType }
    }

    // Unlocks all knowledge without checking prerequisites
    fun unlockAllKnowledge() {
        KnowledgeType.values().forEach { knowledgeType ->
            if (!hasKnowledge(knowledgeType)) {
                knowledgeDataSet.add(KnowledgeData(knowledgeType))
            }
        }
        sync()
    }

    private fun handleThought(knowledgeType: KnowledgeType, thought: String) {
        val knowledgeData = knowledgeDataSet.find { it.knowledgeType == knowledgeType }
        if (knowledgeData == null || !knowledgeData.thoughtSent) {
            // Add thought and mark it as sent
            VoidBoundPlayerUtils.addThought(player, Component.translatable(thought))
            knowledgeDataSet.remove(knowledgeData) // Remove old one if it exists
            knowledgeDataSet.add(KnowledgeData(knowledgeType, thoughtSent = true)) // Add updated one
            sync()
        }
    }

    fun hasThoughtSentForKnowledge(knowledgeType: KnowledgeType): Boolean {
        return knowledgeDataSet.find { it.knowledgeType == knowledgeType }?.thoughtSent == true
    }

    fun addUnlockedItemAbility(ability: ItemAbility) {
        unlockedItemAbilities.add(ability)
        sync()
    }

    fun isTearKnowledgeComplete(): Boolean {
        return hasKnowledge(KnowledgeType.GRIMCULT) && hasKnowledge(KnowledgeType.END) && hasKnowledge(KnowledgeType.NETHER)
    }

    fun addThought(thought: Component, durationTicks: Int, delayTicks: Int) {
        val data = ThoughtData(durationTicks, delayTicks)
        thoughtsQueue[thought] = data
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


    private fun sync() {
        VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.sync(player)
    }

    override fun readFromNbt(tag: CompoundTag) {
        thoughtsQueue.clear() // Clear the existing queue
        thoughtsQueue = ThoughtData.readFromNbt(tag)

        knowledgeDataSet.clear()
        knowledgeDataSet = KnowledgeData.readFromNbt(tag)

        unlockedItemAbilities.clear()
        defaultAbilities().forEach {
            unlockedItemAbilities.add(it)
        }
        unlockedItemAbilities = ItemAbility.readNbt(tag)
    }

    override fun writeToNbt(tag: CompoundTag) {
        ThoughtData.writeToNbt(thoughtsQueue, tag)
        ItemAbility.writeToNbt(unlockedItemAbilities, tag)
        KnowledgeData.writeToNbt(knowledgeDataSet, tag)
    }
}