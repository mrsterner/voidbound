package dev.sterner.networking

import dev.sterner.common.blockentity.OsmoticEnchanterBlockEntity
import me.pepperbell.simplenetworking.SimpleChannel
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import team.lodestar.lodestone.systems.network.LodestoneServerPacket

class StartEnchantingPacket(val pos: BlockPos) : LodestoneServerPacket() {

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeBlockPos(pos)
    }

    constructor(buf: FriendlyByteBuf) : this(buf.readBlockPos())


    override fun executeServer(
        server: MinecraftServer?,
        player: ServerPlayer?,
        listener: ServerGamePacketListenerImpl?,
        responseSender: PacketSender?,
        channel: SimpleChannel?
    ) {
        server?.execute {
            val be = player?.level()?.getBlockEntity(pos)
            if (be is OsmoticEnchanterBlockEntity) {
                be.startEnchanting()
            }
        }
    }
}