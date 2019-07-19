package itemrender;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import itemrender.export.ExportUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

public class ItemRenderCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        /* off */
        dispatcher.register(Commands.literal(ItemRender.MODID)
                .then(Commands.literal("export")
                        .executes(ctx -> export(ctx.getSource(), ".+"))
                        .then(Commands.argument("pattern", StringArgumentType.greedyString())
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
}
