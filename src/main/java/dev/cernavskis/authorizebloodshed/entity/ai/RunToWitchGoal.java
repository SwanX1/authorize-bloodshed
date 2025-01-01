package dev.cernavskis.authorizebloodshed.entity.ai;

import dev.cernavskis.authorizebloodshed.AuthorizeBloodshed;
import dev.cernavskis.authorizebloodshed.config.ABConfig;
import dev.cernavskis.authorizebloodshed.config.ABConfig.AI.WitchesBuffRaiders;
import dev.cernavskis.authorizebloodshed.config.ABConfig.AI.WitchesBuffRaiders.RaidersRunToWitches;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class RunToWitchGoal extends Goal {
  private final PathfinderMob mob;
  @Nullable
  private Witch runningTo;
  @Nullable
  private Path path;

  public RunToWitchGoal(PathfinderMob mob) {
    this.mob = mob;
  }

  private List<Witch> getNearbyWitches() {
    return this.mob.level().getEntitiesOfClass(
        Witch.class,
        this.mob.getBoundingBox().inflate(RaidersRunToWitches.detectionRange)
    );
  }

  @Override
  public boolean canUse() {
    if (!WitchesBuffRaiders.enabled || !RaidersRunToWitches.enabled) {
      return false;
    }

    if (this.mob.getType().equals(EntityType.WITCH)) {
      return false;
    }

    boolean onFire = this.mob.isOnFire();
    boolean isAboveHalfHealth = this.mob.getHealth() >= this.mob.getMaxHealth() / 2;

    if (!onFire && isAboveHalfHealth) {
      return false;
    }

    List<Witch> nearbyWitches = this.getNearbyWitches();

    if (nearbyWitches.isEmpty()) return false;

    // Get nearest witch
    this.runningTo = nearbyWitches.stream().max((witch1, witch2) -> {
      double distance1 = this.mob.distanceToSqr(witch1);
      double distance2 = this.mob.distanceToSqr(witch2);
      return Double.compare(distance1, distance2);
    }).get();

    Vec3 runTo = runningTo.position();

    this.path = this.mob.getNavigation().createPath(runTo.x, runTo.y, runTo.z, RaidersRunToWitches.approachRadius);
    return this.path != null;
  }

  @Override
  public boolean canContinueToUse() {
    return this.runningTo != null && this.runningTo.isAlive();
  }

  @Override
  public void start() {
    if (this.path == null) return;
    this.mob.getNavigation().moveTo(this.path, 1.0D);
  }

  @Override
  public void stop() {
    this.runningTo = null;
    this.path = null;
    this.mob.getNavigation().stop();
  }

  @Override
  public void tick() {
    this.mob.getNavigation().setSpeedModifier(
        this.mob.distanceToSqr(this.runningTo) < 256.0D ?
            this.mob.distanceToSqr(this.runningTo) < 64.0D ?
                1.0D :
                1.2D :
            1.5D
    );
  }
}
