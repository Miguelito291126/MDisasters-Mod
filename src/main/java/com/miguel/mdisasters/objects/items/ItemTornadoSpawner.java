package com.miguel.mdisasters.objects.items;

import com.miguel.mdisasters.MDMain;
import com.miguel.mdisasters.init.InitItems;
import com.miguel.mdisasters.objects.entities.EntityTornado;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemTornadoSpawner extends Item
{
    public ItemTornadoSpawner(String Name) {
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
                EntityTornado tornado = new EntityTornado(world, player);

                tornado.setLocationAndAngles(
                        targetPos.getX() + 0.5,
                        targetPos.getY(),
                        targetPos.getZ() + 0.5,
                        player.rotationYaw,
                        0.0F
                );


                world.spawnEntity(tornado); // spawnea la entidad en el servidor
            }

            if (!player.capabilities.isCreativeMode)
            {
                stack.shrink(1);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

}
