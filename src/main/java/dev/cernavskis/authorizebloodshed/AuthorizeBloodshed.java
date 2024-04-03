// Copyright (c) 2024 Kārlis Čerņavskis, All Rights Reserved unless otherwise explicitly stated.
package dev.cernavskis.authorizebloodshed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.cernavskis.authorizebloodshed.core.event.EventListenerObject;
import dev.cernavskis.authorizebloodshed.core.event.ForgeEvents;
import dev.cernavskis.authorizebloodshed.core.event.ModEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AuthorizeBloodshed.MOD_ID)
public class AuthorizeBloodshed {
  private static final Logger LOGGER = LogManager.getLogger();
  public static final String MOD_ID = "authorizebloodshed";

  public AuthorizeBloodshed() {
    AuthorizeBloodshed.getLogger().info("Initializing Authorize Bloodshed");
    
    IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
    EventListenerObject forgeEvents = new ForgeEvents();
    forgeEvents.onModInitialization(forgeEventBus);

    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    EventListenerObject modEvents = new ModEvents();
    modEvents.onModInitialization(modEventBus);
  }

  public static Logger getLogger() {
    // Check if the calling class is in the same package as this class
    StackTraceElement callingClass = Thread.currentThread().getStackTrace()[2];
    String ourPackage = AuthorizeBloodshed.class.getPackageName();

    if (!callingClass.getClassName().startsWith(ourPackage)) {
      throw new SecurityException("A class outside of the " + ourPackage + " package tried to access the logger!");
    }
    return AuthorizeBloodshed.LOGGER;
  }
}
