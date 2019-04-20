package erc._mc.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._mc.tileentity.TileEntityRail;
import erc.coaster.Coaster;
import erc.coaster.CoasterSettings;
import erc.coaster.Seat;
import erc.loader.ModelPackLoader;
import erc.model.CoasterModel;
import erc.network.MessageSyncCoasterPosStC;
import erc.rail.IRail;
import mochisystems._core.Logger;
import mochisystems.math.Mat4;
import mochisystems.math.Vec3d;
import erc.network.ERC_PacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;


public class EntityCoaster extends Entity  {

	final Coaster coaster;

	private long updatedTick;
	private EntityCoasterSeat[] entitySeats = new EntityCoasterSeat[0];
	private CoasterSettings settings = CoasterSettings.Default();
	public CoasterModel renderer;

	public Coaster GetCoaster(){ return coaster; }

	public EntityCoaster(World worldIn)
	{
		super(worldIn);
		coaster = new Coaster();
        ySize = 0;
        Logger.debugInfo("! "+this.toString());
//		if(worldIn.isRemote)
//        {
//            MessageSyncCoasterSettings packet =
//                    new MessageSyncCoasterSettings(MessageSyncCoasterSettings.State.request, getEntityId());
//            ERC_PacketHandler.INSTANCE.sendToAll(packet);
//        }

//        this.prevPosX = posX;
//        this.prevPosY = posY;
//        this.prevPosZ = posZ;
		Logger.debugInfo(""+settings.Width+" pos:"+posX);
	}

	public EntityCoaster(World world, Coaster coaster, IRail rail, CoasterSettings settings)
	{
		super(world);
        Logger.debugInfo("!! "+this.toString());
        this.coaster = coaster;
//		this.setLocationAndAngles(x + 0.5, y + 0.5, z + 0.5, 0, 0);
//		this.prevPosX = posX;
//		this.prevPosY = posY;
//		this.prevPosZ = posZ;
        this.settings = settings;
        this.settings.Width = 1.2;
        ActivateSetting(settings, world);
		dataWatcher.updateObject(dwSettings, settings.toString());
        dataWatcher.updateObject(dwPosX, rail.GetController().x());
        dataWatcher.updateObject(dwPosY, rail.GetController().y());
        dataWatcher.updateObject(dwPosZ, rail.GetController().z());
    }
    public void ActivateSetting()
    {
        ActivateSetting(settings, worldObj);
    }

    public void ActivateSetting(CoasterSettings settings, World world)
	{
        this.height = 0.01f;
        this.width = 1.0f;
		MakeSeats(settings, world);
	}

    public void MakeSeats(CoasterSettings settings, World world)
    {
        int seatNum = entitySeats.length;
        if(entitySeats != null)
        {
            for(int i = 0; i < seatNum; ++i) entitySeats[i].setDead();
        }
        seatNum = settings.Seats.length;
        entitySeats = new EntityCoasterSeat[seatNum];
        Seat[] seats = new Seat[seatNum];
        for(int i = 0; i < seatNum; ++i)
        {
            entitySeats[i] = new EntityCoasterSeat(world, this);
            seats[i] = this.entitySeats[i].GetSeat();
            entitySeats[i].setCoasterSettings(settings, i);
//            if(!worldObj.isRemote) worldObj.spawnEntityInWorld(entitySeats[i]);
        }
        coaster.SetSeats(seats);
    }


	@Override
	protected boolean canTriggerWalking()
    {
        return false;
    }

	@Override
	protected void entityInit()
    {
		this.dataWatcher.addObject(dwSettings, "");
		this.dataWatcher.addObject(dwPosX, 0);
		this.dataWatcher.addObject(dwPosY, 0);
		this.dataWatcher.addObject(dwPosZ, 0);
    }

//	private static final DataParameter<Float> PosT = EntityDataManager.<Float>createKey(EntityMinecart.class, DataSerializers.FLOAT);

	private final int dwSettings = 19;
	private final int dwPosX = 16;
	private final int dwPosY = 17;
    private final int dwPosZ = 18;
//    private final int dwSeatId = 25;



//    @Override
//    public Entity[] getParts()
//    {
//        return entitySeats;
//    }

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
    {
    	return null; // 当たり判定は椅子のみとする
    }

	@Override
	public AxisAlignedBB getBoundingBox()
    {
        return null; // 当たり判定は椅子のみとする
    }
	
	@Override
	public boolean canBePushed()
    {
        return false;
    }

	@Override
	public boolean canBeCollidedWith()
	{
		return false;
	}

	@Override
    public void setDead()
    {
        super.setDead();
        if(renderer != null) renderer.DeleteBuffer();
    }

