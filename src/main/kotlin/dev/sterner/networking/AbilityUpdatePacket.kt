package dev.sterner.networking

import dev.sterner.api.item.ItemAbility
import dev.sterner.api.util.VoidBoundItemUtils
import me.pepperbell.simplenetworking.SimpleChannel
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import team.lodestar.lodestone.systems.network.LodestoneServerPacket
import java.util.UUID

class AbilityUpdatePacket(val uuid: UUID, val itemAbility: ItemAbility) : LodestoneServerPacket() {

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUUID(uuid)
        buf.writeUtf(itemAbility.name)
    }

    constructor(buf: FriendlyByteBuf) : this(buf.readUUID(), ItemAbility.valueOf(buf.readUtf()))

    override fun executeServer(
        server: MinecraftServer?,
        player: ServerPlayer?,
        listener: ServerGamePacketListenerImpl?,
        responseSender: PacketSender?,
        channel: SimpleChannel?
    ) {
        server?.execute {
            if (player?.uuid == uuid) {
                val item = player.mainHandItem
                VoidBoundItemUtils.addItemAbility(item, itemAbility, true)
                VoidBoundItemUtils.setActiveAbility(item, itemAbility)
            }
        }
    }
}