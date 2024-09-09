package dev.sterner.common.entity.ai.goal

import dev.sterner.common.entity.GrimcultClericEntity
import net.minecraft.world.entity.ai.goal.Goal
import java.util.*

class FocusSpiritRiftGoal(private val cleric: GrimcultClericEntity) : Goal() {

    init {
        this.flags = (EnumSet.of(Flag.MOVE, Flag.LOOK))
    }

    override fun canUse(): Boolean {
        return cleric.getRiftPos().isPresent
    }

    override fun tick() {
        super.tick()
        val riftPos = cleric.getRiftPos().get().immutable()
        cleric.lookControl.setLookAt(riftPos.center)

        if (!riftPos.closerToCenterThan(this.cleric.position(), 4.0)) {

            this.cleric.navigation
                .moveTo(
                    riftPos.x.toDouble() + 0.5,
                    riftPos.y.toDouble(),
                    riftPos.z.toDouble() + 0.5,
                    1.0
                )

        }

        if (riftPos!!.distToCenterSqr(cleric.position()) <= 4.0) {
            cleric.moveControl.strafe(-0.5f, 0f)
        }
    }
}