package me.uquark.mineconomy.gui;

import me.uquark.mineconomy.Mineconomy;
import me.uquark.mineconomy.block.entity.TradingPostBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class TradingPostScreenHandler extends ScreenHandler {
    public static final Identifier USER_READY_PACKET_ID = new Identifier(Mineconomy.modid, "user_ready_packet");
    public static final Identifier TRADE_COUNTDOWN_PACKET_ID = new Identifier(Mineconomy.modid, "trade_countdown_packet");

    private static final HashMap<Integer, TradingPostScreenHandler> SERVER_INSTANCES = new HashMap<>();
    private static final HashMap<Integer, TradingPostScreenHandler> CLIENT_INSTANCES = new HashMap<>();

    private int countdown = 3;

    private static class LockableSlot extends Slot {
        public boolean isLocked;

        public LockableSlot(Inventory inventory, int index, int x, int y, boolean locked) {
            super(inventory, index, x, y);
            isLocked = locked;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return !isLocked;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return !isLocked;
        }
    }

    public final PlayerInventory playerInventory;
    public final Inventory inventory;
    private final TradingPostBlockEntity owner;
    private boolean partnerReady = false;
    private boolean userReady = false;

    public TradingPostScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, null, playerInv, new DoubleInventory(new SimpleInventory(24), new SimpleInventory(24)));
    }

    public TradingPostScreenHandler(int syncId, TradingPostBlockEntity owner, PlayerInventory playerInv, Inventory inv) {
        super(Mineconomy.TRADING_POST_SCREEN_HANDLER_TYPE, syncId);
        playerInventory = playerInv;
        inventory = inv;
        this.owner = owner;
        int i;
        int j;
        for(i = 0; i < 6; ++i) {
            for(j = 0; j < 4; ++j) {
                this.addSlot(new LockableSlot(inventory, j + i * 4, 8 + j * 18, 18 + i * 18, false));
            }
        }

        for(i = 0; i < 6; ++i) {
            for(j = 0; j < 4; ++j) {
                this.addSlot(new LockableSlot(inventory, 24 + j + i * 4, 98 + j * 18, 18 + i * 18, true));
            }
        }

        for(i = 0; i < 3; ++i) {
            for(j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 198));
        }

        if (this.owner == null) {
            CLIENT_INSTANCES.put(syncId, this);
        } else {
            SERVER_INSTANCES.put(syncId, this);
        }
    }

    private void lockPlayerSlots() {
        for (int i = 0; i < 24; i++) {
            ((LockableSlot)this.slots.get(i)).isLocked = true;
        }
    }

    private void unlockPlayerSlots() {
        for (int i = 0; i < 24; i++) {
            ((LockableSlot)this.slots.get(i)).isLocked = false;
        }
    }

    private void onPartnerReady() {
        partnerReady = true;
    }

    private void onPartnerNotReady(PlayerEntity user) {
        if (userReady)
            onUserNotReady(user);
        partnerReady = false;
    }

    public boolean isPartnerReady() {
        return partnerReady;
    }

    public boolean isUserReady() {
        return userReady;
    }

    public int getCountdown() {
        return countdown;
    }

    public void onUserReady(PlayerEntity user) {
        if (owner == null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(syncId);
            buf.writeBoolean(true);
            ClientPlayNetworking.send(USER_READY_PACKET_ID, buf);
        } else {
            owner.onUserReady(user);
        }
        lockPlayerSlots();
        userReady = true;
    }

    public void onUserNotReady(PlayerEntity user) {
        if (owner == null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(syncId);
            buf.writeBoolean(false);
            ClientPlayNetworking.send(USER_READY_PACKET_ID, buf);
        } else {
            owner.onUserNotReady(user);
        }
        unlockPlayerSlots();
        userReady = false;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        if (owner != null)
            return owner.canUse(player);
        else
            return true;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            int playerTradeSlots = this.inventory.size() / 2;
            if (invSlot < playerTradeSlots) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, playerTradeSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public void close(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            for (int i = 0; i < 24; i++) {
                ItemStack stack = inventory.getStack(i);
                if (!player.isAlive() || ((ServerPlayerEntity)player).isDisconnected()) {
                    player.dropItem(stack, false);
                } else {
                    player.getInventory().offerOrDrop(stack);
                }
            }
        }
        if (owner != null)
            owner.dropSession(player);
        SERVER_INSTANCES.remove(syncId);
        CLIENT_INSTANCES.remove(syncId);
        super.close(player);
    }

    public static void registerNetworkingServer() {
        ServerPlayNetworking.registerGlobalReceiver(USER_READY_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            int syncId = buf.readInt();
            boolean ready = buf.readBoolean();
            TradingPostScreenHandler instance = SERVER_INSTANCES.get(syncId);
            if (instance == null)
                return;
            if (ready)
                instance.onUserReady(player);
            else
                instance.onUserNotReady(player);
            TradingPostBlockEntity.Session partnerSession = instance.owner.getOtherSession(player);
            if (partnerSession == null)
                return;
            PacketByteBuf notification = PacketByteBufs.create();
            notification.writeInt(partnerSession.syncId);
            notification.writeBoolean(ready);
            ServerPlayNetworking.send((ServerPlayerEntity) partnerSession.user, USER_READY_PACKET_ID, notification);
        });
    }

    public static void registerNetworkingClient() {
        ClientPlayNetworking.registerGlobalReceiver(USER_READY_PACKET_ID, (client, handler, buf, responseSender) -> {
            int syncId = buf.readInt();
            boolean ready = buf.readBoolean();
            TradingPostScreenHandler instance = CLIENT_INSTANCES.get(syncId);
            if (instance == null)
                return;
            if (ready)
                instance.onPartnerReady();
            else
                instance.onPartnerNotReady(client.player);
        });
        ClientPlayNetworking.registerGlobalReceiver(TRADE_COUNTDOWN_PACKET_ID, (client, handler, buf, responseSender) -> {
            int syncId = buf.readInt();
            int countdown = buf.readInt();
            TradingPostScreenHandler instance = CLIENT_INSTANCES.get(syncId);
            if (instance == null)
                return;
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, countdown == 0 ? 1.0f : 0.5f));
            instance.countdown = countdown;
            if (instance.countdown == 0) {
                instance.onUserNotReady(client.player);
                instance.onPartnerNotReady(client.player);
            }
        });
    }
}
