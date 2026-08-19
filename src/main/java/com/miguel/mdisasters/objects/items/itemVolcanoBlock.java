package com.miguel.mdisasters.objects.items;

import com.miguel.mdisasters.MDMain;
import com.miguel.mdisasters.init.InitItems;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class itemVolcanoBlock extends ItemBlock {

    public itemVolcanoBlock(Block block) {
        super(block);

        // Asigna el RegistryName exacto que tiene el Block
        if (block.getRegistryName() != null) {
            this.setRegistryName(block.getRegistryName());
        }
        this.setUnlocalizedName(block.getUnlocalizedName());
        this.setCreativeTab(MDMain.MD_TAB);

        InitItems.ITEMS.add(this);
    }
}