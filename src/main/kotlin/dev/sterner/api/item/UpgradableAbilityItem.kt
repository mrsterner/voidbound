package dev.sterner.api.item

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag

interface UpgradableAbilityItem {

    fun addExperience(experience: Int)

    fun removeExperience(experience: Int)

    fun writeNbt(abilities: List<ItemAbilityWithLevel>): CompoundTag {
        val listTag = ListTag()
        for (ability in abilities) {
            val ab = ability.writeNbt()
            listTag.add(ab)
        }
        val tag = CompoundTag()
        tag.put("Abilities", listTag)
        return tag
    }

    fun readNbt(tag: CompoundTag): List<ItemAbilityWithLevel> {
        val tagList = tag.getList("Abilities", 10)
        val list = mutableListOf<ItemAbilityWithLevel>()

        for (i in 0 until tagList.size) {
            val abilityTag = tagList.getCompound(i)
            val ability = ItemAbilityWithLevel.readNbt(abilityTag)
            list.add(ability)
        }

        return list
    }
}