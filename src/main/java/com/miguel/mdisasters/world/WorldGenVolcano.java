package com.miguel.mdisasters.world;

import com.miguel.mdisasters.init.InitBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenVolcano extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, BlockPos position) {
        BlockPos startPos = world.getHeight(position);

        // Volcanes masivos (altura entre 30 y 45 bloques, radio base entre 18 y 28 bloques)
        int height = 30 + rand.nextInt(16);
        int maxRadius = 18 + rand.nextInt(10);

        for (int y = 0; y <= height; y++) {
            // Factor de pendiente cóncava suave
            double progress = (double) y / height;
            double radiusFactor = Math.pow(1.0 - progress, 1.5);
            double currentRadius = maxRadius * radiusFactor;

            // Radio de la chimenea central de lava que se va estrechando
            double craterRadius = 2.0 + (progress * 1.5);

            for (int x = (int) -currentRadius - 2; x <= currentRadius + 2; x++) {
                for (int z = (int) -currentRadius - 2; z <= currentRadius + 2; z++) {

                    double dist = Math.sqrt(x * x + z * z);

                    // Añadimos pequeñas imperfecciones al radio con aleatoriedad (ruido orgánico)
                    double noise = (rand.nextDouble() - 0.5) * 1.5;

                    if (dist + noise <= currentRadius) {
                        BlockPos blockPos = startPos.add(x, y, z);

                        if (dist <= craterRadius && y > 2) {
                            // Cima/centro con columna de lava
                            world.setBlockState(blockPos, Blocks.LAVA.getDefaultState(), 2);
                        } else {
                            // Paleta de bloques variada para la montaña
                            world.setBlockState(blockPos, getRandomVolcanicBlock(rand), 2);
                        }
                    }
                }
            }
        }

        return true;
    }
    private IBlockState getRandomVolcanicBlock(Random rand) {
        int chance = rand.nextInt(100);

        if (chance < 50) {
            return InitBlocks.VOLCANO_BLOCK.getDefaultState(); // Bloque principal
        } else if (chance < 75) {
            return Blocks.OBSIDIAN.getDefaultState();         // Detalle oscuro
        } else if (chance < 90) {
            return Blocks.MAGMA.getDefaultState();            // Grietas incandescentes
        } else {
            return Blocks.GRAVEL.getDefaultState();           // Ceniza/grava volcánica
        }
    }
}