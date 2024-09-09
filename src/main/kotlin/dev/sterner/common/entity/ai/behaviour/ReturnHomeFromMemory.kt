package dev.sterner.common.entity.ai.behaviour

import com.mojang.datafixers.util.Pair
import dev.sterner.common.entity.SoulSteelGolemEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.Brain
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.minecraft.world.entity.ai.memory.WalkTarget
import net.minecraft.world.entity.ai.util.DefaultRandomPos
import net.minecraft.world.phys.Vec3
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour
import net.tslat.smartbrainlib.util.BrainUtils
import java.util.*
import kotlin.math.sqrt


class ReturnHomeFromMemory(
    private val speedModifier: Float,
    private val closeEnoughDist: Int,
    private val tooFarDistance: Int,
    private val tooLongUnreachableDuration: Int
) :
    ExtendedBehaviour<SoulSteelGolemEntity>() {

    private fun dropPOI(pVillager: SoulSteelGolemEntity, pTime: Long) {
        val brain: Brain<*> = pVillager.brain
        brain.eraseMemory(MemoryModuleType.HOME)
        brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, pTime)
    }

    override fun checkExtraStartConditions(level: ServerLevel, entity: SoulSteelGolemEntity): Boolean {
        val pos: GlobalPos? = BrainUtils.getMemory(entity, MemoryModuleType.HOME)
        if (pos == null || pos.dimension() !== level.dimension()) {
            return false
        }

        return sqrt(entity.distanceToSqr(pos.pos().x.toDouble(), pos.pos().y.toDouble(), pos.pos().z.toDouble())) >= 16
    }

    override fun start(pLevel: ServerLevel, pEntity: SoulSteelGolemEntity, pGameTime: Long) {
        val brain: Brain<*> = pEntity.brain
        brain.getMemory(MemoryModuleType.HOME).ifPresent { globalPos: GlobalPos ->
            if (!this.wrongDimension(pLevel, globalPos) && !this.tiredOfTryingToFindTarget(
                    pLevel,
                    pEntity
                )
            ) {
                if (this.tooFar(pEntity, globalPos)) {
                    var vec3: Vec3? = null
                    var i = 0

                    while (i < 1000 && (vec3 == null || this.tooFar(
                            pEntity,
                            GlobalPos.of(pLevel.dimension(), BlockPos.containing(vec3))
                        ))
                    ) {
                        vec3 = DefaultRandomPos.getPosTowards(
                            pEntity,
                            15,
                            7,
                            Vec3.atBottomCenterOf(globalPos.pos()),
                            (Math.PI.toFloat() / 2f).toDouble()
                        )
                        ++i
                    }

                    if (i == 1000) {
                        this.dropPOI(pEntity, pGameTime)
                        return@ifPresent
                    }

                    brain.setMemory(
                        MemoryModuleType.WALK_TARGET,
                        WalkTarget(vec3!!, this.speedModifier, this.closeEnoughDist)
                    )
                } else if (!this.closeEnough(pLevel, pEntity, globalPos)) {
                    brain.setMemory(
                        MemoryModuleType.WALK_TARGET,
                        WalkTarget(globalPos.pos(), this.speedModifier, this.closeEnoughDist)
                    )
                }
            } else {
                this.dropPOI(pEntity, pGameTime)
            }
        }
    }

    private fun tiredOfTryingToFindTarget(pLevel: ServerLevel, pVillager: SoulSteelGolemEntity): Boolean {
        val optional: Optional<Long> = pVillager.brain.getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
        return optional.filter { aLong -> pLevel.gameTime - aLong > tooLongUnreachableDuration.toLong() }
            .isPresent
    }

    private fun tooFar(pVillager: SoulSteelGolemEntity, pMemoryType: GlobalPos): Boolean {
        return pMemoryType.pos().distManhattan(pVillager.blockPosition()) > this.tooFarDistance
    }

    private fun wrongDimension(pLevel: ServerLevel, pMemoryPos: GlobalPos): Boolean {
        return pMemoryPos.dimension() !== pLevel.dimension()
    }

    private fun closeEnough(pLevel: ServerLevel, pVillager: SoulSteelGolemEntity, pMemoryPos: GlobalPos): Boolean {
        return pMemoryPos.dimension() === pLevel.dimension() && pMemoryPos.pos()
            .distManhattan(pVillager.blockPosition()) <= this.closeEnoughDist
    }

    override fun getMemoryRequirements(): List<Pair<MemoryModuleType<*>, MemoryStatus>> {
        return listOf<Pair<MemoryModuleType<*>, MemoryStatus>>(
            Pair.of(
                MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                MemoryStatus.REGISTERED
            ),
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
            Pair.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT)
        )
    }
}