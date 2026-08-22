package com.miguel.mdisasters.objects.entities;

import com.miguel.mdisasters.config.MDConfig;
import com.miguel.mdisasters.init.InitEntities;
import com.miguel.mdisasters.init.InitSounds;
import com.miguel.mdisasters.sounds.SoundEarthquake;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static com.miguel.mdisasters.init.InitSounds.EARTHQUAKE;

public class EntityEarthquake extends Entity {

    public double currentRadius = 1.0;
    public double maxRadius = MDConfig.EARTHQUAKE.earthquakeMaxRadius; // Radio máximo de la grieta
    public int duration = MDConfig.EARTHQUAKE.earthquakeDuration; // Duración en ticks (10 segundos)
    double magnitude = MDConfig.EARTHQUAKE.earthquakeMagnitude;

    public EntityEarthquake(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.noClip = true;
        this.isImmuneToFire = true;

        InitEntities.ENTITIES.add(this);
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.ticksExisted > this.duration) {
            this.setDead();
            return;
        }

        if (this.currentRadius < this.maxRadius) {
            this.currentRadius += 0.5;
        }

        BlockPos center = new BlockPos(this.posX, this.posY, this.posZ);

        if (!world.isRemote) {
            generateFissure(center);
            AxisAlignedBB affectArea = new AxisAlignedBB(center).grow(this.currentRadius, 10, this.currentRadius);
            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, affectArea);
            double shakeForce = (this.magnitude / 10.0D) * 0.6D;

            for (Entity e : entities) {
                if (e != this) {
                    boolean isFlying = (e instanceof EntityPlayer) && ((EntityPlayer) e).capabilities.isFlying;
                    if (e.onGround && !isFlying) {
                        // Temblor en el movimiento del jugador/mobs
                        e.motionX += (rand.nextDouble() - 0.5) * shakeForce;
                        e.motionZ += (rand.nextDouble() - 0.5) * shakeForce;

                        if (rand.nextInt(4) == 0) {
                            e.motionY = 0.15D + (shakeForce * 0.3D); // Salto/rebote sísmico
                        }
                        e.velocityChanged = true;
                    }
                }

            }
        } else {
            // 3. Efectos visuales de partículas cayendo y saliendo de la tierra
            spawnQuakeParticles(center);
            // Iniciar el sonido en bucle solo en el primer tick
            if (this.ticksExisted == 1) {
                playEarthquakeSound();
            }
        }
    }

    private void generateFissure(BlockPos center) {
        // La magnitud define cuántas ramificaciones principales se forman (mínimo 3, máximo 12)
        int lines = Math.max(3, (int) Math.round(this.magnitude * 1.2D));

        for (int l = 0; l < lines; l++) {
            double angle = (Math.PI * 2 / lines) * l + (rand.nextDouble() * 0.15D);
            double dirX = Math.cos(angle);
            double dirZ = Math.sin(angle);

            for (double r = 1; r <= this.currentRadius; r += 1.0D) {
                int x = (int) Math.round(r * dirX);
                int z = (int) Math.round(r * dirZ);

                BlockPos groundPos = world.getTopSolidOrLiquidBlock(center.add(x, 0, z)).down();

                // Profundidad de la grieta escalada por la magnitud
                int baseDepth = (int) Math.round(this.magnitude * 1.5D);
                int depth = Math.max(3, baseDepth + rand.nextInt(4));

                for (int y = 0; y < depth; y++) {
                    BlockPos target = groundPos.down(y);
                    IBlockState state = world.getBlockState(target);

                    if (state.getBlock() != Blocks.BEDROCK && state.getBlock() != Blocks.AIR) {
                        // En la capa más profunda hay probabilidad de lava según magnitud
                        if (y == depth - 1 && rand.nextDouble() < (this.magnitude / 10.0D) * 0.3D) {
                            world.setBlockState(target, Blocks.LAVA.getDefaultState(), 3);
                        } else {
                            world.setBlockToAir(target);
                        }
                    }
                }
            }
        }
    }

    private void spawnQuakeParticles(BlockPos center) {
        int particleCount = (int) (10 + (this.magnitude * 3));

        for (int i = 0; i < particleCount; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            double dist = rand.nextDouble() * this.currentRadius;

            double px = this.posX + Math.cos(angle) * dist;
            double pz = this.posZ + Math.sin(angle) * dist;
            BlockPos top = world.getTopSolidOrLiquidBlock(new BlockPos(px, 0, pz));

            IBlockState state = world.getBlockState(top.down());
            int blockId = Block.getStateId(state);

            if (blockId != 0) {
                // Partículas del bloque rompiéndose
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, px, top.getY() + 0.1D, pz,
                        (rand.nextDouble() - 0.5D) * 0.2D, 0.1D, (rand.nextDouble() - 0.5D) * 0.2D, blockId);
            }

            if (rand.nextInt(4) == 0) {
                world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, px, top.getY(), pz, 0, 0.05D, 0);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playEarthquakeSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(new SoundEarthquake(this));
    }

    @Override
    public void setDead() {
        super.setDead();
        InitEntities.ENTITIES.remove(this);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("CurrentRadius")) {
            this.currentRadius = compound.getDouble("CurrentRadius");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setDouble("CurrentRadius", this.currentRadius);
    }
}