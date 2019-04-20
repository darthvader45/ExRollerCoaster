package erc._mc.entity;

import java.util.Iterator;
import java.util.List;

import com.sun.istack.internal.NotNull;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc.coaster.Coaster;
import erc.coaster.CoasterSettings;
import erc.coaster.IEntityController;
import erc.coaster.Seat;
import erc._mc.item.itemSUSHI;
import mochisystems.manager.RollingSeatManager;
import mochisystems.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCoasterSeat extends Entity implements IEntityController{

    public final Seat seat;
    private int UpdatePacketCounter = 4;
    private EntityCoaster parent;

    public EntityCoasterSeat(World world)
    {
        super(world);
        seat = new Seat(null);
    }

	public EntityCoasterSeat(World world, @NotNull EntityCoaster parent)
    {
		super(world);
		seat = new Seat(parent.coaster);
        this.parent = parent;
        setSize(1.1f, 0.8f);
	}

	public Seat GetSeat()
	{
		return seat;
	}

	@Override
	protected void entityInit()
	{
		this.dataWatcher.addObject(dwParentId, 0/*parent.getEntityId()*/);
	}
//	private static final DataParameter<Float> PosT = EntityDataManager.<Float>createKey(EntityMinecart.class, DataSerializers.FLOAT);
	private final int dwParentId = 19;


	public void setCoasterSettings(CoasterSettings settings, int idx)
	{
//		if(op==null)return;
//		if(op.offsetX==null)return;
//		if(op.offsetX.length <= idx)return;
//		setSize(op.size[idx], op.size[idx]);
//		if(worldObj.isRemote)return;
//		setOffsetX(op.offsetX[idx]);
//		setOffsetY(op.offsetY[idx]);
//		setOffsetZ(op.offsetZ[idx]);
//		setRotX(op.rotX[idx]);
//		setRotY(op.rotY[idx]);
//		setRotZ(op.rotZ[idx]);
//		canRide = op.canRide;
	}

	
    protected void setSize(float w, float h)
    {
//    	w*=10.0;h*=10.0;
        if (w != this.width || h != this.height)
        {
            this.width = w;// + 40f;
            this.height = h;
    		this.boundingBox.minX = -w/2 + this.posX;
    		this.boundingBox.minY = +h/2 + this.posY;
    		this.boundingBox.minZ = -w/2 + this.posZ;
    		this.boundingBox.maxX = +w/2 + this.posX; 
    		this.boundingBox.maxY = +h/2 + this.posY;
    		this.boundingBox.maxZ = +w/2 + this.posZ;
        }
        this.myEntitySize = Entity.EnumEntitySize.SIZE_2;
    }
    
	public boolean canBeCollidedWith()
    {
        return true;
    }
	public AxisAlignedBB getBoundingBox()
    {
        return boundingBox;
    }
	
	private boolean canBeRidden()
    {
		return !worldObj.isRemote;
    }

    public boolean attackEntityFrom(DamageSource ds, float damage)
    {
        seat.Attacked(damage);
//    	return parent.attackEntityFrom(ds, p_70097_2_);
        return true;
    }

    public boolean interactFirst(EntityPlayer player)
    {
//    	if(parent.requestConnectCoaster(player))return true;
    	if(isRiddenSUSHI(player))return true;
    	if(requestRidingMob(player))return true;
    	if(!canBeRidden())return true;

        // プレイヤーが座ってる　＋　右クリックしたプレイヤーと違うプレイヤーが座ってる
        if(this.isRidden())
        {
            if (this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
            {
                return true;
            }
            // 右クリックしたプレイヤー以外の何かが乗ってる
            else if (this.riddenByEntity != player)
            {
                //���낷
                riddenByEntity.mountEntity((Entity)null);
                riddenByEntity = null;
                return true;
            }
            //何かが乗ってる　自分かもしれない
            else
            {
                return true;
            }
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
            	RollingSeatManager.ResetAngles();
                player.mountEntity(this);
            }
            return true;
        }
    }

    private boolean isRidden()
    {
        return this.riddenByEntity != null;
    }
		
    protected boolean isRiddenSUSHI(EntityPlayer player)
	{
		if(player.getHeldItem()==null)return false;
		if(player.getHeldItem().getItem() instanceof itemSUSHI)
		{
			if(!worldObj.isRemote)
			{
				entitySUSHI e = new entitySUSHI(worldObj,posX,posY,posZ);
				worldObj.spawnEntityInWorld(e);	
				e.mountEntity(this);
				if(!player.capabilities.isCreativeMode)--player.getHeldItem().stackSize;
			}
			player.swingItem();
			return true;
		}
		return false;
	}
	
	protected boolean requestRidingMob(EntityPlayer player)
	{
		if(worldObj.isRemote)return false;
		ItemStack is = player.getHeldItem();
		if(is==null)return false;
		if(is.getItem() instanceof ItemMonsterPlacer)
		{
			Entity entity = ItemMonsterPlacer.spawnCreature(worldObj, is.getItemDamage(), posX, posY, posZ);
			entity.mountEntity(this);
			if (!player.capabilities.isCreativeMode)--is.stackSize;
			player.swingItem();
			return true;
		}
		if(is.getItem() instanceof ItemLead)
		{
	        double d0 = 7.0D;
			@SuppressWarnings("unchecked")
			List<EntityLiving> list = worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(posX-d0, posY-d0, posZ-d0, posX+d0, posY+d0, posZ+d0));
	        if (list != null)
	        {
	            Iterator<EntityLiving> iterator = list.iterator();
	            while (iterator.hasNext())
	            {
	                EntityLiving entityliving = iterator.next();

	                if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == player)
	                {
	                	entityliving.mountEntity(this);
	                    entityliving.clearLeashed(true, !player.capabilities.isCreativeMode);
	                    player.swingItem();
	                    return true;
	                }
	            }
	        }
		}
		return false;
	}

    @Override
    public void Destroy() {
        setDead();
    }
	
	@Override
	public void onUpdate() 
	{
        savePrevData();
//        seat.onUpdate(worldObj.getWorldTime());
//        syncToClient();
        Vec3d pos = seat.AttitudeMatrix.Pos();
        this.setPosition(pos.x, pos.y, pos.z);
	}

	protected void syncToClient()
	{
		if(this.UpdatePacketCounter--<=0)
		{
			UpdatePacketCounter = 40;
			if(!worldObj.isRemote)
			{
//                ERC_MessageCoasterMisc packet = new ERC_MessageCoasterMisc(this,4);
//                ERC_PacketHandler.INSTANCE.sendToAll(packet);
			}
		}
	}

	
	protected void savePrevData()
    {
    	this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;             
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;       
//        this.prevRotationRoll = this.rotationRoll;
    }
	
	public boolean searchParent()
	{
		return false;
//		double CorePosX = posX - getOffsetX();
//		double y = posY - getOffsetY();
//		double z = posZ - getOffsetZ();
//		double s = Math.max(Math.abs(getOffsetX()), Math.abs(getOffsetY()));
//		s = Math.max(s, Math.abs(getOffsetZ()));
//		@SuppressWarnings("unchecked")
//		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(CorePosX-s, y-s, z-s, CorePosX+s, y+s, z+s));
//		for(Entity e : list)
//		{
//			if(e instanceof ERC_EntityCoaster)
//			{
//				parentCoaster = (ERC_EntityCoaster) e;
//				parentCoaster.resetSeat(getSeatIndex(), this);
//				return true;
//			}
//		}
//		return false;
	}
	
	public double getMountedYOffset()
    {
        return (double)this.height * 0.4;
    }
	

    @Override
    public void updateRiderPosition()
	{
    	if (this.riddenByEntity != null)
        {
//    		Vec3 vx = parent.ERCPosMat.offsetX;
//    		Vec3 vy = parent.ERCPosMat.offsetY;
//    		Vec3 vz = parent.ERCPosMat.offsetZ;
//    		// Z����]
//    		vx = Math.RotateAroundVector(vx, vz, getRotZ());
//    		vy = Math.RotateAroundVector(vy, vz, getRotZ());
//    		// Y����]
//    		vx = Math.RotateAroundVector(vx, parent.ERCPosMat.offsetY, getRotY());
//    		vz = Math.RotateAroundVector(vz, parent.ERCPosMat.offsetY, getRotY());
//    		// X����]
//    		vy = Math.RotateAroundVector(vy, parent.ERCPosMat.offsetX, getRotX());
//    		vz = Math.RotateAroundVector(vz, parent.ERCPosMat.offsetX, getRotX());
//    		{
//    			////////////// �v���C���[��]�ʌv�Z
//    			// ViewYaw��]�CorePosX�N�g���@dir1->dir_rotView, cross->turnCross
//    			Vec3 dir_rotView = Math.RotateAroundVector(vz, vy, java.lang.Math.toRadians(NotifyConnecttionRailManager.rotationViewYaw));
//    			Vec3 turnCross = Math.RotateAroundVector(vx, vy, java.lang.Math.toRadians(NotifyConnecttionRailManager.rotationViewYaw));
//    			// ViewPitch��]�CorePosX�N�g�� dir1->dir_rotView
//    			Vec3 dir_rotViewPitch = Math.RotateAroundVector(dir_rotView, turnCross, java.lang.Math.toRadians(NotifyConnecttionRailManager.rotationViewPitch));
//    			// pitch�p dir_rotViewPitch�̐����CorePosX�N�g��
//    			Vec3 dir_rotViewPitchHorz = Vec3.createVectorHelper(dir_rotViewPitch.xCoord, 0, dir_rotViewPitch.zCoord);
//    			// roll�pturnCross�̐����CorePosX�N�g��
//    			Vec3 crossHorzFix = Vec3.createVectorHelper(0, 1, 0).crossProduct(dir_rotViewPitch);
//    			if(crossHorzFix.lengthVector()==0.0)crossHorzFix=Vec3.createVectorHelper(1, 0, 0);
//
//    			// yaw OK
//    			 rotationYaw = (float) -java.lang.Math.toDegrees( java.lang.Math.atan2(dir_rotViewPitch.xCoord, dir_rotViewPitch.zCoord) );
//
//    			// pitch OK
//    			rotationPitch = (float) java.lang.Math.toDegrees( Math.angleTwoVec3(dir_rotViewPitch, dir_rotViewPitchHorz) * (dir_rotViewPitch.yCoord>=0?-1f:1f) );
//    			if(Float.isNaN(rotationPitch))
//    				rotationPitch=0;
//
//    			// roll
//    			rotationRoll = (float) java.lang.Math.toDegrees( Math.angleTwoVec3(turnCross, crossHorzFix) * (turnCross.yCoord>=0?1f:-1f) );
//    			if(Float.isNaN(rotationRoll))
//    				rotationRoll=0;
//    		}
//    		prevRotationYaw = Math.fixrot(rotationYaw, prevRotationYaw);
//    		prevRotationPitch = Math.fixrot(rotationPitch, prevRotationPitch);
//    		prevRotationRoll = Math.fixrot(rotationRoll, prevRotationRoll);
          
    		this.riddenByEntity.rotationYaw = this.rotationYaw;
    		this.riddenByEntity.rotationPitch = this.rotationPitch;
    		this.riddenByEntity.prevRotationYaw = this.prevRotationYaw;
    		this.riddenByEntity.prevRotationPitch = this.prevRotationPitch; 

    		double toffsety = this.riddenByEntity.getYOffset();
            this.riddenByEntity.setPosition(
                this.posX,
                this.posY,
                this.posZ);

            double coasterSpeed = seat.GetParent().getSpeed();
            this.riddenByEntity.motionX = coasterSpeed;
            this.riddenByEntity.motionY = coasterSpeed;
            this.riddenByEntity.motionZ = coasterSpeed;

//            if(worldObj.isRemote && riddenByEntity instanceof EntityLivingBase)
//            {
//                EntityLivingBase el = (EntityLivingBase) this.riddenByEntity;
//                el.renderYawOffset = parent.ERCPosMat.yaw;
//                if(riddenByEntity == Minecraft.getMinecraft().thePlayer)
//                    el.rotationYawHead = NotifyConnecttionRailManager.rotationViewYaw + el.renderYawOffset;
//            }
            
        }
//    	ERC_CoasterAndRailManager.setRotRoll(rotationRoll, prevRotationRoll);
	}        
	  
    @SideOnly(Side.CLIENT)
    public void setAngles(float deltax, float deltay)
    {
//    	ERC_CoasterAndRailManager.setAngles(deltax, deltay);
    }
    
	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pit, int p_70056_9_)
    {
    	//�d�l�Ƃ��ĉ��������@�T�[�o�[����̋K���Entity�����Ŏg���Ă���A�����𖳌��ɂ��邽��
//		ERC_Logger.debugInfo("catch!");
//		super.setPositionAndRotation2(CorePosX, y, z, yaw, pit, p_70056_9_);
    }
	
