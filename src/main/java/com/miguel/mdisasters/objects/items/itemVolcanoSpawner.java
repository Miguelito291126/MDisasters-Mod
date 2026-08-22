package com.miguel.mdisasters.objects.items;

import com.miguel.mdisasters.MDMain;
import com.miguel.mdisasters.init.InitItems;
import com.miguel.mdisasters.world.WorldGenVolcano;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class itemVolcanoSpawner extends Item {

    public itemVolcanoSpawner(String name) {
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(MDMain.MD_TAB);

        InitItems.ITEMS.add(this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult rayTrace = this.rayTrace(world, player, true);

        if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos targetPos = rayTrace.getBlockPos().offset(rayTrace.sideHit);

            if (!world.isRemote) {
                WorldGenVolcano generator = new WorldGenVolcano();
                generator.generate(world, itemRand, targetPos);
            }

            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }
}