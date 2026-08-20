package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MDConfig;
import com.miguel.mdisasters.init.InitEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EntityFlood extends Entity {
    public double speed = MDConfig.FLOOD.waterSpeed;
    public double currentRadius = 1.0;
    public double maxRadius = MDConfig.FLOOD.waterMaxRadius;

    public EntityFlood(World world) {
        super(world);
        this.setSize(1.0f, 1.0f);
        this.noClip = true;
        InitEntities.ENTITIES.add(this);
    }

    public EntityFlood(World world, EntityPlayer player) {
        this(world);
        // Fija la posición exacta de origen (A nivel de los pies del jugador)
        this.setPosition(player.posX, player.posY, player.posZ);
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.currentRadius += this.speed;

        float currentSize = (float) this.currentRadius * 2;
        this.setSize(currentSize, 1.0f);

        if (this.currentRadius >= this.maxRadius && !MDConfig.FLOOD.infiniteWaterExpansion) {
            this.setDead();
            return;
        }

        BlockPos center = new BlockPos(this.posX, this.posY, this.posZ);

        if (!world.isRemote) {
            updateFlatFlood(center);

            // Empuje de entidades dentro del área inundada
            AxisAlignedBB expansionBox = new AxisAlignedBB(
                    this.posX - this.currentRadius, this.posY, this.posZ - this.currentRadius,
                    this.posX + this.currentRadius, this.posY + 1.5, this.posZ + this.currentRadius
            );

            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, expansionBox);

            for (Entity e : entities) {
                if (e != this) {
                    double dx = e.posX - this.posX;
                    double dz = e.posZ - this.posZ;
                    double dist = Math.sqrt(dx * dx + dz * dz);

                    if (dist > 0.001D) {
                        e.motionX = (dx / dist) * (this.speed * 1.2);
                        e.motionY = 0.05; // Leve flotación
                        e.motionZ = (dz / dist) * (this.speed * 1.2);
                        e.velocityChanged = true;
                    }
                }
            }
        } else {
            // Partículas de salpicadura en el borde exterior
            for (int i = 0; i < 10; i++) {
                double angle = rand.nextDouble() * Math.PI * 2;
                double px = this.posX + Math.cos(angle) * this.currentRadius;
                double pz = this.posZ + Math.sin(angle) * this.currentRadius;

                world.spawnParticle(EnumParticleTypes.WATER_SPLASH, px, this.posY + 0.1, pz, Math.cos(angle) * 0.1, 0.05, Math.sin(angle) * 0.1);
            }
        }
    }

    private void updateFlatFlood(BlockPos center) {
        int r = (int) Math.ceil(this.currentRadius);
        int targetY = center.getY(); // Mantener estricto el nivel Y base

        // Recorre únicamente el perímetro/anillo del radio actual
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                // Comprobación de círculo perfecto
                if (x * x + z * z <= this.currentRadius * this.currentRadius) {
                    BlockPos targetPos = new BlockPos(center.getX() + x, targetY, center.getZ() + z);

                    // Reemplaza solo si es aire o plantas/hierba en ese nivel exacto
                    if (world.isAirBlock(targetPos) || world.getBlockState(targetPos).getBlock().isReplaceable(world, targetPos)) {
                        world.setBlockState(targetPos, Blocks.WATER.getDefaultState(), 3);
                    }
                }
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
        if (compound.hasKey("Speed")) {
            this.speed = compound.getDouble("Speed");
        }
        if (compound.hasKey("CurrentRadius")) {
            this.currentRadius = compound.getDouble("CurrentRadius");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setDouble("Speed", this.speed);
        compound.setDouble("CurrentRadius", this.currentRadius);
    }
}