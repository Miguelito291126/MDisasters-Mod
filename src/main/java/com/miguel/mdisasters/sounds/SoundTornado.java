package com.miguel.mdisasters.sounds;

import com.miguel.mdisasters.init.InitSounds;
import com.miguel.mdisasters.objects.entities.EntityEarthquake;
import com.miguel.mdisasters.objects.entities.EntityTornado;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundTornado extends MovingSound {

    private final EntityTornado tornado;

    public SoundTornado(EntityTornado tornado) {
        super(InitSounds.TORNADO, SoundCategory.WEATHER);
        this.tornado = tornado;
        this.repeat = true;        // Hace loop automático mientras la entidad exista
        this.repeatDelay = 0;      // Sin delay entre loops
        this.volume = 2.0F;        // Volumen / Radio de alcance
        this.pitch = 0.8F;

        // Posición inicial
        this.xPosF = (float) tornado.posX;
        this.yPosF = (float) tornado.posY;
        this.zPosF = (float) tornado.posZ;
    }

    @Override
    public void update() {
        // SI LA ENTIDAD MUERE O ES BORRADA, SE DETIENE EL SONIDO DE INMEDIATO
        if (this.tornado.isDead) {
            this.donePlaying = true;
            return;
        }

        // Actualizar la posición del sonido con la entidad
        this.xPosF = (float) this.tornado.posX;
        this.yPosF = (float) this.tornado.posY;
        this.zPosF = (float) this.tornado.posZ;
    }
}