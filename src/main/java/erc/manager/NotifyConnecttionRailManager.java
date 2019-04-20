package erc.manager;

import erc.coaster.Seat;
import erc._mc.entity.EntityCoaster;
import erc.rail.IRailController;
import mochisystems.math.Vec3d;
import erc._mc.tileentity.TileEntityRail;
import erc.rail.IRail;

import erc._core.ERC_Logger;
import erc._mc.entity.EntityCoasterSeat;
import erc.network.ERC_MessageConnectRailCtS;
import erc.network.ERC_PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class NotifyConnecttionRailManager {

	enum Connect{
		
	}
//	public static ERC_TileEntityRailTest prevTileRail;
	public static int savedPrevX = -1;
	public static int savedPrevY = -1;
	public static int savedPrevZ = -1;
	public static int savedNextX = -1;
	public static int savedNextY = -1;
	public static int savedNextZ = -1;
	// �R�[�X�^�[�ݒu�ʒu���N���C�A���g���ɒm�点��p
	public static int coastersetX = -1;
	public static int coastersetY = -1;
	public static int coastersetZ = -1;
	// �A���R�[�X�^�[�p�@�e�R�[�X�^�[ID
	private static int parentCoasterID = -1;
	// ���f���I��p�@���f��ID
	public static int saveModelID = -1;
	// ��Ԓ����_�ړ���

    // �J�����p�CorePosX����
//    public static float rotationYaw = 0;
//	public static float prevRotationYaw = 0;
//	public static float rotationPitch = 0;
//	public static float prevRotationPitch = 0;
//    public static float rotationRoll = 0f;
//    public static float prevRotationRoll = 0f;
	
	public static TileEntityRail clickedTileForGUI;
	
	public NotifyConnecttionRailManager()
	{
		ResetData();
		clickedTileForGUI = null;
	}

	public static void SetPrevRailPosConnectedDestroyBlock(int x, int y, int z)
	{
		savedPrevX = x;
		savedPrevY = y;
		savedPrevZ = z;
	}
	public static void SetPrevRailPosConnectedDestroyBlock(IRail rail)
	{
		if(rail != null)
		{
			IRailController controller = rail.GetController();
			savedPrevX = controller.x();
			savedPrevY = controller.y();
			savedPrevZ = controller.z();
		}
		else savedPrevX = savedPrevY = savedPrevZ = -1;
	}

	public static void SetNextRailPosConnectedDestroyBlock(int x, int y, int z)
	{
		savedNextX = x;
		savedNextY = y;
		savedNextZ = z;
	}
	public static void SetNextRailPosConnectedDestroyBlock(IRail rail)
	{
		if(rail != null)
		{
			IRailController controller = rail.GetController();
			savedNextX = controller.x();
			savedNextY = controller.y();
			savedNextZ = controller.z();
		}
		else savedNextX = savedNextY = savedNextZ = -1;
	}

	public static void ResetData()
	{
		savedPrevX = -1;
		savedPrevY = -1;
		savedPrevZ = -1;
		savedNextX = -1;
		savedNextY = -1;
		savedNextZ = -1;
	}
	
	public static boolean isPlacedRail()
	{
		return isSavedPrevRail() || isSavedNextRail();
	}
	
	public static boolean isSavedPrevRail()
	{
		return savedPrevY > -1;
	}
	public static boolean isSavedNextRail()
	{
		return savedNextY > -1;
	}
	
	public static void NotifyConnectPrevRail(int x, int y, int z)
	{
		ERC_MessageConnectRailCtS packet
			= new ERC_MessageConnectRailCtS(
					savedPrevX, savedPrevY, savedPrevZ,
					x, y, z
					);
		ERC_PacketHandler.INSTANCE.sendToServer(packet);
	}
	
	public static void NotifyconnectNextRail(int x, int y, int z)
	{
		ERC_MessageConnectRailCtS packet 
			= new ERC_MessageConnectRailCtS(
				x, y, z,
				savedNextX, savedNextY, savedNextZ
				);
		ERC_PacketHandler.INSTANCE.sendToServer(packet);

		ResetData();
	}
	
	public static TileEntityRail GetPrevTileEntity(World world)
	{
		return ((TileEntityRail)world.getTileEntity(savedPrevX, savedPrevY, savedPrevZ));
	}
	public static TileEntityRail GetNextTileEntity(World world)
	{
		return ((TileEntityRail)world.getTileEntity(savedNextX, savedNextY, savedNextZ));
	}
	
	public static void SaveRailForOpenGUI(TileEntityRail tl)
	{
		clickedTileForGUI = tl;
	}
	public static void CloseRailGUI()
	{
		clickedTileForGUI = null;
	}
	
	public static void SetCoasterPos(int x, int y, int z)
	{
		coastersetX = x;
		coastersetY = y;
		coastersetZ = z;
	}
	
	public static void client_setParentCoaster(EntityCoaster parent)
	{
		parentCoasterID = parent.getEntityId();
	}
	
	public static EntityCoaster client_getParentCoaster(World world)
	{
		EntityCoaster ret = (EntityCoaster) world.getEntityByID(parentCoasterID);
		parentCoasterID = -1;
		return ret;
	}
	
//	@SideOnly(Side.CLIENT)

    
//    public static void setRots(float y, float py, float p, float pp, float r, float pr)
//    {
//    	rotationYaw = y;
//    	prevRotationYaw = py;
//    	rotationPitch = p;
//    	prevRotationPitch = pp;
//    	rotationRoll = r;
//    	prevRotationRoll = pr;
//    }



    static Vec3d dir;
    static double speed;
    static EntityPlayer player;
    public static void GetOffAndButtobi(EntityPlayer Player)
    {
    	if(/*!Player.worldObj.isRemote &&*/ Player.isSneaking())
    	{
    		if(Player.ridingEntity instanceof EntityCoasterSeat)
    		{
    			Seat seat = ((EntityCoasterSeat)Player.ridingEntity).GetSeat();
    			dir = seat.GetParent().AttitudeMatrix.Dir();
    			player = Player;
    			speed = seat.GetParent().getSpeed();
    			//Player.motionX += seat.parent.Speed * dir.xCoord * 1;
    			//Player.motionY += seat.parent.Speed * dir.yCoord * 1;
    			//Player.motionZ += seat.parent.Speed * dir.zCoord * 1;
    			ERC_Logger.debugInfo("NotifyRailConnectionMgr : " + dir.toString());
    		}
    	}
    }
    public static void motionactive()
    {
    	player.motionX += speed * dir.x * 1;
    	player.motionY += speed * dir.y * 1;
		player.motionZ += speed * dir.z * 1;
    }
}

