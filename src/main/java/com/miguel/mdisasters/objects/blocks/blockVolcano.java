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
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);

        if (!world.isRemote) {
            // 1. Debe estar expuesto al aire por arriba (superficie/cima)
            if (world.isAirBlock(pos.up())) {

                // 2. FILTRO ANTI-LAG: Solo la cima del volcán tiene lava adyacente (chimenea central)
                if (isNearLavaChamber(world, pos)) {

                    // 3. Frecuencia reducida (1 entre 20 ticks aleatorios para evitar sobrecarga)
                    if (rand.nextInt(20) == 0) {
                        triggerEruptingLava(world, pos, rand);
                    }
                }
            }
        }
    }

    /**
     * Comprueba si el bloque está tocando la columna interna de lava del volcán.
     */
    private boolean isNearLavaChamber(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (world.getBlockState(pos.offset(facing)).getBlock() == Blocks.LAVA) {
                return true;
            }
        }
        return false;
    }

    private void triggerEruptingLava(World world, BlockPos pos, Random rand) {
        // Sonido de explosión
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.5F, 0.6F);

        // Lanzar solo 1 o 2 proyectiles por erupción para evitar lag
        int proyectiles = 1 + rand.nextInt(2);
        for (int i = 0; i < proyectiles; i++) {
            double motionX = (rand.nextDouble() - 0.5) * 0.6;
            double motionY = 0.8 + (rand.nextDouble() * 0.5); // Impulso vertical controlado
            double motionZ = (rand.nextDouble() - 0.5) * 0.6;

            EntityFallingBlock lavaLump = new EntityFallingBlock(
                    world,
                    pos.getX() + 0.5,
                    pos.getY() + 1.2,
                    pos.getZ() + 0.5,
                    Blocks.MAGMA.getDefaultState()
            );

            lavaLump.motionX = motionX;
            lavaLump.motionY = motionY;
            lavaLump.motionZ = motionZ;
            lavaLump.fallTime = 1;

            lavaLump.setHurtEntities(true);

            world.spawnEntity(lavaLump);
        }
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        // Solo mostrar partículas visuales de humo cerca de la chimenea de lava
        if (worldIn.isAirBlock(pos.up()) && isNearLavaChamber(worldIn, pos)) {
            double px = pos.getX() + rand.nextDouble();
            double py = pos.getY() + 1.1;
            double pz = pos.getZ() + rand.nextDouble();

            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, px, py, pz, 0.0, 0.1, 0.0);
            worldIn.spawnParticle(EnumParticleTypes.LAVA, px, py, pz, 0.0, 0.0, 0.0);
        }
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