package com.miguel.mdisasters.commands;

import com.miguel.mdisasters.objects.entities.EntityMeteor;
import com.miguel.mdisasters.objects.entities.EntityTornado;
import com.miguel.mdisasters.objects.entities.EntityTsunami;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandClearDisasters extends CommandBase {

    @Override
    public String getName() {
        return "mdisasters";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/mdisasters clear";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();

        // Validar si el argumento es "clear"
        if (args.length > 0 && args[0].equalsIgnoreCase("clear")) {
            if (!world.isRemote) {
                int count = 0;

                List<Entity> loadedEntities = new ArrayList<>(world.loadedEntityList);

                for (Entity entity : loadedEntities) {
                    if (entity instanceof EntityTsunami || entity instanceof EntityTornado || entity instanceof EntityMeteor) {
                        entity.setDead();
                        count++;
                    }
                }

                TextComponentString message = new TextComponentString(count + "disasters have been eliminated from the world.");
                message.getStyle().setColor(TextFormatting.GREEN);
                sender.sendMessage(message);
            }
        } else {
            // Mensaje de uso en caso de escribir mal el subcomando
            TextComponentString usage = new TextComponentString("Correct usage: /mdisasters clear");
            usage.getStyle().setColor(TextFormatting.RED);
            sender.sendMessage(usage);
        }
    }

    // Opcional: Autocompletar el subcomando "clear" con la tecla TAB
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "clear");
        }
        return Collections.emptyList();
    }
}