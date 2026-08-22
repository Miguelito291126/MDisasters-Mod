package com.miguel.mdisasters.objects.blocks;

import com.miguel.mdisasters.MDMain;
import com.miguel.mdisasters.init.InitBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class blockVolcano extends Block {

    public blockVolcano(String name, Material material) {
        super(material);
        setUnlocalizedName(name); // Asigna "volcano_block"
        setRegistryName(name);    // Asigna "volcano_block"
        setCreativeTab(MDMain.MD_TAB);

        this.setHardness(1.5F);      // Dureza equivalente a la piedra de Minecraft
        this.setResistance(10.0F);   // Resistencia a explosiones
        this.setHarvestLevel("pickaxe", 0); // Requiere pico para ser minado

        InitBlocks.BLOCKS.add(this);

        this.setTickRandomly(true);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        // Verificar que la entidad no sea inmune al fuego (como los Blazes o Magma Cubes)
        // y que no tenga el encantamiento de Paso Helado (Frost Walker) o botas de protección si quieres añadir filtros
        if (!entityIn.isImmuneToFire() && entityIn instanceof EntityLivingBase) {
            // Aplica daño de fuego/quemadura
            entityIn.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F); // 1.0F equivale a medio corazón de daño
        }

        super.onEntityWalk(worldIn, pos, entityIn);
    }
}