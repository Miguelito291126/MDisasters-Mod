package com.miguel.mdisasters.init;

import com.miguel.mdisasters.objects.items.*;
import net.minecraft.item.Item;
import java.util.ArrayList;
import java.util.List;


public class InitItems {

    public static List<Item> ITEMS = new ArrayList<Item>();
    public static Item TSUNAMI_ITEM = new ItemTsunamiSpawner("tsunami_spawn");
    public static Item TORNADO_ITEM = new ItemTornadoSpawner("tornado_spawn");
    public static Item METEORS_ITEM = new ItemMeteorsSpawner("meteors_spawn");
    public static Item VOLCANO_ITEM = new itemVolcanoSpawner("volcano_spawn");
    public static Item VOLCANO_BLOCK = new itemVolcanoBlock(InitBlocks.VOLCANO_BLOCK);

}
