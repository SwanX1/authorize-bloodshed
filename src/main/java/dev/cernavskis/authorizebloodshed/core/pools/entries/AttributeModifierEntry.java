package dev.cernavskis.authorizebloodshed.core.pools.entries;

import com.electronwill.nightconfig.core.Config;
import dev.cernavskis.authorizebloodshed.core.pools.util.NumberRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributeModifierEntry extends WeightedEntry {
  public static final String identifier = "attribute_modifier";

  public final ResourceLocation attribute;
  private Attribute attributeCache;
  private final NumberRange<Double> range;

  public AttributeModifierEntry(int weight, ResourceLocation attribute, NumberRange<Double> range) {
    super(weight);
    this.attribute = attribute;
    this.range = range;
  }

  public double getAttributeValue(RandomSource random) {
    return this.range.getRandom(random);
  }

  public static Config serialize(AttributeModifierEntry entry) {
    Config config = Config.inMemory();
    config.set("weight", entry.weight);
    config.set("attribute", entry.attribute.toString());
    config.set("range", NumberRange.serialize(entry.range));
    return config;
  }

  public static AttributeModifierEntry deserialize(Config config) {
    return new AttributeModifierEntry(
      config.getInt("weight"),
      new ResourceLocation(config.get("attribute")),
      NumberRange.deserialize(config.get("range"))
    );
  }

  private Attribute getAttribute() {
    if (this.attributeCache == null) {
      this.attributeCache = ForgeRegistries.ATTRIBUTES.getValue(this.attribute);
    }
    return this.attributeCache;
  }

  public boolean applyToMob(Mob mob) {
    if (this.getAttribute() == null) {
      return false;
    }

    AttributeInstance mobAttribute = mob.getAttribute(this.getAttribute());
    if (mobAttribute == null) {
      return false;
    }

    mobAttribute.setBaseValue(this.getAttributeValue(mob.getRandom()));
    return true;
  }
}