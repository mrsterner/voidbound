package dev.sterner.common.item.tool

import dev.sterner.api.util.VoidBoundUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import team.lodestar.lodestone.systems.item.tools.magic.MagicHoeItem

open class EarthsongItem(material: Tier?, damage: Int, speed: Float, magicDamage: Float, properties: Properties?) :
    MagicHoeItem(
        material, damage, speed,
        magicDamage,
        properties
    ), UpgradableTool {

    override fun getDestroySpeed(stack: ItemStack, state: BlockState): Float {
        return super.getDestroySpeed(stack, state) + getExtraMiningSpeed(stack)
    }

    //TODO evaluate this
    override fun mineBlock(
        stack: ItemStack,
        level: Level,
        state: BlockState,
        pos: BlockPos,
        miningEntity: LivingEntity
    ): Boolean {
        if (level is ServerLevel && state.`is`(BlockTags.CROPS)) {
            val list = NonNullList.create<ItemStack>()
            list.addAll(Block.getDrops(state, level, pos, null, miningEntity, stack))
            Containers.dropContents(level, pos, list)
        }
        return super.mineBlock(stack, level, state, pos, miningEntity)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player

        if (player!!.isShiftKeyDown) {
            return super.useOn(context)
        }

        for (xx in -1..1) {
            for (zz in -1..1) {
                super.useOn(
                    UseOnContext(
                        player, player.usedItemHand,
                        BlockHitResult(
                            context.clickLocation,
                            context.horizontalDirection,
                            context.clickedPos.offset(xx, 0, zz),
                            context.isInside
                        )
                    )
                )
            }
        }
        return InteractionResult.SUCCESS
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        VoidBoundUtils.addNetheritedTooltip(stack, tooltipComponents)
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced)
    }
}