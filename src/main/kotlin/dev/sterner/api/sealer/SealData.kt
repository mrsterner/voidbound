package dev.sterner.api.sealer

import com.sammy.malum.core.systems.spirit.MalumSpiritType
import com.sammy.malum.registry.common.SpiritTypeRegistry
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.entity.player.Player

data class SealData(val spiritType: MalumSpiritType) {

    fun tickEffect(player: Player) {
        spiritTickMap[spiritType]?.invoke(player)
    }

    companion object {
        fun readFromNbt(tag: CompoundTag): MutableSet<SealData> {
            val dataSet = mutableSetOf<SealData>()
            val list = tag.getList("SealDataSet", 10)
            for (i in 0 until list.size) {
                val spiritTag = list.getCompound(i)
                val spiritType = SpiritTypeRegistry.SPIRITS[spiritTag.getString("Type")]!!
                dataSet.add(SealData(spiritType))
            }
            return dataSet
        }

        fun writeToNbt(dataSet: MutableSet<SealData>, tag: CompoundTag) {
            val list = ListTag()
            dataSet.forEach { data ->
                val spiritTag = CompoundTag()
                spiritTag.putString("Type", data.spiritType.identifier)
                list.add(spiritTag)
            }
            tag.put("SealDataSet", list)
        }

        private fun tickAerial(player: Player) {

        }

        private fun tickAqueous(player: Player) {

        }

        private fun tickInfernal(player: Player) {

        }

        private fun tickEarthen(player: Player) {

        }

        private fun tickArcane(player: Player) {

        }

        private fun tickWicked(player: Player) {

        }

        private fun tickEldritch(player: Player) {

        }

        private fun tickSacred(player: Player) {

        }

        private val spiritTickMap = mapOf<MalumSpiritType, (Player) -> Unit>(
            SpiritTypeRegistry.AERIAL_SPIRIT to ::tickAerial,
            SpiritTypeRegistry.AQUEOUS_SPIRIT to ::tickAqueous,
            SpiritTypeRegistry.INFERNAL_SPIRIT to ::tickInfernal,
            SpiritTypeRegistry.EARTHEN_SPIRIT to ::tickEarthen,
            SpiritTypeRegistry.ARCANE_SPIRIT to ::tickArcane,
            SpiritTypeRegistry.WICKED_SPIRIT to ::tickWicked,
            SpiritTypeRegistry.ELDRITCH_SPIRIT to ::tickEldritch,
            SpiritTypeRegistry.SACRED_SPIRIT to ::tickSacred
        )
    }
}
