package erc._mc.tileentity;

import erc.gui.GUIRail;
import erc.manager.NotifyConnecttionRailManager;
import erc.renderer.rail.DefaultRailRenderer;
import erc.network.MessageSyncRailStC;
import erc.renderer.rail.RailRenderer;
import io.netty.buffer.ByteBuf;
import mochisystems._core.Logger;
import mochisystems.math.Vec3d;
import erc.network.ERC_PacketHandler;
import erc.rail.IRail;
import erc.rail.IRailController;
import erc.rail.Rail;
import erc.rail.AccelRail;
import erc.rail.AntiGravityRail;
import erc.rail.BranchRail;
import erc.rail.ConstVelocityRail;
import erc.rail.DetectorRail;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.ArrayList;

public abstract class TileEntityRail extends TileEntity implements IRailController {

	protected abstract IRail GetRailInstance();

	protected IRail rail;
	public IRail getRail() { return rail; }

	protected RailRenderer renderer;
    public int nx = -1, ny = -1, nz = -1;
    public int px = -1, py = -1, pz = -1;

    @Override
	public World World(){
		return worldObj;
	}

	@Override
	public int CorePosX() {
		return xCoord;
	}
	@Override
	public int CorePosY() {
		return yCoord;
	}
	@Override
	public int CorePosZ() {
		return zCoord;
	}
	@Override
	public int x() {
		return xCoord;
	}
	@Override
	public int y() {
		return yCoord;
	}
	@Override
	public int z() {
		return zCoord;
	}

	@Override
	public boolean RegisterPrevRailPos(IRailController prevController)
	{
        if(prevController == null){px = py = pz = -1; return true;}
        if(px != prevController.x()){ px = prevController.x(); return true;}
        if(py != prevController.y()){ py = prevController.y(); return true;}
        if(pz != prevController.z()){ pz = prevController.z(); return true;}
        return false;
	}
	@Override
	public boolean RegisterNextRailPos(IRailController nextController)
    {
        if(nextController == null){nx = ny = nz = -1; return true;}
        if(nx != nextController.x()){ nx = nextController.x(); return true;}
        if(ny != nextController.y()){ ny = nextController.y(); return true;}
        if(nz != nextController.z()){ nz = nextController.z(); return true;}
        return false;
    }

	@Override
	public int CoreSide()
	{
		return 0;
	}

	@Override
	public IRail GetRail(int x, int y, int z)
	{
		World world = World();
		if(world == null) return null;
		TileEntityRail tile = (TileEntityRail)world.getTileEntity(x, y, z);
		if(tile == null) return null;
		return tile.getRail();
	}
    @Override
    public boolean IsInvalid()
    {
        return isInvalid();
    }

	@Override
    public boolean IsRemote()
    {
        return worldObj.isRemote;
    }

	@Override
    public void markBlockForUpdate()
    {
        worldObj.markBlockForUpdate(CorePosX(), CorePosY(), CorePosZ());
    }

    @Override
    public void RegisterBufferForSync(ByteBuf buf)
    {
        buf.writeInt(px);
        buf.writeInt(py);
        buf.writeInt(pz);
        buf.writeInt(nx);
        buf.writeInt(ny);
        buf.writeInt(nz);
    }
    @Override
    public void ReadBufferForSync(ByteBuf buf)
    {
        px = buf.readInt();
        py = buf.readInt();
        pz = buf.readInt();
        nx = buf.readInt();
        ny = buf.readInt();
        nz = buf.readInt();
    }


    public TileEntityRail()
	{
		this.rail = GetRailInstance();
		this.rail.SetController(this);
		renderer = new DefaultRailRenderer(rail);
	}

	public void InitRail(Vec3d pos, Vec3d dir, Vec3d up)
    {
        rail.SetBasePoint(pos, dir, up, 5f);
        rail.SetNextPoint(pos.add(dir.New().mul(5)), dir, up, 5f);
        rail.SetPointNum(20);
        rail.ConstructCurve();
		Logger.debugInfo("init rail");
		renderer.SetDirty();
    }

