package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.init.InitEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityTornado extends Entity {
    public static double speed = 0.2;
    public static final double maxSpeed = 0.4;
    public static final int TornadoWidth = 4;
    public static final int TornadoHeight  = 10;

    public EntityTornado(World world) {
        super(world);
        this.setSize(TornadoWidth, TornadoHeight);
        this.noClip = true;
        this.motionX = 0.2;
        this.motionZ = 0.0;

        InitEntities.ENTITIES.add(this);
    }

    public EntityTornado(World world, EntityPlayer player) {
        this(world);
        float yaw = player.rotationYaw;

        this.motionX = -MathHelper.sin(yaw * 0.017453292F) * speed;
        this.motionZ = MathHelper.cos(yaw * 0.017453292F) * speed;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote) {
            BlockPos currentPos = new BlockPos(this.posX, this.posY, this.posZ);
            BlockPos groundPos = world.getHeight(currentPos);

            this.setPosition(this.posX, groundPos.getY(), this.posZ);

            double radius = 8.0;
            List<Entity> list = world.getEntitiesWithinAABB(Entity.class, getEntityBoundingBox().grow(radius));
            for (Entity e : list) {
                if (e != this) {
                    double dx = posX - e.posX;
                    double dz = posZ - e.posZ;
                    double dist = Math.max(0.1, Math.sqrt(dx * dx + dz * dz));

                    e.motionX += (dx / dist) * 0.2;
                    e.motionY = 0.35;
                    e.motionZ += (dz / dist) * 0.2;

                    e.velocityChanged = true; // Sincroniza el empuje con los jugadores
                }
            }

            if (this.ticksExisted % 20 == 0) {
                this.motionX += (this.rand.nextDouble() - 0.5) * 0.3;
                this.motionZ += (this.rand.nextDouble() - 0.5) * 0.3;


                this.motionX = MathHelper.clamp(this.motionX, -maxSpeed, maxSpeed);
                this.motionZ = MathHelper.clamp(this.motionZ, -maxSpeed, maxSpeed);
            }

            this.move(MoverType.SELF, this.motionX, 0, this.motionZ);
        }

        if (world.isRemote) {
            for (int i = 0; i < 60; i++) {
                double progresoAltura = rand.nextDouble();
                double dy = posY + (progresoAltura * height);

                double radioBase = 1.0;
                double radioMaximo = 5.0;
                double radioActual = radioBase + (Math.pow(progresoAltura, 1.5) * (radioMaximo - radioBase));

                double angulo = rand.nextDouble() * Math.PI * 2;

                double dx = posX + Math.cos(angulo) * radioActual;
                double dz = posZ + Math.sin(angulo) * radioActual;

                double velocidadGiro = 0.4 + (progresoAltura * 0.2);

                double motX = -Math.sin(angulo) * velocidadGiro;
                double motZ = Math.cos(angulo) * velocidadGiro;

                double atraccionCentro = 0.05;
                motX -= Math.cos(angulo) * atraccionCentro;
                motZ -= Math.sin(angulo) * atraccionCentro;

                double motY = 0.15 + (rand.nextDouble() * 0.1);

                world.spawnParticle(
                        EnumParticleTypes.CLOUD,
                        dx, dy, dz,
                        motX, motY, motZ
                );
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
        // Carga de datos NBT si fuera necesaria
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        // Guardado de datos NBT si fuera necesaria
    }
}