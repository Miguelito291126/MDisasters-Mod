package com.miguel.mdisasters.events;

import com.miguel.mdisasters.init.InitBiomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TerrainEventHandler {
    @SubscribeEvent
    public void onTerrainPopulate(PopulateChunkEvent.Pre.Populate event) {
        if (event.getType() == PopulateChunkEvent.Populate.EventType.LAKE) {
            int x = event.getChunkX() * 16 + 8;
            int z = event.getChunkZ() * 16 + 8;

            BlockPos pos = new BlockPos(x, 64, z);
            Biome biome = event.getWorld().getBiome(pos);

            if (biome == InitBiomes.VOLCANO_BIOME) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void onBiomeDecorate(DecorateBiomeEvent.Decorate event) {
        if (event.getType() == DecorateBiomeEvent.Decorate.EventType.LAKE_WATER) {

            BlockPos pos = event.getPos();
            Biome biome = event.getWorld().getBiome(pos);

            if (biome == InitBiomes.VOLCANO_BIOME) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}