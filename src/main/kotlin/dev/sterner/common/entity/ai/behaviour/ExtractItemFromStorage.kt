package dev.sterner.common.entity.ai.behaviour

import com.mojang.datafixers.util.Pair
import dev.sterner.api.util.VoidBoundItemUtils
import dev.sterner.common.entity.SoulSteelGolemEntity
import dev.sterner.registry.VoidBoundMemoryTypeRegistry
import net.minecraft.world.Container
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour
import net.tslat.smartbrainlib.util.BrainUtils

class ExtractItemFromStorage : ExtendedBehaviour<SoulSteelGolemEntity>() {

    private var fillMode: Boolean = false
    private var emptyMode: Boolean = false

    override fun getMemoryRequirements(): MutableList<Pair<MemoryModuleType<*>, MemoryStatus>> {
        return mutableListOf(
            Pair.of(VoidBoundMemoryTypeRegistry.OUTPUT_STORAGE_LOCATION.get(), MemoryStatus.VALUE_PRESENT)
        )
    }

    override fun shouldKeepRunning(entity: SoulSteelGolemEntity): Boolean {
        return entity.inventory.isEmpty
    }

    override fun tick(entity: SoulSteelGolemEntity) {
        super.tick(entity)

        val memory = BrainUtils.getMemory(entity, VoidBoundMemoryTypeRegistry.OUTPUT_STORAGE_LOCATION.get())

        if (memory != null && memory.distToCenterSqr(entity.position()) < 2) {
            val be = entity.level().getBlockEntity(memory)

            if (be is Container) {
                val container = be as Container

                for (i in 0 until entity.inventory.containerSize) {
                    if (!container.getItem(i).isEmpty) {
                        val itemStack2 = VoidBoundItemUtils.addItem(entity.inventory, container.removeItem(i, 1))

                        if (itemStack2.isEmpty) {
                            container.setChanged()
                        }
                    }
                }
            }
        }
    }


    fun fill(): ExtendedBehaviour<SoulSteelGolemEntity> {
        this.fillMode = true
        return this
    }

    fun empty(): ExtendedBehaviour<SoulSteelGolemEntity> {
        this.emptyMode = true
        return this
    }
}