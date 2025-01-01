package dev.cernavskis.authorizebloodshed.core.pools.entries;

import com.electronwill.nightconfig.core.Config;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class EquipmentEntry extends WeightedEntry {
  public static final String identifier = "equipment";

  public final ItemEntry item;
  public final EquipmentSlot slot;

  public EquipmentEntry(int weight, ItemEntry item, EquipmentSlot slot) {
    super(weight);
    this.item = item;
    this.slot = slot;
  }

  public static EquipmentEntry deserialize(Config config) {
    return new EquipmentEntry(
      config.getIntOrElse("weight", 1),
      ItemEntry.deserialize(config.get("item")),
      EquipmentSlot.byName(config.get("slot"))
    );
  }

  public static Config serialize(EquipmentEntry entry) {
    Config config = Config.inMemory();
    config.set("weight", entry.weight);
    config.set("item", ItemEntry.serialize(entry.item));
    config.set("slot", entry.slot.getName());
    return config;
  }

  public boolean applyToMob(Mob mob) {
    ItemStack stack = this.item.createItem(mob.getRandom());
    if (stack == null) {
      return false;
    }
    mob.setItemSlot(this.slot, stack);
    return true;
  }
}