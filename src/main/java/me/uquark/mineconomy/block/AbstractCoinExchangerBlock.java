package me.uquark.mineconomy.block;

import me.uquark.mineconomy.Mineconomy;
import me.uquark.mineconomy.item.GalleonItem;
import me.uquark.mineconomy.item.Items;
import me.uquark.mineconomy.item.KnutItem;
import me.uquark.mineconomy.item.SickleItem;
import me.uquark.quarkcore.block.AbstractHorizontalFacingBlock;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractCoinExchangerBlock extends AbstractHorizontalFacingBlock {
    protected AbstractCoinExchangerBlock(String name) {
        super(
            Mineconomy.modid,
            name,
            FabricBlockSettings.of(Material.METAL).strength(5, 6),
            new Item.Settings().group(ItemGroup.MISC).maxCount(64)
        );
    }

    protected void packMoney(PlayerEntity player) {
        int galleons = 0, sickles = 0, knuts = 0;
        for (int i = 0; i <= 45; i++) {
            ItemStack itemStack = player.getInventory().getStack(i);
            if (player.getMainHandStack() == itemStack)
                continue;
            if (itemStack.getItem() instanceof GalleonItem) {
                galleons += itemStack.getCount();
                itemStack.decrement(itemStack.getCount());
            }
            if (itemStack.getItem() instanceof SickleItem) {
                sickles += itemStack.getCount();
                itemStack.decrement(itemStack.getCount());
            }
            if (itemStack.getItem() instanceof KnutItem) {
                knuts += itemStack.getCount();
                itemStack.decrement(itemStack.getCount());
            }
        }
        player.giveItemStack(new ItemStack(Items.GALLEON_ITEM, galleons));
        player.giveItemStack(new ItemStack(Items.SICKLE_ITEM, sickles));
        player.giveItemStack(new ItemStack(Items.KNUT_ITEM, knuts));
    }

    protected abstract boolean exchange(ItemStack stack, PlayerEntity player);

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (exchange(player.getMainHandStack(), player))
            return ActionResult.SUCCESS;
        else
            return ActionResult.FAIL;
    }
}
