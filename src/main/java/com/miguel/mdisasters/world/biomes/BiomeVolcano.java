package com.miguel.mdisasters.world.biomes;

import com.miguel.mdisasters.init.InitBlocks;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class BiomeVolcano extends Biome {

    public BiomeVolcano() {
        super(new BiomeProperties("Volcanic Plains")
                .setBaseHeight(1.2F)       // Subimos la altura base (antes 0.8F)
                .setHeightVariation(0.3F)  // Reducimos depresiones profundas
                .setTemperature(2.0F)
                .setRainfall(0.0F)
                .setRainDisabled()
        );

        this.setRegistryName("volcanic_plains");

        // Capas del terreno:
        // Bloque superior de la superficie (en lugar de césped)
        this.topBlock = InitBlocks.VOLCANO_BLOCK.getDefaultState();

        // Bloque secundario debajo de la superficie (en lugar de tierra)
        this.fillerBlock = Blocks.MAGMA.getDefaultState();

        // Configuración de características de generación
        this.decorator.treesPerChunk = -999; // Desactivar la generación de árboles normales
        this.decorator.grassPerChunk = -999; // Desactivar el césped
        this.decorator.cactiPerChunk = -999;

        // Limpiar criaturas pacíficas normales (vacas, ovejas, etc.)
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();

        // Si quieres que aparezcan Magma Cubes o Blazes salvajes
        this.spawnableMonsterList.add(new SpawnListEntry(EntityMagmaCube.class, 8, 1, 3));
    }
    @Override
    public void decorate(World world, Random rand, BlockPos pos) {
        super.decorate(world, rand, pos);

        // Generar lagos/charcos de lava en la superficie (de 2 a 3 por chunk)
        if (net.minecraftforge.event.terraingen.TerrainGen.decorate(world, rand, pos, net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.CUSTOM)) {

            int lavaPools = 2 + rand.nextInt(2); // Cantidad de lagos

            for (int i = 0; i < lavaPools; i++) {
                int x = pos.getX() + rand.nextInt(16) + 8;
                int z = pos.getZ() + rand.nextInt(16) + 8;

                // Obtener el bloque más alto de la superficie en esa coordenada
                BlockPos surfacePos = world.getHeight(new BlockPos(x, 0, z));

                generateLavaPool(world, rand, surfacePos);
            }
        }
    }
    private void generateLavaPool(World world, Random rand, BlockPos surfacePos) {
        int radius = 3 + rand.nextInt(3); // Radio del lago (entre 3 y 5 bloques)

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                // Darle forma circular/orgánica
                if (x * x + z * z <= radius * radius) {

                    // Probabilidad para que los bordes sean irregulares (no un círculo perfecto)
                    if (rand.nextInt(4) != 0) {
                        // Bajamos 1 bloque bajo la superficie
                        BlockPos targetPos = surfacePos.add(x, -1, z);

                        // Solo reemplazamos si no es aire
                        if (!world.isAirBlock(targetPos)) {
                            // Ponemos el bloque de lava
                            world.setBlockState(targetPos, Blocks.LAVA.getDefaultState(), 2);

                            // Asegurarnos de limpiar el bloque justo encima para que la lava sea visible
                            world.setBlockState(targetPos.up(), Blocks.AIR.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
    }
}