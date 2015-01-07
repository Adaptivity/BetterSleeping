package cz.ondraster.bettersleeping.client.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cz.ondraster.bettersleeping.BetterSleeping;
import cz.ondraster.bettersleeping.Config;
import cz.ondraster.bettersleeping.player.SleepingProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class SleepOverlay extends Gui {
   public static final int BTN_WIDTH = 8;
   public static final int BAR_WIDTH = 32;
   public static final int MAX_OFFSET = BAR_WIDTH - BTN_WIDTH;
   public static final int BAR_HEIGHT = 8;

   public static SleepingProperty playerProperty;

   private static RenderItem itemRenderer = new RenderItem();

   @SubscribeEvent
   public void onGuiRender(RenderGameOverlayEvent event) {

      if ((event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE && event.type != RenderGameOverlayEvent.ElementType.JUMPBAR) || event.isCancelable()) {
         return;
      }

      if (playerProperty == null)
         return;

      TextureManager mgr = Minecraft.getMinecraft().renderEngine;
      mgr.bindTexture(new ResourceLocation(BetterSleeping.MODID, "textures/gui/bar.png"));

      drawTexturedModalRect(4, 8, 0, 0, BAR_WIDTH, BAR_HEIGHT);

      int takenPercent = (int) (((double) playerProperty.sleepCounter / Config.maximumSleepCounter) * MAX_OFFSET);
      if (takenPercent > MAX_OFFSET)
         takenPercent = MAX_OFFSET;

      drawTexturedModalRect(4 + takenPercent, 8, 0, 8, BTN_WIDTH, BAR_HEIGHT);

      ItemStack bed = new ItemStack(Items.bed);

      mgr.bindTexture(TextureMap.locationItemsTexture);
      drawTexturedModelRectFromIcon(4 + BAR_WIDTH + 4, 4, Items.bed.getIcon(bed, 1), 16, 16);

   }
}