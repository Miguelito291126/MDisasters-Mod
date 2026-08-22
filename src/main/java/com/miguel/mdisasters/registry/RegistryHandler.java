package com.miguel.mdisasters.registry;

import com.miguel.mdisasters.MDMain;
import com.miguel.mdisasters.init.InitBlocks;
import com.miguel.mdisasters.init.InitEntities;
import com.miguel.mdisasters.init.InitItems;
import com.miguel.mdisasters.init.InitSounds;
import com.miguel.mdisasters.objects.entities.*;
import com.miguel.mdisasters.render.RenderEmpty;
import com.miguel.mdisasters.render.RenderMeteor;
import com.miguel.mdisasters.render.RenderTornado;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class RegistryHandler {

    @Mod.EventBusSubscriber(modid = MDMain.MODID)
    public static class CommonRegistries {

        @SubscribeEvent
        public static void onBlockRegister(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(InitBlocks.BLOCKS.toArray(new Block[0]));
        }

        @SubscribeEvent
        public static void onItemRegister(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(InitItems.ITEMS.toArray(new Item[0]));
        }

        @SubscribeEvent
        public static void onEntityRegister(RegistryEvent.Register<EntityEntry> event) {
            InitEntities.registerEntity("meteor", EntityMeteor.class, 256, 3, true);
            InitEntities.registerEntity("tornado", EntityTornado.class, 256, 3, true);
            InitEntities.registerEntity("tsunami", EntityTsunami.class, 256, 3, true);
            InitEntities.registerEntity("tsunami_lava", EntityTsunamiLava.class, 256, 3, true);
            InitEntities.registerEntity("flood", EntityFlood.class, 256, 3, true);
            InitEntities.registerEntity("earthquake", EntityEarthquake.class, 256, 3, true);
            InitEntities.registerEntity("volcano", EntityVolcano.class, 256, 3, true);
        }

        @SubscribeEvent
        public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
            InitSounds.EARTHQUAKE = InitSounds.registerSound("earthquake");
            InitSounds.TORNADO = InitSounds.registerSound("tornado");

            InitSounds.SOUNDS.add(InitSounds.EARTHQUAKE);
            InitSounds.SOUNDS.add(InitSounds.TORNADO);

            event.getRegistry().registerAll(InitSounds.SOUNDS.toArray(new SoundEvent[0]));
        }
    }

    @Mod.EventBusSubscriber(modid = MDMain.MODID, value = Side.CLIENT)
    public static class ClientRegistries {

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void onModelRegister(ModelRegistryEvent event) {
            // Modelos de Items
            for (Item item : InitItems.ITEMS) {
                ModelLoader.setCustomModelResourceLocation(
                        item,
                        0,
                        new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory")
                );
            }

            // Modelos de Bloques
            for (Block block : InitBlocks.BLOCKS) {
                Item blockItem = Item.getItemFromBlock(block);
                if (blockItem != null && blockItem.getRegistryName() != null) {
                    ModelLoader.setCustomModelResourceLocation(
                            blockItem,
                            0,
                            new ModelResourceLocation(blockItem.getRegistryName(), "inventory")
                    );
                }
            }

            // Renderizado de Entidades
            RenderingRegistry.registerEntityRenderingHandler(EntityMeteor.class, RenderMeteor::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityTornado.class, RenderTornado::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityTsunami.class, RenderEmpty::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityTsunamiLava.class, RenderEmpty::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityFlood.class, RenderEmpty::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityEarthquake.class, RenderEmpty::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityVolcano.class, RenderEmpty::new);
        }
    }
}