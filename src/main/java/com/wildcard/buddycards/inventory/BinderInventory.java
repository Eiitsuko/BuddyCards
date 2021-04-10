package com.wildcard.buddycards.inventory;

import com.wildcard.buddycards.items.BinderItem;
import com.wildcard.buddycards.items.CardItem;
import com.wildcard.buddycards.util.ConfigManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class BinderInventory extends Inventory {
    public BinderInventory(int slots, ItemStack binderIn) {
        super(slots);
        binder = binderIn;
    }

    public BinderInventory(int slots, boolean isEnder) {
        super(slots);
        ender = isEnder;
    }

    public ItemStack binder;
    public boolean ender;

    @Override
    public void openInventory(PlayerEntity player)
    {
        //Set all slots in the binder as empty by default
        for(int i = 0; i < this.getSizeInventory(); i++) {
            setInventorySlotContents(i, ItemStack.EMPTY);
        }

        if(binder.hasTag())
        {
            //If the binder has nbt data, turn it into items
            CompoundNBT nbt = binder.getTag();
            ListNBT list = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
            for(int i = 0; i < list.size(); i++) {
                CompoundNBT compoundnbt = list.getCompound(i);
                int k = compoundnbt.getByte("Slot") & 255;
                if (k >= 0 && k < this.getSizeInventory()) {
                    this.setInventorySlotContents(k, ItemStack.read(compoundnbt));
                }
            }
        }
    }

    @Override
    public void closeInventory(PlayerEntity player)
    {
        boolean calcPoints = ConfigManager.challengeMode.get();
        int points = 0;

        if(!binder.isEmpty())
        {
            //When the binder has cards in it, turn them into nbt data and put them in the binder
            CompoundNBT nbt;
            if(binder.hasTag())
                nbt = binder.getTag();
            else
                nbt = new CompoundNBT();
            ListNBT list = new ListNBT();
            for(int i = 0; i < this.getSizeInventory(); i++) {
                ItemStack itemstack = this.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    CompoundNBT compoundnbt = new CompoundNBT();
                    if (calcPoints)
                        points += ((CardItem)itemstack.getItem()).getPointValue(itemstack) * itemstack.getCount();
                    compoundnbt.putByte("Slot", (byte)i);
                    itemstack.write(compoundnbt);
                    list.add(compoundnbt);
                }
            }
            if (calcPoints)
                nbt.putInt("points", points);
            nbt.put("Items", list);
            binder.setTag(nbt);
        }
    }
}
