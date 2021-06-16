package me.uquark.mineconomy;

import me.uquark.mineconomy.block.Blocks;
import me.uquark.mineconomy.command.PayCommand;
import me.uquark.mineconomy.item.Items;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;

public class Mineconomy implements ModInitializer {
    public static final String modid = "mineconomy";

    @Override
    public void onInitialize() {
        Items.GALLEON_ITEM.register();
        Items.SICKLE_ITEM.register();
        Items.KNUT_ITEM.register();

        Blocks.LOWERING_COIN_EXCHANGER_BLOCK.register();
        Blocks.RAISING_COIN_EXCHANGER_BLOCK.register();

        CommandRegistry.INSTANCE.register(false, dispatcher -> {
            PayCommand.register(dispatcher);
        });
    }
}
