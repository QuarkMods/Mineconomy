package me.uquark.mineconomy.mixin;

import me.uquark.mineconomy.MineconomyClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Nameable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable {
    @Inject(method = "updateItems", at = @At("HEAD"))
    public void updateItems(CallbackInfo info) {
        MineconomyClient.balanceHud.updateBalance();
    }
}
