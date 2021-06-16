package me.uquark.mineconomy.block;

import me.uquark.mineconomy.item.GalleonItem;
import me.uquark.mineconomy.item.Items;
import me.uquark.mineconomy.item.KnutItem;
import me.uquark.mineconomy.item.SickleItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class RaisingCoinExchangerBlock extends AbstractCoinExchangerBlock {
    public RaisingCoinExchangerBlock() {
        super("raising_coin_exchanger");
    }

    @Override
    protected boolean exchange(ItemStack stack, PlayerEntity player) {
        packMoney(player);
        if (stack.getCount() < 10)
            return false;
        if (stack.getItem() instanceof KnutItem) {
            stack.decrement(10);
            player.giveItemStack(new ItemStack(Items.SICKLE_ITEM, 1));
            return true;
        }
        if (stack.getItem() instanceof SickleItem) {
            stack.decrement(10);
            player.giveItemStack(new ItemStack(Items.GALLEON_ITEM, 1));
            return true;
        }
        if (stack.getItem() instanceof GalleonItem)
            return false;
        return false;
    }
}
