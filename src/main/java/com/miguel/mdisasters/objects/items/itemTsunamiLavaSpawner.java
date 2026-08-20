package com.miguel.mdisasters.objects.items;

import com.miguel.mdisasters.MDMain;
import com.miguel.mdisasters.init.InitItems;
import com.miguel.mdisasters.objects.entities.EntityTsunami;
import com.miguel.mdisasters.objects.entities.EntityTsunamiLava;
import com.mojang.realmsclient.dto.PlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import scala.tools.nsc.doc.model.Public;

public class itemTsunamiLavaSpawner extends Item {
    public itemTsunamiLavaSpawner(String Name) {
        this.setRegistryName(Name);
        this.setUnlocalizedName(Name);
        this.setCreativeTab(MDMain.MD_TAB);

        InitItems.ITEMS.add(this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        RayTraceResult raytrace = this.rayTrace(worldIn, playerIn, true);
        if (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            BlockPos targetPos = raytrace.getBlockPos().offset(raytrace.sideHit);
            if (!worldIn.isRemote)
            {
                EntityTsunamiLava tsunamilava = new EntityTsunamiLava(worldIn, playerIn);
                tsunamilava.setLocationAndAngles(
                        targetPos.getX() + 0.5,
                        targetPos.getY(),
                        targetPos.getZ() + 0.5,
                        playerIn.rotationYaw,
                        0.0F
                );

                worldIn.spawnEntity(tsunamilava);
            }

            if (!playerIn.capabilities.isCreativeMode) {
                stack.shrink(1);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }
}