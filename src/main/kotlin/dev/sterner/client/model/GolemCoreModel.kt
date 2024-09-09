package dev.sterner.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.VoidBound
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import java.util.function.Function

class GolemCoreModel(root: ModelPart) : Model(Function { location: ResourceLocation ->
    RenderType.entityCutoutNoCull(
        location
    )
}) {

    private val golemCore: ModelPart = root.getChild("golemCore")

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        golemCore.render(poseStack, buffer, packedLight, packedOverlay)
    }

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(VoidBound.id("golem_core"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val root: PartDefinition = meshDefinition.root

            val golemCore: PartDefinition = root.addOrReplaceChild(
                "golemCore",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0F, -13.3F, -3.3F, 8.0f, 8.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0F, 24.0F, 0.0F)
            )

            return LayerDefinition.create(meshDefinition, 16, 16)
        }
    }
}