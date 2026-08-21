package com.miguel.mdisasters.init;

import com.miguel.mdisasters.MDMain;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class InitSounds {

    public static List<SoundEvent> SOUNDS = new ArrayList<SoundEvent>();
    public static SoundEvent EARTHQUAKE;
    public static SoundEvent TORNADO;

    public static SoundEvent registerSound(String soundName) {
        ResourceLocation location = new ResourceLocation(MDMain.MODID, soundName);
        SoundEvent sound = new SoundEvent(location);
        sound.setRegistryName(location);
        return sound;
    }
}