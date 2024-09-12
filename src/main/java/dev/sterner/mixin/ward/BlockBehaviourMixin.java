package dev.sterner.mixin.ward;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.api.util.VoidBoundPlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @ModifyReturnValue(method = "getDestroyProgress", at = @At("RETURN"))
    private float voidbound$ward(float original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) Player player, @Local(argsOnly = true) BlockGetter level, @Local(argsOnly = true) BlockPos pos) {
        if (!VoidBoundPlayerUtils.INSTANCE.canPlayerBreakBlock(player.level(), player, pos)) {
            return 0f;
        }
        return original;
    }
}
