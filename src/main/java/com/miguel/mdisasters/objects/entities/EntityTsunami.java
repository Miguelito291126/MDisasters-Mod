package com.miguel.mdisasters.objects.entities;

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

public class EntityTsunami extends Entity {
    public static double speed = 0.35;
    public static float moveYaw = 0.0F;
    public static final int waveWidth = 80;
    public static final int waveHeight = 10;
    public static final int waveDepth = 2;

    public EntityTsunami(World world) {
        super(world);
        this.setSize(waveWidth , waveHeight);
        this.noClip = true;
        InitEntities.ENTITIES.add(this);
    }

    public EntityTsunami(World world, EntityPlayer player) {
        this(world);
        this.moveYaw = player.rotationYaw;
        this.rotationYaw = player.rotationYaw;

        this.motionX = -MathHelper.sin(this.moveYaw * 0.017453292F) * speed;
        this.motionZ = MathHelper.cos(this.moveYaw * 0.017453292F) * speed;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote) {
            this.motionX = -MathHelper.sin(this.rotationYaw * 0.017453292F) * speed;
            this.motionZ = MathHelper.cos(this.rotationYaw * 0.017453292F) * speed;
            this.motionY = 0;

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

            updateWaterWave(new BlockPos(this.posX, this.posY, this.posZ));

            // EMPUJE PRECISO: Caja ajustada exactamente al grosor y altura del agua
            AxisAlignedBB waterBoundingBox = new AxisAlignedBB(
                    this.posX - (waveWidth / 2.0),
                    this.posY,
                    this.posZ - (waveDepth / 2.0),
                    this.posX + (waveWidth / 2.0),
                    this.posY + waveHeight,
                    this.posZ + (waveDepth / 2.0)
            );

            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, waterBoundingBox);

            for (Entity e : entities) {
                if (e != this) {
                    e.motionX = this.motionX * 2.0;
                    e.motionY = 0.25;
                    e.motionZ = this.motionZ * 2.0;
                    e.velocityChanged = true;
                }
            }
        } else {
            for (int i = 0; i < 12; i++) {
                double dx = posX + (rand.nextDouble() - 0.5) * waveWidth;
                double dy = posY + rand.nextDouble() * waveHeight;
                double dz = posZ + (rand.nextDouble() - 0.5) * waveWidth;

                world.spawnParticle(EnumParticleTypes.WATER_SPLASH, dx, dy, dz, 0, 0.2, 0);
            }
        }
    }

    private void updateWaterWave(BlockPos center) {
        int halfWidth = waveWidth / 2;

        // Radianes del ángulo de orientación
        double rad = Math.toRadians(this.rotationYaw);

        // Vector perpendicular al avance (extensión a los lados)
        double sideX = Math.cos(rad);
        double sideZ = Math.sin(rad);

        // Vector de avance directo
        double forwardX = -Math.sin(rad);
        double forwardZ = Math.cos(rad);

        for (int w = -halfWidth; w <= halfWidth; w++) {
            for (int d = 0; d < waveDepth; d++) {
                for (int y = 0; y < waveHeight; y++) {

                    // Calcular la posición exacta del bloque en la pared rectangular
                    int posX = (int) Math.round(w * sideX + d * forwardX);
                    int posZ = (int) Math.round(w * sideZ + d * forwardZ);

                    BlockPos frontPos = center.add(posX, y, posZ);

                    if (world.isAirBlock(frontPos)) {
                        world.setBlockState(frontPos, Blocks.WATER.getDefaultState(), 3);
                    }

                    // Limpiar el agua 3 bloques por detrás del avance
                    int backX = (int) Math.round(posX - (forwardX * 3));
                    int backZ = (int) Math.round(posZ - (forwardZ * 3));
                    BlockPos backPos = center.add(backX, y, backZ);

                    if (world.getBlockState(backPos).getBlock() == Blocks.WATER) {
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
        this.speed = compound.getDouble("Speed");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setDouble("Speed", this.speed);
    }
}