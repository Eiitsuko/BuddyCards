package com.wildcard.buddycards.blocks.tiles;

import com.wildcard.buddycards.items.CardItem;
import com.wildcard.buddycards.util.RegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;

import java.util.UUID;

public class CardDisplayTile extends TileEntity implements IClearable {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(6, ItemStack.EMPTY);
    private boolean locked = false;
    private String player = "";

    public CardDisplayTile() {
        super(RegistryHandler.CARD_DISPLAY_TILE.get());
    }

    public void putCardInSlot(ItemStack stack, int pos) {
        if(this.world != null) {
            this.inventory.set(pos - 1, stack);
            this.markDirty();
            this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public ItemStack getCardInSlot(int pos) {
        return this.inventory.get(pos - 1);
    }

    public boolean isLocked() {
        return this.locked;
    }

    public boolean toggleLock(UUID playerUUID) {
        if(this.locked) {
            if(this.player.equals(playerUUID.toString())) {
                this.locked = false;
            }
            else
                return false;
        }
        else {
            this.player = playerUUID.toString();
            this.locked = true;
        }
        this.markDirty();
        this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
        return true;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        ItemStackHelper.saveAllItems(compound, this.inventory, true);
        compound.putBoolean("locked", this.locked);
        compound.putString("player", this.player);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.inventory.clear();
        ItemStackHelper.loadAllItems(nbt, this.inventory);
        this.locked = nbt.getBoolean("locked");
        this.player = nbt.getString("player");
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(this.getBlockState(), pkt.getNbtCompound());
    }

    public NonNullList<ItemStack> getInventory() {
        return this.inventory;
    }

    public int getCardsAmt() {
        int amt = 0;
        for (int i = 0; i < 6; i++) {
            if (this.inventory.get(i).getItem() instanceof CardItem)
                amt++;
        }
        return amt;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }
}
