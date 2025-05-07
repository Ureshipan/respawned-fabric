package com.pepchiki;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Respound.MOD_ID, value = Dist.CLIENT)
public class RespoundClient {
    
    private static boolean wasPlayerDead = false;
    private static int tickCounter = 0;
    private static boolean initialized = false;
    private static double lastX = 0;
    private static double lastY = 0;
    private static double lastZ = 0;
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        tickCounter++;
        if (tickCounter % 10 != 0) return;
        
        // Play on first join
        if (!initialized) {
            initialized = true;
            lastX = mc.player.getX();
            lastY = mc.player.getY();
            lastZ = mc.player.getZ();
            playSound(mc.player);
            return;
        }
        
        // Check for teleport
        double dx = mc.player.getX() - lastX;
        double dy = mc.player.getY() - lastY;
        double dz = mc.player.getZ() - lastZ;
        double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
        
        if (distance > 15.0) {
            playSound(mc.player);
        }
        
        // Check for respawn
        if (wasPlayerDead && mc.player.getHealth() > 0) {
            playSound(mc.player);
            wasPlayerDead = false;
        }
        
        // Update player state
        if (mc.player.getHealth() <= 0) {
            wasPlayerDead = true;
        }
        
        lastX = mc.player.getX();
        lastY = mc.player.getY();
        lastZ = mc.player.getZ();
    }
    
    private static void playSound(Player player) {
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
    }
} 