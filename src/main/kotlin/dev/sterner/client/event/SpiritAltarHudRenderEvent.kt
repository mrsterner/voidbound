package dev.sterner.client.event

import com.sammy.malum.common.block.curiosities.spirit_altar.AltarCraftingHelper.Ranking
import com.sammy.malum.common.block.curiosities.spirit_altar.SpiritAltarBlockEntity
import com.sammy.malum.common.recipe.SpiritInfusionRecipe
import dev.sterner.api.VoidBoundApi
import dev.sterner.api.util.VoidBoundRenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.BlockHitResult
import java.util.function.Function
import java.util.function.Predicate


object SpiritAltarHudRenderEvent {

    /**
     * Renders the required items a spirit infusion requires to complete the craft at crosshair
     */
    fun spiritAltarRecipeHud(guiGraphics: GuiGraphics, partialTick: Float) {
        val client: Minecraft = Minecraft.getInstance()

        if (VoidBoundApi.hasGoggles()) {
            if (client.level != null && client.hitResult is BlockHitResult) {
                val result = client.hitResult as BlockHitResult
                val pos: BlockPos = result.blockPos
                if (client.level!!.getBlockEntity(pos) is SpiritAltarBlockEntity) {
                    val se = client.level!!.getBlockEntity(pos) as SpiritAltarBlockEntity
                    val recipes = se.possibleRecipes

                    if (recipes.isNotEmpty()) {
                        val poseStack = guiGraphics.pose()
                        poseStack.pushPose()
                        poseStack.translate(
                            if (client.window.guiScaledWidth % 2 != 0) 0.5 else 0.0,
                            if (client.window.guiScaledHeight % 2 != 0) 0.5 else 0.0,
                            0.0
                        )
                        var startX: Int = client.window.guiScaledWidth / 2
                        var centerY: Int = client.window.guiScaledHeight / 2
                        startX -= recipes.size / 2 * 16
                        centerY += 10

                        poseStack.translate(startX.toDouble(), centerY.toDouble(), 0.0)

                        val recipe: SpiritInfusionRecipe? = se.possibleRecipes.entries.stream()
                            .filter { it: Map.Entry<SpiritInfusionRecipe?, Ranking?> -> it.value != null }
                            .max(java.util.Map.Entry.comparingByValue())
                            .map({ it.key })
                            .orElse(null) as SpiritInfusionRecipe
                        val extras = recipe?.extraItems
                        val inventory = se.extrasInventory

                        if (extras != null) {
                            for (extra in extras) {
                                val renderStack = ItemStack(extra.item, extra.getCount())

                                fun isItemInInventory(item: Item): Boolean {
                                    for (slot in 0 until inventory.slotCount) {
                                        if (inventory.getStackInSlot(slot).item == item) {
                                            return true
                                        }
                                    }
                                    return false
                                }

                                val checked = isItemInInventory(extra.item)

                                guiGraphics.renderItem(renderStack, 0, 0)
                                if (!checked) {
                                    guiGraphics.renderItemDecorations(client.font, renderStack, 0, 0)
                                } else {
                                    VoidBoundRenderUtils.drawScreenIcon(poseStack, VoidBoundRenderUtils.CHECKMARK)
                                }
                                poseStack.translate(16.0, 0.0, 0.0)
                            }
                        }


                        poseStack.popPose()
                    }
                }
            }
        }
    }
}