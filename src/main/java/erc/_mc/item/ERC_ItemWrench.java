package erc._mc.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._core.ERC_Core;
import erc._mc.block.rail.BlockRail;
import erc.manager.NotifyConnecttionRailManager;
import erc.network.ERC_MessageConnectRailCtS;
import erc.network.ERC_MessageItemWrenchSync;
import erc.network.ERC_PacketHandler;
import erc._mc.tileentity.TileEntityRail;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ERC_ItemWrench extends Item {

	int mode = 0;
	static final int modenum = 2;
	final String ModeStr[] = {	"Connection mode", 
								"Adjustment mode"};
	final String texStr[] = {	"wrench_c1", 
								"wrench_c2",
								"wrench_e1",
								"wrench_e2"};
	
	protected IIcon itemIcons[] = new IIcon[texStr.length];
	protected IIcon temIcon = itemIcons[0];
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if(player.isSneaking())
		{
			if(world.isRemote) 
			{	//client
				// ���[�h�ύX�̓N����
				mode = (++mode)%modenum;
				NotifyConnecttionRailManager.ResetData(); // ���[�h�`�F���W�ŋL������
				player.addChatComponentMessage(new ChatComponentText(ModeStr[mode]));
			}
			return itemstack;
		}
		return super.onItemRightClick(itemstack, world, player);
	}
	
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, Block block)
    {
		Block convblock = world.getBlock(x, y, z);
		if( (convblock != Blocks.air) && (convblock != Blocks.water) && (convblock != Blocks.flowing_water) )return false;
		
    	if (!world.setBlock(x, y, z, block, 0, 3))
    	{
    		return false;
    	}
//    	ERC_Logger.info("place block");
    	if (world.getBlock(x, y, z) == block)
    	{
    		block.onBlockPlacedBy(world, x, y, z, player, stack);
    		block.onPostBlockPlaced(world, x, y, z, 0);
    		world.markBlockForUpdate(x, y, z);
    	}
    	return true;
    }
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		if(player.isSneaking()) return false;
		
		// func_150051...�֐��ւ̓��͂̌^��BlockRailTest�Ȃ�true
    	if (BlockRail.isBlockRail(world.getBlock(x, y, z)))
    	{
    		if(world.isRemote) // �N���C�A���g
    		{
    			switch(mode)
        		{
        		case 0 : 
        			//�N���C�A���g�̓��[����������Γo�^�A����΃T�[�o�[�ɐڑ��p�p�P�b�g���M
        			if(!NotifyConnecttionRailManager.isSavedPrevRail())NotifyConnecttionRailManager.SetPrevRailPosConnectedDestroyBlock(x, y, z);
        			else{
        				ERC_MessageConnectRailCtS packet 
        					= new ERC_MessageConnectRailCtS(
        							NotifyConnecttionRailManager.savedPrevX, NotifyConnecttionRailManager.savedPrevY, NotifyConnecttionRailManager.savedPrevZ,
        							x, y, z
							);
        				ERC_PacketHandler.INSTANCE.sendToServer(packet);
//        				ERC_Logger.info("connection : "+"."+ERC_CoasterAndRailManager.prevX+"."+ERC_CoasterAndRailManager.prevY+"."+ERC_CoasterAndRailManager.prevZ
//    	        				+" -> "+CorePosX+"."+y+"."+z);
        				NotifyConnecttionRailManager.ResetData();
        			}
        			break;
        		case 1 : 
        			TileEntityRail rail =  ((TileEntityRail)world.getTileEntity(x, y, z));
        			NotifyConnecttionRailManager.SaveRailForOpenGUI(rail);
//        			ERC_Logger.warn("save rail to manager : "+tile.getRail().getClass().getName());
        			break;
        		}
    			
			    ERC_PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(mode,x,y,z));
        		return false;
    		}
    		
    		return true;
    	}
        return false;
	}

	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		if (BlockRail.isBlockRail(world.getBlock(x, y, z)))
			return true;
		return false;
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister p_94581_1_)
    {
    	for(int i=0;i<texStr.length;++i)
    	{
    		this.itemIcons[i] = p_94581_1_.registerIcon(ERC_Core.MODID+":"+texStr[i]);
    	}
    	temIcon = itemIcons[0];
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int p_77617_1_) 
	{
		return ERCwrench_getIcon();
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) 
	{
		return ERCwrench_getIcon();
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) 
	{
		return ERCwrench_getIcon();
	}
    
    private IIcon ERCwrench_getIcon()
    {
    	int iconid=0;
    	switch(mode)
    	{
    	case 0:/*connect*/ 	iconid = NotifyConnecttionRailManager.isPlacedRail() ? 1 : 0; break;
    	case 1:/*adjust*/	iconid = NotifyConnecttionRailManager.isPlacedRail() ? 3 : 2; break;
    	}
    	
		return this.itemIcons[iconid];
    }
}
