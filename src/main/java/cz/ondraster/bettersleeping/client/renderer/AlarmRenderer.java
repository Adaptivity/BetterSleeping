package cz.ondraster.bettersleeping.client.renderer;

import cz.ondraster.bettersleeping.BetterSleeping;
import cz.ondraster.bettersleeping.client.model.ModelAlarm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class AlarmRenderer extends TileEntitySpecialRenderer {

   private final ModelAlarm modelAlarm;

   public AlarmRenderer() {
      modelAlarm = new ModelAlarm();
   }

   @Override
   public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {

      TextureManager tm = Minecraft.getMinecraft().getTextureManager();

      GL11.glPushMatrix();

      GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
      GL11.glRotatef(180f, 0f, 0f, 1f);
      tm.bindTexture(new ResourceLocation(BetterSleeping.MODID, "textures/models/alarm.png"));
      modelAlarm.render();
      GL11.glPopMatrix();
   }
}