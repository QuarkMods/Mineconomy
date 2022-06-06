package me.uquark.mineconomy.block.entity;

import me.uquark.mineconomy.Mineconomy;
import me.uquark.mineconomy.gui.TradingPostScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TradingPostBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    private final SimpleInventory[] inventories = new SimpleInventory[]{
            new SimpleInventory(24),
            new SimpleInventory(24)
    };
    public final DoubleInventory fullInventory = new DoubleInventory(inventories[0], inventories[1]);
    private final DoubleInventory[] views = new DoubleInventory[]{
            new DoubleInventory(inventories[0], inventories[1]),
            new DoubleInventory(inventories[1], inventories[0])
    };

    private int countdown;

    public class Session {
        public PlayerEntity user;
        public boolean ready;
        public int syncId;
        public DoubleInventory view;

        public Session(PlayerEntity user, int syncId) {
            this.user = user;
            this.syncId = syncId;
            this.ready = false;

            if (sessions.size() == 0)
                view = views[0];
            if (sessions.size() == 1) {
                if (sessions.get(0).view == views[0])
                    view = views[1];
                if (sessions.get(0).view == views[1])
                    view = views[0];
            }
        }
    }

    private final ArrayList<Session> sessions = new ArrayList<>();

    public Session getOtherSession(PlayerEntity user) {
        if (sessions.size() < 2)
            return null;
        if (sessions.get(0).user == user)
            return sessions.get(1);
        if (sessions.get(1).user == user)
            return sessions.get(0);
        return null;
    }

    public void dropSession(PlayerEntity user) {
        for (int i = 0; i < sessions.size(); i++)
            if (sessions.get(i).user == user) {
                sessions.remove(i);
                return;
            }
    }

    private Session getUserSession(PlayerEntity user) {
        for (Session session : sessions)
            if (session.user == user)
                return session;
        return null;
    }

    private void runTrade() {
        if (sessions.size() < 2)
            return;
        countdown = 4;
        Timer ticker = new Timer();
        ticker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!sessions.get(0).ready || !sessions.get(1).ready) {
                    ticker.cancel();
                    return;
                }
                countdown--;
                if (countdown == 0) {
                    swap();
                }
                for (Session session : sessions) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(session.syncId);
                    buf.writeInt(countdown);
                    ServerPlayNetworking.send((ServerPlayerEntity) session.user, TradingPostScreenHandler.TRADE_COUNTDOWN_PACKET_ID, buf);
                }
            }
        }, 0, 1000);
    }

    public void onUserReady(PlayerEntity user) {
        Session session = getUserSession(user);
        Session otherSession = getOtherSession(user);
        if (session == null)
            return;
        session.ready = true;
        if (otherSession != null && otherSession.ready)
            runTrade();
    }

    public void onUserNotReady(PlayerEntity user) {
        Session session = getUserSession(user);
        if (session == null)
            return;
        session.ready = false;
    }

    public TradingPostBlockEntity(BlockPos pos, BlockState state) {
        super(Mineconomy.TRADING_POST_BLOCK_ENTITY_TYPE, pos, state);
    }

    private void swap() {
        ItemStack[] firstItems = new ItemStack[24];
        ItemStack[] secondItems = new ItemStack[24];
        for (int i = 0; i < 24; i++) {
            firstItems[i] = inventories[0].getStack(i);
        }
        for (int i = 24; i < 48; i++) {
            secondItems[i-24] = inventories[1].getStack(i-24);
        }
        for (int i = 0; i < 24; i++) {
            inventories[0].setStack(i, secondItems[i]);
        }
        for (int i = 24; i < 48; i++) {
            inventories[1].setStack(i-24, firstItems[i-24]);
        }
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (sessions.size() >= 2)
            return null;
        Session session = new Session(player, syncId);
        sessions.add(session);
        return new TradingPostScreenHandler(syncId, this, inv, session.view);
    }

    public boolean canUse(PlayerEntity user) {
        if (sessions.size() < 2)
            return true;
        boolean hasSession = false;
        for (Session session : sessions)
            if (session.user == user) {
                hasSession = true;
                break;
            }
        return hasSession;
    }
}
