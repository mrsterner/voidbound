package dev.sterner.api.rift

import com.sammy.malum.core.systems.recipe.SpiritWithCount
import com.sammy.malum.core.systems.spirit.MalumSpiritType
import com.sammy.malum.registry.common.SpiritTypeRegistry
import net.minecraft.nbt.CompoundTag

/**
 * Simple way to store a lot of spirits, with NBT support and basic operations
 */
data class SimpleSpiritCharge(
    private val charges: MutableMap<MalumSpiritType, Int> = mutableMapOf(
        SpiritTypeRegistry.AQUEOUS_SPIRIT to 0,
        SpiritTypeRegistry.AERIAL_SPIRIT to 0,
        SpiritTypeRegistry.ARCANE_SPIRIT to 0,
        SpiritTypeRegistry.EARTHEN_SPIRIT to 0,
        SpiritTypeRegistry.ELDRITCH_SPIRIT to 0,
        SpiritTypeRegistry.INFERNAL_SPIRIT to 0,
        SpiritTypeRegistry.SACRED_SPIRIT to 0,
        SpiritTypeRegistry.WICKED_SPIRIT to 0
    )
) {

    private fun getMutableList(): MutableList<SpiritWithCount> {
        val list = mutableListOf<SpiritWithCount>()
        forEach { type, count ->
            list.add(SpiritWithCount(type, count))
        }

        return list
    }

    fun getNonEmptyMutableList(): MutableList<SpiritWithCount> {
        return getMutableList().filter { it.count > 0 }.toMutableList()
    }

    fun size(): Int {
        return charges.size
    }

    fun getChargeForType(spiritType: MalumSpiritType): Int {
        return charges[spiritType]!!
    }

    fun forEach(action: (MalumSpiritType, Int) -> Unit) {
        charges.forEach { (type, count) -> action(type, count) }
    }

    fun setInfiniteCount() {
        charges.keys.forEach { charges[it] = 50 }
    }

    fun shouldBeInfinite(): Boolean {
        return charges.values.all { it >= 50 }
    }

    fun addToCharge(type: MalumSpiritType, count: Int) {
        charges[type] = (charges[type] ?: 0) + count
    }

    fun removeFromCharge(type: MalumSpiritType, count: Int): Boolean {
        val currentCount = charges[type] ?: return false
        return if (currentCount >= count) {
            charges[type] = currentCount - count
            true
        } else {
            false
        }
    }

    fun deserializeNBT(nbt: CompoundTag): SimpleSpiritCharge {
        charges.keys.forEach { charges[it] = nbt.getInt(it.identifier) }
        return this
    }

    fun serializeNBT(nbt: CompoundTag) {
        charges.forEach { (type, count) -> nbt.putInt(type.identifier, count) }
    }

    fun getTotalCharge(): Int {
        return charges.values.sum()
    }

    fun rechargeAllCount() {
        charges.forEach { (type, count) ->
            if (count < 50) {
                charges[type] = count + 1
                return
            }
        }
    }

    fun rechargeRandomCount() {
        val validTypes = charges.filter { (_, count) -> count < 50 }.keys

        if (validTypes.isNotEmpty()) {
            val randomType = validTypes.random()
            charges[randomType] = (charges[randomType] ?: 0) + 1
        }
    }

    fun addToCharge(type: MalumSpiritType) {
        charges[type] = charges.getOrDefault(type, 0) + 1
    }
}