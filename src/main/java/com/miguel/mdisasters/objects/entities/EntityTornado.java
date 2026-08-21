package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MDConfig;
import com.miguel.mdisasters.init.InitEntities;
import com.miguel.mdisasters.init.InitSounds;
import com.miguel.mdisasters.sounds.SoundTornado;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

        if (this.ticksExisted > MDConfig.TORNADO.tornadoDuration) {
            this.setDead();
            return;
        }

        BlockPos currentPos = new BlockPos(this.posX, this.posY, this.posZ);
        BlockPos groundPos = world.getHeight(currentPos);

        if (!world.isRemote) {
            this.setPosition(this.posX, groundPos.getY(), this.posZ);

            // Usa la configuración para el radio de succión
            double radius = MDConfig.TORNADO.tornadoWidth;
            List<Entity> list = world.getEntitiesWithinAABB(Entity.class, getEntityBoundingBox().grow(radius));

            for (Entity e : list) {
                if (e != this) {
                    boolean isFlying = (e instanceof EntityPlayer) && ((EntityPlayer) e).capabilities.isFlying;
                    if (!isFlying) {
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
            }

            // Variación aleatoria de trayectoria
            if (this.ticksExisted % 20 == 0) {
                this.motionX += (this.rand.nextDouble() - 0.5) * 0.3;
                this.motionZ += (this.rand.nextDouble() - 0.5) * 0.3;

                this.motionX = MathHelper.clamp(this.motionX, -MDConfig.TORNADO.maxSpeed, MDConfig.TORNADO.maxSpeed);
                this.motionZ = MathHelper.clamp(this.motionZ, -MDConfig.TORNADO.maxSpeed, MDConfig.TORNADO.maxSpeed);
            }

            this.move(MoverType.SELF, this.motionX, 0, this.motionZ);
        } else {
            spawnDustCloudParticles(currentPos);

            if (this.ticksExisted == 1) {
                playTornadoSound();
            }

        }
    }

    @SideOnly(Side.CLIENT)
    private void playTornadoSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(new SoundTornado(this));
    }

    private void spawnDustCloudParticles(BlockPos center) {
        int particleCount = 15; // Cantidad de partículas por tick

        for (int i = 0; i < particleCount; i++) {
            // 1. Calcular una posición circular alrededor del centro del tornado
            double angle = rand.nextDouble() * Math.PI * 2;
            double radius = 1.0D + (rand.nextDouble() * MDConfig.TORNADO.tornadoWidth); // Radio dinámico

            double px = this.posX + Math.cos(angle) * radius;
            double pz = this.posZ + Math.sin(angle) * radius;

            // 2. Obtener la altura exacta del suelo en esa posición (X, Z)
            BlockPos surfacePos = world.getTopSolidOrLiquidBlock(new BlockPos(px, 0, pz));
            double py = surfacePos.getY() + 0.1D; // Ligeramente por encima del bloque para evitar parpadeos

            // 3. Obtener el bloque del suelo para usar sus partículas reales de rotura/polvo
            IBlockState state = world.getBlockState(surfacePos.down());
            int blockId = Block.getStateId(state);

            // 4. Velocidad tangencial/giratoria pegada al suelo
            double vx = -Math.sin(angle) * 0.3D;
            double vz = Math.cos(angle) * 0.3D;

            if (blockId != 0) {
                // Partículas del propio terreno (tierra, piedra, césped)
                world.spawnParticle(
                        EnumParticleTypes.BLOCK_CRACK,
                        px, py, pz,
                        vx, 0.01D, vz, // Velocidad Y casi nula (0.01) para que no vuelen
                        blockId
                );
            }

            // Humo/Polvo denso raspando el suelo
            if (rand.nextBoolean()) {
                world.spawnParticle(
                        EnumParticleTypes.SMOKE_LARGE,
                        px, py, pz,
                        vx * 0.5D, 0.0D, vz * 0.5D
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