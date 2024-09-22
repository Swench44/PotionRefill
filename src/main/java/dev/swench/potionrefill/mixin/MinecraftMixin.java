package dev.swench.potionrefill.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {MinecraftClient.class})
public class MinecraftMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void PotionRefill(CallbackInfo ci) {

        if (mc().currentScreen instanceof InventoryScreen) {
            int potionInInventory = findHealthPotion(9, 36);
            if (potionInInventory != -1 && GLFW.glfwGetMouseButton(mc().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_PRESS) {
                mc().interactionManager.clickSlot(mc().player.currentScreenHandler.syncId, potionInInventory, 1, SlotActionType.QUICK_MOVE, mc().player);
            }
        }
    }

    private int findHealthPotion(int startSlot, int endSlot) {
        int instantHealth2Slot = -1;
        int instantHealth1Slot = -1;

        for (int i = startSlot; i < endSlot; i++) {
            ItemStack stack = mc().player.getInventory().getStack(i);
            if (stack.getItem() != Items.SPLASH_POTION) continue;

            for (StatusEffectInstance effectInstance : PotionUtil.getPotionEffects(stack)) {
                if (effectInstance.getEffectType() == StatusEffects.INSTANT_HEALTH) {
                    if (effectInstance.getAmplifier() == 1) {
                        instantHealth2Slot = i;
                    } else if (effectInstance.getAmplifier() == 0) {
                        instantHealth1Slot = i;
                    }
                }
            }
        }

        if (instantHealth2Slot != -1) {
            return instantHealth2Slot;
        } else {
            return instantHealth1Slot;
        }
    }

    private MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }
}
