package com.miguel.mdisasters.init;

import com.miguel.mdisasters.MDMain;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import java.util.ArrayList;
import java.util.List;

public class InitEntities {
    private static int id = 0; // contador interno de IDs de entidades

    public static List<Entity> ENTITIES = new ArrayList<Entity>();

    public static void registerEntity(String name, Class<? extends Entity> entityClass, int range, int updateFrequency, boolean sendsVelocityUpdates) {
        EntityRegistry.registerModEntity(
                new ResourceLocation(MDMain.MODID, name),
                entityClass,
                name,
                id++,
                MDMain.instance,
                range,
                updateFrequency,
                sendsVelocityUpdates
        );
    }
}
