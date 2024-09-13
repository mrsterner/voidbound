package dev.sterner.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.sammy.malum.MalumMod;
import com.sammy.malum.core.handlers.SoulWardHandler;
import dev.sterner.VoidBound;
import dev.sterner.registry.VoidBoundItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoulWardHandler.ClientOnly.class)
public class SoulWardHandlerMixin {

    @ModifyReturnValue(method = "getSoulWardTexture", at = @At("RETURN"))
    private static ResourceLocation voidbound$modSoulWardTexture(ResourceLocation original){
        if (Minecraft.getInstance().player != null) {
            boolean bl = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.HEAD).is(VoidBoundItemRegistry.INSTANCE.getICHORIUM_CROWN().get());
            if (bl) {
                return VoidBound.INSTANCE.id("textures/gui/soul_ward/ichorium.png");
            }
        }
        return original;
    }
}
