package com.miguel.mdisasters;

import com.miguel.mdisasters.proxy.CommonProxy;
import com.miguel.mdisasters.tabs.MDTab;
import com.miguel.mdisasters.world.WorldTypeVolcano;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = MDMain.MODID, name = MDMain.NAME, version = MDMain.VERSION, useMetadata = true)
public class MDMain
{
    public static final String MODID = "mdisasters";
    public static final String NAME = "MDisasters";
    public static final String VERSION = "2.0.3.3";
    public static final String CLIENT_PROXY_CLASS = "com.miguel.mdisasters.proxy.ClientProxy";
    public static final String COMMON_PROXY_CLASS = "com.miguel.mdisasters.proxy.CommonProxy";

    private static Logger logger;

    public static CreativeTabs MD_TAB;
    public static WorldType VOLCANO_WORLD;

    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = COMMON_PROXY_CLASS)
    public static CommonProxy proxy;


    @Mod.Instance
    public static MDMain instance;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        logger.info("MDMain preInit cargado!");

        MD_TAB = new MDTab();
        VOLCANO_WORLD = new WorldTypeVolcano();

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void Init(FMLInitializationEvent event)
    {
        logger.info("MDMain init cargado!");
        proxy.Init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        logger.info("MDMain postInit cargado!");
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    public static Logger getLogger() {
        return logger;
    }

}
