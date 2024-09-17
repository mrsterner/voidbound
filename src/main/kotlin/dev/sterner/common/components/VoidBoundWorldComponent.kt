package dev.sterner.common.components

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import dev.sterner.api.util.VoidBoundRenderUtils
import dev.sterner.client.VoidBoundTokens
import dev.sterner.common.item.equipment.SealerItem
import dev.sterner.registry.VoidBoundComponentRegistry
import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.nbt.*
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.*

/**
 * Keeps track of the worlds warded blockPoses and the players UUIDs connected
 */
class VoidBoundWorldComponent(val level: Level) : AutoSyncedComponent {

    /**
     * Every block can have one UUID connected to it, makes lookup time complexity O(1) in most cases
     */
    private val posOwnerMap = mutableMapOf<GlobalPos, UUID>()

    /**
     * Only used to save calculations on rendering warded blocks
     */
    private var cachedPositions: List<BlockPos>? = null

    /**
     * Get every warded block and cache if cache is null
     */
    fun getAllPos(): List<BlockPos> {
        // Check if the cache is invalid
        if (cachedPositions == null) {
            // Recompute the positions and update the cache
            cachedPositions = posOwnerMap
                .filter { it.key.dimension() == level.dimension() }
                .map { it.key.pos() }
        }
        return cachedPositions ?: emptyList()
    }

    /**
     * Get every warded block bound to a player, cache if cache is null
     */
    fun getAllPos(player: Player): List<BlockPos> {
        // Check if the cache is invalid
        if (cachedPositions == null) {
            // Recompute the positions and update the cache
            cachedPositions = posOwnerMap.filter { it.value == player.uuid }
                .filter { it.key.dimension() == level.dimension() }
                .map { it.key.pos() }
        }
        return cachedPositions ?: emptyList()
    }

    fun isEmpty(): Boolean {
        return posOwnerMap.isEmpty()
    }

    /**
     * Returns true if the block is warded but not to this player
     */
    fun isPosBoundToAnotherPlayer(player: Player, pos: GlobalPos): Boolean {
        val ownerUuid = posOwnerMap[pos]
        return ownerUuid != null && ownerUuid != player.uuid
    }

    /**
     * If the pos is warded
     * Time complexity: O(1) on average
     */
    fun hasBlockPos(pos: GlobalPos): Boolean {
        return posOwnerMap.containsKey(pos)
    }

    /**
     * If the pos is warded by player
     * Time complexity: O(1) on average
     */
    fun hasBlockPos(player: Player, pos: GlobalPos): Boolean {
        return posOwnerMap[pos] == player.uuid
    }

    /**
     * Add a warded pos if it doesn't already exist and clear cache
     */
    fun addPos(uuid: UUID, pos: GlobalPos) {
        // Only add the position if it is not already owned
        if (hasBlockPos(pos)) {
            return
        }

        // Add the position to the global map with the UUID as the owner
        posOwnerMap[pos] = uuid
        clearCache() // Invalidate the cache
        // Sync the changes
        VoidBoundComponentRegistry.VOID_BOUND_WORLD_COMPONENT.sync(level)
    }

    /**
     * Removes a warded pos, and clear cache
     */
    fun removePos(pos: GlobalPos) {
        posOwnerMap.remove(pos)
        clearCache() // Invalidate the cache
        // Sync the changes
        VoidBoundComponentRegistry.VOID_BOUND_WORLD_COMPONENT.sync(level)
    }

    /**
     * Removes a pos only if it's bound to this uuid
     */
    fun removePos(uuid: UUID, pos: GlobalPos) {
        // Remove the position only if it is owned by the specified UUID
        if (posOwnerMap[pos] == uuid) {
            posOwnerMap.remove(pos)
            clearCache() // Invalidate the cache
            // Sync the changes
            VoidBoundComponentRegistry.VOID_BOUND_WORLD_COMPONENT.sync(level)
        }
    }

