package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MDConfig;
import com.miguel.mdisasters.init.InitEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityMeteor extends Entity {

    public double speed = MDConfig.METEOR.speed; // Velocidad de caída propia

    public EntityMeteor(World world) {
        super(world);
        // Corregido: Usar meteorWidth en lugar de meteorWeight
        this.setSize(MDConfig.METEOR.meteorWidth, MDConfig.METEOR.meteorHeight);
        this.motionY = -speed;
        InitEntities.ENTITIES.add(this);
    }

    public EntityMeteor(World world, EntityPlayer player) {
        this(world);
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote) {
            // Aceleración de gravedad
            this.motionY -= 0.03;

            // Aplicar el movimiento
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

            // Detonar si colisiona contra cualquier superficie
            if (this.collided || this.onGround) {
                explode();
                this.setDead();
            }
        } else {
            spawnMeteorParticles();
        }

        // Muerte de seguridad tras 20 segundos
        if (this.ticksExisted > 400) {
            this.setDead();
        }
    }

    private void spawnMeteorParticles()
    {
        // Partículas de fuego y humo denso en el cliente
        for (int i = 0; i < 6; i++) {
            double px = posX + (rand.nextDouble() - 0.5) * width;
            double py = posY + rand.nextDouble() * height;
            double pz = posZ + (rand.nextDouble() - 0.5) * width;

            world.spawnParticle(EnumParticleTypes.FLAME, px, py, pz, 0, 0.05, 0);
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, px, py, pz, 0, 0.05, 0);
        }
    }

    private void explode() {
        // Usar los valores de la configuración
        int radius = MDConfig.METEOR.explosionRadius;
        float power = MDConfig.METEOR.explosionPower;

        // Daño a entidades cercanas
        world.getEntitiesWithinAABB(Entity.class, getEntityBoundingBox().grow(radius))
                .stream()
                .filter(e -> e != this)
                .forEach(e -> e.attackEntityFrom(DamageSource.GENERIC, 25.0F));

        // Generar cráter
        BlockPos center = new BlockPos(this);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        BlockPos targetPos = center.add(x, y, z);
                        if (!world.isAirBlock(targetPos)) {
                            world.setBlockToAir(targetPos);
                        }
                    }
                }
            }
        }

        // Explosión visual/física
        world.createExplosion(this, posX, posY, posZ, power, true);
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
        } else {
            this.speed = MDConfig.METEOR.speed;
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setDouble("Speed", this.speed);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}