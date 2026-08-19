package com.miguel.mdisasters.world;

import com.miguel.mdisasters.init.InitBiomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class ModWorldGen implements IWorldGenerator {

    private final WorldGenerator volcanoGen = new WorldGenVolcano();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        // Asegurarse de que solo se genere en el Overworld (dimensión 0)
        if (world.provider.getDimension() == 0) {
            generateVolcano(world, random, chunkX, chunkZ);
        }
    }

    private void generateVolcano(World world, Random random, int chunkX, int chunkZ) {
        // Convertimos las coordenadas de Chunk a coordenadas de bloques
        int x = (chunkX * 16) + random.nextInt(16) + 8;
        int z = (chunkZ * 16) + random.nextInt(16) + 8;
        BlockPos pos = new BlockPos(x, 0, z);

        // Verificamos si el bioma en estas coordenadas es el bioma de volcán
        if (world.getBiome(pos) == InitBiomes.VOLCANO_BIOME) {
            // Probabilidad de aparición por chunk (por ejemplo, 1 entre 12 chunks dentro del bioma)
            if (random.nextInt(12) == 0) {
                volcanoGen.generate(world, random, pos);
            }
        }
    }
}