//	public float getRoll(float partialTicks)
//	{
//		return offsetRot + parentCoaster.prevRotationRoll + (parentCoaster.rotationRoll - parentCoaster.prevRotationRoll)*partialTicks;
//	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
//		setSeatIndex(nbt.getInteger("seatindex"));
//		setOffsetX(nbt.getFloat("seatoffsetx"));
//		setOffsetY(nbt.getFloat("seatoffsety"));
//		setOffsetZ(nbt.getFloat("seatoffsetz"));
//		setRotX(nbt.getFloat("seatrotx"));
//		setRotY(nbt.getFloat("seatroty"));
//		setRotZ(nbt.getFloat("seatrotz"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) 
	{
//		nbt.setInteger("seatindex", getSeatIndex());
//		nbt.setFloat("seatoffsetx", getOffsetX());
//		nbt.setFloat("seatoffsety", getOffsetY());
//		nbt.setFloat("seatoffsetz", getOffsetZ());
//		nbt.setFloat("seatrotx", getRotX());
//		nbt.setFloat("seatroty", getRotY());
//		nbt.setFloat("seatrotz", getRotZ());
	}


//	public void SyncCoasterMisc_Send(ByteBuf buf, int flag)
//	{
//		switch(flag)
//		{
//		case 3 : //CtS �\��
//			break;
//		case 4 : //StC �e���N���ɋ�����
//			buf.writeInt(parent.getEntityId());
//			break;
//		}
//	}
//	public void SyncCoasterMisc_Receive(ByteBuf buf, int flag)
//	{
//		switch(flag)
//		{
//		case 3:
//			ERC_MessageCoasterMisc packet = new ERC_MessageCoasterMisc(this,4);
//			ERC_PacketHandler.INSTANCE.sendToAll(packet);
////			ERC_Logger.info("server repost parentID to client");
//			break;
//		case 4 :
//			int parentid = buf.readInt();
//			parent = (EntityCoaster) worldObj.getEntityByID(parentid);
//			if(parent==null){
//				ERC_Logger.warn("parentCoaster id is Invalid.  id:"+parentid);
//				return;
//			}
//			parent.addSeat(this, getSeatIndex());
////			ERC_Logger.info("client get parentCoaster");
//			return;
//		}
//	}
	
	////////////////////////////////////////datawatcher
