package dev.sterner.api.item

import dev.sterner.common.item.tool.ichor.IchoriumTerraformer
import dev.sterner.common.item.tool.ichor.IchoriumVorpal
import dev.sterner.registry.VoidBoundTags
import net.minecraft.nbt.CompoundTag
import net.minecraft.tags.TagKey
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.*

import kotlin.reflect.KClass

enum class ItemAbility: StringRepresentable {
    NONE,//Fully implemented
    AUTOSMELT,//Fully implemented
    MINING_3X3,//Fully implemented
    MINING_5X5,//Fully implemented
    VAMPIRISM,//TODO implement
    HARVEST,//Fully implemented
    OPENER;//TODO implement

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
                list.add(AUTOSMELT)
                list.add(MINING_3X3)
                list.add(MINING_5X5)
            }
            if (item is IchoriumVorpal) {
                list.add(VAMPIRISM)
                list.add(OPENER)
                list.add(HARVEST)
            }

            return list
        }
    }
}