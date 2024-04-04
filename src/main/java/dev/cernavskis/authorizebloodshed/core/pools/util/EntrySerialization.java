package dev.cernavskis.authorizebloodshed.core.pools.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.electronwill.nightconfig.core.Config;

import dev.cernavskis.authorizebloodshed.core.pools.entries.AttributeModifierEntry;
import dev.cernavskis.authorizebloodshed.core.pools.entries.EffectEntry;
import dev.cernavskis.authorizebloodshed.core.pools.entries.EquipmentEntry;
import dev.cernavskis.authorizebloodshed.core.pools.entries.WeightedEntry;

public class EntrySerialization {
  private static Map<Class<? extends WeightedEntry>, String> serializableIdentifiers = new HashMap<>();
  private static Map<Class<? extends WeightedEntry>, Boolean> hasTypeField = new HashMap<>();
  private static Map<String, Function<Config, WeightedEntry>> deserializers = new HashMap<>();
  private static Map<Class<WeightedEntry>, Function<WeightedEntry, Config>> serializers = new HashMap<>();

  static {
    register(AttributeModifierEntry.class, AttributeModifierEntry.identifier, AttributeModifierEntry::serialize, AttributeModifierEntry::deserialize, false);
    register(EffectEntry.class, EffectEntry.identifier, EffectEntry::serialize, EffectEntry::deserialize, false);
    register(EquipmentEntry.class, EquipmentEntry.identifier, EquipmentEntry::serialize, EquipmentEntry::deserialize, false);
    // register(CustomEntry.class, "custom", CustomEntry::serialize, CustomEntry::deserialize, true);
  }

  @SuppressWarnings("unchecked")
  private static <T extends WeightedEntry> void register(Class<T> clazz, String serializableIdentifier, Function<T, Config> serializer, Function<Config, T> deserializer, boolean addTypeField) {
    deserializers.put(serializableIdentifier, (Function<Config, WeightedEntry>) deserializer);
    serializers.put((Class<WeightedEntry>) clazz, (Function<WeightedEntry, Config>) serializer);
    serializableIdentifiers.put(clazz, serializableIdentifier);
    hasTypeField.put(clazz, addTypeField);
  }

  public static Config serializeEntry(WeightedEntry entry) {
    Config serialized = serializers.get(entry.getClass()).apply((WeightedEntry) entry);
    if (hasTypeField.get(entry.getClass())) {
      serialized.set("type", serializableIdentifiers.get(entry.getClass()));
    }
    return serialized;
  }

  public static <T extends WeightedEntry> T deserializeEntry(Config serialized) {
    return deserializeEntry(serialized, serialized.get("type"));
  }
  
  @SuppressWarnings("unchecked")
  public static <T extends WeightedEntry> T deserializeEntry(Config serialized, String type) {
    if (!deserializers.containsKey(type)) {
      throw new IllegalArgumentException("Unknown type: " + type);
    }
    return (T) deserializers.get(type).apply(serialized);
  }
}
