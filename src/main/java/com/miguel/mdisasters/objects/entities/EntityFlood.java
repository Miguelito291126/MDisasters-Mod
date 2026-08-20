package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MDConfig;
import com.miguel.mdisasters.init.InitEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityFlood extends Entity {
    public double speed = MDConfig.FLOOD.waterSpeed;
    public double currentRadius = 1.0;
    public double maxRadius = MDConfig.FLOOD.waterMaxRadius;

    public EntityFlood(World world) {
        super(world);
        this.setSize(1.0f, MDConfig.TSUNAMI.waveHeight);
        this.noClip = true;
        InitEntities.ENTITIES.add(this);
    }

    public EntityFlood(World world, EntityPlayer player) {
        this(world);
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.currentRadius += this.speed;

        float currentSize = (float) this.currentRadius * 2;
        this.setSize(currentSize, MDConfig.TSUNAMI.waveHeight);

        if (currentRadius >= maxRadius) {
            this.setDead();
            return;
        }

        BlockPos center = new BlockPos(this.posX, this.posY, this.posZ);

        if (!world.isRemote) {
            updateRadialWave(center);

            // Caja de colisión precisa basada en config
            AxisAlignedBB expansionBox = this.getEntityBoundingBox().grow(1.0);

            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, expansionBox);

            for (Entity e : entities) {
                if (e != this) {
                    double dx = e.posX - this.posX;
                    double dz = e.posZ - this.posZ;
                    double dist = Math.sqrt(dx * dx + dz * dz);

                    e.motionX = (dx / dist) * (this.speed * 1.5);
                    e.motionY = 0.2;
                    e.motionZ = (dz / dist) * (this.speed * 1.5);
                    e.velocityChanged = true;
                }
            }
        } else {
            // Partículas en 360 grados
            for (int i = 0; i < 20; i++) {
                double angle = rand.nextDouble() * Math.PI * 2;
                double px = this.posX + Math.cos(angle) * this.currentRadius;
                double pz = this.posZ + Math.sin(angle) * this.currentRadius;
                double py = this.posY + rand.nextDouble() * MDConfig.TSUNAMI.waveHeight;

                world.spawnParticle(EnumParticleTypes.WATER_SPLASH, px, py, pz, Math.cos(angle) * 0.2, 0.1, Math.sin(angle) * 0.2);
            }
        }
    }

    private void updateRadialWave(BlockPos center) {
        int depth = MDConfig.TSUNAMI.waveDepth;
        int height = MDConfig.TSUNAMI.waveHeight;

        int steps = Math.max(36, (int) (2 * Math.PI * this.currentRadius));

        for (int i = 0; i < steps; i++) {
            double angle = (2 * Math.PI / steps) * i;
            double dirX = Math.cos(angle);
            double dirZ = Math.sin(angle);

            for (int d = 0; d < depth; d++) {
                double r = this.currentRadius + d;
                int x = (int) Math.round(r * dirX);
                int z = (int) Math.round(r * dirZ);

                for (int y = 0; y < height; y++) {
                    BlockPos frontPos = center.add(x, y, z);
                    if (world.isAirBlock(frontPos)) {
                        world.setBlockState(frontPos, Blocks.LAVA.getDefaultState(), 3);
                    }
                }
            }

            double innerR = this.currentRadius - 1.0;
            if (innerR > 0) {
                int backX = (int) Math.round(innerR * dirX);
                int backZ = (int) Math.round(innerR * dirZ);

                for (int y = 0; y < height; y++) {
                    BlockPos backPos = center.add(backX, y, backZ);
                    if (world.getBlockState(backPos).getBlock() == Blocks.LAVA) {
                        world.setBlockToAir(backPos);
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