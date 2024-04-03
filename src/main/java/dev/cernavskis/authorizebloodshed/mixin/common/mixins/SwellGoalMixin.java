package dev.cernavskis.authorizebloodshed.mixin.common.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.cernavskis.authorizebloodshed.config.ABConfig;
import dev.cernavskis.authorizebloodshed.mixin.common.accessors.SwellGoalAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.pathfinder.Path;

@Mixin(SwellGoal.class)
public class SwellGoalMixin {
  @Inject(
    method = "tick",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/monster/Creeper;setSwellDir(I)V",
      ordinal = 3
    )
  )
  public void onCreeperSwell(CallbackInfo ci) {
    if (!ABConfig.AI.CreepersStayClose.enabled) return;

    SwellGoal goal = (SwellGoal) (Object) this;
    Creeper creeper = ((SwellGoalAccessor) goal).getCreeper();
    LivingEntity target = ((SwellGoalAccessor) goal).getTarget();

    if (target == null) return;

    PathNavigation nav = creeper.getNavigation();
    double distanceToTarget = creeper.distanceTo(target);
    
    if (!nav.isInProgress() || nav.isDone()) {
      // Rotate around target
      
      // Get radians between target and creeper
      double radians = Math.atan2(target.getZ() - creeper.getZ(), target.getX() - creeper.getX());
      // Creeper should keep distance between 1 and 2.5 blocks
      // Creeper stops swelling at 3 blocks
      double keepDistance = Mth.clamp(1, distanceToTarget, 2.5);
      
      // Get radians + PI/4
      double newRadians = radians + Math.PI / 4;
      
      // Set new direction
      Path path = nav.createPath(new BlockPos(
        (int) (creeper.getX() + Math.cos(newRadians) * keepDistance),
        (int) (creeper.getY()),
        (int) (creeper.getZ() + Math.sin(newRadians) * keepDistance)
      ), 0);
  
      nav.moveTo(path, ABConfig.AI.CreepersStayClose.walkSpeed);
    }

    if (ABConfig.AI.CreepersStayClose.sprintEnabled) {
      if (distanceToTarget > 2) {
        nav.setSpeedModifier(ABConfig.AI.CreepersStayClose.sprintSpeed);
      } else {
        nav.setSpeedModifier(ABConfig.AI.CreepersStayClose.walkSpeed);
      }
    }
  }
}
