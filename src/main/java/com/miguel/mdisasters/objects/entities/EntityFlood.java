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
import net.minecraft.util.math.MathHelper;
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
        // Centrar perfectamente en el bloque (evita desviaciones por decimales)
        int blockX = MathHelper.floor(player.posX);
        int blockY = MathHelper.floor(player.posY);
        int blockZ = MathHelper.floor(player.posZ);

        this.setPosition(blockX + 0.5D, blockY, blockZ + 0.5D);
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        super.onUpdate();

        // Congelar posición física para asegurar simetría
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;

        this.currentRadius += this.speed;

        if (this.currentRadius >= this.maxRadius && !MDConfig.FLOOD.infiniteWaterExpansion) {
            this.setDead();
            return;
        }

        BlockPos center = new BlockPos(this.posX, this.posY, this.posZ);

        if (!world.isRemote) {
            updateFlatFlood(center);

            // Empuje de entidades simétrico
            AxisAlignedBB expansionBox = new AxisAlignedBB(
                    this.posX - this.currentRadius, this.posY, this.posZ - this.currentRadius,
                    this.posX + this.currentRadius, this.posY + 1.5, this.posZ + this.currentRadius
            );

            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, expansionBox);

            for (Entity e : entities) {
                if (e != this) {
                    boolean isFlying = (e instanceof EntityPlayer) && ((EntityPlayer) e).capabilities.isFlying;
                    if (!isFlying) {
                        double dx = e.posX - this.posX;
                        double dz = e.posZ - this.posZ;
                        double dist = Math.sqrt(dx * dx + dz * dz);

                        if (dist > 0.001D) {
                            e.motionX = (dx / dist) * (this.speed * 1.2);
                            e.motionY = 0.05;
                            e.motionZ = (dz / dist) * (this.speed * 1.2);
                            e.velocityChanged = true;
                        }

                    }
                }
            }
        } else {
            spawnWaterParticles();
        }
    }

    private void spawnWaterParticles()
    {
        for (int i = 0; i < 10; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            double px = this.posX + Math.cos(angle) * this.currentRadius;
            double pz = this.posZ + Math.sin(angle) * this.currentRadius;

            world.spawnParticle(EnumParticleTypes.WATER_SPLASH, px, this.posY + 0.1, pz, Math.cos(angle) * 0.1, 0.05, Math.sin(angle) * 0.1);
        }
    }

    private void updateFlatFlood(BlockPos center) {
        int r = (int) Math.ceil(this.currentRadius);

        double innerRadiusSq = Math.max(0, (this.currentRadius - 1.5) * (this.currentRadius - 1.5));
        double outerRadiusSq = this.currentRadius * this.currentRadius;

        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                double distSq = x * x + z * z;

                if (distSq >= innerRadiusSq && distSq <= outerRadiusSq) {
                    BlockPos targetPos = center.add(x, 0, z);

                    if (world.isAirBlock(targetPos) || world.getBlockState(targetPos).getBlock().isReplaceable(world, targetPos)) {
                        world.setBlockState(targetPos, Blocks.WATER.getDefaultState(), 3);
                    }

                    // Rellenar hacia abajo si hay un precipicio o depresión en el terreno
                    BlockPos downPos = targetPos.down();
                    while (downPos.getY() > 0 && (world.isAirBlock(downPos) || world.getBlockState(downPos).getBlock().isReplaceable(world, downPos))) {
                        world.setBlockState(downPos, Blocks.WATER.getDefaultState(), 3);
                        downPos = downPos.down();
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