package dev.cernavskis.authorizebloodshed.mixin.common.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import dev.cernavskis.authorizebloodshed.config.ABConfig;
import net.minecraft.world.entity.animal.Rabbit;

@Mixin(Rabbit.class)
public class RabbitMixin {
  @ModifyArg(
    method = "registerGoals",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/animal/Rabbit$RabbitPanicGoal;<init>(Lnet/minecraft/world/entity/animal/Rabbit;D)V"
    ),
    index = 1
  )
  public double modifyRabbitPanicSpeed(double speed) {
    return speed * ABConfig.AI.RabbitPanic.speedMultiplier;
  }

  @ModifyArg(
    method = "registerGoals",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/animal/Rabbit$RabbitAvoidEntityGoal;<init>(Lnet/minecraft/world/entity/animal/Rabbit;Ljava/lang/Class;FDD)V"
    ),
    index = 3
  )
  public double modifyRabbitAvoidEntityWalkSpeed(double speed) {
    return speed * ABConfig.AI.RabbitPanic.speedMultiplier;
  }

  @ModifyArg(
    method = "registerGoals",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/animal/Rabbit$RabbitAvoidEntityGoal;<init>(Lnet/minecraft/world/entity/animal/Rabbit;Ljava/lang/Class;FDD)V"
    ),
    index = 4
  )
  public double modifyRabbitAvoidEntitySprintSpeed(double speed) {
    return speed * ABConfig.AI.RabbitPanic.speedMultiplier;
  }
}
