package me.uquark.mineconomy;

import me.uquark.mineconomy.block.Blocks;
import me.uquark.mineconomy.block.TradingPostBlock;
import me.uquark.mineconomy.block.entity.TradingPostBlockEntity;
import me.uquark.mineconomy.gui.TradingPostScreenHandler;
import me.uquark.mineconomy.item.Items;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.registry.Registry;

public class Mineconomy implements ModInitializer {
    public static final String modid = "mineconomy";

    public static BlockEntityType<TradingPostBlockEntity> TRADING_POST_BLOCK_ENTITY_TYPE;
    static {
        TRADING_POST_BLOCK_ENTITY_TYPE = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                TradingPostBlock.id,
                FabricBlockEntityTypeBuilder.create(TradingPostBlockEntity::new, Blocks.TRADING_POST_BLOCK).build()
        );
    }

    public static ScreenHandlerType<TradingPostScreenHandler> TRADING_POST_SCREEN_HANDLER_TYPE;
    static {
        TRADING_POST_SCREEN_HANDLER_TYPE = new ScreenHandlerType<>(TradingPostScreenHandler::new);
        Registry.register(Registry.SCREEN_HANDLER, TradingPostBlock.id, TRADING_POST_SCREEN_HANDLER_TYPE);
    }

    @Override
    public void onInitialize() {
        Items.GALLEON_ITEM.register();
        Items.SICKLE_ITEM.register();
        Items.KNUT_ITEM.register();

        Blocks.LOWERING_COIN_EXCHANGER_BLOCK.register();
        Blocks.RAISING_COIN_EXCHANGER_BLOCK.register();
        Blocks.TRADING_POST_BLOCK.register();

        TradingPostScreenHandler.registerNetworkingServer();
    }
}
