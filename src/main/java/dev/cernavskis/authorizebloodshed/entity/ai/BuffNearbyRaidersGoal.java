package dev.cernavskis.authorizebloodshed.entity.ai;

import javax.annotation.Nullable;

import dev.cernavskis.authorizebloodshed.config.ABConfig.AI.WitchesBuffRaiders;
import dev.cernavskis.authorizebloodshed.core.ABPotions;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class BuffNearbyRaidersGoal extends TargetGoal {
  @Nullable
  protected LivingEntity target;
  private int cooldown;

  public BuffNearbyRaidersGoal(Witch witch, boolean mustSee, boolean mustReach) {
    super(witch, mustSee, mustReach);
  }

  public int getCooldown() {
    return this.cooldown;
  }


  public void decrementCooldown() { this.cooldown--; }

  @Override
  public boolean canUse() {
    if (!WitchesBuffRaiders.enabled) {
      return false;
    }

    if (this.cooldown > 0 || this.mob.getRandom().nextBoolean()) {
      return false;
    }

    this.findTarget();
    return this.target != null;
  }

  private AABB getSearchArea() {
    return this.mob.getBoundingBox().inflate(WitchesBuffRaiders.buffRange, 4, WitchesBuffRaiders.buffRange);
  }

  protected void findTarget() {
    List<Raider> possibleTargets = this.mob.level().getEntitiesOfClass(Raider.class, getSearchArea(), raider -> !raider.getType().equals(this.mob.getType()) && this.getBuffPriority(raider) > 0);
    if (possibleTargets.isEmpty()) {
      return;
    }

    possibleTargets.sort((a, b) -> {
      int priorityA = getBuffPriority(a);
      int priorityB = getBuffPriority(b);
      if (priorityA != priorityB) {
        return priorityB - priorityA;
      }

      double distancePriorityA = getDistancePriority(a);
      double distancePriorityB = getDistancePriority(b);
      return Double.compare(distancePriorityB, distancePriorityA);
    });

    this.target = possibleTargets.get(0);
  }

  @Override
  public void start() {
    this.cooldown = WitchesBuffRaiders.buffCooldown;
    this.mob.setTarget(this.target);
    super.start();
  }

  public int getBuffPriority(Raider raider) {
    if (WitchesBuffRaiders.useFireResistancePotion && raider.getEffect(MobEffects.FIRE_RESISTANCE) == null && raider.isOnFire() && !raider.fireImmune()) {
      return 4;
    }

    if (raider.getHealth() < raider.getMaxHealth()) {
      if (WitchesBuffRaiders.useInstantHealthPotion) {
        return 3;
      }

      if (WitchesBuffRaiders.useRegenPotion && raider.getEffect(MobEffects.REGENERATION) == null) {
        return 2;
      }
    }


    if (WitchesBuffRaiders.useResistancePotion && raider.getEffect(MobEffects.DAMAGE_RESISTANCE) == null) {
      return 1;
    }

    if (WitchesBuffRaiders.useStrengthPotion && raider.getEffect(MobEffects.DAMAGE_BOOST) == null) {
      return 1;
    }

    if (WitchesBuffRaiders.useSpeedPotion && raider.getEffect(MobEffects.MOVEMENT_SPEED) == null) {
      return 1;
    }

    return 0;
  }

  public double getDistancePriority(Raider raider) {
    return (raider.getHealth() / raider.getMaxHealth()) * (1 / Math.max(this.mob.distanceToSqr(raider), 5));
  }

  @Nullable
  public static Potion getBuff(Raider raider) {
    if (WitchesBuffRaiders.useFireResistancePotion && raider.getEffect(MobEffects.FIRE_RESISTANCE) == null && raider.isOnFire() && !raider.fireImmune()) {
      return Potions.LONG_FIRE_RESISTANCE;
    }

    if (raider.getHealth() < raider.getMaxHealth()) {
      if (WitchesBuffRaiders.useInstantHealthPotion) {
        return Potions.STRONG_HEALING;
      }

      if (WitchesBuffRaiders.useRegenPotion && raider.getEffect(MobEffects.REGENERATION) == null) {
        return Potions.STRONG_REGENERATION;
      }
    }

    List<Potion> availablePotions = new ArrayList<>(3);

    if (WitchesBuffRaiders.useStrengthPotion && raider.getEffect(MobEffects.DAMAGE_BOOST) == null) {
      availablePotions.add(Potions.STRONG_STRENGTH);
    }

    if (WitchesBuffRaiders.useSpeedPotion && raider.getEffect(MobEffects.MOVEMENT_SPEED) == null) {
      availablePotions.add(raider.getRandom().nextBoolean() ? Potions.LONG_SWIFTNESS : Potions.STRONG_SWIFTNESS);
    }

    if (WitchesBuffRaiders.useResistancePotion && raider.getEffect(MobEffects.DAMAGE_RESISTANCE) == null) {
      availablePotions.add(ABPotions.RESISTANCE);
    }

    if (!availablePotions.isEmpty()) {
      return availablePotions.get(raider.getRandom().nextInt(availablePotions.size()));
    }

    return null;
  }
}
