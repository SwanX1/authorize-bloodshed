package dev.cernavskis.authorizebloodshed.mixin.common.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import dev.cernavskis.authorizebloodshed.config.ABConfig.AI.WitchesBuffRaiders;
import dev.cernavskis.authorizebloodshed.entity.ai.BuffNearbyRaidersGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Witch.class)
public class WitchMixin {
  @Unique
  private BuffNearbyRaidersGoal ab$buffNearbyRaidersGoal;

  @Inject(
      method = "performRangedAttack",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/entity/projectile/ThrownPotion;setItem(Lnet/minecraft/world/item/ItemStack;)V",
          shift = Shift.AFTER
      ),
      cancellable = true
  )
  private void modifyThrownPotion(LivingEntity target, float damageMultiplier, CallbackInfo ci, @Local ThrownPotion thrown) {
    if (!WitchesBuffRaiders.enabled) {
      return;
    }

    if (target instanceof Raider) {
      Potion modifiedPotion = BuffNearbyRaidersGoal.getBuff((Raider) target);
      if (modifiedPotion != null) {
        thrown.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), modifiedPotion));
      } else {
        ci.cancel();
      }
    }
  }

  @ModifyArg(
      method = "registerGoals",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",
          ordinal = 6
      ),
      index = 1
  )
  private Goal replaceWithBuffNearbyRaidersGoal(Goal original) {
    if (WitchesBuffRaiders.enabled) {
      this.ab$buffNearbyRaidersGoal = new BuffNearbyRaidersGoal((Witch) (Object) this, true, true);
      return this.ab$buffNearbyRaidersGoal;
    } else {
      return original;
    }
  }

  @Redirect(
      method = "aiStep",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/entity/ai/goal/target/NearestHealableRaiderTargetGoal;getCooldown()I"
      )
  )
  private int replacedCooldown(NearestHealableRaiderTargetGoal<?> goal) {
    if (WitchesBuffRaiders.enabled) {
      this.ab$buffNearbyRaidersGoal.decrementCooldown();
      return this.ab$buffNearbyRaidersGoal.getCooldown();
    } else {
      return goal.getCooldown();
    }
  }
}
