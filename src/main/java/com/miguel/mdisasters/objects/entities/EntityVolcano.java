package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MDConfig;
import com.miguel.mdisasters.init.InitEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityVolcano extends Entity {

    private static final DataParameter<Boolean> IS_ERUPTING = EntityDataManager.createKey(EntityVolcano.class, DataSerializers.BOOLEAN);

    public double speed = MDConfig.VOLCANO.Speed;
    public double pressure = 0;
    public boolean IsGoingToErupt = false;
    public boolean IsPressureLeak = false;

    public int eruptionPhase = 0;
    public int phaseTimer = 0;


    public EntityVolcano(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.noClip = true;
        this.isImmuneToFire = true;

        InitEntities.ENTITIES.add(this);
    }

    public EntityVolcano(World world, BlockPos pos) {
        this(world);
        this.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
    }


    @Override
    protected void entityInit() {
        this.dataManager.register(IS_ERUPTING, IsGoingToErupt);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        BlockPos center = new BlockPos(this.posX, this.posY, this.posZ);

        if (!world.isRemote) {
            presureIncrement();
            presureCheck();

            if (IsGoingToErupt)
            {
                processEruptionSequence(center);
            }

        } else {
            spawnSmokeParticles();
        }
    }


    private void spawnSmokeParticles() {
        int particleCount = this.dataManager.get(IS_ERUPTING) ? 12 : 4;

        for (int i = 0; i < particleCount; i++) {
            double vx = (rand.nextDouble() - 0.5D) * 0.2D;
            double vy = 0.1D + (rand.nextDouble() * 0.3D);
            double vz = (rand.nextDouble() - 0.5D) * 0.2D;

            world.spawnParticle(
                    EnumParticleTypes.SMOKE_LARGE,
                    this.posX + (rand.nextDouble() - 0.5D) * 2.0D,
                    this.posY,
                    this.posZ + (rand.nextDouble() - 0.5D) * 2.0D,
                    vx, vy, vz
            );

            if (this.dataManager.get(IS_ERUPTING)) {
                world.spawnParticle(
                        EnumParticleTypes.LAVA,
                        this.posX + (rand.nextDouble() - 0.5D) * 1.5D,
                        this.posY,
                        this.posZ + (rand.nextDouble() - 0.5D) * 1.5D,
                        0.0D, 0.5D, 0.0D
                );
            }
        }
    }

    private void presureIncrement()
    {
        if (!IsGoingToErupt && !IsPressureLeak)
        {
            pressure += speed;
        }
        else if (!IsGoingToErupt && IsPressureLeak)
        {
            pressure -= speed;
            if (pressure <= 0)
            {
                IsPressureLeak = false;
            }
        }
    }

    private void presureCheck() {
        if (pressure >= 100 && !IsGoingToErupt) {
            IsGoingToErupt = true;
            this.dataManager.set(IS_ERUPTING, IsGoingToErupt);
            this.eruptionPhase = 1; // Inicia la secuencia en la fase 1
            this.phaseTimer = 0;
        }
    }

    private void spawnLavaLumps(BlockPos pos, int count) {
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.5F, 0.6F);

        for (int i = 0; i < count; i++) {
            double motionX = (rand.nextDouble() - 0.5) * 0.6;
            double motionY = 0.8 + (rand.nextDouble() * 0.5);
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

    private void processEruptionSequence(BlockPos pos) {
        phaseTimer++;

        switch (eruptionPhase) {
            case 1:
                // Paso 1: Terremoto inicial
                if (rand.nextInt(3) == 0) {
                    EntityEarthquake earthquake = new EntityEarthquake(this.world);
                    earthquake.setPosition(this.posX, this.posY, this.posZ);
                    this.world.spawnEntity(earthquake);
                }

                // Equivale a: await(40) -> esperar 40 ticks (2 segundos) para la fase 2
                eruptionPhase = 2;
                phaseTimer = 0;
                break;

            case 2:
                // Espera hasta que pasen 40 ticks
                if (phaseTimer >= 40) {
                    spawnLavaLumps(pos, 2); // Primera oleada de proyectiles

                    // Equivale a: await(30) -> esperar 30 ticks para la fase 3
                    eruptionPhase = 3;
                    phaseTimer = 0;
                }
                break;

            case 3:
                // Espera hasta que pasen 30 ticks
                if (phaseTimer >= 30) {
                    spawnLavaLumps(pos, 4); // Segunda oleada más masiva

                    // Equivale a: await(20) -> fin de erupción
                    eruptionPhase = 4;
                    phaseTimer = 0;
                }
                break;

            case 4:
                if (phaseTimer >= 20) {
                    IsGoingToErupt = false;
                    IsPressureLeak = true;
                    this.dataManager.set(IS_ERUPTING, IsGoingToErupt);
                    this.pressure = 99;
                    this.eruptionPhase = 0;
                    this.phaseTimer = 0;
                }
                break;
        }
    }

    @Override
    public void setDead() {
        super.setDead();
        InitEntities.ENTITIES.remove(this);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("Pressure")) {
            this.pressure = compound.getDouble("Pressure");
        }
        if (compound.hasKey("IsGoingToErupt")) {
            this.IsGoingToErupt = compound.getBoolean("IsGoingToErupt");
            this.dataManager.set(IS_ERUPTING, this.IsGoingToErupt);
        }
        if (compound.hasKey("IsGoingToLeak")) {
            this.IsPressureLeak = compound.getBoolean("IsGoingToLeak");
        }
        if (compound.hasKey("Speed")) {
            this.speed = compound.getDouble("Speed");
        }
        else{
            this.speed = MDConfig.VOLCANO.Speed;
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setDouble("Pressure", this.pressure);
        compound.setDouble("Speed", this.speed);
        compound.setBoolean("IsGoingToErupt", this.IsGoingToErupt);
        compound.setBoolean("IsGoingToLeak", this.IsPressureLeak);
    }
}