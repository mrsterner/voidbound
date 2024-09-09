package dev.sterner.common.item

import com.sammy.malum.common.entity.FloatingItemEntity
import com.sammy.malum.common.item.IMalumEventResponderItem
import com.sammy.malum.common.item.spirit.SpiritShardItem
import com.sammy.malum.registry.common.SpiritTypeRegistry
import dev.sterner.common.entity.ParticleEntity
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import java.awt.Color


class DividerItem(properties: Properties) : Item(properties), IMalumEventResponderItem {

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.BRUSH
    }

    override fun getUseDuration(stack: ItemStack): Int {
        return 200
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        player.startUsingItem(usedHand)
        return super.use(level, player, usedHand)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player
        player!!.startUsingItem(context.hand)
        return InteractionResult.CONSUME
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
        if (remainingUseDuration >= 0 && livingEntity is Player) {
            val spirits = getEntitiesInBox(level, livingEntity)
            for (spirit in spirits) {
                if (spirit.item.item is SpiritShardItem) {
                    for (spiritType in SpiritTypeRegistry.SPIRITS) {
                        val dest = ParticleEntity.getRandomOffset(spirit.position(), level.random)

                        val particle = ParticleEntity(level, dest)
                        particle.setSpirit(spiritType.value)
                        particle.moveTo(spirit.position())

                        level.addFreshEntity(particle)
                    }

                    for (i in 0..10) {
                        val dest = spirit.position()
                            .add(8.0, (level.random.nextDouble() - 0.5), (level.random.nextDouble() - 0.5))
                        val nDest = spirit.position()
                            .add(-8.0, (level.random.nextDouble() - 0.5), (level.random.nextDouble() - 0.5))
                        val particle = ParticleEntity(level, dest)
                        val nParticle = ParticleEntity(level, nDest)

                        particle.setSpirit(spirit.spiritType)
                        particle.moveTo(spirit.position())

                        nParticle.setSpirit(spirit.spiritType)
                        nParticle.moveTo(spirit.position())

                        level.addFreshEntity(particle)
                        level.addFreshEntity(nParticle)
                    }
                    spirit.discard()
                }
            }
        }

    }

    private fun getEntitiesInBox(level: Level, player: Player): Collection<FloatingItemEntity> {
        // Calculate the direction the player is looking at
        val lookDirection = player.lookAngle
        val position = player.position()

        // Calculate the center of the box 2 blocks in front of the player
        val centerX = position.x + lookDirection.x * 2
        val centerY = position.y + lookDirection.y * 2
        val centerZ = position.z + lookDirection.z * 2

        // Define the bounding box centered at the calculated position
        val boundingBox = AABB(centerX - 3, centerY - 3, centerZ - 3, centerX + 3, centerY + 3, centerZ + 3)

        // Retrieve entities in the defined bounding box
        val entitiesInBox = mutableListOf<FloatingItemEntity>()
        for (entity in level.getEntitiesOfClass(FloatingItemEntity::class.java, boundingBox)) {
            if (player.distanceToSqr(entity) < 9.0) {
                entitiesInBox.add(entity)
            }
        }

        return entitiesInBox
    }

    override fun canAttackBlock(state: BlockState, level: Level, pos: BlockPos, player: Player): Boolean {
        return false
    }

    override fun getDestroySpeed(stack: ItemStack, state: BlockState): Float {
        return 1f
    }

    override fun hurtEnemy(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        return false
    }

    override fun mineBlock(
        stack: ItemStack,
        level: Level,
        state: BlockState,
        pos: BlockPos,
        miningEntity: LivingEntity
    ): Boolean {
        return false
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        tooltipComponents.add(
            Component.translatable("Not yet implemented").withStyle(ChatFormatting.ITALIC).withStyle(
                Style.EMPTY.withColor(Color.red.rgb)
            )
        )

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced)
    }
}