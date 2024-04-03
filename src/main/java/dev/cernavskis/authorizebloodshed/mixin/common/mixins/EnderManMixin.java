package dev.cernavskis.authorizebloodshed.mixin.common.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import dev.cernavskis.authorizebloodshed.config.ABConfig;
import net.minecraft.world.entity.monster.EnderMan;

@Mixin(EnderMan.class)
public class EnderManMixin {
  @ModifyArg(
    method = "registerGoals",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/ai/goal/MeleeAttackGoal;<init>(Lnet/minecraft/world/entity/PathfinderMob;DZ)V"
    ),
    index = 1
  )
  public double modifyEndermanAggroMovementSpeed(double speed) {
    return ABConfig.AI.EndermenChase.enabled ? speed * ABConfig.AI.EndermenChase.speedMultiplier : speed;
  }
}
