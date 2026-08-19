package com.miguel.mdisasters.objects.items;

import com.miguel.mdisasters.MDMain;
import com.miguel.mdisasters.init.InitItems;
import com.miguel.mdisasters.objects.entities.EntityTsunami;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemTsunamiSpawner extends Item {

    public ItemTsunamiSpawner(String Name) {
        this.setRegistryName(Name);
        this.setUnlocalizedName(Name);
        this.setCreativeTab(MDMain.MD_TAB);

        InitItems.ITEMS.add(this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        // Raytrace para detectar el bloque al que mira el jugador (hasta 5 blocks de distancia)
        RayTraceResult rayTrace = this.rayTrace(world, player, true);

        if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos targetPos = rayTrace.getBlockPos().offset(rayTrace.sideHit);

            if (!world.isRemote) {
                EntityTsunami tsunami = new EntityTsunami(world, player);

                // Posicionar el tsunami en las coordenadas del bloque apuntado
                tsunami.setLocationAndAngles(
                        targetPos.getX() + 0.5,
                        targetPos.getY(),
                        targetPos.getZ() + 0.5,
                        player.rotationYaw,
                        0.0F
                );

                world.spawnEntity(tsunami);
            }

            // Consumir el ítem si el jugador está en modo supervivencia
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }
}