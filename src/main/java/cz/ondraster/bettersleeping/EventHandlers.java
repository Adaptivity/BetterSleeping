package cz.ondraster.bettersleeping;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cz.ondraster.bettersleeping.api.PlayerData;
import cz.ondraster.bettersleeping.logic.Alarm;
import cz.ondraster.bettersleeping.logic.AlternateSleep;
import cz.ondraster.bettersleeping.logic.DebuffLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

public class EventHandlers {

   public static EventHandlers INSTANCE;

   private int ticksSinceUpdate = 0;

   public EventHandlers() {
      INSTANCE = this;
   }

   @SubscribeEvent
   public void onPlayerDeath(LivingDeathEvent event) {
      if (event.entity.worldObj.isRemote)
         return;

      if (!Config.enableSleepCounter)
         return;

      if (!Config.resetCounterOnDeath)
         return;

      if (event.entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer) event.entity;
         PlayerData data = BSSavedData.instance().getPlayerData(player.getUniqueID());
         data.sleepCounter = Config.spawnSleepCounter;
         BSSavedData.instance().markDirty();
      }
   }

   @SubscribeEvent
   public void onPreWorldTick(TickEvent.WorldTickEvent event) {
      if (!(event.world instanceof WorldServer))
         return;

      if (event.phase != TickEvent.Phase.START)
         return;

      WorldServer world = (WorldServer) event.world;

      if (world.areAllPlayersAsleep()) {
         Alarm.sleepWorld(world);
      }
   }

   @SubscribeEvent
   public void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.phase != TickEvent.Phase.START)
         return;

      PlayerData data = null;
      if (event.player.worldObj.isRemote)
         return;

      if (!event.player.isEntityAlive())
         return;

      if (Config.enableSleepCounter) {
         data = BSSavedData.instance().getData(event.player);
         data.ticksSinceUpdate++;
         if (data.ticksSinceUpdate >= Config.ticksPerSleepCounter) {
            data.ticksSinceUpdate = 0;

            if (!event.player.capabilities.isCreativeMode)
               data.sleepCounter--;

            if (data.sleepCounter < 0)
               data.sleepCounter = 0;

         }

         if (event.player.isPlayerSleeping() && Config.giveSleepCounterOnSleep > 0) {
            data.sleepCounter += Config.giveSleepCounterOnSleep;
         }

         // send update about tiredness to the client
         DebuffLogic.updateClientIfNeeded(event, data);
      }

      if (data == null)
         return; // safety, should not happen except maybe some edge cases

      if (Config.enableDebuffs && Config.enableSleepCounter && ticksSinceUpdate > 20) {
         // check for debuffs
         DebuffLogic.checkForDebuffs(event, data);

         ticksSinceUpdate = 0;
      }

      BSSavedData.instance().markDirty();

      ticksSinceUpdate++;
   }

   @SubscribeEvent
   public void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
      if (event.entityPlayer.worldObj.isRemote)
         return;

      if (Config.disableSleeping) {
         event.entityPlayer.addChatComponentMessage(new ChatComponentTranslation("msg.sleepingDisabled"));
         event.result = EntityPlayer.EnumStatus.OTHER_PROBLEM;
         return;
      }

      if (Config.enableSleepCounter) {
         PlayerData data = BSSavedData.instance().getData(event.entityPlayer);

         if (data.getSleepCounter() >= Config.maximumSleepCounter) {
            event.entityPlayer.addChatComponentMessage(new ChatComponentTranslation("msg.notTired"));
            event.result = EntityPlayer.EnumStatus.OTHER_PROBLEM;
         }
      }

      // check for amount of people sleeping in this dimension
      AlternateSleep.trySleepingWorld(event.entityPlayer.worldObj);
   }

   @SubscribeEvent
   public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
      if (event.player.worldObj == null)
         return;

      if (event.player.worldObj.isRemote)
         return;

      if (Config.percentPeopleToSleep > 1)
         return;

      AlternateSleep.trySleepingWorld(event.player.worldObj, true);
   }
}
