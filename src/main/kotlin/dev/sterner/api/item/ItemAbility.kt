package dev.sterner.api.item

import dev.sterner.common.item.equipment.ichor.IchoriumCrown
import dev.sterner.common.item.equipment.ichor.IchoriumEdge
import dev.sterner.common.item.equipment.ichor.IchoriumTerraformer
import dev.sterner.common.item.equipment.ichor.IchoriumVorpal
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.Item

enum class ItemAbility : StringRepresentable {
    NONE,//Fully implemented
    SCORCHING_HEAT,//Fully implemented
    EXCAVATOR,//Fully implemented
    EARTH_RUMMAGER,
    VAMPIRISM,
    HARVEST,
    OPENER,//TODO re-implement. First hit on a mob deals increased damage and grants a stack of Wrath, lasting a minute, up to 10 stacks. Transforms into Finale when you reach 10 stacks, or sneak right click
    FINALE,//TODO implement. Consumes All stacks of Opening Strike, multiplying damage dealt by the amount of stacks total.
    TRIPLE_REBOUND,//TODO implement
    VENGEANCE,//TODO implement. Rebound now actively seeks the target who most recently attacked you, damage taken by the owner of the scythe extends it's flight time. Initial flight time greatly increased
    PROPAGATION,//TODO implement, Rebound causes a sweeping attack. Scythe Sweeping now propagates, spreading itself through hordes of enemies like a chain
    SPIRIT_VISION; //TODO implement, hallowed goggles ability

    override fun getSerializedName(): String {
        return this.name.lowercase()
    }

    fun writeSingleNbt(): CompoundTag {
        val tag = CompoundTag()
        tag.putString("Ability", name)
        return tag
    }

    companion object {

        fun writeToNbt(unlockedItemAbilities: MutableSet<ItemAbility>, tag: CompoundTag) {
            val unlockedList = ListTag()
            unlockedItemAbilities.forEach { unlockedTag ->
                val abilityTag = unlockedTag.writeSingleNbt()
                unlockedList.add(abilityTag)
            }
            tag.put("UnlockedItems", unlockedList)
        }

        fun readNbt(tag: CompoundTag): MutableSet<ItemAbility> {
            val unlockedItemAbilities = mutableSetOf<ItemAbility>()
            val unlockedList = tag.getList("UnlockedItems", 10)
            for (i in 0 until unlockedList.size) {
                val item = unlockedList.getCompound(i)
                val itemAbility = ItemAbility.readSingleNbt(item)
                unlockedItemAbilities.add(itemAbility)
            }
            return unlockedItemAbilities
        }

        fun readSingleNbt(abilityTag: CompoundTag): ItemAbility {
            return valueOf(abilityTag.getString("Ability"))
        }

        fun getAvailableAbilitiesFromItem(item: Item): Set<ItemAbility> {
            val list = mutableSetOf<ItemAbility>()
            list.add(NONE)
            if (item is IchoriumTerraformer) {
                list.add(SCORCHING_HEAT)
                list.add(EXCAVATOR)
                list.add(EARTH_RUMMAGER)
            }
            if (item is IchoriumVorpal) {
                list.add(VAMPIRISM)
                list.add(OPENER)
                list.add(FINALE)
                list.add(HARVEST)
            }
            if (item is IchoriumEdge) {
                list.add(TRIPLE_REBOUND)
                list.add(VENGEANCE)
                list.add(PROPAGATION)
            }
            if (item is IchoriumCrown) {
                list.add(SPIRIT_VISION)
            }

            return list
        }


    }
}