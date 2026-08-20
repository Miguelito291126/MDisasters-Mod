package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MDConfig;
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

    public EntityTornado(World world) {
        super(world);
        this.setSize(MDConfig.TORNADO.tornadoWidth, MDConfig.TORNADO.tornadoHeight);
        this.noClip = true;
        this.motionX = 0.2;
        this.motionZ = 0.0;

        InitEntities.ENTITIES.add(this);
    }

    public EntityTornado(World world, EntityPlayer player) {
        this(world);
        float yaw = player.rotationYaw;

        this.motionX = -MathHelper.sin(yaw * 0.017453292F) * MDConfig.TORNADO.speed;
        this.motionZ = MathHelper.cos(yaw * 0.017453292F) * MDConfig.TORNADO.speed;
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

            // Usa la configuración para el radio de succión
            double radius = MDConfig.TORNADO.tornadoWidth;
            List<Entity> list = world.getEntitiesWithinAABB(Entity.class, getEntityBoundingBox().grow(radius));

            for (Entity e : list) {
                if (e != this) {
                    // Corregido: Atraer HACIA el centro del tornado
                    double dx = posX - e.posX;
                    double dz = posZ - e.posZ;
                    double dist = Math.max(0.1, Math.sqrt(dx * dx + dz * dz));

                    // Tirón hacia el vórtice
                    e.motionX += (dx / dist) * 0.15;
                    e.motionY = 0.35;
                    e.motionZ += (dz / dist) * 0.15;

                    e.velocityChanged = true;
                }
            }

            // Variación aleatoria de trayectoria
            if (this.ticksExisted % 20 == 0) {
                this.motionX += (this.rand.nextDouble() - 0.5) * 0.3;
                this.motionZ += (this.rand.nextDouble() - 0.5) * 0.3;

                this.motionX = MathHelper.clamp(this.motionX, -MDConfig.TORNADO.maxSpeed, MDConfig.TORNADO.maxSpeed);
                this.motionZ = MathHelper.clamp(this.motionZ, -MDConfig.TORNADO.maxSpeed, MDConfig.TORNADO.maxSpeed);
            }

            this.move(MoverType.SELF, this.motionX, 0, this.motionZ);

            // Destrucción tras tiempo límite (ejemplo: ~1 minuto / 1200 ticks)
            if (this.ticksExisted > 1200) {
                this.setDead();
            }
        } else {
            // Renderizado de partículas adaptado a la anchura de la config
            double radioBase = 1.0;
            double radioMaximo = MDConfig.TORNADO.tornadoWidth / 2.0;

            for (int i = 0; i < 60; i++) {
                double progresoAltura = rand.nextDouble();
                double dy = posY + (progresoAltura * height);

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
        this.motionX = compound.getDouble("MotionX");
        this.motionZ = compound.getDouble("MotionZ");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setDouble("MotionX", this.motionX);
        compound.setDouble("MotionZ", this.motionZ);
    }
}