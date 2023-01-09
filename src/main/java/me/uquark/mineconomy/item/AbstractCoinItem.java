package me.uquark.mineconomy.item;

import me.uquark.mineconomy.Mineconomy;
import me.uquark.quarkcore.item.AbstractItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;

public abstract class AbstractCoinItem extends AbstractItem {
    public final float value;

    public AbstractCoinItem(String name, float value) {
        super(Mineconomy.modid, name, new Settings().maxCount(64));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(this));
        this.value = value;
    }
}
