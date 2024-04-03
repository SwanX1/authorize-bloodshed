package dev.cernavskis.authorizebloodshed.entity;

import dev.cernavskis.authorizebloodshed.config.ABConfig;
import dev.cernavskis.authorizebloodshed.entity.ai.RunFromExplodingCreeperGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;

public class ABGoals {
  public static void addToNewEntity(Mob mob) {
    if (mob instanceof PathfinderMob pathfinderMob) {
      if (ABConfig.AI.RunFromExplodingCreepers.enabled) {
        pathfinderMob.goalSelector.addGoal(3, new RunFromExplodingCreeperGoal(pathfinderMob));
      }
    }
  }
}
