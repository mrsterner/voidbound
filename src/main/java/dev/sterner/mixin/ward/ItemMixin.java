package dev.sterner.mixin.ward;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.api.VoidBoundApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public class ItemMixin {

    @ModifyReturnValue(method = "canAttackBlock", at = @At("RETURN"))
    private boolean voidbound$ward(boolean original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) Player player) {
        if (!VoidBoundApi.INSTANCE.canPlayerBreakBlock(level, player, pos)) {
            return false;
        }
        return original;
    }
}
