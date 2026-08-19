package com.miguel.mdisasters.objects.items;

import com.miguel.mdisasters.MDMain;
import com.miguel.mdisasters.init.InitItems;
import com.miguel.mdisasters.objects.entities.EntityMeteor;
import com.miguel.mdisasters.tabs.MDTab;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemMeteorsSpawner extends Item {

    public ItemMeteorsSpawner(String Name) {
        this.setRegistryName(Name);
        this.setUnlocalizedName(Name);
        this.setCreativeTab(MDMain.MD_TAB);

        InitItems.ITEMS.add(this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        // Raytrace de mayor alcance (hasta 100 bloques)
        RayTraceResult rayTrace = customRayTrace(world, player, 100.0D);

        if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos targetPos = rayTrace.getBlockPos().offset(rayTrace.sideHit);

            if (!world.isRemote) {
                // Instancia correcta usando (world, player)
                EntityMeteor meteor = new EntityMeteor(world, player);

                // Posicionamos el meteorito 30 bloques por encima del punto seleccionado
                meteor.setLocationAndAngles(
                        targetPos.getX() + 0.5,
                        targetPos.getY() + 30.0,
                        targetPos.getZ() + 0.5,
                        player.rotationYaw,
                        0.0F
                );

                world.spawnEntity(meteor);
            }

            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    private RayTraceResult customRayTrace(World world, EntityPlayer player, double distance) {
        Vec3d eyeVec = player.getPositionEyes(1.0F);
        Vec3d lookVec = player.getLook(1.0F);
        Vec3d endVec = eyeVec.addVector(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
        return world.rayTraceBlocks(eyeVec, endVec, false, false, true);
    }
}