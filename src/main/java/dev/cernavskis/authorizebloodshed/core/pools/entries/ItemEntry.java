package dev.cernavskis.authorizebloodshed.core.pools.entries;

import java.util.List;

import javax.annotation.Nullable;

import com.electronwill.nightconfig.core.Config;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.cernavskis.authorizebloodshed.core.pools.util.Entries;
import dev.cernavskis.authorizebloodshed.core.pools.util.NumberRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemEntry extends WeightedEntry {
  public final ResourceLocation item;
  private Item itemCache;
  public final String nbt;
  private CompoundTag nbtCache;
  public final Entries<EnchantmentEntry> enchantments = new Entries<>();
  public final NumberRange<Integer> damage;
  public final NumberRange<Integer> count;

  public ItemEntry(int weight, ResourceLocation item, String nbt, NumberRange<Integer> damage, NumberRange<Integer> count) {
    super(weight);
    this.item = item;
    this.nbt = nbt;
    this.damage = damage;
    this.count = count;
  }

  public int getItemCount(RandomSource random) {
    return this.count.getRandom(random);
  }

  public int getItemDamage(RandomSource random) {
    return this.damage.getRandom(random);
  }

  public ItemEntry addEnchantmentEntry(EnchantmentEntry enchantmentEntry) {
    this.enchantments.add(enchantmentEntry);
    return this;
  }

  @Nullable
  public EnchantmentEntry getEnchantment(RandomSource random) {
    return this.enchantments.getRandomWeighted(random);
  }

  private Item getItem() {
    if (this.itemCache == null) {
      this.itemCache = ForgeRegistries.ITEMS.getValue(this.item);
    }
    return this.itemCache;
  }

  private CompoundTag getNbt() {
    if (this.nbtCache == null) {
      try {
        this.nbtCache = TagParser.parseTag(this.nbt);
      } catch (CommandSyntaxException e) {
        throw new IllegalArgumentException("Invalid NBT tag: " + this.nbt, e);
      }
    }
    return this.nbtCache;
  }

  public ItemStack createItem(RandomSource random) {
    if (this.getItem() == null) {
      throw new IllegalStateException("Item not found: " + this.item);
    }

    ItemStack stack = new ItemStack(this.getItem());
    stack.setCount(this.getItemCount(random));
    stack.setDamageValue(this.getItemDamage(random));
    if (!this.nbt.isEmpty()) {
      stack.setTag(this.getNbt());
    }

    EnchantmentEntry enchantmentEntry = this.getEnchantment(random);
    if (enchantmentEntry != null) {
      enchantmentEntry.applyToItemStack(stack, random);
    }

    return stack;
  }

  public static ItemEntry deserialize(Config config) {
    ItemEntry itemEntry = new ItemEntry(
      config.getIntOrElse("weight", 1),
      new ResourceLocation(config.get("item")),
      config.getOrElse("nbt", ""),
      NumberRange.deserialize(config.getOrElse("damage", 0)).toInt(),
      NumberRange.deserialize(config.getOrElse("count", 1)).toInt()
    );

    config.<List<Config>>getOrElse("enchantments", List.of()).stream().map(EnchantmentEntry::deserialize).forEach(itemEntry::addEnchantmentEntry);

    return itemEntry;
  }

  public static Config serialize(ItemEntry itemEntry) {
    Config config = Config.inMemory();
    config.set("weight", itemEntry.weight);
    config.set("item", itemEntry.item.toString());
    if (!itemEntry.nbt.isEmpty()) {
      try {
        TagParser.parseTag(itemEntry.nbt);
      } catch (CommandSyntaxException e) {
        throw new IllegalArgumentException("Invalid NBT tag: " + itemEntry.nbt, e);
      }
      config.set("nbt", itemEntry.nbt);
    }
    config.set("enchantments", itemEntry.enchantments.stream().map(EnchantmentEntry::serialize).toList());
    config.set("damage", NumberRange.serialize(itemEntry.damage));
    config.set("count", NumberRange.serialize(itemEntry.count));
    return config;
  }
}