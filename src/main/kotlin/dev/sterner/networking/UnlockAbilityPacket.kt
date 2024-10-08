package dev.sterner.networking

import dev.sterner.api.item.ItemAbility
import dev.sterner.registry.VoidBoundComponentRegistry
import me.pepperbell.simplenetworking.SimpleChannel
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import team.lodestar.lodestone.systems.network.LodestoneServerPacket
import java.util.*

class UnlockAbilityPacket(val uuid: UUID, val itemAbility: ItemAbility) : LodestoneServerPacket() {

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
                VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.get(player)
                    .addUnlockedItemAbility(itemAbility)
            }
        }
    }
}