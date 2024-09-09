package dev.sterner.client.model

import dev.sterner.common.entity.AbstractCultistEntity
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelPart

abstract class AbstractGrimcultModel<T : AbstractCultistEntity>(root: ModelPart) : HumanoidModel<T>(root)