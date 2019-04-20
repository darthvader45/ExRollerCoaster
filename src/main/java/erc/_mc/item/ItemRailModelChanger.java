package erc._mc.item;

import erc._mc.block.rail.BlockRail;
import erc._mc.tileentity.TileEntityRail;
import erc.renderer.rail.BlockModelRailRenderer;
import mochisystems.blockcopier.IItemModelRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemRailModelChanger extends Item implements IItemModelRegistry {

	public ItemRailModelChanger(){}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		if(!stack.hasTagCompound()) return true;
		if(!BlockRail.isBlockRail(world.getBlock(x, y, z))) return true;

		TileEntityRail tile = (TileEntityRail) world.getTileEntity(x, y, z);
		NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().getTag("model");


        tile.ChangeModel(new BlockModelRailRenderer(tile.getRail(), world, nbt, x, y, z));
        return true;
	}

}
