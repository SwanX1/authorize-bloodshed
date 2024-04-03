package dev.cernavskis.authorizebloodshed.core.event;

import dev.cernavskis.authorizebloodshed.AuthorizeBloodshed;
import net.minecraftforge.eventbus.api.IEventBus;

public abstract class EventListenerObject {
  public void onModInitialization(IEventBus eventBus) {
    AuthorizeBloodshed.getLogger().info("Registering events in {}...", this.getClass().getSimpleName());
    eventBus.register(this);
  }
}
