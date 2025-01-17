package com.wildcard.buddycards.items;

import com.wildcard.buddycards.BuddyCards;
import com.wildcard.buddycards.container.BinderContainer;
import com.wildcard.buddycards.inventory.BinderInventory;
import com.wildcard.buddycards.util.EnderBinderSaveData;
import com.wildcard.buddycards.util.RegistryHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.HashMap;
import java.util.UUID;

public class EnderBinderItem extends Item{

    public EnderBinderItem() {
        super(new Item.Properties().tab(BuddyCards.TAB).stacksTo(1));
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        if(playerIn instanceof ServerPlayerEntity) {
            //Open the GUI on server side
            NetworkHooks.openGui((ServerPlayerEntity) playerIn, new SimpleNamedContainerProvider(
                    (id, playerInventory, entity) -> new BinderContainer(id, playerIn.inventory,
                            EnderBinderSaveData.get(((ServerPlayerEntity) playerIn).getLevel()).getOrMakeEnderBinder(playerIn.getUUID()))
                    , playerIn.getItemInHand(handIn).getHoverName()));
        }
        return ActionResult.success(playerIn.getItemInHand(handIn));
    }
}
