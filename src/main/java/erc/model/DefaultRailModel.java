//package erc.model;
//
//import erc.renderer.rail.IRailRenderer;
//import erc.renderer.rail.RailRenderer;
//import mochisystems.math.Vec3d;
//import org.lwjgl.opengl.GL11;
//
//import erc._core.ERC_Logger;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.client.model.IModelCustom;
//
//public class DefaultRailModel extends RailRenderer {
//
//	private int pointNum;
//	Vec3d posArray[];
//	Vec3d normalArray[];
//
//	public DefaultRailModel(){} //���[�h����t�@�C�������w��C���X�^���X�������ۂł���H
//
//	public DefaultRailModel(IModelCustom Obj, ResourceLocation Tex){}
//
//	public void setModelNum(int PosNum_org)
//	{
//		pointNum = PosNum_org;
//		posArray = new Vec3d[PosNum_org*4];
//		normalArray = new Vec3d[PosNum_org];
//		for(int i = 0; i< pointNum *4; ++i)posArray[i] = new Vec3d(0, 0, 0);
//		for(int i = 0; i< pointNum; ++i)normalArray[i] = new Vec3d(0, 0, 0);
//	}
//
//	@Override
//	public void construct(int idx, Vec3d Pos, Vec3d Dir, Vec3d Cross, double exParam)
//	{
//		int j = idx*4;
//		double t1 = 0.4 + 0.1;
//		double t2 = 0.4 - 0.1;
//
//		if(j>=posArray.length)
//		{
//			ERC_Logger.warn("ERC_DefaultRailModel : index exception");
//			return;
//		}
//
//		posArray[j  ].x = Pos.x - Cross.x*t1;
//		posArray[j  ].y = Pos.y - Cross.y*t1;
//		posArray[j  ].z = Pos.z - Cross.z*t1;
//		posArray[j+1].x = Pos.x - Cross.x*t2;
//		posArray[j+1].y = Pos.y - Cross.y*t2;
//		posArray[j+1].z = Pos.z - Cross.z*t2;
//
//		posArray[j+2].x = Pos.x + Cross.x*t2;
//		posArray[j+2].y = Pos.y + Cross.y*t2;
//		posArray[j+2].z = Pos.z + Cross.z*t2;
//		posArray[j+3].x = Pos.x + Cross.x*t1;
//		posArray[j+3].y = Pos.y + Cross.y*t1;
//		posArray[j+3].z = Pos.z + Cross.z*t1;
//
//		normalArray[idx].CopyFrom(Dir).cross(Cross).normalize();
//	}
//
//	public void render(Tessellator tess, float mu, float xu, float mv, float xv)
//	{
//		float turnflag = mv;
//		tess.startDrawing(GL11.GL_TRIANGLE_STRIP);
//
//		for(int i = 0; i< pointNum; ++i)
//		{
//			int index = i*4;
//			tess.addVertexWithUV(posArray[index].x, posArray[index].y, posArray[index].z, mu, turnflag);
//			tess.addVertexWithUV(posArray[index+1].x, posArray[index+1].y, posArray[index+1].z, xu, turnflag);
//			turnflag = turnflag>mv?mv:xv;
//			tess.setNormal((float)normalArray[i].x, (float)normalArray[i].y, (float)normalArray[i].z);
//		}
//		tess.draw();
//		turnflag = mv;
//		tess.startDrawing(GL11.GL_TRIANGLE_STRIP);
//		for(int i = 0; i< pointNum; ++i)
//		{
//			int index = i*4+2;
//			tess.addVertexWithUV(posArray[index].x, posArray[index].y, posArray[index].z, mu, turnflag);
//			tess.addVertexWithUV(posArray[index+1].x, posArray[index+1].y, posArray[index+1].z, xu, turnflag);
//			turnflag = turnflag>mv?mv:xv;
//			tess.setNormal((float)normalArray[i].x, (float)normalArray[i].y, (float)normalArray[i].z);
//		}
//		tess.draw();
//	}
//}