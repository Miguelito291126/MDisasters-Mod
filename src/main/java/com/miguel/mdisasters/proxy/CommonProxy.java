package com.miguel.mdisasters.proxy;

import com.miguel.mdisasters.MDMain;
import com.miguel.mdisasters.commands.CommandClearDisasters;
import com.miguel.mdisasters.events.TerrainEventHandler;
import com.miguel.mdisasters.init.InitBiomes;
import com.miguel.mdisasters.world.ModWorldGen;
import com.miguel.mdisasters.world.WorldTypeVolcano;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        InitBiomes.registerBiomes();
        GameRegistry.registerWorldGenerator(new ModWorldGen(), 0);
        MinecraftForge.TERRAIN_GEN_BUS.register(new TerrainEventHandler());

    }

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandClearDisasters());
    }

    public void Init(FMLInitializationEvent e) {
        MDMain.VOLCANO_WORLD = new WorldTypeVolcano();
    }

    public void postInit(FMLPostInitializationEvent e) {
    }
}