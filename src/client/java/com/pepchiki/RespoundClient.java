// src/client/java/com/pepchiki/RespoundClient.java
package com.pepchiki;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;


public class RespoundClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("respound");
    private BlockPos lastPlayerPos = BlockPos.ORIGIN;
    private float lastHealth = 20.0F;
    private boolean wasTeleported = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) return;

            BlockPos currentPos = player.getBlockPos();
            float currentHealth = player.getHealth();

            // Обнаружение телепортации (резкое изменение позиции)
            boolean isTeleported = !currentPos.isWithinDistance(lastPlayerPos, 10) && player.getAbilities().flying == false;

            // Обнаружение "мгновенного респавна" (здоровье было 0, теперь >0)
            boolean instantRespawn = lastHealth <= 0 && currentHealth > 0;

            if ((isTeleported || instantRespawn) && !wasTeleported) {
                // Воспроизводим звук вокруг игрока
                player.getWorld().playSound(
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    Respound.RESPAWN_SOUND,
                    player.getSoundCategory(),
                    7.0F,
                    1.0F,
                    false
                );
                wasTeleported = true;
            } else if (currentPos.equals(lastPlayerPos)) {
                wasTeleported = false;
            }

            lastPlayerPos = currentPos;
            lastHealth = currentHealth;
        });
    }
}