package com.miguel.mdisasters.init;

import com.miguel.mdisasters.objects.blocks.blockVolcano;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import java.util.ArrayList;
import java.util.List;


public class InitBlocks {
    public static List<Block> BLOCKS = new ArrayList<Block>();
    public static Block VOLCANO_BLOCK = new blockVolcano("volcano_block", Material.ROCK);
}
