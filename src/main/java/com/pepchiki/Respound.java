// src/main/java/com/pepchiki/Respound.java
package com.pepchiki;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Respound.MOD_ID)
public class Respound {
    public static final String MOD_ID = "respound";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    // Регистрация звуковых событий
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = 
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
        
    // Создание звукового события для "respawn_sound"
    public static final RegistryObject<SoundEvent> RESPAWN_SOUND = SOUND_EVENTS.register("respawn_sound",
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "respawn_sound")));

    public Respound() {
        LOGGER.info("Initializing Respound Mod");
        
        // Получаем шину событий FML
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Регистрируем наши звуки на шине событий мода
        SOUND_EVENTS.register(modEventBus);
        
        // Добавляем слушатель для настройки клиентских компонентов
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);
        
        // Регистрируем глобальную шину событий Forge для отслеживания событий в игре
        MinecraftForge.EVENT_BUS.register(this);
        
        LOGGER.info("Respound initialization complete");
    }
    
    // Настройка общих компонентов
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Respound common setup");
    }
    
    // Настройка клиентских компонентов
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Respound client setup - registering RespoundClient events");
        MinecraftForge.EVENT_BUS.register(RespoundClient.class);
    }
}