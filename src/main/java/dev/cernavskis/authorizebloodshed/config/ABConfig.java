package dev.cernavskis.authorizebloodshed.config;

import org.infernalstudios.config.Config;
import org.infernalstudios.config.annotation.Category;
import org.infernalstudios.config.annotation.Configurable;

public class ABConfig {
  public static Config INSTANCE;

  @Category("ai")
  public static class AI {
    
    @Category("runfromexplodingcreepers")
    public static class RunFromExplodingCreepers {
      @Configurable(description = "Should AI run from exploding creepers?")
      public static boolean enabled = true;
    
      @Configurable(description = "Detection range (in blocks) for exploding creepers")
      public static int detectionRange = 10;
    }
  
    @Category("endermenchase")
    public static class EndermenChase {
      @Configurable(description = "Should Endermen chase slower when they're aggroed?")
      public static boolean enabled = true;
      @Configurable(description = "Speed multiplier for Endermen when they're aggroed")
      public static double speedMultiplier = 0.8;
    }
  
    @Category("rabbitpanic")
    public static class RabbitPanic {
      @Configurable(description = "Should rabbits move slower when they're panicking?")
      public static boolean enabled = true;

      @Configurable(description = "Speed multiplier for rabbits when they're panicking (when hurt or when avoiding a player)")
      public static double speedMultiplier = 0.65;
    }

    @Category("creepersstayclose")
    public static class CreepersStayClose {
      @Configurable(description = "Should creepers stay close to their target when they're about to explode?")
      public static boolean enabled = true;
    
      @Configurable(description = "Speed at which creepers should move when staying close to their target")
      public static double walkSpeed = 1.2D;
    
      @Configurable(description = "Should creepers sprint to stay close to their target?")
      public static boolean sprintEnabled = true;
    
      @Configurable(description = "Speed at which creepers should sprint when staying close to their target")
      public static double sprintSpeed = 1.6D;
    }
  }
}
