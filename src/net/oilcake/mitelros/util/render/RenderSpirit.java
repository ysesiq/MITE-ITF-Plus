package net.oilcake.mitelros.util.render;

import net.minecraft.Entity;
import net.minecraft.EntityEnderman;
import net.minecraft.EntityLiving;
import net.minecraft.bgk;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Random;

public class RenderSpirit extends bgk {
//    private final bbh g;
//
//    public RenderSpirit() {
//        super();
//        this.g = (bbh)this.i;
//        this.a(this.g);
//    }
//
//    @Override
//    protected void setTextures() {
//        this.setTexture(0, "textures/entity/spirit");
//    }
//    protected bjo a(EntityInsentient par1EntitySpirit) {
//        return this.textures[0];
//    }
//    protected bjo a(Entity par1Entity) {
//        return this.a((EntityEnderman) par1Entity);
//    }
//
//    @Override
//    public void a(Entity entity, double v, double v1, double v2, float v3, float v4) {
//
//    }
public static final int body_texture = 0;

//    private ModelEnderman endermanModel;
    private Random rnd = new Random();

    public RenderSpirit()
    {
//        super(new ModelEnderman(), 0.5F);
//        this.endermanModel = (ModelEnderman)super.mainModel;
//        this.setRenderPassModel(this.endermanModel);
    }

    protected void setTextures() {
        this.setTexture(0, "textures/entity/spirit","textures/entity/spirit_glow");
    }

    public void renderEnderman(EntityEnderman par1EntityEnderman, double par2, double par4, double par6, float par8, float par9) {
//        this.endermanModel.isCarrying = par1EntityEnderman.getCarried() > 0;
//        this.endermanModel.isAttacking = par1EntityEnderman.isScreaming();

        if (par1EntityEnderman.isScreaming())
        {
            double var10 = 0.02D;
            par2 += this.rnd.nextGaussian() * var10;
            par6 += this.rnd.nextGaussian() * var10;
        }

 //       super.doRenderLiving(par1EntityEnderman, par2, par4, par6, par8, par9);
    }

//    protected ResourceLocation getEndermanTextures(EntityEnderman par1EntityEnderman)
//    {
//        return this.textures[0];
//    }

    protected void renderCarrying(EntityEnderman par1EntityEnderman, float par2) {
//        super.renderEquippedItems(par1EntityEnderman, par2);

        if (par1EntityEnderman.getCarried() > 0) {
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glPushMatrix();
            float var3 = 0.5F;
            GL11.glTranslatef(0.0F, 0.6875F, -0.75F);
            var3 *= 1.0F;
            GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(-var3, -var3, var3);
//            int var4 = par1EntityEnderman.getBrightnessForRender(par2);
//            int var5 = var4 % 65536;
//            int var6 = var4 / 65536;
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var5 / 1.0F, (float)var6 / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//            this.bindTexture(TextureMap.locationBlocksTexture);
//            this.renderBlocks.renderBlockAsItem(Block.blocksList[par1EntityEnderman.getCarried()], par1EntityEnderman.getCarryingData(), 1.0F);
            GL11.glPopMatrix();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
        this.renderEnderman((EntityEnderman)par1EntityLiving, par2, par4, par6, par8, par9);
    }

//    protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2)
//    {
//        this.renderCarrying((EntityEnderman)par1EntityLivingBase, par2);
//    }

//    public void renderPlayer(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9)
//    {
//        this.renderEnderman((EntityEnderman)par1EntityLivingBase, par2, par4, par6, par8, par9);
//    }
//
//    protected ResourceLocation getEntityTexture(Entity par1Entity)
//    {
//        return this.getEndermanTextures((EntityEnderman)par1Entity);
//    }

    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.renderEnderman((EntityEnderman)par1Entity, par2, par4, par6, par8, par9);
    }
}
