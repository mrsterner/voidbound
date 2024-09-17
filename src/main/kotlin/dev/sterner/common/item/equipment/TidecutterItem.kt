package dev.sterner.common.item.equipment

import dev.sterner.api.util.VoidBoundBlockUtils
import dev.sterner.api.util.VoidBoundUtils
import dev.sterner.networking.AxeOfTheStreamParticlePacket
import dev.sterner.networking.BubbleParticlePacket
import dev.sterner.registry.VoidBoundItemRegistry
import dev.sterner.registry.VoidBoundPacketRegistry
import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f
import team.lodestar.lodestone.systems.item.tools.magic.MagicAxeItem
import java.awt.Color


open class TidecutterItem(
    material: Tier?,
    damage: Float,
    speed: Float,
    magicDamage: Float,
    properties: Properties?
) :
    MagicAxeItem(
        material, damage, speed,
        magicDamage,
        properties
    ), UpgradableTool {

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        VoidBoundUtils.addNetheritedTooltip(stack, tooltipComponents)
        tooltipComponents.add(
            Component.translatable("Not yet implemented").withStyle(ChatFormatting.ITALIC).withStyle(
                Style.EMPTY.withColor(Color.red.rgb)
            )
        )

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced)
    }

    override fun getDestroySpeed(stack: ItemStack, state: BlockState): Float {
        return super.getDestroySpeed(stack, state) + getExtraMiningSpeed(stack)
    }

    override fun getUseDuration(stack: ItemStack): Int {
        return 72000
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        player.startUsingItem(usedHand)
        return super.use(level, player, usedHand)
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
        val stuff = level.getEntitiesOfClass(ItemEntity::class.java, livingEntity.boundingBox.inflate(10.0))

        if (stuff != null && stuff.isNotEmpty()) {
           //TODO
        }

        super.onUseTick(level, livingEntity, stack, remainingUseDuration)
    }

    companion object {

        /**
         * Cancels the event and perform the specific axe effect for axe of the stream as wel as ichorium axe.
         * Axe of the Stream breaks the furthest block on a tree
         * Ichorium Axe breaks the whole tree
         */
        fun breakBlock(breakEvent: BlockEvents.BreakEvent?) {
            val player = breakEvent?.player
            val level = player?.level()
            val pos = breakEvent!!.pos

            if (level != null) {
                val block = level.getBlockState(pos)
                if (!player.isShiftKeyDown && block.`is`(BlockTags.LOGS)) {
                    if (player.mainHandItem.`is`(VoidBoundItemRegistry.ICHORIUM_TERRAFORMER.get())) {
                        val logsToBreak: List<BlockPos> = VoidBoundBlockUtils.gatherConnectedLogs(
                            level,
                            pos,
                            mutableListOf(),
                            level.getBlockState(pos).block
                        )
                        for (logPos in logsToBreak) {

                            val logState = level.getBlockState(logPos)
                            val be = level.getBlockEntity(logPos)

                            Block.dropResources(logState, level, logPos, be)
                            level.setBlock(logPos, Blocks.AIR.defaultBlockState(), 3)
                        }
                        breakEvent.isCanceled = true
                    }

                    if (player.mainHandItem.`is`(VoidBoundItemRegistry.TIDECUTTER_AXE.get())) {

                        if (level is ServerLevel) {
                            for (serverPlayer in PlayerLookup.tracking(level, pos)) {
                                VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToClient(
                                    AxeOfTheStreamParticlePacket(
                                        level.getBlockState(
                                            pos
                                        ), pos
                                    ), serverPlayer
                                )
                            }
                        }

                        //TODO break furthest block
                        breakEvent.isCanceled = true
                    }
                }
            }
        }
    }
}