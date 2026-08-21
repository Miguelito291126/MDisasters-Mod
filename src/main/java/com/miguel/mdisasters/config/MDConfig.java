package com.miguel.mdisasters.config;

import com.miguel.mdisasters.MDMain;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = MDMain.MODID, name = "mdisasters")
public class MDConfig {

    @Config.Comment("Configuración de Tsunamis")
    public static TsunamiCategory TSUNAMI = new TsunamiCategory();

    @Config.Comment("Configuración de Tornados")
    public static TornadoCategory TORNADO = new TornadoCategory();

    @Config.Comment("Configuración de Meteoros")
    public static MeteorCategory METEOR = new MeteorCategory();

    @Config.Comment("Configuración del Spawner Aleatorio")
    public static SpawnerCategory SPAWNER = new SpawnerCategory();

    @Config.Comment("Configuración de Terremotos")
    public static EarthquakeCategory EARTHQUAKE = new EarthquakeCategory();

    @Config.Comment("Configuración de Inundaciones")
    public static FloodCategory FLOOD = new FloodCategory();

    public static class TsunamiCategory {
        @Config.Comment("Velocidad a la que va")
        @Config.RangeDouble(min = 0.0, max = 200.0)
        public double speed = 0.35;

        @Config.Comment("Ancho frontal de la ola en bloques")
        @Config.RangeInt(min = 5, max = 200)
        public int waveWidth = 80;

        @Config.Comment("Altura de la ola de agua en bloques")
        @Config.RangeInt(min = 1, max = 50)
        public int waveHeight = 10;

        @Config.Comment("Grosor/Fondo de la ola")
        @Config.RangeInt(min = 1, max = 10)
        public int waveDepth = 2;
    }

    public static class TornadoCategory {
        @Config.Comment("Velocidad a la que va")
        @Config.RangeDouble(min = 0.0, max = 200.0)
        public double speed = 0.2;

        @Config.Comment("Velocidad Maxima a la que va")
        @Config.RangeDouble(min = 0.0, max = 200.0)
        public double maxSpeed = 0.4;

        @Config.Comment("Tiempo que dura el tornado (en ticks: 20 ticks = 1 segundo)")
        @Config.RangeInt(min = 20, max = 72000) // Se ajustó el límite máximo
        public int tornadoDuration = 600;

        @Config.Comment("Altura del tornado en bloques")
        @Config.RangeInt(min = 5, max = 150)
        public int tornadoHeight = 30;

        @Config.Comment("Ancho/Radio del tornado en bloques")
        @Config.RangeInt(min = 1, max = 150)
        public int tornadoWidth = 4;
    }

    public static class MeteorCategory {
        @Config.Comment("Velocidad de caída")
        @Config.RangeDouble(min = 0.1, max = 200.0)
        public double speed = 1.0;

        @Config.Comment("Distancia de aparición sobre el objetivo")
        @Config.RangeInt(min = 5, max = 500)
        public int meteorDistance = 80;

        @Config.Comment("Radio del cráter provocado por el impacto")
        @Config.RangeInt(min = 0, max = 500)
        public int explosionRadius = 20;

        @Config.Comment("Potencia de la explosión")
        @Config.RangeDouble(min = 0.0, max = 500.0)
        public float explosionPower = 10.0f;

        @Config.Comment("Altura física del meteorito")
        @Config.RangeDouble(min = 0.1, max = 500.0)
        public float meteorHeight = 1.5f;

        @Config.Comment("Anchura física del meteorito")
        @Config.RangeDouble(min = 0.1, max = 500.0)
        public float meteorWidth = 1.5f;
    }

    public static class SpawnerCategory {
        @Config.Comment("Habilita o deshabilita la generación automática de desastres en el mundo.")
        public boolean enableSpawner = true;

        @Config.Comment("Probabilidad de generación aleatoria (en ticks). Menor número = Más frecuente.")
        @Config.RangeInt(min = 200, max = 100000)
        public int spawnChance = 12000;
    }

    public static class EarthquakeCategory {
        @Config.Comment("Magnitud del terremoto (Escala Richter 1.0 - 10.0)")
        @Config.RangeDouble(min = 1.0, max = 10.0)
        public double earthquakeMagnitude = 7.0;

        @Config.Comment("Tiempo que dura el terremoto (en ticks: 20 ticks = 1 segundo)")
        @Config.RangeInt(min = 20, max = 72000) // Se ajustó el límite máximo
        public int earthquakeDuration = 600;

        @Config.Comment("Radio Máximo de afectación en bloques")
        @Config.RangeInt(min = 1, max = 500) // Se ajustó el límite máximo
        public int earthquakeMaxRadius = 80;
    }

    public static class FloodCategory {
        @Config.Comment("Velocidad de expansión")
        @Config.RangeDouble(min = 0.01, max = 10.0)
        public double waterSpeed = 0.4;

        @Config.Comment("Radio máximo")
        @Config.RangeDouble(min = 1.0, max = 1000.0)
        public double waterMaxRadius = 100.0;

        @Config.Comment("Si el agua se expande indefinidamente")
        public boolean infiniteWaterExpansion = false;
    }

    @Mod.EventBusSubscriber(modid = MDMain.MODID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(MDMain.MODID)) {
                ConfigManager.sync(MDMain.MODID, Config.Type.INSTANCE);
            }
        }
    }
}