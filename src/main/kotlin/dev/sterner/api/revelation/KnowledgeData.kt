package dev.sterner.api.revelation

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag

data class KnowledgeData(
    val knowledgeType: KnowledgeType,
    var thoughtSent: Boolean = false
) {
    companion object {
        fun readFromNbt(tag: CompoundTag): MutableSet<KnowledgeData> {
            val dataSet = mutableSetOf<KnowledgeData>()
            val list = tag.getList("KnowledgeDataSet", 10)
            for (i in 0 until list.size) {
                val knowledgeTag = list.getCompound(i)
                val knowledgeType = KnowledgeType.valueOf(knowledgeTag.getString("KnowledgeType"))
                val thoughtSent = knowledgeTag.getBoolean("ThoughtSent")
                dataSet.add(KnowledgeData(knowledgeType, thoughtSent))
            }
            return dataSet
        }

        fun writeToNbt(dataSet: MutableSet<KnowledgeData>, tag: CompoundTag) {
            val list = ListTag()
            dataSet.forEach { data ->
                val knowledgeTag = CompoundTag()
                knowledgeTag.putString("KnowledgeType", data.knowledgeType.name)
                knowledgeTag.putBoolean("ThoughtSent", data.thoughtSent)
                list.add(knowledgeTag)
            }
            tag.put("KnowledgeDataSet", list)
        }
    }
}