    private fun clearCache() {
        cachedPositions = null
    }

    override fun readFromNbt(tag: CompoundTag) {
        posOwnerMap.clear()

        val list = tag.getList("PlayerWardPosMap", 10)

        for (i in 0 until list.size) {
            val playerTag = list.getCompound(i)
            val playerId = playerTag.getUUID("PlayerUUID")

            val posList = playerTag.getList("Positions", 10)

            for (j in 0 until posList.size) {
                val posTag = posList.getCompound(j)
                val optionalDimension = getLodestoneDimension(posTag)
                if (optionalDimension.isPresent) {
                    val blockPos = NbtUtils.readBlockPos(posTag.getCompound("GlobalPos"))
                    val globalPos = GlobalPos.of(optionalDimension.get(), blockPos)
                    posOwnerMap[globalPos] = playerId
                }
            }
        }
    }

    override fun writeToNbt(tag: CompoundTag) {
        val list = ListTag()

        for ((globalPos, uuid) in posOwnerMap) {
            val playerTag = CompoundTag()
            playerTag.putUUID("PlayerUUID", uuid)

            val posListTag = ListTag()
            val posTag = CompoundTag()

            // Write the GlobalPos to NBT
            NbtUtils.writeBlockPos(globalPos.pos()).let { blockPosTag -> posTag.put("GlobalPos", blockPosTag) }

            // Write the dimension (LodestoneDimension) to NBT
            addLodestoneTags(globalPos.dimension(), posTag)

            posListTag.add(posTag)
            playerTag.put("Positions", posListTag)
            list.add(playerTag)
        }

        tag.put("PlayerWardPosMap", list)
    }

    /**
     * Copy of Minecraft's getLodestoneDimension
     */
    private fun getLodestoneDimension(compoundTag: CompoundTag): Optional<ResourceKey<Level>> {
        val dimensionTag = compoundTag["Dimension"]
        return if (dimensionTag != null) {
            Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, dimensionTag).result()
        } else {
            Optional.empty()
        }
    }

    /**
     * Copy of Minecraft's addLodestoneTags
     */
    private fun addLodestoneTags(
        lodestoneDimension: ResourceKey<Level>,
        compoundTag: CompoundTag
    ) {
        Level.RESOURCE_KEY_CODEC
            .encodeStart(NbtOps.INSTANCE, lodestoneDimension)
            .resultOrPartial { _: String? -> }
            .ifPresent { tag: Tag ->
                compoundTag.put("Dimension", tag)
            }
    }

    companion object {
        /**
         * If a block manages to break, remove the ward
         */
        fun removeWard(breakEvent: BlockEvents.BreakEvent?) {
            val pos = breakEvent?.pos
            if (pos != null) {
                val comp = VoidBoundComponentRegistry.VOID_BOUND_WORLD_COMPONENT.get(breakEvent.player.level())
                comp.removePos(GlobalPos.of(breakEvent.player.level().dimension(), pos))
            }
        }

        /**
         * Uses the cache to render all the warded block owned by the client player
         */
        fun renderCubeAtPos(ctx: WorldRenderContext) {
            val camera = ctx.camera()
            val poseStack = ctx.matrixStack()
            val localPlayer = Minecraft.getInstance().player
            if (localPlayer != null) {
                if (localPlayer.mainHandItem.item is SealerItem) {
                    val sealer = localPlayer.mainHandItem
                    val levelComp =
                        VoidBoundComponentRegistry.VOID_BOUND_WORLD_COMPONENT.get(localPlayer.level())

                    val poses: List<BlockPos> = levelComp.getAllPos(localPlayer)
                    for (pos in poses) {
                        VoidBoundRenderUtils.renderCubeAtPos(
                            camera,
                            poseStack,
                            pos,
                            VoidBoundTokens.wardBorder,
                            20,
                            20
                        )
                    }
                }
            }
        }
    }
}