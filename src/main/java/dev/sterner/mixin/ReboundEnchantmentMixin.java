package dev.sterner.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.sammy.malum.common.enchantment.ReboundEnchantment;
import dev.sterner.common.item.tool.ichor.IchoriumScytheItem;
import dev.sterner.mixin_logic.ReboundEnchantmentMixinLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReboundEnchantment.class)
public class ReboundEnchantmentMixin {

    @Inject(method = "onRightClickItem", at = @At(value = "INVOKE", target = "Lcom/sammy/malum/common/entity/boomerang/ScytheBoomerangEntity;shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V"))
    private static void voidbound$onRightClick(ServerPlayer player, InteractionHand interactionHand, ItemStack stack, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) float baseDamage, @Local(ordinal = 1) float magicDamage) {

        if (stack.getItem() instanceof IchoriumScytheItem) {
            ReboundEnchantmentMixinLogic.INSTANCE.logic(player, stack, baseDamage, magicDamage, 25);
            ReboundEnchantmentMixinLogic.INSTANCE.logic(player, stack, baseDamage, magicDamage, -25);
        }
    }
}
