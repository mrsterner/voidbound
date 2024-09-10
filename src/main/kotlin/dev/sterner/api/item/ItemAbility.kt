package dev.sterner.api.item

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.*

import kotlin.reflect.KClass

enum class ItemAbility(private val equipmentSlot: EquipmentSlot?, private val clazz: KClass<out Item>?): StringRepresentable {
    NONE(null, null),//TODO implement
    AUTOSMELT(null, DiggerItem::class),//Fully implemented
    MINING_3X3(null, DiggerItem::class),//TODO implement
    MINING_5X5(null, DiggerItem::class),//TODO implement
    VAMPIRISM(null, SwordItem::class),//TODO implement
    QUICKDRAW(null, ProjectileWeaponItem::class),//TODO implement
    DISPERSED_STRIKE(null, SwordItem::class),//TODO implement
    SLOW_FALL(EquipmentSlot.FEET, null);//TODO implement

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
            for (ability in entries) {
                // Check if the item matches the clazz or the equipmentSlot
                if (ability.equipmentSlot == null && ability.clazz?.isInstance(item) == true) {
                    list.add(ability)
                } else if (ability.equipmentSlot != null && item is ArmorItem && item.equipmentSlot == ability.equipmentSlot) {
                    list.add(ability)
                }
            }
            return list
        }
    }
}