	@Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
		if (!getWorld().isRemote && !this.isDead)
		{
			//this.setRollingDirection(-this.getRollingDirection());
			//this.setRollingAmplitude(10);
			//this.setBeenAttacked();
			//this.setDamage(this.getDamage() + amount * 10.0F);
			boolean canDamage =
					source.getSourceOfDamage() instanceof EntityPlayer
					&& ((EntityPlayer)source.getSourceOfDamage()).capabilities.isCreativeMode;
			
			if (canDamage)// || this.getDamage() > 40.0F)
			{
//				this.removePassengers();
				coaster.Delete();
				this.setDead();
			}
    	}
		return true;
    }
    boolean b = true;
    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if(Keyboard.isKeyDown(Keyboard.KEY_RETURN))setDead();
//        setDead();
        syncToClient();
//
//        if(worldObj.isRemote && b)
//        {
//            b = false;
//            Mat4 m1 = new Mat4();
//            Mat4 m2 = new Mat4();
//            m1.Pos().SetFrom(1, 0, 0);
//            m2.Pos().SetFrom(0, 0, 1);
//            m1.Dir().SetFrom(0, 0, 1);
//            m2.Dir().SetFrom(-1, 0, 0);
//            m1.Up().SetFrom(0, 1, 0);
//            m2.Up().CopyFrom(m1.Up());
//            m1.Side().SetFrom(1, 0, 0);
//            m2.Side().SetFrom(0, 0, 1);
//            Mat4 m = new Mat4();
//            m.CopyFrom(m1); m.Lerp(m2, 0.0f); Logger.debugInfo(m.Dir().toString()+m.Pos().toString());
//            m.CopyFrom(m1); m.Lerp(m2, 0.33f); Logger.debugInfo(m.Dir().toString()+m.Pos().toString());
//            m.CopyFrom(m1); m.Lerp(m2, 0.66f); Logger.debugInfo(m.Dir().toString()+m.Pos().toString());
//            m.CopyFrom(m1); m.Lerp(m2, 1.0f); Logger.debugInfo(m.Dir().toString()+m.Pos().toString());
//        }

		coaster.Update(getWorld().getWorldTime());
		Vec3d pos = coaster.position;//.AttitudeMatrix.Pos();
//		if(worldObj.isRemote)Logger.debugInfo("pos:"+pos.toString());
        coaster.AttitudeMatrix.makeBuffer();
        this.setPosition(pos.x, pos.y, pos.z);
        for(int i = 0; i < entitySeats.length; ++i) entitySeats[i].onUpdate();
    }

	// サーバーのパケット送信カウント
	protected int UpdatePacketCounter = 5;

	// railpos.tとSettingsの同期
	protected void syncToClient()
	{
        if(coaster.GetCurrentRail() == null)
        {
            settings.FromString(dataWatcher.getWatchableObjectString(dwSettings));
            TileEntity tile = worldObj.getTileEntity(
                    dataWatcher.getWatchableObjectInt(dwPosX),
                    dataWatcher.getWatchableObjectInt(dwPosY),
                    dataWatcher.getWatchableObjectInt(dwPosZ));
            if(tile instanceof TileEntityRail) coaster.SetNewRail(((TileEntityRail)tile).getRail());
            ActivateSetting();
        }
		if(renderer == null)
		{
			CoasterSettings pack = ModelPackLoader.GetHeadCoasterSettings("__default");
			renderer = new CoasterModel(pack.Model, pack.Texture, pack.ModelID);
			renderer.SetDirty();
		}
//        if(this.UpdatePacketCounter-- <= 0) {
//            UpdatePacketCounter = 50;
//			if(!getWorld().isRemote && coaster.pos.rail != null)
//			{
//                MessageSyncCoasterPosStC packet =
//                        new MessageSyncCoasterPosStC(this);
//				ERC_PacketHandler.INSTANCE.sendToAll(packet);
//			}
//        }
	}

	@Override
	public void setPosition(double x, double y, double z)
	{
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		double f = this.width / 2.0F;
		double f1 = this.height / 2.0F;
		this.boundingBox.setBounds(x - f, y - f1, z - f,
								   x + f, y + f1, z + f);
	}

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pit, int p_70056_9_)
    {
        //仕様として何も無し　サーバーからの規定のEntity同期で使われており、同期を無効にするため
    }

	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float p_70070_1_)
	{
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.posZ);
		int k = MathHelper.floor_double(this.posY);
		return this.getWorld().getLightBrightnessForSkyBlocks(i, k, j, 0);
	}

	@Override
	public boolean isInRangeToRenderDist(double p_70112_1_)
	{
		return true;
	}


	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
        int x = nbt.getInteger("railx");
        int y = nbt.getInteger("raily");
        int z = nbt.getInteger("railz");
//        coaster.SetNewRail();
        settings.FromString(nbt.getString("settings"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
	    if(coaster.GetCurrentRail() != null) {
            nbt.setInteger("railx", coaster.GetCurrentRail().GetController().x());
            nbt.setInteger("raily", coaster.GetCurrentRail().GetController().y());
            nbt.setInteger("railz", coaster.GetCurrentRail().GetController().z());
        }
        nbt.setDouble("coasterwidth", settings.Width);
        nbt.setDouble("coasterheight", settings.Height);
        nbt.setString("settings", settings.ToString());
	}


	public World getWorld()
	{
		return worldObj;
	}

}
