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

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GLContext;

import itemrender.export.ExportFormat;
import itemrender.export.ExportUtils;
import itemrender.export.IItemList;
import itemrender.export.ItemList;
import itemrender.jei.JEICompat.JEIItemList;
import itemrender.keybind.KeybindExport;
import itemrender.keybind.KeybindRenderCurrentPlayer;
import itemrender.keybind.KeybindRenderEntity;
import itemrender.keybind.KeybindRenderInventoryBlock;
import itemrender.keybind.KeybindToggleRender;
import itemrender.keybind.KeybindWarn;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
        modid = ItemRender.MODID, name = "Item Render Dark", version = "@VERSION@", guiFactory = "itemrender.ItemRenderGuiFactory", acceptedMinecraftVersions = "[1.12, 1.12.10]", clientSideOnly = true
)
public class ItemRender
{

    static final String MODID = "itemrender";

    public static final int DEFAULT_MAIN_BLOCK_SIZE = 128;
    public static final int DEFAULT_GRID_BLOCK_SIZE = 32;
    public static final int DEFAULT_MAIN_ENTITY_SIZE = 512;
    public static final int DEFAULT_GRID_ENTITY_SIZE = 128;
    public static final int DEFAULT_PLAYER_SIZE = 1024;
    public static float renderScale = 1.0F;

    @Mod.Instance(MODID)
    public static ItemRender instance;

    public static Configuration cfg;
    public static boolean gl32_enabled = false;

    public static int mainBlockSize = DEFAULT_MAIN_BLOCK_SIZE;
    public static int gridBlockSize = DEFAULT_GRID_BLOCK_SIZE;
    public static int mainEntitySize = DEFAULT_MAIN_ENTITY_SIZE;
    public static int gridEntitySize = DEFAULT_GRID_ENTITY_SIZE;
    public static int playerSize = DEFAULT_PLAYER_SIZE;
    public static boolean debugMode = false;
    public static ExportFormat format = ExportFormat.MCMODCN;
    public static IItemList itemList;
    public Logger log;

    @SideOnly(Side.CLIENT)
    private RenderTickHandler renderTickHandler = new RenderTickHandler();

    private static void syncConfig()
    {
        mainBlockSize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderBlockMain", DEFAULT_MAIN_BLOCK_SIZE, I18n.format("itemrender.cfg.mainblock")).getInt();
        gridBlockSize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderBlockGrid", DEFAULT_GRID_BLOCK_SIZE, I18n.format("itemrender.cfg.gridblock")).getInt();
        mainEntitySize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderEntityMain", DEFAULT_MAIN_ENTITY_SIZE, I18n.format("itemrender.cfg.mainentity")).getInt();
        gridEntitySize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderEntityGrid", DEFAULT_GRID_ENTITY_SIZE, I18n.format("itemrender.cfg.gridentity")).getInt();
        playerSize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderPlayer", DEFAULT_PLAYER_SIZE, I18n.format("itemrender.cfg.player")).getInt();
        debugMode = cfg.get(Configuration.CATEGORY_GENERAL, "DebugMode", false, I18n.format("itemrender.cfg.debug")).getBoolean();
        format = ExportFormat.parse(cfg.getString("ExportFormat", Configuration.CATEGORY_GENERAL, "mcmodcn", "Supported values: mcmodcn, standard"));
        if (cfg.hasChanged())
            cfg.save();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();
        gl32_enabled = GLContext.getCapabilities().OpenGL32;

        // Config
        cfg = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(instance);
        MinecraftForge.EVENT_BUS.register(renderTickHandler);
        ClientCommandHandler.instance.registerCommand(new CommandItemRender());

        if (gl32_enabled)
        {
            ExportUtils.INSTANCE = new ExportUtils();

            KeybindRenderInventoryBlock defaultRender = new KeybindRenderInventoryBlock(mainBlockSize, "", Keyboard.KEY_LBRACKET, I18n.format("itemrender.key.block", mainBlockSize));
            RenderTickHandler.keybindToRender = defaultRender;
            MinecraftForge.EVENT_BUS.register(new KeybindRenderEntity(mainEntitySize, "", Keyboard.KEY_SEMICOLON, I18n.format("itemrender.key.entity", mainEntitySize)));
            MinecraftForge.EVENT_BUS.register(new KeybindRenderEntity(gridEntitySize, "_grid", Keyboard.KEY_APOSTROPHE, I18n.format("itemrender.key.entity", gridEntitySize)));
            MinecraftForge.EVENT_BUS.register(defaultRender);
            MinecraftForge.EVENT_BUS.register(new KeybindRenderInventoryBlock(gridBlockSize, "_grid", Keyboard.KEY_RBRACKET, I18n.format("itemrender.key.block", gridBlockSize)));
            MinecraftForge.EVENT_BUS.register(new KeybindToggleRender());
            MinecraftForge.EVENT_BUS.register(new KeybindRenderCurrentPlayer(playerSize));
            MinecraftForge.EVENT_BUS.register(new KeybindExport());
        }
        else
        {
            MinecraftForge.EVENT_BUS.register(new KeybindWarn());
            log.error(I18n.format("itemrender.msg.openglerror"));
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (Loader.isModLoaded("jei"))
        {
            itemList = JEIItemList.INSTANCE;
        }
        else
        {
            itemList = new ItemList();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(ItemRender.MODID))
            syncConfig();
    }
}
