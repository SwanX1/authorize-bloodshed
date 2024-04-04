// Copyright (c) 2024 Kārlis Čerņavskis, All Rights Reserved unless otherwise explicitly stated.
package dev.cernavskis.authorizebloodshed.core.event;

import java.util.List;

import dev.cernavskis.authorizebloodshed.config.ABConfig;
import dev.cernavskis.authorizebloodshed.core.pools.EntityModifier;
import dev.cernavskis.authorizebloodshed.entity.ABGoals;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeEvents extends EventListenerObject {
  @SubscribeEvent
  public void onEntitySpawn(EntityJoinLevelEvent event) {
    if (event.getEntity() instanceof Mob mob) {
      ABGoals.addToNewEntity(mob);

      // Apply modifiers
      if (!event.loadedFromDisk()) {
        List<EntityModifier> modifiers = ABConfig.entityModifier.stream()
          .filter(modifier -> modifier.entity.equals(ForgeRegistries.ENTITY_TYPES.getKey(mob.getType())))
          .toList();
  
        modifiers.forEach(modifier -> modifier.applyToMob(mob));
      }
    }
  }
}
