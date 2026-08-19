package com.miguel.mdisasters.tabs;

import com.miguel.mdisasters.init.InitBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MDTab extends CreativeTabs {

    public MDTab() {
        super("mdisasters");
    }


    public ItemStack createIcon() {
        // Validación de seguridad para evitar crashes si el bloque es null
        if (InitBlocks.VOLCANO_BLOCK != null) {
            return new ItemStack(InitBlocks.VOLCANO_BLOCK);
        }
        return new ItemStack(Items.LAVA_BUCKET); // Icono de respaldo si aún no carga el bloque
    }

    @Override
    public ItemStack getTabIconItem() {
        return this.createIcon();
    }
}