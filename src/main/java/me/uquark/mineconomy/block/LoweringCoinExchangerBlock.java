package me.uquark.mineconomy.block;

import me.uquark.mineconomy.item.GalleonItem;
import me.uquark.mineconomy.item.Items;
import me.uquark.mineconomy.item.KnutItem;
import me.uquark.mineconomy.item.SickleItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class LoweringCoinExchangerBlock extends AbstractCoinExchangerBlock {
    public LoweringCoinExchangerBlock() {
        super("lowering_coin_exchanger");
    }

    @Override
    protected boolean exchange(ItemStack stack, PlayerEntity player) {
        packMoney(player);
        if (stack.getItem() instanceof KnutItem)
            return false;
        if (stack.getItem() instanceof SickleItem) {
            stack.decrement(1);
            player.giveItemStack(new ItemStack(Items.KNUT_ITEM, 10));
            return true;
        }
        if (stack.getItem() instanceof GalleonItem) {
            stack.decrement(1);
            player.giveItemStack(new ItemStack(Items.SICKLE_ITEM, 10));
            return true;
        }
        return false;
    }
}
