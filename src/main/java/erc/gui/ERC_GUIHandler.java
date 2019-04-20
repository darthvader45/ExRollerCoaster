
package erc.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import erc._core.ERC_Core;
import erc._mc.tileentity.TileEntityRailModelConstructor;
import erc.gui.container.ContainerRailModelConstructor;
import erc.gui.container.DefContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


public class ERC_GUIHandler implements IGuiHandler {
		
	/*�T�[�o�[���̏���*/
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID)
        {
            case ERC_Core.GUIID_RailBase :
                return new DefContainer(x, y, z, null);
            case ERC_Core.GUIID_RailModelConstructor :
                return new ContainerRailModelConstructor(player.inventory, (TileEntityRailModelConstructor) world.getTileEntity(x, y, z));

        }
        return null;
    }
    
    /*�N���C�A���g���̏���*/
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID)
        {
            case ERC_Core.GUIID_RailBase :
                return new GUIRail(x, y, z);
            case ERC_Core.GUIID_RailModelConstructor :
                return new GUIRailModelConstructor(x, y, z, player.inventory, (TileEntityRailModelConstructor) world.getTileEntity(x, y, z));
        }
        return null;
    }
}
