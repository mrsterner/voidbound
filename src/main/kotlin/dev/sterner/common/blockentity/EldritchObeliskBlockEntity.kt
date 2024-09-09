package dev.sterner.common.blockentity

import dev.sterner.registry.VoidBoundBlockEntityTypeRegistry
import dev.sterner.registry.VoidBoundBlockRegistry
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import team.lodestar.lodestone.systems.multiblock.MultiBlockCoreEntity
import team.lodestar.lodestone.systems.multiblock.MultiBlockStructure
import java.util.function.Supplier

class EldritchObeliskBlockEntity(
    type: BlockEntityType<*>?,
    structure: MultiBlockStructure?,
    pos: BlockPos?,
    state: BlockState?
) : MultiBlockCoreEntity(
    type,
    structure, pos, state
) {

    constructor(pos: BlockPos?, state: BlockState?) : this(
        VoidBoundBlockEntityTypeRegistry.ELDRITCH_OBELISK.get(),
        STRUCTURE.get(), pos, state
    )


    companion object {
        val STRUCTURE: Supplier<MultiBlockStructure> =
            Supplier {
                (MultiBlockStructure.of(
                    MultiBlockStructure.StructurePiece(
                        0,
                        1,
                        0,
                        VoidBoundBlockRegistry.ELDRITCH_OBELISK_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        0,
                        2,
                        0,
                        VoidBoundBlockRegistry.ELDRITCH_OBELISK_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        0,
                        3,
                        0,
                        VoidBoundBlockRegistry.ELDRITCH_OBELISK_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        0,
                        4,
                        0,
                        VoidBoundBlockRegistry.ELDRITCH_OBELISK_COMPONENT.get().defaultBlockState()
                    ),
                    MultiBlockStructure.StructurePiece(
                        0,
                        5,
                        0,
                        VoidBoundBlockRegistry.ELDRITCH_OBELISK_COMPONENT.get().defaultBlockState()
                    )
                ))
            }

    }

}