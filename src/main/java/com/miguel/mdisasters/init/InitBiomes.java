package com.miguel.mdisasters.init;

import com.miguel.mdisasters.world.biomes.BiomeVolcano;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class InitBiomes {

    public static final List<Biome> BIOMES = new ArrayList<>();

    public static final Biome VOLCANO_BIOME = new BiomeVolcano();

    public static void registerBiomes() {
        initBiome(VOLCANO_BIOME, "Volcanic Plains", BiomeType.DESERT, 10, Type.HOT, Type.DRY, Type.MOUNTAIN, Type.DEAD);
    }

    private static void initBiome(Biome biome, String name, BiomeType biomeType, int weight, Type... types) {
        ForgeRegistries.BIOMES.register(biome);
        BIOMES.add(biome);

        // Añadir tipos al diccionario de Forge para compatibilidad con otros mods
        BiomeDictionary.addTypes(biome, types);

        // Registrar en el BiomeManager para que aparezca en la generación global del mundo
        // 'weight' es la rareza (ej. 10 es una rareza moderada similar al desierto/jungla)
        BiomeManager.addBiome(biomeType, new BiomeEntry(biome, weight));
        BiomeManager.addSpawnBiome(biome);
    }
}