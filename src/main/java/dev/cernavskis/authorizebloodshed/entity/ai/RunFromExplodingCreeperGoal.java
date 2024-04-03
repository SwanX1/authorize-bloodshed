package dev.cernavskis.authorizebloodshed.entity.ai;

import java.util.List;

import javax.annotation.Nullable;

import dev.cernavskis.authorizebloodshed.config.ABConfig;
import dev.cernavskis.authorizebloodshed.mixin.common.accessors.CreeperAccessor;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RunFromExplodingCreeperGoal extends Goal {
  private final PathfinderMob mob;
  @Nullable
  private Creeper runningFrom;
  @Nullable
  private Path path;
  
  public RunFromExplodingCreeperGoal(PathfinderMob mob) {
    this.mob = mob;
  }
  
  private boolean canCreeperAffect(Creeper creeper) {
    int explosionRadius = ((CreeperAccessor) creeper).getExplosionRadius();
    return
      creeper.isIgnited() &&
      // Adjusted distance to account for bounding box errors, maybe?
      this.mob.distanceToSqr(creeper) - 4 < explosionRadius * explosionRadius;
  }
  
  private List<Creeper> getNearbyExplodingCreepers() {
    return this.mob.level().getEntitiesOfClass(
      Creeper.class,
      AABB.ofSize(this.mob.position(), ABConfig.AI.RunFromExplodingCreepers.detectionRange, ABConfig.AI.RunFromExplodingCreepers.detectionRange, ABConfig.AI.RunFromExplodingCreepers.detectionRange),
      this::canCreeperAffect
    );
  }
  
  @Override
  public boolean canUse() {
    List<Creeper> explodingCreepers = this.getNearbyExplodingCreepers();
    
    if (explodingCreepers.isEmpty()) return false;
    
    // Get nearest creeper
    this.runningFrom = explodingCreepers.stream().max((creeper1, creeper2) -> {
      double distance1 = this.mob.distanceToSqr(creeper1);
      double distance2 = this.mob.distanceToSqr(creeper2);
      return Double.compare(distance1, distance2);
    }).get();
    
    
    Vec3 runTo = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.runningFrom.position());
    if (
      // If we can't find a suitable place to run to, don't run
      runTo == null ||
      // If the distance to the creeper is less than the distance to the place we're running to, don't run
      this.runningFrom.distanceToSqr(runTo.x, runTo.y, runTo.z) < this.runningFrom.distanceToSqr(this.mob)
    ) {
      return false;
    }
    
    this.path = this.mob.getNavigation().createPath(runTo.x, runTo.y, runTo.z, 0);
    return this.path != null;
  }
  
  @Override
  public boolean canContinueToUse() {
    return this.runningFrom != null && this.runningFrom.isAlive() && this.canCreeperAffect(this.runningFrom);
  }
  
  @Override
  public void start() {
    if (this.path == null) return;
    this.mob.getNavigation().moveTo(this.path, 1.0D);  
  }
  
  @Override
  public void stop() {
    this.runningFrom = null;
    this.path = null;

    this.mob.getNavigation().stop();
  }
  
  @Override
  public void tick() {
    this.mob.getNavigation().setSpeedModifier(
      this.mob.distanceToSqr(this.runningFrom) < 49.0D ?
        this.mob.distanceToSqr(this.runningFrom) < 9.0D ?
          1.5D :
          1.2D :
        1.0D
    );
  }
}
