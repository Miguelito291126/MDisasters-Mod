package com.miguel.mdisasters.sounds;

import com.miguel.mdisasters.init.InitSounds;
import com.miguel.mdisasters.objects.entities.EntityEarthquake;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundEarthquake extends MovingSound {

    private final EntityEarthquake earthquake;

    public SoundEarthquake(EntityEarthquake earthquake) {
        super(InitSounds.EARTHQUAKE, SoundCategory.AMBIENT);
        this.earthquake = earthquake;
        this.repeat = true;        // Hace loop automático mientras la entidad exista
        this.repeatDelay = 0;      // Sin delay entre loops
        this.volume = 2.0F;        // Volumen / Radio de alcance
        this.pitch = 0.8F;

        // Posición inicial
        this.xPosF = (float) earthquake.posX;
        this.yPosF = (float) earthquake.posY;
        this.zPosF = (float) earthquake.posZ;
    }

    @Override
    public void update() {
        // SI LA ENTIDAD MUERE O ES BORRADA, SE DETIENE EL SONIDO DE INMEDIATO
        if (this.earthquake.isDead) {
            this.donePlaying = true;
            return;
        }

        // Actualizar la posición del sonido con la entidad
        this.xPosF = (float) this.earthquake.posX;
        this.yPosF = (float) this.earthquake.posY;
        this.zPosF = (float) this.earthquake.posZ;
    }
}