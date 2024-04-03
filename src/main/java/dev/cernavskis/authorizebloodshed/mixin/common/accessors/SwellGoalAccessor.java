package dev.cernavskis.authorizebloodshed.mixin.common.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;

@Mixin(SwellGoal.class)
public interface SwellGoalAccessor {
  @Accessor("target")
  LivingEntity getTarget();
  
  @Accessor("creeper")
  Creeper getCreeper();
}
