// Copyright (c) 2024 Kārlis Čerņavskis, All Rights Reserved unless otherwise explicitly stated.
package dev.cernavskis.authorizebloodshed;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infernalstudios.config.Config;
import org.infernalstudios.config.Config.ReloadStage;

import com.electronwill.nightconfig.core.io.ParsingException;

import dev.cernavskis.authorizebloodshed.config.ABConfig;
import dev.cernavskis.authorizebloodshed.core.event.EventListenerObject;
import dev.cernavskis.authorizebloodshed.core.event.ForgeEvents;
import dev.cernavskis.authorizebloodshed.core.event.ModEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

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
    
    
    try {
      ABConfig.INSTANCE = Config
      .builder(FMLPaths.CONFIGDIR.get().resolve("foodeffects-common.toml"))
      .loadClass(ABConfig.class)
      .build();
    } catch (IllegalStateException | IllegalArgumentException | IOException | ParsingException e) {
      throw new RuntimeException(
        "Failed to load Authorize Bloodshed config" +
          (e instanceof ParsingException ? ", try fixing/deleting your config file" : ""),
        e
      );
    }
    
    ABConfig.INSTANCE.onReload(stage -> {
      if (stage == ReloadStage.PRE) {
        AuthorizeBloodshed.getLogger().debug("Reloading Authorize Bloodshed config");
      }
    });
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
