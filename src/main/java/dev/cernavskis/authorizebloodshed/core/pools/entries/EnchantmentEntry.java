package dev.cernavskis.authorizebloodshed.core.pools.entries;

import com.electronwill.nightconfig.core.Config;

import dev.cernavskis.authorizebloodshed.core.pools.util.NumberRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

public class EnchantmentEntry extends WeightedEntry {
  public final ResourceLocation enchantment;
  private Enchantment enchantmentCache;
  public final NumberRange<Integer> level;
  
  public EnchantmentEntry(int weight, ResourceLocation enchantment, NumberRange<Integer> level) {
    super(weight);

    this.enchantment = enchantment;
    this.level = level;
  }

  public int getEnchantmentLevel(RandomSource random) {
    return this.level.getRandom(random);
  }

  private Enchantment getEnchantment() {
    if (this.enchantmentCache == null) {
      this.enchantmentCache = ForgeRegistries.ENCHANTMENTS.getValue(this.enchantment);
    }
    return this.enchantmentCache;
  }

  public void applyToItemStack(ItemStack stack, RandomSource random) {
    Enchantment enchantment = this.getEnchantment();
    if (enchantment == null) {
      throw new IllegalStateException("Enchantment not found: " + this.enchantment);
    }

    stack.enchant(enchantment, this.getEnchantmentLevel(random));
  }

  public static EnchantmentEntry deserialize(Config config) {
    return new EnchantmentEntry(
      config.getInt("weight"),
      new ResourceLocation(config.get("enchantment")),
      NumberRange.deserialize(config.get("level")).toInt()
    );
  }

  public static Config serialize(EnchantmentEntry entry) {
    Config config = Config.inMemory();
    config.set("weight", entry.weight);
    config.set("enchantment", entry.enchantment.toString());
    config.set("level", NumberRange.serialize(entry.level));
    return config;
  }
}
