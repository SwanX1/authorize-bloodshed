package dev.cernavskis.authorizebloodshed.config;

import org.infernalstudios.config.Config;
import org.infernalstudios.config.annotation.Configurable;

public class ABConfig {
  public static Config INSTANCE;

  @Configurable(category = "ai.runfromexplodingcreepers", description = "Should AI run from exploding creepers?")
  public static boolean runFromExplodingCreepers = true;

  @Configurable(category = "ai.runfromexplodingcreepers", description = "Detection range (in blocks) for exploding creepers")
  public static int explodingCreeperDetectionRange = 10;
}
