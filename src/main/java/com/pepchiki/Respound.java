package com.pepchiki;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;

public class Respound implements ModInitializer {
    public static final String MOD_ID = "respound";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Identifier SOUND_ID = new Identifier(MOD_ID, "respawn_sound");
    public static final SoundEvent RESPAWN_SOUND = SoundEvent.of(SOUND_ID);

    @Override
    public void onInitialize() {
        // Регистрируем звук
        Registry.register(Registries.SOUND_EVENT, SOUND_ID, RESPAWN_SOUND);

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if(entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                entity.getWorld().playSound(
                    null, // null = слышат все игроки рядом
                    player.getSpawnPointPosition(), // Позиция воспроизведения
                    RESPAWN_SOUND,
                    entity.getSoundCategory(), // Категория звука (обычно PLAYER)
                    4.0F, // Громкость
                    1.0F  // Тон
                );
            }
            return true;
        });

        LOGGER.info("Respound mod initialized!");
    }
}