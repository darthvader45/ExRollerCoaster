package erc.model;

import com.sun.prism.Texture;
import erc._mc.entity.EntityCoaster;
import mochisystems.bufferedrenderer.IBufferedRenderer;
import mochisystems.util.HashMaker;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

public class CoasterModel extends IBufferedRenderer {
	
	private IModelCustom modelCoaster;
	private ResourceLocation TextureResource;
    private int hash;

	@SuppressWarnings("unused")
	private CoasterModel(){}

    public CoasterModel(IModelCustom Obj, ResourceLocation Tex, String id)
    {
        modelCoaster = Obj;
        TextureResource = Tex;
        hash = new HashMaker().Append(id).GetHash();
    }

	@Override
	public int GetHash() {
		return hash;
	}


    @Override
    protected void Draw() {
        modelCoaster.renderAll();
    }

    public ResourceLocation GetTexture()
    {
        return TextureResource;
    }

//	public void render(EntityCoaster coaster, double x, double y, double z, float t)
//	{
// 		GL11.glPushMatrix();
//		GL11.glTranslatef((float)x, (float)y, (float)z);
//// 		if(coaster.ERCPosMat != null)
////		{
//// 			GL11.glMultMatrix(coaster.ERCPosMat.rotmat);
////		}
////		GL11.glRotatef(coaster.ERCPosMat.getFixedYaw(t), 0f, -1f, 0f);
//// 		GL11.glRotatef(coaster.ERCPosMat.getFixedPitch(t),1f, 0f, 0f);
//// 		GL11.glRotatef(coaster.ERCPosMat.getFixedRoll(t), 0f, 0f, 1f);
// 		GL11.glMultMatrix(coaster.GetCoaster().AttitudeMatrix.getBuffer());
//
//		GL11.glScalef(1.0f, 1.0f, 1.0f);
//		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureResource);
//		modelCoaster.renderAll();
//		GL11.glPopMatrix();
//	}

}