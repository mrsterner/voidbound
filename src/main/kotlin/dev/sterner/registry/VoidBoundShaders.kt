package dev.sterner.registry

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.datafixers.util.Pair
import dev.sterner.VoidBound
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.server.packs.resources.ResourceProvider
import team.lodestar.lodestone.events.LodestoneShaderRegistrationEvent
import team.lodestar.lodestone.registry.client.LodestoneShaderRegistry
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder
import java.util.function.Consumer

object VoidBoundShaders {

    var BOLT: ShaderHolder = ShaderHolder(
        VoidBound.id("bolt"),
        DefaultVertexFormat.POSITION_COLOR,
        "LumiTransparency"
    )

    var GRAVITY_VORTEX: ShaderHolder = ShaderHolder(
        VoidBound.id("gravity_vortex"),
        DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
        "LumiTransparency"
    )

    fun init() {
        LodestoneShaderRegistrationEvent.EVENT.register(LodestoneShaderRegistrationEvent.Register { provider: ResourceProvider?, shaderList1: MutableList<Pair<ShaderInstance?, Consumer<ShaderInstance?>?>?> ->
            shaderList1.add(
                Pair.of(
                    GRAVITY_VORTEX.createInstance(provider),
                    LodestoneShaderRegistry.getConsumer()
                )
            )
            shaderList1.add(
                Pair.of(
                    BOLT.createInstance(provider),
                    LodestoneShaderRegistry.getConsumer()
                )
            )
        })
    }
}