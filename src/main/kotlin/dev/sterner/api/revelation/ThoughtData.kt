package dev.sterner.api.revelation

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component

data class ThoughtData(var duration: Int, var delay: Int) {

    companion object {
        fun readFromNbt(tag: CompoundTag): MutableMap<Component, ThoughtData> {
            val thoughtsQueue = mutableMapOf<Component, ThoughtData>()
            val thoughtsList = tag.getList("ThoughtsQueue", Tag.TAG_COMPOUND.toInt())
            for (i in 0 until thoughtsList.size) {
                val thoughtTag = thoughtsList.getCompound(i)
                val thought = Component.Serializer.fromJson(thoughtTag.getString("Text"))
                val duration = thoughtTag.getInt("Duration")
                val delay = thoughtTag.getInt("Delay")
                if (thought != null) {
                    thoughtsQueue[thought] = ThoughtData(duration, delay)
                }
            }
            return thoughtsQueue
        }

        fun writeToNbt(thoughtsQueue: Map<Component, ThoughtData>, tag: CompoundTag) {
            val thoughtsList = ListTag()
            thoughtsQueue.forEach { (thought, data) ->
                val thoughtTag = CompoundTag().apply {
                    putString("Text", Component.Serializer.toJson(thought))
                    putInt("Duration", data.duration)
                    putInt("Delay", data.delay)
                }
                thoughtsList.add(thoughtTag)
            }
            tag.put("ThoughtsQueue", thoughtsList)
        }
    }
}