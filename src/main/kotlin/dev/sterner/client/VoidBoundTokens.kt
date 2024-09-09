package dev.sterner.client

import com.sammy.malum.MalumMod
import dev.sterner.VoidBound
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken

object VoidBoundTokens {
    val wardBorder: RenderTypeToken =
        RenderTypeToken.createToken(VoidBound.id("textures/block/runeborder.png"))
    val noisy: RenderTypeToken =
        RenderTypeToken.createToken(MalumMod.malumPath("textures/vfx/tt.png"))

    val laser: RenderTypeToken =
        RenderTypeToken.createToken(VoidBound.id("textures/laser.png"))
}