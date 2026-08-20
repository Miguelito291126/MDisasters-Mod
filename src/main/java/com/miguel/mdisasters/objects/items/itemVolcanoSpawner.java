package com.miguel.mdisasters.objects.items;

import com.miguel.mdisasters.MDMain;
import com.miguel.mdisasters.init.InitBlocks;
import com.miguel.mdisasters.init.InitItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class itemVolcanoSpawner extends Item {

    public itemVolcanoSpawner(String name) {
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(MDMain.MD_TAB);

        InitItems.ITEMS.add(this);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            // Instancia de Random obtenida del World
            Random rand = world.rand;

            // Dimensiones aleatorias del volcán
            int height = 30 + rand.nextInt(16);
            int maxRadius = 18 + rand.nextInt(10);

            // Posicionamiento enfrente del jugador
            EnumFacing lookFacing = player.getHorizontalFacing();
            int distanceAhead = maxRadius + 2;
            BlockPos startPos = pos.offset(facing).offset(lookFacing, distanceAhead);

            // Generación de la estructura del volcán
            for (int y = 0; y <= height; y++) {
                double progress = (double) y / height;
                double radiusFactor = Math.pow(1.0 - progress, 1.5);
                double currentRadius = maxRadius * radiusFactor;
                double craterRadius = 2.0 + (progress * 1.5);

                for (int x = (int) -currentRadius - 2; x <= currentRadius + 2; x++) {
                    for (int z = (int) -currentRadius - 2; z <= currentRadius + 2; z++) {

                        double dist = Math.sqrt(x * x + z * z);
                        double noise = (rand.nextDouble() - 0.5) * 1.5;

                        if (dist + noise <= currentRadius) {
                            BlockPos blockPos = startPos.add(x, y, z);

                            if (dist <= craterRadius && y > 2) {
                                world.setBlockState(blockPos, Blocks.LAVA.getDefaultState(), 2);
                            } else {
                                world.setBlockState(blockPos, getRandomVolcanicBlock(rand), 2);
                            }
                        }
                    }
                }
            }


        }
        // Consumir el ítem si no está en modo Creativo
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }

        return EnumActionResult.SUCCESS;
    }

    private IBlockState getRandomVolcanicBlock(Random rand) {
        int chance = rand.nextInt(100);

        if (chance < 50) {
            return InitBlocks.VOLCANO_BLOCK.getDefaultState();
        } else if (chance < 75) {
            return Blocks.OBSIDIAN.getDefaultState();
        } else if (chance < 90) {
            return Blocks.MAGMA.getDefaultState();
        } else {
            return Blocks.GRAVEL.getDefaultState();
        }
    }
}