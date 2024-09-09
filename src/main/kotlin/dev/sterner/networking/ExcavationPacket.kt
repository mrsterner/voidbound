package dev.sterner.networking

import me.pepperbell.simplenetworking.SimpleChannel
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LevelEvent
import team.lodestar.lodestone.systems.network.LodestoneServerNBTPacket


class ExcavationPacket(data: CompoundTag?) : LodestoneServerNBTPacket(data) {

    override fun encode(buf: FriendlyByteBuf?) {
        super.encode(buf)
    }

    constructor(buf: FriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(blockPos: BlockPos, breakTime: Int, maxBreakTime: Int, progress: Int) : this(CompoundTag().apply {
        put("Pos", NbtUtils.writeBlockPos(blockPos))
        putInt("BreakTime", breakTime)
        putInt("MaxBreakTime", maxBreakTime)
        putInt("Progress", progress)
    })

    override fun executeServerNbt(
        server: MinecraftServer?,
        player: ServerPlayer,
        listener: ServerGamePacketListenerImpl?,
        responseSender: PacketSender?,
        channel: SimpleChannel?,
        data: CompoundTag
    ) {
        server?.execute {

            val blockPos = NbtUtils.readBlockPos(data.getCompound("Pos"))
            val breakTime = data.getInt("BreakTime")
            val progress = data.getInt("Progress")
            val maxBreakTime = data.getInt("MaxBreakTime")

            if (breakTime >= maxBreakTime - 1) {
                player.level().destroyBlock(blockPos, true)
                player.level().levelEvent(
                    LevelEvent.PARTICLES_DESTROY_BLOCK,
                    blockPos, Block.getId(player.level().getBlockState(blockPos))
                )
            }

            player.connection.send(
                ClientboundBlockDestructionPacket(
                    player.id + generatePosHash(blockPos),
                    blockPos,
                    progress
                )
            )
        }
    }

    companion object {
        fun generatePosHash(blockPos: BlockPos): Int {
            return (31 * 31 * blockPos.x) + (31 * blockPos.y) + blockPos.z
        }
    }
}