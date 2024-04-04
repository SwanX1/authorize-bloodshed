package dev.cernavskis.authorizebloodshed.core.pools;

import java.util.List;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;

import dev.cernavskis.authorizebloodshed.AuthorizeBloodshed;
import dev.cernavskis.authorizebloodshed.core.pools.entries.AttributeModifierEntry;
import dev.cernavskis.authorizebloodshed.core.pools.entries.EffectEntry;
import dev.cernavskis.authorizebloodshed.core.pools.entries.EquipmentEntry;
import dev.cernavskis.authorizebloodshed.core.pools.util.Entries;
import dev.cernavskis.authorizebloodshed.core.pools.util.EntrySerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;

public class EntityModifier {
  public final ResourceLocation entity;
  private final Entries<AttributeModifierEntry> attributes = new Entries<>();
  private final Entries<EquipmentEntry> equipment = new Entries<>();
  private final Entries<EffectEntry> effects = new Entries<>();
  // private final Entries<CustomEntry> custom = new Entries<>(); TODO: implement custom entries

  public EntityModifier(ResourceLocation entity) {
    this.entity = entity;
  }

  public EntityModifier addAttribute(AttributeModifierEntry attribute) {
    this.attributes.add(attribute);
    return this;
  }

  public EntityModifier addEquipment(EquipmentEntry equipment) {
    this.equipment.add(equipment);
    return this;
  }

  public EntityModifier addEffect(EffectEntry effect) {
    this.effects.add(effect);
    return this;
  }

  // public EquipmentPool addCustom(CustomEntry custom) {
  //   this.custom.add(custom);
  //   return this;
  // }

  public void applyToMob(Mob mob) {
    RandomSource random = mob.getRandom();

    AttributeModifierEntry attributeModifier = this.attributes.getRandomWeighted(random);
    if (attributeModifier != null) {
      if (!attributeModifier.applyToMob(mob)) {
        AuthorizeBloodshed.getLogger().warn("Failed to apply attribute " + attributeModifier.attribute + " to " + mob.getDisplayName().getString() + "!");
        AuthorizeBloodshed.getLogger().warn("Attribute either doesn't exist or is not applicable to the entity.");
      }
    }

    EquipmentEntry equipmentEntry = this.equipment.getRandomWeighted(random);
    if (equipmentEntry != null) {
      boolean success;
      try {
        success = equipmentEntry.applyToMob(mob);
      } catch (Exception e) {
        success = false;
        throw new RuntimeException("Failed to apply equipment to " + mob.getDisplayName().getString(), e);
      }

      if (!success) {
        AuthorizeBloodshed.getLogger().warn("Failed to apply equipment to " + mob.getDisplayName().getString());
        AuthorizeBloodshed.getLogger().warn("Equipment either doesn't exist or is not specified correctly.");
      }
    }

    EffectEntry effectEntry = this.effects.getRandomWeighted(random);
    if (effectEntry != null) {
      if (!effectEntry.applyToMob(mob)) {
        AuthorizeBloodshed.getLogger().warn("Failed to apply effect " + effectEntry.effect + " to " + mob.getDisplayName().getString() + "!");
        AuthorizeBloodshed.getLogger().warn("Effect either doesn't exist or is not applicable to the entity.");
      }
    }
    
    // this.custom.forEach(entry -> entry.applyToMob(mob));
  }

  public static Config serialize(EntityModifier pool) {
    CommentedConfig config = CommentedConfig.inMemory();
    config.set("entity", pool.entity.toString());

    config.set("attributes", pool.attributes.stream().map(EntrySerialization::serializeEntry).toList());
    config.set("equipment", pool.equipment.stream().map(EntrySerialization::serializeEntry).toList());
    config.set("effects", pool.effects.stream().map(EntrySerialization::serializeEntry).toList());
    // config.set("custom", pool.custom.stream().map(EntrySerialization::serializeEntry).toList());

    return config;
  }

  public static EntityModifier deserialize(Object obj) {
    if (obj instanceof Config config) {
      return deserialize(config);
    } else {
      throw new IllegalArgumentException("Expected Config, got " + obj.getClass().getSimpleName());
    }
  }

  public static EntityModifier deserialize(Config config) {

    EntityModifier pool = new EntityModifier(new ResourceLocation(config.get("entity")));
    config.<List<Config>>get("attributes").stream().map(entry -> EntrySerialization.<AttributeModifierEntry>deserializeEntry(entry, AttributeModifierEntry.identifier)).forEach(pool::addAttribute);
    config.<List<Config>>get("equipment").stream().map(entry -> EntrySerialization.<EquipmentEntry>deserializeEntry(entry, EquipmentEntry.identifier)).forEach(pool::addEquipment);
    config.<List<Config>>get("effects").stream().map(entry -> EntrySerialization.<EffectEntry>deserializeEntry(entry, EffectEntry.identifier)).forEach(pool::addEffect);
    // config.<List<Config>>get("custom").stream().map(EntrySerialization::<CustomEntry>deserializeEntry).forEach(pool::addCustom);

    return pool;
  }
}
