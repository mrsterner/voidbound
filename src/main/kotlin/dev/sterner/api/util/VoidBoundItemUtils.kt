@file:Suppress("NAME_SHADOWING")

package dev.sterner.api.util

import dev.sterner.api.item.ItemAbility
import dev.sterner.common.entity.SoulSteelGolemEntity
import net.minecraft.world.Container
import net.minecraft.world.entity.ai.behavior.BehaviorUtils
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import kotlin.math.min

object VoidBoundItemUtils {

    private fun throwItemsTowardPos(golem: SoulSteelGolemEntity, stacks: List<ItemStack>, pos: Vec3) {
        if (stacks.isNotEmpty()) {
            for (itemStack in stacks) {
                BehaviorUtils.throwItem(golem, itemStack, pos.add(0.0, 1.0, 0.0))
            }
        }
    }

    private fun removeOneItemFromItemEntity(itemEntity: ItemEntity): ItemStack {
        val itemStack = itemEntity.item
        val itemStack2 = itemStack.split(1)
        if (itemStack.isEmpty) {
            itemEntity.discard()
        } else {
            itemEntity.item = itemStack
        }

        return itemStack2
    }

    fun pickUpItem(golem: SoulSteelGolemEntity, itemEntity: ItemEntity) {
        golem.take(itemEntity, 64)
        val itemStack: ItemStack = removeOneItemFromItemEntity(itemEntity)

        putInInventory(golem, itemStack)
    }

    private fun throwItemsTowardRandomPos(golem: SoulSteelGolemEntity, stacks: List<ItemStack>) {
        throwItemsTowardPos(golem, stacks, VoidBoundPosUtils.getRandomNearbyPos(golem))
    }

    private fun putInInventory(golem: SoulSteelGolemEntity, stack: ItemStack) {
        val itemStack = golem.inventory.addItem(stack)
        throwItemsTowardRandomPos(golem, listOf(itemStack))
    }


    fun addItem(destination: Container, stack: ItemStack): ItemStack {
        var stack = stack

        val j = destination.containerSize

        var i = 0
        while (i < j && !stack.isEmpty) {
            stack = tryMoveInItem(destination, stack, i)
            i++
        }

        return stack
    }

    private fun tryMoveInItem(
        destination: Container,
        stack: ItemStack,
        slot: Int
    ): ItemStack {
        var stack = stack
        val itemStack = destination.getItem(slot)
        if (canPlaceItemInContainer(destination, stack, slot)) {
            var bl = false
            if (itemStack.isEmpty) {
                destination.setItem(slot, stack)
                stack = ItemStack.EMPTY
                bl = true
            } else if (canMergeItems(itemStack, stack)) {
                val i = stack.maxStackSize - itemStack.count
                val j = min(stack.count.toDouble(), i.toDouble()).toInt()
                stack.shrink(j)
                itemStack.grow(j)
                bl = j > 0
            }

            if (bl) {
                destination.setChanged()
            }
        }

        return stack
    }

    private fun canMergeItems(stack1: ItemStack, stack2: ItemStack): Boolean {
        return stack1.count <= stack1.maxStackSize && ItemStack.isSameItemSameTags(stack1, stack2)
    }

    private fun canPlaceItemInContainer(
        container: Container,
        stack: ItemStack,
        slot: Int
    ): Boolean {
        return container.canPlaceItem(slot, stack)
    }

    // Function to retrieve abilities from the ItemStack's NBT, returns a Set
    fun getItemAbilities(stack: ItemStack): Set<ItemAbility> {
        val abilities = mutableSetOf<ItemAbility>()
        val tag = stack.tag ?: return abilities // Return empty if no NBT

        val abilitiesTag = tag.getList("Abilities", 10) // 10 is the NBT type for CompoundTag
        for (i in 0 until abilitiesTag.size) {
            val abilityTag = abilitiesTag.getCompound(i)
            val ability = ItemAbility.readNbt(abilityTag)
            abilities.add(ability) // Add to the set, prevents duplicates
        }

        return abilities
    }

    // Function to add an ItemAbility to the ItemStack's NBT
    fun addItemAbility(stack: ItemStack, ability: ItemAbility, makeActive: Boolean = false) {
        val tag = stack.orCreateTag // Ensures the stack has NBT
        val abilitiesTag = tag.getList("Abilities", 10) // Fetch or create list

        // Check if ability already exists, if so, exit
        for (i in 0 until abilitiesTag.size) {
            val abilityTag = abilitiesTag.getCompound(i)
            val existingAbility = ItemAbility.readNbt(abilityTag)
            if (existingAbility == ability) {
                return // Ability already exists, exit without adding
            }
        }

        // Add new ability
        abilitiesTag.add(ability.writeNbt())
        tag.put("Abilities", abilitiesTag)

        // Optionally set the new ability as the active one
        if (makeActive) {
            setActiveAbility(stack, ability)
        }
    }

    // Function to set the active ability in NBT
    fun setActiveAbility(stack: ItemStack, ability: ItemAbility) {
        val tag = stack.orCreateTag
        val abilities = getItemAbilities(stack)

        if (abilities.contains(ability)) {
            // Store the active ability as a string in NBT
            tag.putString("ActiveAbility", ability.name)
        }
    }

    // Function to get the active ability from the ItemStack's NBT
    fun getActiveAbility(stack: ItemStack): ItemAbility? {
        val tag = stack.tag ?: return null
        val activeAbilityName = tag.getString("ActiveAbility")

        // Check if the active ability is one of the stored abilities
        val abilities = getItemAbilities(stack)
        return abilities.firstOrNull { it.name == activeAbilityName }
    }
}