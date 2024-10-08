package dev.sterner.common

import com.google.common.base.Supplier
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.VoidBound
import dev.sterner.api.item.ItemAbility
import dev.sterner.api.util.VoidBoundItemUtils
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier
import io.github.fabricators_of_create.porting_lib.loot.LootModifier
import io.github.fabricators_of_create.porting_lib.loot.PortingLibLoot
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition


class VoidBoundLootModifier(conditionsIn: Array<out LootItemCondition>?) : LootModifier(conditionsIn) {

    override fun codec(): Codec<out IGlobalLootModifier> {
        return CODEC.get()
    }

    override fun doApply(
        generatedLoot: ObjectArrayList<ItemStack>?,
        context: LootContext?
    ): ObjectArrayList<ItemStack> {

        val stack = context?.getParamOrNull(LootContextParams.TOOL)
        if (stack != null && VoidBoundItemUtils.getActiveAbility(stack) == ItemAbility.SCORCHING_HEAT) {

            val level = context.level
            val smeltedItems = generatedLoot?.asSequence()?.map { originalStack ->
                val inventory = SimpleContainer(originalStack)
                val smeltingRecipe = level.recipeManager.getRecipeFor(RecipeType.SMELTING, inventory, level)

                if (smeltingRecipe.isPresent) {
                    val result = smeltingRecipe.get().assemble(inventory, level.registryAccess())
                    if (result.count > 0) {
                        result.count *= originalStack.count
                    }
                    result
                } else {
                    originalStack
                }
            }?.filter { !it.isEmpty }?.toList() ?: emptyList()

            return ObjectArrayList(smeltedItems)
        }

        return generatedLoot ?: ObjectArrayList()
    }

    companion object {

        val MODIFIERS = LazyRegistrar.create(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS_KEY, VoidBound.modid)

        val CODEC: Supplier<Codec<VoidBoundLootModifier>> = Supplier {
            RecordCodecBuilder.create { inst ->
                codecStart(inst).apply(inst, ::VoidBoundLootModifier)
            }
        }

        val AUTOSMELT = MODIFIERS.register("autosmelt", CODEC)
    }
}