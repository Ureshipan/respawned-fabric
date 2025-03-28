// src/main/java/com/pepchiki/Respound.java
package com.pepchiki;

import net.fabricmc.api.ModInitializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Respound implements ModInitializer {
    public static final String MOD_ID = "respound";
    public static final SoundEvent RESPAWN_SOUND = SoundEvent.of(new Identifier(MOD_ID, "respawn_sound"));

    @Override
    public void onInitialize() {
        Registry.register(Registries.SOUND_EVENT, new Identifier(MOD_ID, "respawn_sound"), RESPAWN_SOUND);
    }
}