	@Override
	public double getMaxRenderDistanceSquared()
	{
		return 100000d;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void invalidate()
	{
		super.invalidate();
		renderer.DeleteBuffer();
        SaveRailPosConnectedFromThisRail();
        super.invalidate();
	}

	public void ChangeModel(int modelIndex)
	{
		ChangeModel(new DefaultRailRenderer(rail));
	}

    public void ChangeModel(RailRenderer renderer)
    {
		this.renderer.DeleteBuffer();
		this.renderer = renderer;
        Logger.debugInfo("change model");
        this.renderer.SetDirty();
    }

	private void SaveRailPosConnectedFromThisRail()
    {
        if(worldObj.isRemote)
        {
            double dist = Minecraft.getMinecraft().thePlayer.getDistance(xCoord+0.5, yCoord, zCoord+0.5);
            if(6d > dist)
            {
                NotifyConnecttionRailManager.SetPrevRailPosConnectedDestroyBlock(getRail().PrevRail());
                NotifyConnecttionRailManager.SetNextRailPosConnectedDestroyBlock(getRail().NextRail());
            }
        }
    }

	public void UpdateDirection(GUIRail.editFlag flag, int idx)
	{
		double angle=0;
		switch(idx)
		{
			case 0 : angle = -0.5; break;
			case 1 : angle = -0.05; break;
			case 2 : angle =  0.05; break;
			case 3 : angle =  0.5; break;
		}
		switch(flag)
		{
			case ROTRED : // yaw
				rail.RotateDirectionAsYaw(angle);
				break;
			case ROTGREEN: // pitch:
				rail.RotateDirectionAsPitch(angle);
				break;
			case ROTBLUE : // roll
				rail.RotateDirectionAsRoll(angle);
				break;
			default:
				break;
		}
		rail.ConstructCurve();
        renderer.SetDirty();
	}

	public void FixConnection()
	{
	    rail.SetPrevRail(GetRail(px, py, pz));
	    rail.SetNextRail(GetRail(nx, ny, nz));
	}

	public void SyncData(EntityLivingBase player)
	{
		MessageSyncRailStC packet = new MessageSyncRailStC(rail, true);
		ERC_PacketHandler.INSTANCE.sendTo(packet, (EntityPlayerMP) player);
	}

	@Override
	public void SyncData()
	{
		MessageSyncRailStC packet = new MessageSyncRailStC(rail,true);
		ERC_PacketHandler.INSTANCE.sendToAll(packet);
	}

	@Override
	public void SyncMiscData()
	{
		MessageSyncRailStC packet = new MessageSyncRailStC(rail,false);
		ERC_PacketHandler.INSTANCE.sendToAll(packet);
	}

	@Override
	public void NotifyChange()
	{
		World world = World();
		int x = CorePosX();
		int y = CorePosY();
		int z = CorePosZ();
		Block block = world.getBlock(x, y, z);
		world.notifyBlocksOfNeighborChange (x, y, z, block);
		world.notifyBlocksOfNeighborChange (x, y - 1,z, block);
		world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)x + 0.5D, "random.click", 0.3F, 0.6F);
//        Block block = world.getBlockState(pos).getBlock();
//        world.notifyNeighborsOfStateChange(pos, block, false);
//        world.notifyNeighborsOfStateChange(pos.down(), block, false);
//        world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_STONE_PRESSPLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
        super.readFromNBT(tag);
		if(rail == null) rail = GetRailInstance();

		rail.GetBasePoint().ReadFromNBT("basepoint", tag);
		rail.GetNextPoint().ReadFromNBT("nextpoint", tag);
		int point = (tag.hasKey("point"))?tag.getInteger("point"):20;
		if(point != rail.GetPointNum()){
			rail.SetPointNum(point);
		}
		px = tag.getInteger("px");
		py = tag.getInteger("py");
		pz = tag.getInteger("pz");
        IRail prev = GetRail(px, py, pz);
		if(prev != null) rail.SetPrevRail(prev);
		else{
            lazyRunner.add(() -> {
            	rail.SetPrevRail(GetRail(px, py, pz));
				rail.ConstructCurve();
			});
        }
		nx = tag.getInteger("nx");
		ny = tag.getInteger("ny");
		nz = tag.getInteger("nz");
		IRail next = GetRail(nx, ny, nz);
		if(next != null) rail.SetNextRail(next);
		else {
            lazyRunner.add(() -> {
            	rail.SetNextRail(GetRail(nx, ny, nz));
				rail.ConstructCurve();
			});
        }

		rail.ForceDirty();
		rail.ConstructCurve();
        Logger.debugInfo("read nbt");
        renderer.SetDirty();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
        super.writeToNBT(tag);
		if(rail == null) return;

		rail.GetBasePoint().WriteToNBT("basepoint", tag);
		rail.GetNextPoint().WriteToNBT("nextpoint", tag);
		tag.setInteger("point", rail.GetPointNum());
        tag.setInteger("px", px);
        tag.setInteger("py", py);
        tag.setInteger("pz", pz);
        tag.setInteger("nx", nx);
        tag.setInteger("ny", ny);
        tag.setInteger("nz", nz);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		this.writeToNBT(nbtTagCompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		this.readFromNBT(pkt.func_148857_g());
	}

	public void ConstructModel()
	{
        Logger.debugInfo("construct model");
        renderer.SetDirty();
	}

	public void Render(Tessellator tessellator)
	{
		renderer.RenderRail();
	}


	private ArrayList<Runnable> lazyRunner = new ArrayList<>();

	@Override
    public void updateEntity()
    {
    	if(lazyRunner.size() > 0) {
			for (Runnable lazy : lazyRunner) lazy.run();
			lazyRunner.clear();
    	}
    }


	public static class Normal extends TileEntityRail{
		@Override
		public IRail GetRailInstance()
		{
			return new Rail();
		}
	}

	public static class Accel extends TileEntityRail{
		@Override
		public IRail GetRailInstance()
		{
			return new AccelRail();
		}
	}

	public static class Branch extends TileEntityRail{
		@Override
		public IRail GetRailInstance()
		{
			return new BranchRail();
		}
	}

	public static class Const extends TileEntityRail{
		@Override
		public IRail GetRailInstance()
		{
			return new ConstVelocityRail();
		}
	}

	public static class Detector extends TileEntityRail{
		@Override
		public IRail GetRailInstance()
		{
			return new DetectorRail();
		}
	}

	public static class Invisible extends TileEntityRail{
		@Override
		public IRail GetRailInstance()
		{
			return new DetectorRail();
		}
	}

	public static class AntiGravity extends TileEntityRail{
		@Override
		public IRail GetRailInstance()
		{
			return new AntiGravityRail();
		}
	}
}
