package me.uquark.mineconomy.gui;

import me.uquark.mineconomy.item.AbstractCoinItem;
import me.uquark.quarkcore.gui.AbstractHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class BalanceHud extends AbstractHud {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private float balance;

    public void updateBalance() {
        if (client.player != null)
            if (client.player.getInventory() != null) {
                balance = 0;
                for (int i=0; i <= 35; i++) {
                    ItemStack itemStack = client.player.getInventory().getStack(i);
                    if (itemStack != null && itemStack.getItem() instanceof AbstractCoinItem)
                        balance += ((AbstractCoinItem) itemStack.getItem()).value * itemStack.getCount();
                }
                balance = (float) (Math.round(balance * 100) / 100.0);
            }
    }

    @Override
    public void render(MatrixStack matrixStack) {
        drawStringWithShadow(
            matrixStack,
            client.textRenderer,
            "Balance: " + balance,
            5,
            5,
            0xFFFFFF
        );
    }
}
