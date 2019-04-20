package erc.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


public class slotCanInsertOnlyItem extends Slot{
	Item srcItem;
	public slotCanInsertOnlyItem(Item srcItem, IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
		this.srcItem = srcItem;
	}
	public boolean isItemValid(ItemStack p_75214_1_)
	{
		return p_75214_1_ !=null && p_75214_1_.getItem() == srcItem;
	}
}

