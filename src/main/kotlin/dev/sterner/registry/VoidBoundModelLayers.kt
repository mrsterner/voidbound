package dev.sterner.registry

import dev.sterner.client.model.*
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry

object VoidBoundModelLayers {

    fun init() {
        EntityModelLayerRegistry.registerModelLayer(SoulSteelGolemEntityModel.LAYER_LOCATION) { SoulSteelGolemEntityModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(GolemCoreModel.LAYER_LOCATION) { GolemCoreModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(FocusModel.LAYER_LOCATION) { FocusModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(WandItemModel.LAYER_LOCATION) { WandItemModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(GrimcultKnightModel.LAYER_LOCATION) { GrimcultKnightModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(GrimcultArcherModel.LAYER_LOCATION) { GrimcultArcherModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(GrimcultClericModel.LAYER_LOCATION) { GrimcultClericModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(GrimcultJesterModel.LAYER_LOCATION) { GrimcultJesterModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(GrimcultHeavyKnightModel.LAYER_LOCATION) { GrimcultHeavyKnightModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(GrimcultNecromancerModel.LAYER_LOCATION) { GrimcultNecromancerModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(GrimcultBookModel.LAYER_LOCATION) { GrimcultBookModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(EnchanterGrimcultBookModel.LAYER_LOCATION) { EnchanterGrimcultBookModel.createBodyLayer() }

        EntityModelLayerRegistry.registerModelLayer(ObeliskCoreModel.LAYER_LOCATION) { ObeliskCoreModel.createBodyLayer() }
        EntityModelLayerRegistry.registerModelLayer(ObeliskModel.LAYER_LOCATION) { ObeliskModel.createBodyLayer() }

        EntityModelLayerRegistry.registerModelLayer(IchoriumCircletModel.LAYER_LOCATION) { IchoriumCircletModel.createBodyLayer() }

    }
}