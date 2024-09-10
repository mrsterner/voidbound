package dev.sterner.api.item

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag

interface UpgradableAbilityItem {

    fun addExperience(experience: Int)

    fun removeExperience(experience: Int)

    fun writeNbt(abilities: List<ItemAbility>): CompoundTag {
        val listTag = ListTag()
        for (ability in abilities) {
            val abilityTag = ability.writeNbt()
            listTag.add(abilityTag)
        }
        val tag = CompoundTag()
        tag.put("Abilities", listTag)
        return tag
    }

    fun readNbt(tag: CompoundTag): List<ItemAbility> {
        val tagList = tag.getList("Abilities", 10)
        val list = mutableListOf<ItemAbility>()

        for (i in 0 until tagList.size) {
            val abilityTag = tagList.getCompound(i)
            val ability = ItemAbility.readNbt(abilityTag)

            list.add(ability)
        }

        return list
    }
}