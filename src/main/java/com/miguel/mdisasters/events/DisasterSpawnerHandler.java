package com.miguel.mdisasters.events;

import com.miguel.mdisasters.MDMain;
import com.miguel.mdisasters.config.MDConfig;
import com.miguel.mdisasters.objects.entities.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

@Mod.EventBusSubscriber(modid = MDMain.MODID)
public class DisasterSpawnerHandler {

    private static final Random RAND = new Random();

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) {
            return;
        }

        World world = event.world;

        if (world.provider.getDimension() != 0) {
            return;
        }

        if (!MDConfig.SPAWNER.enableSpawner) {
            return;
        }

        int spawnChance = MDConfig.SPAWNER.spawnChance;

        for (EntityPlayer player : world.playerEntities) {
            if (RAND.nextInt(spawnChance) == 0) {
                spawnRandomDisaster(world, player);
            }
        }
    }

    private static void spawnRandomDisaster(World world, EntityPlayer player) {
        double angle = RAND.nextDouble() * Math.PI * 2;
        double distance = 30 + RAND.nextInt(31);

        double spawnX = player.posX + Math.cos(angle) * distance;
        double spawnZ = player.posZ + Math.sin(angle) * distance;

        BlockPos targetPos = world.getTopSolidOrLiquidBlock(new BlockPos(spawnX, 0, spawnZ));

        int disasterType = RAND.nextInt(3);

        switch (disasterType) {
            case 0:
                EntityMeteor meteor = new EntityMeteor(world);
                meteor.setPosition(spawnX, targetPos.getY() + MDConfig.METEOR.meteorDistance, spawnZ);
                world.spawnEntity(meteor);

                sendDisasterAlert(player, "A meteorite is falling near your location!", TextFormatting.RED);
                break;

            case 1:
                EntityTornado tornado = new EntityTornado(world);
                tornado.setPosition(spawnX, targetPos.getY(), spawnZ);
                world.spawnEntity(tornado);

                sendDisasterAlert(player, "A tornado has formed nearby!", TextFormatting.DARK_GRAY);
                break;

            case 2:
                EntityTsunami tsunami = new EntityTsunami(world, player);
                tsunami.setPosition(spawnX, targetPos.getY(), spawnZ);
                world.spawnEntity(tsunami);

                sendDisasterAlert(player, "A tsunami is approaching!", TextFormatting.BLUE);
                break;

            case 3:
                EntityFlood flood = new EntityFlood(world, player);
                flood.setPosition(spawnX, targetPos.getY(), spawnZ);
                world.spawnEntity(flood);

                sendDisasterAlert(player, "A flood is approaching!", TextFormatting.DARK_BLUE);
                break;

            case 4:
                EntityEarthquake earthquake = new EntityEarthquake(world, player);
                earthquake.setPosition(spawnX, targetPos.getY(), spawnZ);
                world.spawnEntity(earthquake);

                sendDisasterAlert(player, "A earthquake is appear!", TextFormatting.DARK_RED);
                break;
        }
    }

    private static void sendDisasterAlert(EntityPlayer player, String text, TextFormatting color) {
        TextComponentString message = new TextComponentString(text);
        message.getStyle().setColor(color);
        player.sendMessage(message);
    }
}