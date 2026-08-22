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

public class EntityTsunamiLava extends Entity {
    private float moveYaw = 0.0F; // Corregido: ya no es static
    public double speed = MDConfig.TSUNAMI.speed; // Velocidad propia de cada entidad

    public EntityTsunamiLava(World world) {
        super(world);
        this.setSize(MDConfig.TSUNAMI.waveWidth, MDConfig.TSUNAMI.waveHeight);
        this.noClip = true;
        this.isImmuneToFire = true;

        InitEntities.ENTITIES.add(this);
    }

    public EntityTsunamiLava(World world, EntityPlayer player) {
        this(world);
        this.moveYaw = player.rotationYaw;
        this.rotationYaw = player.rotationYaw;

        this.motionX = -MathHelper.sin(this.moveYaw * 0.017453292F) * this.speed;
        this.motionZ = MathHelper.cos(this.moveYaw * 0.017453292F) * this.speed;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote) {
            this.motionX = -MathHelper.sin(this.rotationYaw * 0.017453292F) * this.speed;
            this.motionZ = MathHelper.cos(this.rotationYaw * 0.017453292F) * this.speed;
            this.motionY = 0;

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

            updateLavaWave(new BlockPos(this.posX, this.posY, this.posZ));

            // Caja de colisión precisa basada en config
            AxisAlignedBB lavaBoundingBox = new AxisAlignedBB(
                    this.posX - (MDConfig.TSUNAMI.waveWidth / 2.0),
                    this.posY,
                    this.posZ - (MDConfig.TSUNAMI.waveDepth / 2.0),
                    this.posX + (MDConfig.TSUNAMI.waveWidth / 2.0),
                    this.posY + MDConfig.TSUNAMI.waveHeight,
                    this.posZ + (MDConfig.TSUNAMI.waveDepth / 2.0)
            );

            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, lavaBoundingBox);

            for (Entity e : entities) {
                if (e != this) {
                    boolean isFlying = (e instanceof EntityPlayer) && ((EntityPlayer) e).capabilities.isFlying;
                    if (!isFlying) {
                        e.motionX = this.motionX * 2.0;
                        e.motionY = 0.25;
                        e.motionZ = this.motionZ * 2.0;
                        e.velocityChanged = true;
                    }
                }
            }
        } else {

        }
    }

    private void spawnLavaParticles()
    {
        for (int i = 0; i < 12; i++) {
            double dx = posX + (rand.nextDouble() - 0.5) * MDConfig.TSUNAMI.waveWidth;
            double dy = posY + rand.nextDouble() * MDConfig.TSUNAMI.waveHeight;
            double dz = posZ + (rand.nextDouble() - 0.5) * MDConfig.TSUNAMI.waveWidth;

            world.spawnParticle(EnumParticleTypes.DRIP_LAVA, dx, dy, dz, 0, 0.2, 0);
        }
    }

    private void updateLavaWave(BlockPos center) {
        int halfWidth = MDConfig.TSUNAMI.waveWidth / 2;

        double rad = Math.toRadians(this.rotationYaw);

        double sideX = Math.cos(rad);
        double sideZ = Math.sin(rad);

        double forwardX = -Math.sin(rad);
        double forwardZ = Math.cos(rad);

        for (int w = -halfWidth; w <= halfWidth; w++) {
            for (int d = 0; d < MDConfig.TSUNAMI.waveDepth; d++) {
                for (int y = 0; y < MDConfig.TSUNAMI.waveHeight; y++) {

                    int posX = (int) Math.round(w * sideX + d * forwardX);
                    int posZ = (int) Math.round(w * sideZ + d * forwardZ);

                    BlockPos frontPos = center.add(posX, y, posZ);

                    if (world.isAirBlock(frontPos)) {
                        world.setBlockState(frontPos, Blocks.LAVA.getDefaultState(), 3);
                    }

                    int backX = (int) Math.round(posX - (forwardX * 3));
                    int backZ = (int) Math.round(posZ - (forwardZ * 3));
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
        else {
            this.speed = MDConfig.TSUNAMI.speed;
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setDouble("Speed", this.speed);
    }
}