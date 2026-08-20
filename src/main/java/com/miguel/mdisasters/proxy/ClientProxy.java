package com.miguel.mdisasters.proxy;

import com.miguel.mdisasters.objects.entities.*;
import com.miguel.mdisasters.render.RenderEmpty;
import com.miguel.mdisasters.render.RenderMeteor;
import com.miguel.mdisasters.render.RenderTornado;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityMeteor.class, RenderMeteor::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTornado.class, RenderTornado::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTsunami.class, RenderEmpty::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTsunamiLava.class, RenderEmpty::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFlood.class, RenderEmpty::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityEarthquake.class, RenderEmpty::new);
    }
}