package dev.sterner.client.model

import dev.sterner.VoidBound
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartNames
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.entity.LivingEntity


class HallowedGogglesModel<T : LivingEntity>(root: ModelPart) :
    HumanoidModel<T>(root, RenderType::entityTranslucent) {

    private val goggles: ModelPart = root.getChild("head").getChild("goggles")

    companion object {
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(VoidBound.id("hallowed_goggles"), "main")

        fun createBodyLayer(): LayerDefinition {
            val data = createMesh(CubeDeformation.NONE, 0f)
            val root = data.root
            val head = root.getChild(PartNames.HEAD)
            val goggles = head.addOrReplaceChild(
                "goggles",
                CubeListBuilder.create().texOffs(30, 48)
                    .addBox(-4.5f, -30.0f, -3.5f, 9.0f, 2.0f, 8.0f, CubeDeformation(-0.001f)),
                PartPose.offset(0.0f, 24.5f, 0.0f)
            )

            val eyes = goggles.addOrReplaceChild(
                "eyes",
                CubeListBuilder.create().texOffs(30, 58)
                    .addBox(0.5f, -30.5f, -5.0f, 4.0f, 4.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(30, 48).addBox(-0.5f, -29.5f, -4.5f, 1.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(30, 58).addBox(-4.5f, -30.5f, -5.0f, 4.0f, 4.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )
            return LayerDefinition.create(data, 64, 64)
        }
    }
}