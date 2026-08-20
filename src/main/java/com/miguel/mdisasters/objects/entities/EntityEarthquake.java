package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MDConfig;
import com.miguel.mdisasters.init.InitEntities;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EntityEarthquake extends Entity {

    public double currentRadius = 1.0;
    public double maxRadius = MDConfig.EARTHQUAKE.earthquakeMaxRadius; // Radio máximo de la grieta
    public int duration = MDConfig.EARTHQUAKE.earthquakeDuration; // Duración en ticks (10 segundos)

    public EntityEarthquake(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.noClip = true;
        InitEntities.ENTITIES.add(this);
    }

    public EntityEarthquake(World world, EntityPlayer player) {
        this(world);
        this.setPosition(player.posX, player.posY, player.posZ);
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.ticksExisted > this.duration) {
            this.setDead();
            return;
        }

        // Expandir el radio del terremoto progresivamente
        if (this.currentRadius < this.maxRadius) {
            this.currentRadius += 0.5;
        }

        BlockPos center = new BlockPos(this.posX, this.posY, this.posZ);

        if (!world.isRemote) {
            // 1. Romper el suelo creando grietas subterráneas
            generateFissure(center);

            // 2. Sacudir y dañar entidades en el área del terremoto
            AxisAlignedBB affectArea = new AxisAlignedBB(center).grow(this.currentRadius, 10, this.currentRadius);
            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, affectArea);

            for (Entity e : entities) {
                if (e != this) {
                    // Temblor en el movimiento del jugador/mobs
                    e.motionX += (rand.nextDouble() - 0.5) * 0.4;
                    e.motionZ += (rand.nextDouble() - 0.5) * 0.4;

                    if (e.onGround && rand.nextInt(5) == 0) {
                        e.motionY = 0.25; // Pequeño salto por el impacto sísmico
                    }
                    e.velocityChanged = true;
                }
            }
        } else {
            // 3. Efectos visuales de partículas cayendo y saliendo de la tierra
            spawnQuakeParticles(center);
        }
    }

    private void generateFissure(BlockPos center) {
        // Generar grietas en varias direcciones (líneas que se ramifican)
        int lines = 5; // Número de grietas principales
        for (int l = 0; l < lines; l++) {
            double angle = (Math.PI * 2 / lines) * l + (rand.nextDouble() * 0.2);
            double dirX = Math.cos(angle);
            double dirZ = Math.sin(angle);

            for (double r = 1; r <= this.currentRadius; r += 1.0) {
                int x = (int) Math.round(r * dirX);
                int z = (int) Math.round(r * dirZ);

                BlockPos groundPos = world.getTopSolidOrLiquidBlock(center.add(x, 0, z)).down();

                // Destruir bloques hacia abajo para crear una grieta profunda
                int depth = 4 + rand.nextInt(6);
                for (int y = 0; y < depth; y++) {
                    BlockPos target = groundPos.down(y);
                    IBlockState state = world.getBlockState(target);

                    if (state.getBlock() != Blocks.BEDROCK && state.getBlock() != Blocks.AIR) {
                        // Reemplazar la capa superior por aire o magma/lava en el fondo
                        if (y == depth - 1 && rand.nextBoolean()) {
                            world.setBlockState(target, Blocks.LAVA.getDefaultState(), 3);
                        } else {
                            world.setBlockToAir(target);
                        }
                    }
                }
            }
        }
    }

    private void spawnQuakeParticles(BlockPos center) {
        for (int i = 0; i < 15; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            double dist = rand.nextDouble() * this.currentRadius;

            double px = this.posX + Math.cos(angle) * dist;
            double pz = this.posZ + Math.sin(angle) * dist;
            BlockPos top = world.getTopSolidOrLiquidBlock(new BlockPos(px, 0, pz));

            IBlockState state = world.getBlockState(top.down());
            int blockId = Block.getStateId(state);

            // Generar partículas del bloque del suelo rompiéndose
            world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, px, top.getY() + 0.1, pz,
                    (rand.nextDouble() - 0.5) * 0.2, 0.1, (rand.nextDouble() - 0.5) * 0.2, blockId);

            if (rand.nextInt(3) == 0) {
                world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, px, top.getY(), pz, 0, 0.05, 0);
            }
        }
    }

    @Override
    public void setDead() {
        super.setDead();
        InitEntities.ENTITIES.remove(this);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("CurrentRadius")) {
            this.currentRadius = compound.getDouble("CurrentRadius");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setDouble("CurrentRadius", this.currentRadius);
    }
}