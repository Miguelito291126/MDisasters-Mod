package com.miguel.mdisasters.tabs;

import com.miguel.mdisasters.init.InitBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MDTab extends CreativeTabs {

    public MDTab() {
        super("mdisasters");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        // Validación de seguridad para evitar crashes si el bloque es null
        if (InitBlocks.VOLCANO_BLOCK != null) {
            return new ItemStack(InitBlocks.VOLCANO_BLOCK);
        }
        return new ItemStack(Items.LAVA_BUCKET); // Icono de respaldo si aún no carga el bloque
    }
}