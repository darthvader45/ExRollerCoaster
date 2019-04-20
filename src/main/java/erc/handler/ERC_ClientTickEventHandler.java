package erc.handler;

import erc.coaster.Coaster;
import erc._mc.entity.EntityCoaster;
import mochisystems.handler.TickEventHandler;
import mochisystems.manager.RollingSeatManager;
import org.lwjgl.opengl.Display;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public class ERC_ClientTickEventHandler extends TickEventHandler {
	
	Minecraft mc;
	boolean isRideCoaster = false;
//	private float smoothCamYaw;
//	private float smoothCamPitch;
//	private float smoothCamPartialTicks;
	
//	private static float prevTick = 0.0F;
//	private static double mouseDeltaX = 0.0D;
//	private static double mouseDeltaY = 0.0D;
	  
	
	public ERC_ClientTickEventHandler(Minecraft minecraft)
	{
//	    super(minecraft);
	    mc = minecraft;
		
//	    this.ticks = new MCH_ClientTickHandlerBase[] { new MCH_ClientHeliTickHandler(minecraft, config), new MCP_ClientPlaneTickHandler(minecraft, config), new MCH_ClientGLTDTickHandler(minecraft, config), new MCH_ClientVehicleTickHandler(minecraft, config), new MCH_ClientLightWeaponTickHandler(minecraft, config), new MCH_ClientSeatTickHandler(minecraft, config), new MCH_ClientToolTickHandler(minecraft, config) };
	}
	
	public void onPlayerTickPre(EntityPlayer player) {}
	  
	public void onPlayerTickPost(EntityPlayer player) {}
	  
	public void onRenderTickPre(float partialTicks)
	{
		if (Minecraft.getMinecraft().isGamePaused()) {
			return;
	    }
	    EntityPlayer player = this.mc.thePlayer;
	    if (player == null) {
	    	return;
	    }
	    Coaster coaster = null;
	    if (player.ridingEntity instanceof EntityCoaster)
	    {
	    	coaster = ((EntityCoaster) player.ridingEntity).GetCoaster();
	    }

	    if ((coaster != null) /*&& (coaster.canMouseRot())*/)
	    {
			isRideCoaster = true;

			if (this.mc.inGameHasFocus && Display.isActive())
	        {
	            this.mc.mouseHelper.mouseXYChange();
	            float f1 = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
	            float f2 = f1 * f1 * f1 * 8.0F;
	            float f3 = (float)this.mc.mouseHelper.deltaX * f2;
	            float f4 = (float)this.mc.mouseHelper.deltaY * f2;

	            if (this.mc.gameSettings.invertMouse)
	            {
	                f4 *= -1;
	            }

				RollingSeatManager.ShakeHeadOnRollingSeat(f3, f4);
	        }
	    }
	    else
	    {
//	    	ERC_Reflection.setCameraRoll(Roll);
//	    	ERC_CoasterAndRailManager.setRots(
//    				0, 0, 
//    				0, 0, 
//    				0, 0);
//	    	// 2�l��莞�̃V�[�g�ǉ������ꍇ�ɍl����
//			MCH_EntitySeat seat = (player.field_70154_o instanceof MCH_EntitySeat) ? (MCH_EntitySeat)player.field_70154_o : null;
//			if ((seat != null) && (seat.getParent() != null))
//			{
//				updateMouseDelta(stickMode, partialTicks);
//	        
//		        ac = seat.getParent();
//		        
//		        boolean fixRot = false;
//		        MCH_SeatInfo seatInfo = ac.getSeatInfo(player);
//		        if ((seatInfo != null) && (seatInfo.fixRot) && (ac.getIsGunnerMode(player)))
//		        {
//			        fixRot = true;
//			        mouseRollDeltaX *= 0.0D;
//			        mouseRollDeltaY *= 0.0D;
//			        mouseDeltaX *= 0.0D;
//			        mouseDeltaY *= 0.0D;
//		        }
//		        Vec3 v = Vec3.func_72443_a(mouseDeltaX, mouseRollDeltaY, 0.0D);
//		        W_Vec3.rotateAroundZ((float)(ac.calcRotRoll(partialTicks) / 180.0F * 3.141592653589793D), v);
//		        
//		        player.func_70082_c((float)mouseDeltaX, (float)mouseDeltaY);
//	        
//				float y = ac.getRotYaw();
//				float p = ac.getRotPitch();
//				float r = ac.getRotRoll();
//				ac.setRotYaw(ac.calcRotYaw(partialTicks));
//				ac.setRotPitch(ac.calcRotPitch(partialTicks));
//				ac.setRotRoll(ac.calcRotRoll(partialTicks));
//				
//				float revRoll = 0.0F;
//				if (fixRot)
//				{
//					player.field_70177_z = (ac.getRotYaw() + seatInfo.fixYaw);
//					player.field_70125_A = (ac.getRotPitch() + seatInfo.fixPitch);
//					if (player.field_70125_A > 90.0F)
//					{
//					      player.field_70127_C -= (player.field_70125_A - 90.0F) * 2.0F;
//					      player.field_70125_A -= (player.field_70125_A - 90.0F) * 2.0F;
//					      player.field_70126_B += 180.0F;
//					      player.field_70177_z += 180.0F;
//					      revRoll = 180.0F;
//					}
//	          		else if (player.field_70125_A < -90.0F)
//	          		{	
//			            player.field_70127_C -= (player.field_70125_A - 90.0F) * 2.0F;
//			            player.field_70125_A -= (player.field_70125_A - 90.0F) * 2.0F;
//			            player.field_70126_B += 180.0F;
//			            player.field_70177_z += 180.0F;
//			            revRoll = 180.0F;
//	          		}
//				}
//		        ac.setupAllRiderRenderPosition(partialTicks);
//		        ac.setRotYaw(y);
//		        ac.setRotPitch(p);
//		        ac.setRotRoll(r);
//		        
//		        mouseRollDeltaX *= 0.9D;
//		        mouseRollDeltaY *= 0.9D;
//	        
//	        	float roll = MathHelper.func_76142_g(ac.getRotRoll());
//	        	float yaw = MathHelper.func_76142_g(ac.getRotYaw() - player.field_70177_z);
//	        	roll *= MathHelper.func_76134_b((float)(yaw * 3.141592653589793D / 180.0D));
//	        	if ((ac.getTVMissile() != null) && (W_Lib.isClientPlayer(ac.getTVMissile().shootingEntity)) && (ac.getIsGunnerMode(player))) {
//	        		roll = 0.0F;
//	        	}
//	        	W_Reflection.setCameraRoll(roll + revRoll);
//	        	correctViewEntityDummy(player);
//			}
//			else
//	      	{
//	    	  	if (isRideCoaster)
//	        	{
//	        		W_Reflection.setCameraRoll(0.0F);
//	        		isRideCoaster = false;
//	        	}
//	        	mouseRollDeltaX = 0.0D;
//	        	mouseRollDeltaY = 0.0D;
//	      	}
	    }
//    	prevTick = partialTicks;
//    	GL11.glPushMatrix();
//    	GL11.glRotatef(Roll, 0, 0, 1);
	}
	  
	public void onRenderTickPost(float partialTicks)
	{
//		if (Minecraft.getMinecraft().isGamePaused()) {
//			return;
//	    }
//	    EntityPlayer player = this.mc.thePlayer;
//	    if (player == null) {
//	    	return;
//	    }
//		GL11.glPopMatrix();
	}
	  
	public void onTickPre() 
	{
//		 ERC_ManagerPrevTickCoasterSeatSetPos.update();
	}
	  
	public void onTickPost() 
	{
	    float r = 0;
	    float pr = 0;
		EntityPlayer player = this.mc.thePlayer;
	    if (player != null) {
	    	Coaster coaster = null;
	    	if (player.ridingEntity instanceof EntityCoaster)
	    	{
	    		coaster = ((EntityCoaster)player.ridingEntity).GetCoaster();
	    		//r = coaster.rotationRoll;
	    		//pr = coaster.prevRotationRoll;
	    	}
	    }
//	    NotifyConnecttionRailManager.setRotRoll(r, pr);
	}
	  
	  
	@SubscribeEvent
	public void onPlayerTickEvent(TickEvent.PlayerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START) {
//			ERC_Logger.info("Player tick start");
			onPlayerTickPre(event.player);
		}
		if (event.phase == TickEvent.Phase.END) {
			onPlayerTickPost(event.player);
//			ERC_Logger.info("Player tick end");
		}
	}
	
	@SubscribeEvent
	public void onClientTickEvent(TickEvent.ClientTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START) {
//			ERC_Logger.info("Client tick start");
			onTickPre();
			tickcounter = (getTickCounter() + 1);
		}
		if (event.phase == TickEvent.Phase.END) {
			onTickPost();
//			ERC_Logger.info("Client tick end");
		}
	}
	
	@SubscribeEvent
	public void onRenderTickEvent(TickEvent.RenderTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START) {
//			ERC_Logger.info("Render tick start");
			onRenderTickPre(event.renderTickTime);
		}
		if (event.phase == TickEvent.Phase.END) {
			onRenderTickPost(event.renderTickTime);
//			ERC_Logger.info("Render tick end");
		}
	}
	
}
