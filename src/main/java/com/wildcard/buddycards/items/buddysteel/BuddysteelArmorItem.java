package com.wildcard.buddycards.items.buddysteel;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.wildcard.buddycards.BuddyCards;
import com.wildcard.buddycards.client.PerfectBuddysteelArmorLayers;
import com.wildcard.buddycards.client.models.PerfectBuddysteelArmorModel;
import com.wildcard.buddycards.util.BuddysteelGearHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class BuddysteelArmorItem extends ArmorItem {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    private static final int[][] DAMAGE_REDUCTION_ARRAY = new int[][] {{1, 4, 5, 2}, {2, 5, 6, 2}, {3, 6, 8, 3}, {4, 7, 9, 4}, {5, 8, 10, 5}};

    public BuddysteelArmorItem(ArmorMaterial materialIn, EquipmentSlot slot) {
        super(materialIn, slot, new Item.Properties().tab(BuddyCards.TAB));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        BuddysteelGearHelper.addInformation(stack, tooltip);
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return material.equals(BuddysteelArmorMaterial.BUDDYSTEEL) ? Rarity.UNCOMMON : Rarity.EPIC;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        BuddysteelGearHelper.setTag(playerIn, handIn);
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == this.getSlot() && stack.hasTag()) {
            multimap = LinkedHashMultimap.create();
            UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
            float ratio = 0;
            if(stack.getTag().contains("completion"))
                ratio = stack.getTag().getFloat("completion");
            int perfect = material.equals(BuddysteelArmorMaterial.BUDDYSTEEL) ? 0 : 1;
            multimap.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", DAMAGE_REDUCTION_ARRAY[(int) (3 * ratio) + perfect][slot.getIndex()], AttributeModifier.Operation.ADDITION));
            multimap.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (int) (ratio * 2) + perfect, AttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        //Add an alternative with max power for creative menu
        if (this.allowdedIn(group)) {
            ItemStack maxed = new ItemStack(this);
            CompoundTag nbt = new CompoundTag();
            nbt.putFloat("completion", 1);
            maxed.setTag(nbt);
            items.add(new ItemStack(this));
            items.add(maxed);
        }
    }

    public static final Render renderStuff = new Render();

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(renderStuff);
    }

    private static final class Render implements IItemRenderProperties {
        @Override
        @OnlyIn(Dist.CLIENT)
        public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
            if (!((ArmorItem)itemStack.getItem()).getMaterial().equals(BuddysteelArmorMaterial.BUDDYSTEEL))
                return (A) PerfectBuddysteelArmorLayers.getArmor(armorSlot);
            return IItemRenderProperties.super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
        }
    }
}
