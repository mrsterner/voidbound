package dev.sterner.common.item

import dev.sterner.common.entity.SoulSteelGolemEntity
import dev.sterner.registry.VoidBoundEntityTypeRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.gameevent.GameEvent

class GolemEntityItem : Item(Properties().stacksTo(1)) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val blockPos = context.clickedPos
        val blockState = level.getBlockState(blockPos)
        val itemStack = context.itemInHand
        val direction = context.clickedFace

        if (level is ServerLevel) {
            val blockPos2: BlockPos = if (blockState.getCollisionShape(level, blockPos).isEmpty) {
                blockPos
            } else {
                blockPos.relative(direction)
            }

            val golemType: EntityType<*> = VoidBoundEntityTypeRegistry.SOUL_STEEL_GOLEM_ENTITY.get()

            val golem: Entity? = golemType.spawn(
                level,
                itemStack,
                context.player,
                blockPos2,
                MobSpawnType.MOB_SUMMONED,
                true,
                blockPos != blockPos2 && direction == Direction.UP
            )

            if (golem is SoulSteelGolemEntity) {
                itemStack.shrink(1)
                level.gameEvent(context.player, GameEvent.ENTITY_PLACE, blockPos)
                golem.setOwner(context.player!!.uuid)
            }
        }

        return super.useOn(context)
    }
}