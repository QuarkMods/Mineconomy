package me.uquark.mineconomy;

import me.uquark.mineconomy.gui.BalanceHud;
import me.uquark.quarkcore.gui.HudManager;
import net.fabricmc.api.ClientModInitializer;

public class MineconomyClient implements ClientModInitializer {
    public static final BalanceHud balanceHud = new BalanceHud();

    @Override
    public void onInitializeClient() {
        HudManager.huds.add(balanceHud);
    }
}
