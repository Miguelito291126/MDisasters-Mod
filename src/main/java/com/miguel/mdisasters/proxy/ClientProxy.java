package com.miguel.mdisasters.proxy;
import com.miguel.mdisasters.objects.entities.EntityMeteor;
import com.miguel.mdisasters.objects.entities.EntityTornado;
import com.miguel.mdisasters.objects.entities.EntityTsunami;
import com.miguel.mdisasters.render.RenderEmpty;
import com.miguel.mdisasters.render.RenderMeteor;
import com.miguel.mdisasters.render.RenderTornado;
import com.miguel.mdisasters.render.RenderTsunami;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e)
    {
        super.preInit(e);
        RenderingRegistry.registerEntityRenderingHandler(EntityMeteor.class, RenderMeteor::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTornado.class, RenderTornado::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTsunami.class, RenderEmpty::new);
    }
}