//	public int getSeatIndex()
//	{
//		return dataWatcher.getWatchableObjectInt(21);
//	}
//	public void setSeatIndex(int idx)
//	{
//		dataWatcher.updateObject(21, Integer.valueOf(idx));
//	}
//
//	public float getOffsetX()
//	{
//		return dataWatcher.getWatchableObjectFloat(22);
//	}
//	public void setOffsetX(float offsetx)
//	{
//		dataWatcher.updateObject(22, Float.valueOf(offsetx));
//	}
//
//	public float getOffsetY()
//	{
//		return dataWatcher.getWatchableObjectFloat(23);
//	}
//	public void setOffsetY(float offsety)
//	{
//		dataWatcher.updateObject(23, Float.valueOf(offsety));
//	}
//
//	public float getOffsetZ()
//	{
//		return dataWatcher.getWatchableObjectFloat(24);
//	}
//	public void setOffsetZ(float offsetz)
//	{
//		dataWatcher.updateObject(24, Float.valueOf(offsetz));
//	}
//
//	public float getRotX()
//	{
//		return dataWatcher.getWatchableObjectFloat(25);
//	}
//	public void setRotX(float rot)
//	{
//		dataWatcher.updateObject(25, Float.valueOf(rot));
//	}
//
//	public float getRotY()
//	{
//		return dataWatcher.getWatchableObjectFloat(26);
//	}
//	public void setRotY(float rot)
//	{
//		dataWatcher.updateObject(26, Float.valueOf(rot));
//	}
//
//	public float getRotZ()
//	{
//		return dataWatcher.getWatchableObjectFloat(27);
//	}
//	public void setRotZ(float rot)
//	{
//		dataWatcher.updateObject(27, Float.valueOf(rot));
//	}
//

}
