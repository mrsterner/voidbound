package dev.sterner.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.sterner.common.item.equipment.UpgradableTool;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getItem();

    @ModifyReturnValue(method = "fireImmune", at = @At("RETURN"))
    private boolean fireImmune(boolean original) {
        if (getItem().getItem() instanceof UpgradableTool tool) {
            return tool.isFireproof(getItem());
        }
        return original;
    }
}
