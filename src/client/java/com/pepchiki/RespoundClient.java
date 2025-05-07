// src/client/java/com/pepchiki/RespoundClient.java
package com.pepchiki;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ClientTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Respound.MOD_ID, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class RespoundClient {
    public static final Logger LOGGER = LoggerFactory.getLogger("respound");
    private static BlockPos lastPlayerPos = BlockPos.ORIGIN;
    private static float lastHealth = 20.0F;
    private static boolean wasTeleported = false;
    private static boolean firstTick = true;
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        
        // Пропускаем, если игрок отсутствует или это первый тик после входа
        if (player == null) return;
        
        // Пропускаем первые несколько тиков, чтобы избежать ложных срабатываний при загрузке мира
        if (firstTick) {
            firstTick = false;
            lastPlayerPos = player.blockPosition();
            lastHealth = player.getHealth();
            LOGGER.info("RespoundClient initialized, waiting for events");
            return;
        }
        
        tickCounter++;
        if (tickCounter % 20 == 0) { // Проверяем раз в секунду для отладки
            LOGGER.debug("Player health: {}, pos: {}", player.getHealth(), player.blockPosition());
        }

        BlockPos currentPos = player.blockPosition();
        float currentHealth = player.getHealth();

        // Обнаружение телепортации (резкое изменение позиции)
        boolean isTeleported = !currentPos.closerThan(lastPlayerPos, 10) && 
                              !player.getAbilities().flying && 
                              currentPos.distSqr(lastPlayerPos) > 100; // Минимум 10 блоков

        // Обнаружение "мгновенного респавна" (здоровье было 0 или близко к 0, теперь >0)
        boolean wasNearDeath = lastHealth <= 1.0F;
        boolean instantRespawn = wasNearDeath && currentHealth > lastHealth + 5.0F;

        if ((isTeleported || instantRespawn) && !wasTeleported) {
            LOGGER.info("Detected event - Teleport: {}, Respawn: {}", isTeleported, instantRespawn);
            LOGGER.info("Last pos: {}, Current pos: {}, Distance: {}", 
                      lastPlayerPos, currentPos, Math.sqrt(currentPos.distSqr(lastPlayerPos)));
            LOGGER.info("Last health: {}, Current health: {}", lastHealth, currentHealth);
            
            // Воспроизводим звук вокруг игрока
            player.level().playSound(
                player,                           // Кто должен слышать
                player.getX(),                    // X позиция
                player.getY(),                    // Y позиция
                player.getZ(),                    // Z позиция
                Respound.RESPAWN_SOUND.get(),     // Звук
                SoundSource.PLAYERS,              // Категория звука
                1.0F,                             // Громкость
                1.0F                              // Высота (питч)
            );
            
            // Для отладки, воспроизведем стандартный звук Minecraft, чтобы убедиться что звуки вообще работают
            player.level().playSound(
                player,
                player.getX(),
                player.getY(),
                player.getZ(),
                net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
            );
            
            wasTeleported = true;
            LOGGER.info("Played sound effect!");
        } else if (!currentPos.equals(lastPlayerPos) && wasTeleported && currentPos.distSqr(lastPlayerPos) < 5) {
            // Сбрасываем флаг только когда игрок немного переместился после телепортации
            wasTeleported = false;
            LOGGER.debug("Reset teleport flag");
        }

        lastPlayerPos = currentPos;
        lastHealth = currentHealth;
    }
    
    // Отлавливаем событие входа в игру
    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        LOGGER.info("Player logged in, playing sound");
        
        // Сбрасываем значения для нового входа
        firstTick = true;
        wasTeleported = false;
        
        // Воспроизводим звук с задержкой через 10 тиков (полсекунды)
        Minecraft.getInstance().tell(() -> {
            try {
                Thread.sleep(500); // Подождем полсекунды для загрузки мира
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    player.level().playSound(
                        player,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        Respound.RESPAWN_SOUND.get(),
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F
                    );
                    LOGGER.info("Played login sound!");
                }
            } catch (Exception e) {
                LOGGER.error("Error playing login sound", e);
            }
        });
    }
}