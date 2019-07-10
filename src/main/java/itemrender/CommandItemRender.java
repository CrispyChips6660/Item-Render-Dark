/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */
package itemrender;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import itemrender.client.export.ExportUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;

public class CommandItemRender extends CommandBase implements IClientCommand
{

    @Override
    public String getName()
    {
        return "itemrender";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/itemrender scale|export ...";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            throw new CommandException("/itemrender scale|export ...");
        }
        else if (args[0].equalsIgnoreCase("scale"))
        {
            if (args.length == 2)
            {
                float value = Float.valueOf(args[1]);
                if (value > 0.0F && value <= 2.0F)
                {
                    ItemRenderMod.renderScale = Float.valueOf(args[1]);
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Scale: " + value));
                }
                else
                {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Scale Range: (0.0, 2.0]"));
                }
            }
            else
            {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "/itemrender scale [value]"));
                sender.sendMessage(new TextComponentString(TextFormatting.AQUA + "Execute this command to control entity/item rendering scale."));
                sender.sendMessage(new TextComponentString(TextFormatting.AQUA + "Scale Range: (0.0, 2.0]. Default: 1.0. Current: " + ItemRenderMod.renderScale));
            }
        }
        else if (args[0].equalsIgnoreCase("export"))
        {
            String pattern;
            if (args.length == 1)
            {
                pattern = ".*";
            }
            else if (args.length == 2)
            {
                pattern = args[1];
            }
            else
            {
                throw new CommandException("/itemrender export [regex pattern]");
            }
            try
            {
                ExportUtils.INSTANCE.exportMods(pattern);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new CommandException(e.toString());
            }
        }
        else
            throw new CommandException("/itemrender scale|export ...");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "scale", "export");
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message)
    {
        return false;
    }
}
