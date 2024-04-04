package dev.cernavskis.authorizebloodshed.config;

import java.util.List;

import org.infernalstudios.config.Config;
import org.infernalstudios.config.annotation.Category;
import org.infernalstudios.config.annotation.Configurable;
import org.infernalstudios.config.annotation.ListValue;

import dev.cernavskis.authorizebloodshed.core.pools.EntityModifier;
import dev.cernavskis.authorizebloodshed.core.pools.entries.AttributeModifierEntry;
import dev.cernavskis.authorizebloodshed.core.pools.entries.EffectEntry;
import dev.cernavskis.authorizebloodshed.core.pools.entries.EnchantmentEntry;
import dev.cernavskis.authorizebloodshed.core.pools.entries.EquipmentEntry;
import dev.cernavskis.authorizebloodshed.core.pools.entries.ItemEntry;
import dev.cernavskis.authorizebloodshed.core.pools.util.NumberRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;

public class ABConfig {
  public static Config INSTANCE;

  @Category("ai")
  public static class AI {
    
    @Category("runfromexplodingcreepers")
    public static class RunFromExplodingCreepers {
      @Configurable(description = "Should AI run from exploding creepers?")
      public static boolean enabled = true;
    
      @Configurable(description = "Detection range (in blocks) for exploding creepers")
      public static int detectionRange = 10;
    }
  
    @Category("endermenchase")
    public static class EndermenChase {
      @Configurable(description = "Should Endermen chase slower when they're aggroed?")
      public static boolean enabled = true;
      @Configurable(description = "Speed multiplier for Endermen when they're aggroed")
      public static double speedMultiplier = 0.8;
    }
  
    @Category("rabbitpanic")
    public static class RabbitPanic {
      @Configurable(description = "Should rabbits move slower when they're panicking?")
      public static boolean enabled = true;

      @Configurable(description = "Speed multiplier for rabbits when they're panicking (when hurt or when avoiding a player)")
      public static double speedMultiplier = 0.65;
    }

    @Category("creepersstayclose")
    public static class CreepersStayClose {
      @Configurable(description = "Should creepers stay close to their target when they're about to explode?")
      public static boolean enabled = true;
    
      @Configurable(description = "Speed at which creepers should move when staying close to their target")
      public static double walkSpeed = 1.2D;
    
      @Configurable(description = "Should creepers sprint to stay close to their target?")
      public static boolean sprintEnabled = true;
    
      @Configurable(description = "Speed at which creepers should sprint when staying close to their target")
      public static double sprintSpeed = 1.6D;
    }
  }

  @Configurable(showDefault = false)
  @ListValue(
    serialize = "dev.cernavskis.authorizebloodshed.core.pools.EntityModifier::serialize",
    deserialize = "dev.cernavskis.authorizebloodshed.core.pools.EntityModifier::deserialize"
  )
  public static List<EntityModifier> entityModifier = List.of(
    new EntityModifier(new ResourceLocation("minecraft:zombie"))
      .addAttribute(new AttributeModifierEntry(1, ForgeRegistries.ATTRIBUTES.getKey(Attributes.MOVEMENT_SPEED), NumberRange.ofDouble(5.0D, 5.0D)))
      .addEffect(new EffectEntry(1, ForgeRegistries.MOB_EFFECTS.getKey(MobEffects.GLOWING), NumberRange.ofInt(1, 1)))
      .addEquipment(new EquipmentEntry(
        1,
        new ItemEntry(1, ForgeRegistries.ITEMS.getKey(Items.STICK), "", NumberRange.ofInt(0, 0), NumberRange.ofInt(1, 1))
          .addEnchantmentEntry(new EnchantmentEntry(1, ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.KNOCKBACK), NumberRange.ofInt(10, 10))),
        EquipmentSlot.MAINHAND
      ))
  );
}
