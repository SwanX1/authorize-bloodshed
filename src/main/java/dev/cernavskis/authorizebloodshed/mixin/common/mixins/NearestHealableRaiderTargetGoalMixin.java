package dev.cernavskis.authorizebloodshed.mixin.common.mixins;

import dev.cernavskis.authorizebloodshed.AuthorizeBloodshed;
import dev.cernavskis.authorizebloodshed.config.ABConfig.AI.WitchesBuffRaiders;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NearestHealableRaiderTargetGoal.class)
public class NearestHealableRaiderTargetGoalMixin {
  @Unique
  private static boolean HAS_WARNED = false;

  @Inject(
      method = "start",
      at = @At("HEAD"),
      cancellable = true
  )
  private void start(CallbackInfo ci) {
    if (!WitchesBuffRaiders.enabled) {
      if (!HAS_WARNED) {
        HAS_WARNED = true;
        AuthorizeBloodshed.getLogger().warn("NearestHealableRaiderTargetGoal has been overridden by Authorize Bloodshed.");
        AuthorizeBloodshed.getLogger().warn("Please disable WitchesHealIllagers in the config to prevent this from happening.");
        AuthorizeBloodshed.getLogger().warn("This message is shown, because another mod has used this goal, and it has been canceled.");
        AuthorizeBloodshed.getLogger().warn("This message will only appear once.");
        AuthorizeBloodshed.getLogger().warn("Stacktrace provided below:", new Throwable());
      }
      ci.cancel();
    }
  }
}
