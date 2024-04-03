// Copyright (c) 2024 Kārlis Čerņavskis, All Rights Reserved unless otherwise explicitly stated.
package dev.cernavskis.authorizebloodshed.core.event;

import dev.cernavskis.authorizebloodshed.entity.ABGoals;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEvents extends EventListenerObject {
  @SubscribeEvent
  public void onEntitySpawn(EntityJoinLevelEvent event) {
    if (event.getEntity() instanceof Mob mob) {
      ABGoals.addToNewEntity(mob);
    }
  }
}
