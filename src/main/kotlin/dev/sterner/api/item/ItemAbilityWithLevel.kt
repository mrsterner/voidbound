package dev.sterner.api.item

import net.minecraft.nbt.CompoundTag

data class ItemAbilityWithLevel(val itemAbility: ItemAbility, val level: Int) {

    fun writeNbt(): CompoundTag {
        val compoundTag = CompoundTag()
        compoundTag.putString("ItemAbility", itemAbility.name)
        compoundTag.putInt("Level", level)
        return compoundTag
    }

    companion object {
        fun readNbt(nbt: CompoundTag): ItemAbilityWithLevel {
            val ability = ItemAbility.valueOf(nbt.getString("ItemAbility"))
            val level = nbt.getInt("Level")

            return ItemAbilityWithLevel(ability, level)
        }
    }


}
