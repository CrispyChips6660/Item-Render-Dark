package itemrender;

import org.apache.commons.lang3.tuple.Pair;

import itemrender.export.ExportFormat;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ItemRenderConfig
{
    static final ForgeConfigSpec spec;

    public static IntValue mainBlockSize;
    public static IntValue gridBlockSize;
    public static IntValue mainEntitySize;
    public static IntValue gridEntitySize;
    public static IntValue playerSize;
    public static EnumValue<ExportFormat> format;

    static
    {
        final Pair<ItemRenderConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ItemRenderConfig::new);
        spec = specPair.getRight();
    }

    private ItemRenderConfig(ForgeConfigSpec.Builder builder)
    {
        mainBlockSize = builder.defineInRange("mainBlockSize", 128, 0, Integer.MAX_VALUE);
        gridBlockSize = builder.defineInRange("gridBlockSize", 32, 0, Integer.MAX_VALUE);
        mainEntitySize = builder.defineInRange("mainEntitySize", 512, 0, Integer.MAX_VALUE);
        gridEntitySize = builder.defineInRange("gridEntitySize", 128, 0, Integer.MAX_VALUE);
        playerSize = builder.defineInRange("playerSize", 1024, 0, Integer.MAX_VALUE);
        format = builder.comment("MCMODCN or STANDARD").defineEnum("format", ExportFormat.MCMODCN);
    }
}
