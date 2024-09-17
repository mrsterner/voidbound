package dev.sterner.common.components

import com.sammy.malum.core.systems.spirit.MalumSpiritType
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent
import dev.sterner.api.sealer.SealData
import dev.sterner.registry.VoidBoundComponentRegistry
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player

class VoidBoundPlayerSealerComponent(val player: Player): AutoSyncedComponent, CommonTickingComponent {

    var sealedSpirits = mutableSetOf<SealData>()

    fun trySealSpirit(sealData: SealData) : Boolean {
        val added = sealedSpirits.add(sealData)
        if (added) {
            sync()
            return true
        }
        return false
    }

    fun hasSealedSpirit(spiritType: MalumSpiritType) : Boolean {
        return sealedSpirits.contains(SealData(spiritType))
    }

    override fun tick() {
        for (sealedSpirit in sealedSpirits) {
            sealedSpirit.tickEffect(player)
        }
    }

    fun sync(){
        VoidBoundComponentRegistry.VOID_BOUND_PLAYER_SEALER_COMPONENT.sync(player)
    }

    override fun readFromNbt(tag: CompoundTag) {
        sealedSpirits.clear()
        sealedSpirits = SealData.readFromNbt(tag)
    }

    override fun writeToNbt(tag: CompoundTag) {
        SealData.writeToNbt(sealedSpirits, tag)
    }
}