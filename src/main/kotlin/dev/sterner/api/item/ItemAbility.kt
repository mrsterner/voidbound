package dev.sterner.api.item

import dev.sterner.common.item.equipment.ichor.IchoriumCrown
import dev.sterner.common.item.equipment.ichor.IchoriumEdge
import dev.sterner.common.item.equipment.ichor.IchoriumTerraformer
import dev.sterner.common.item.equipment.ichor.IchoriumVorpal
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.Item

enum class ItemAbility : StringRepresentable {
    NONE,//Fully implemented
    SCORCHING_HEAT,//Fully implemented
    EXCAVATOR,//Fully implemented
    EARTH_RUMMAGER,//TODO, re-implement. 5x5 mining, 25% mining speed penalty, avoids breaking ores
    VAMPIRISM,//TODO re-implement. Saps the target's lifeforce, healing half a heart for each spirit their soul has / half a heart for each armor point a player has, with a 10 tick cooldown for each half heart
    HARVEST,//TODO re-implement. Enables tilling, also allows excavation of plants and whatnot in a 7x7x7 area
    OPENER,//TODO re-implement. First hit on a mob deals increased damage and grants a stack of Wrath, lasting a minute, up to 10 stacks. Transforms into Finale when you reach 10 stacks, or sneak right click
    FINALE,//TODO implement. Consumes All stacks of Opening Strike, multiplying damage dealt by the amount of stacks total.
    TRIPLE_REBOUND,//TODO implement
    VENGEANCE,//TODO implement. Rebound now actively seeks the target who most recently attacked you, damage taken by the owner of the scythe extends it's flight time. Initial flight time greatly increased
    PROPAGATION,//TODO implement, Rebound causes a sweeping attack. Scythe Sweeping now propagates, spreading itself through hordes of enemies like a chain
    SPIRIT_VISION; //TODO implement, hallowed goggles ability

    override fun getSerializedName(): String {
        return this.name.lowercase()
    }

    fun writeNbt(): CompoundTag {
        val tag = CompoundTag()
        tag.putString("Ability", name)
        return tag
    }

    companion object {
        fun readNbt(abilityTag: CompoundTag): ItemAbility {
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