package dev.sterner.registry

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeProvider
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken

object VoidBoundRenderTypes {

    val BOLT: RenderTypeProvider = RenderTypeProvider {
        LodestoneRenderTypeRegistry.createGenericRenderType(
            "bolt",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            LodestoneRenderTypeRegistry.builder()
                .setShaderState(VoidBoundShaders.BOLT)
                .setTransparencyState(LodestoneRenderTypeRegistry.TRANSLUCENT_TRANSPARENCY)
        )
    }

    val GRAVITY_VORTEX: RenderTypeProvider = RenderTypeProvider { token: RenderTypeToken ->
        LodestoneRenderTypeRegistry.createGenericRenderType(
            "gravity_vortex",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            LodestoneRenderTypeRegistry.builder()
                .setShaderState(VoidBoundShaders.GRAVITY_VORTEX)
                .setTransparencyState(LodestoneRenderTypeRegistry.ADDITIVE_TRANSPARENCY)
                .setLightmapState(LodestoneRenderTypeRegistry.LIGHTMAP)
                .setTextureState(token.get())
        )
    }


    val GRAVITY_VORTEX_DEPTH: RenderTypeProvider = RenderTypeProvider { token: RenderTypeToken ->
        LodestoneRenderTypeRegistry.createGenericRenderType(
            "gravity_vortex_depth",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            LodestoneRenderTypeRegistry.builder()
                .setShaderState(VoidBoundShaders.GRAVITY_VORTEX)
                .setTransparencyState(LodestoneRenderTypeRegistry.ADDITIVE_TRANSPARENCY)
                .setLightmapState(LodestoneRenderTypeRegistry.LIGHTMAP)
                .setDepthTestState(LodestoneRenderTypeRegistry.GREATER_DEPTH_TEST)
                .setTextureState(token.get())
        )
    }
}