package com.miguel.mdisasters.world;

import com.miguel.mdisasters.init.InitBiomes;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldTypeVolcano extends WorldType {

    public WorldTypeVolcano() {
        super("volcano_world");
    }

    @Override
    public BiomeProvider getBiomeProvider(World world) {
        // Fuerza a cargar únicamente tu bioma
        return new BiomeProviderSingle(InitBiomes.VOLCANO_BIOME);
    }

    @Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
        return new ChunkGeneratorOverworld(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions);
    }
}