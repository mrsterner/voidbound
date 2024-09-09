package dev.sterner.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.registry.VoidBoundBlockRegistry;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import team.lodestar.lodestone.registry.common.LodestoneBlockEntityRegistry;
import team.lodestar.lodestone.systems.multiblock.ILodestoneMultiblockComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is so sad, lodestones multiblock api is on life support on fabric due to loading order
 * MALDING
 */
@Mixin(LodestoneBlockEntityRegistry.class)
public class LodestoneBlockEntityRegistryMixin {

    @ModifyReturnValue(method = "getBlocks", at = @At("RETURN"))
    private static Block[] getBlocks(Block[] original, @Local(argsOnly = true) Class<?>... blockClasses) {

        List<Block> modifiedList = new ArrayList<>(Arrays.asList(original));

        if (containsMyClassInstance2(ILodestoneMultiblockComponent.class, blockClasses)) {
            modifiedList.add(VoidBoundBlockRegistry.INSTANCE.getELDRITCH_OBELISK_COMPONENT().get());
        }

        return modifiedList.toArray(new Block[0]);
    }

    @Unique
    private static boolean containsMyClassInstance2(Class<?> interfaced, Class<?>... blockClasses) {
        for (Class<?> blockClass : blockClasses) {
            // Check if interfaced is assignable from blockClass
            if (interfaced.isAssignableFrom(blockClass)) {
                return true; // Found a class that is or extends interfaced
            }
        }
        return false; // None of the classes matched
    }
}
