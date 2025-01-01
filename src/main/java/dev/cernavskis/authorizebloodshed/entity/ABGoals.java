package dev.cernavskis.authorizebloodshed.entity;

import dev.cernavskis.authorizebloodshed.config.ABConfig.AI.RunFromExplodingCreepers;
import dev.cernavskis.authorizebloodshed.config.ABConfig.AI.WitchesBuffRaiders;
import dev.cernavskis.authorizebloodshed.config.ABConfig.AI.WitchesBuffRaiders.RaidersRunToWitches;
import dev.cernavskis.authorizebloodshed.entity.ai.RunFromExplodingCreeperGoal;
import dev.cernavskis.authorizebloodshed.entity.ai.RunToWitchGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.raid.Raider;

public class ABGoals {
  public static void addToNewEntity(Mob mob) {
    if (mob instanceof PathfinderMob pathfinderMob) {
      if (RunFromExplodingCreepers.enabled) {
        pathfinderMob.goalSelector.addGoal(3, new RunFromExplodingCreeperGoal(pathfinderMob));
      }

      if (WitchesBuffRaiders.enabled && RaidersRunToWitches.enabled && pathfinderMob instanceof Raider) {
        pathfinderMob.goalSelector.addGoal(2, new RunToWitchGoal(pathfinderMob));
      }
    }
  }
}
