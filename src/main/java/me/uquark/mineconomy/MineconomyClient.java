package me.uquark.mineconomy;

import me.uquark.mineconomy.gui.BalanceHud;
import me.uquark.mineconomy.gui.TradingPostScreen;
import me.uquark.mineconomy.gui.TradingPostScreenHandler;
import me.uquark.quarkcore.gui.HudManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class MineconomyClient implements ClientModInitializer {
    public static final BalanceHud balanceHud = new BalanceHud();

    @Override
    public void onInitializeClient() {
        HudManager.huds.add(balanceHud);
        HandledScreens.register(Mineconomy.TRADING_POST_SCREEN_HANDLER_TYPE, TradingPostScreen::new);
        TradingPostScreenHandler.registerNetworkingClient();
    }
}
