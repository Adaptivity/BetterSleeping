package cz.ondraster.bettersleeping;

import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
   public static int defaultWakeUpTime = 0;
   public static int oversleepWithoutAlarm = 2000;
   public static int oversleepWithAlarm = 400;
   public static double chanceToStopRain = 0.9;
   public static boolean enableSleepCounter = true;
   public static long spawnSleepCounter = 18000;
   public static int ticksPerSleepCounter = 4;
   public static double sleepPerSleptTick = 0.5;
   public static int maximumSleepCounter = 18000;
   public static boolean enableDebuffs = true;
   public static int slownessDebuff = 200;
   public static int visionDebuff = 100;
   public static boolean sleepOnGround = true;
   public static int guiOffsetLeft = 4;
   public static int guiOffsetTop = 8;
   public static boolean enableSleepyBar = true;

   public static double percentPeopleToSleep = 0.5;

   public static boolean enableRingWatch = true;

   public static boolean enableAlarmSound = true;

   private static final int DEBUFF_COUNT = 9;
   public static PlayerDebuff[] debuffs = new PlayerDebuff[DEBUFF_COUNT];

   // private config stuff
   public static final int POTION_DURATION = 40;

   private Configuration cfg;

   public Config(String filename) {
      cfg = new Configuration(new File(filename));
      cfg.load();
      percentPeopleToSleep = cfg.get("config", "percentPeopleToSleep", percentPeopleToSleep, "How many players have to be in bed in a dimension to sleep.").getDouble();
      defaultWakeUpTime = cfg.getInt("defaultWakeUpTime", "config", defaultWakeUpTime, 0, 23999, "morning offset when no alarm is found [ticks]");
      oversleepWithoutAlarm = cfg.getInt("oversleepWithoutAlarm", "config", oversleepWithoutAlarm, 0, 23999, "how much at maximum should a person oversleep without an alarm [ticks]");
      oversleepWithAlarm = cfg.getInt("oversleepWithAlarm", "config", oversleepWithAlarm, 0, 23999, "how much at maximum should a person oversleep with an alarm [ticks]");
      chanceToStopRain = cfg.get("config", "chanceToStopRain", chanceToStopRain, "what is the chance that it will stop raining").getDouble();
      enableSleepCounter = cfg.getBoolean("enableSleepCounter", "config", enableSleepCounter, "Enable sleep counter for all sub features");
      ticksPerSleepCounter = cfg.getInt("ticksPerSleepCounter", "config", ticksPerSleepCounter, 1, 23999, "How many player ticks between decreasing sleep counter");
      sleepPerSleptTick = cfg.get("config", "sleepPerSleptTick", sleepPerSleptTick, "How much sleep is increased with every slept tick").getDouble();
      maximumSleepCounter = cfg.getInt("maximumSleepCounter", "config", maximumSleepCounter, 0, Integer.MAX_VALUE, "How much sleep counter you can reach before being denied sleep privilege.");
      enableDebuffs = cfg.getBoolean("enableDebuffs", "config", enableDebuffs, "Enable all debuffs related to exhaustion");

      sleepOnGround = cfg.getBoolean("sleepOnGround", "config", sleepOnGround, "sleep on ground when absolutely exhausted");

      guiOffsetLeft = cfg.getInt("guiOffsetLeft", "gui", guiOffsetLeft, 1, 256, "Left offset of the sleepybar");
      guiOffsetTop = cfg.getInt("guiOffsetTop", "gui", guiOffsetTop, 1, 256, "Top offset of the sleepybar");
      enableSleepyBar = cfg.getBoolean("enableSleepyBar", "gui", enableSleepyBar, "Whether to enable the sleepybar render");

      enableRingWatch = cfg.getBoolean("enableRingWatch", "gui", enableSleepCounter, "Enable Baubles Ring Watch");

      enableAlarmSound = cfg.getBoolean("enableAlarmSound", "gui", enableAlarmSound, "Enable alarm sound when woken up");

      // debuffs
      String[] debuffNames = {"moveSlowdown", "digSlowdown", "harm", "confusion", "blindness", "hunger", "weakness", "poison", "wither"};
      boolean[] defaultEnable = {true, true, false, false, true, false, true, false, false};
      int[] defaultTiredLevel = {800, 800, 800, 800, 800, 800, 800, 800, 800};
      int[] defaultMaxScale = {3, 3, 1, 1, 2, 1, 3, 1, 1};
      int[] potionEffect = {Potion.moveSlowdown.getId(), Potion.digSlowdown.getId(), Potion.harm.getId(), Potion.confusion.getId(), Potion.blindness.getId(), Potion.hunger.getId(), Potion.weakness.getId(), Potion.poison.getId(), Potion.wither.getId()};

      for (int i = 0; i < debuffNames.length; i++) {
         PlayerDebuff debuff = new PlayerDebuff();
         debuff.potion = Potion.potionTypes[i];
         debuff.enable = cfg.getBoolean("enable", debuffNames[i], defaultEnable[i], "Enable this debuff");
         debuff.maxScale = cfg.getInt("maxScale", debuffNames[i], defaultMaxScale[i], 0, 5, "Maximum scaling of this debuff");
         debuff.tiredLevel = cfg.getInt("level", debuffNames[i], defaultTiredLevel[i], 0, 23999, "At which level is this debuff applied");
         debuffs[i] = debuff;
      }

      save();
   }

   public void save() {
      cfg.save();
   }
}
