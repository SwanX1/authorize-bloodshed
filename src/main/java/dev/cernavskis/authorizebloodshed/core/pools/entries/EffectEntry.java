package dev.cernavskis.authorizebloodshed.core.pools.entries;

import com.electronwill.nightconfig.core.Config;

import dev.cernavskis.authorizebloodshed.core.pools.util.NumberRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectEntry extends WeightedEntry {
  public static final String identifier = "effect";

  public final ResourceLocation effect;
  private MobEffect effectCache;
  public final NumberRange<Integer> amplifier;

  public EffectEntry(int weight, ResourceLocation effect, NumberRange<Integer> amplifier) {
    super(weight);
    this.effect = effect;
    this.amplifier = amplifier;
  }

  public int getEffectAmplifier(RandomSource random) {
    return this.amplifier.getRandom(random);
  }

  private MobEffect getEffect() {
    if (this.effectCache == null) {
      this.effectCache = ForgeRegistries.MOB_EFFECTS.getValue(this.effect);
    }
    return this.effectCache;
  }

  public boolean applyToMob(Mob mob) {
    MobEffect effect = this.getEffect();
    
    if (effect == null) {
      return false;
    }

    MobEffectInstance effectInstance = new MobEffectInstance(
      effect,
      effect.isInstantenous() ? 1 : MobEffectInstance.INFINITE_DURATION,
      this.getEffectAmplifier(mob.getRandom())
    );

    return mob.addEffect(effectInstance);
  }

  public static EffectEntry deserialize(Config config) {
    return new EffectEntry(
      config.getIntOrElse("weight", 0),
      new ResourceLocation(config.get("effect")),
      NumberRange.deserialize(config.get("amplifier")).toInt()
    );
  }

  public static Config serialize(EffectEntry entry) {
    Config config = Config.inMemory();
    config.set("weight", entry.weight);
    config.set("effect", entry.effect.toString());
    config.set("amplifier", NumberRange.serialize(entry.amplifier));
    return config;
  }
}
