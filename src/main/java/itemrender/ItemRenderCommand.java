package itemrender;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import itemrender.export.ExportUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

public class ItemRenderCommand
{
    private static final SimpleCommandExceptionType WRONG_PATTERN_EXCEPTION = new SimpleCommandExceptionType(new StringTextComponent("模式器解析失败"));
    private static final SimpleCommandExceptionType IO_EXCEPTION = new SimpleCommandExceptionType(new StringTextComponent("文件创建失败"));

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        /* off */
        dispatcher.register(Commands.literal(ItemRender.MODID)
                .then(Commands.literal("export")
                        .executes(ctx -> export(ctx.getSource(), ".+"))
                        .then(Commands.argument("pattern", StringArgumentType.string())
                                .executes(ctx -> export(ctx.getSource(), StringArgumentType.getString(ctx, "pattern")))))
                .then(Commands.literal("scale")
                        .executes(ctx -> getScale(ctx.getSource()))
                        .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 2))
                                .executes(ctx -> setScale(ctx.getSource(), FloatArgumentType.getFloat(ctx, "scale"))))));
        /* on */
    }

    private static int setScale(CommandSource source, float scale)
    {
        ItemRender.renderScale = scale;
        source.sendFeedback(new StringTextComponent(String.format("缩放：%s", scale)), true);
        return 0;
    }

    private static int getScale(CommandSource source)
    {
        source.sendFeedback(new StringTextComponent(String.format("目前缩放：%s", ItemRender.renderScale)), true);
        return 0;
    }

    private static int export(CommandSource source, String pattern) throws CommandSyntaxException
    {
        Minecraft.getInstance().execute(() -> {
            try
            {
                long ms = Util.milliTime();
                Pattern p = Pattern.compile(pattern);
                int r = ExportUtils.exportMods(p);
                if (r == 0)
                {
                    source.sendErrorMessage(new StringTextComponent("未发现匹配的条目"));
                }
                else
                {
                    source.sendFeedback(new StringTextComponent(String.format("导出完毕。耗时%ss", (Util.milliTime() - ms) / 1000f)), true);
                    source.sendFeedback(new StringTextComponent(String.format("成功导出%d条数据", r)), true);
                }
            }
            catch (PatternSyntaxException e)
            {
                source.sendErrorMessage(new StringTextComponent("模式器解析失败"));
            }
            catch (IOException e)
            {
                source.sendErrorMessage(new StringTextComponent("文件创建失败"));
            }
        });
        return 0;
    }

    //    @Override
    //    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    //    {
    //        if (args.length == 0)
    //        {
    //            throw new CommandException("/itemrender scale|export ...");
    //        }
    //        else if (args[0].equalsIgnoreCase("scale"))
    //        {
    //            if (args.length == 2)
    //            {
    //                float value = Float.valueOf(args[1]);
    //                if (value > 0.0F && value <= 2.0F)
    //                {
    //                    ItemRender.renderScale = Float.valueOf(args[1]);
    //                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Scale: " + value));
    //                }
    //                else
    //                {
    //                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Scale Range: (0.0, 2.0]"));
    //                }
    //            }
    //            else
    //            {
    //                sender.sendMessage(new TextComponentString(TextFormatting.RED + "/itemrender scale [value]"));
    //                sender.sendMessage(new TextComponentString(TextFormatting.AQUA + "Execute this command to control entity/item rendering scale."));
    //                sender.sendMessage(new TextComponentString(TextFormatting.AQUA + "Scale Range: (0.0, 2.0]. Default: 1.0. Current: " + ItemRender.renderScale));
    //            }
    //        }
    //        else if (args[0].equalsIgnoreCase("export"))
    //        {
    //            String pattern;
    //            if (args.length == 1)
    //            {
    //                pattern = ".*";
    //            }
    //            else if (args.length == 2)
    //            {
    //                pattern = args[1];
    //            }
    //            else
    //            {
    //                throw new CommandException("/itemrender export [regex pattern]");
    //            }
    //            try
    //            {
    //                Pattern pattern2 = Pattern.compile(pattern);
    //                ExportUtils.INSTANCE.exportMods(pattern2);
    //            }
    //            catch (PatternSyntaxException | IOException e)
    //            {
    //                e.printStackTrace();
    //                throw new CommandException(e.toString());
    //            }
    //        }
    //        else
    //            throw new CommandException("/itemrender scale|export ...");
    //    }

}
