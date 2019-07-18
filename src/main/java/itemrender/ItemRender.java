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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import itemrender.export.IItemList;
import itemrender.jei.JEICompat.JEIItemList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(ItemRender.MODID)
public class ItemRender
{
    public static final String MODID = "itemrender";
    public static final String NAME = "ItemRenderDark";

    public static Logger logger = LogManager.getLogger(ItemRender.NAME);

    public static float renderScale = 1.0F;

    public static IItemList itemList = JEIItemList.INSTANCE;

    public ItemRender()
    {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER)
        {
            return;
        }
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::init);
        MinecraftForge.EVENT_BUS.addListener(this::serverInit);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ItemRenderConfig.spec, MODID + ".toml");

    }

    public void serverInit(FMLServerStartingEvent event)
    {
        if (!(event.getServer() instanceof DedicatedServer))
        {
            ItemRenderCommand.register(event.getCommandDispatcher());
        }
    }

    public void init(FMLClientSetupEvent event)
    {
        //        MinecraftForge.EVENT_BUS.register(RenderTickHandler.class);

        //            KeybindRenderInventoryBlock defaultRender = new KeybindRenderInventoryBlock(ItemRenderConfig.mainBlockSize.get(), "", Keyboard.KEY_LBRACKET, I18n.format("itemrender.key.block", mainBlockSize));
        //            RenderTickHandler.keybindToRender = defaultRender;
        //            MinecraftForge.EVENT_BUS.register(new KeybindRenderEntity(ItemRenderConfig.mainEntitySize.get(), "", Keyboard.KEY_SEMICOLON, I18n.format("itemrender.key.entity", mainEntitySize)));
        //            MinecraftForge.EVENT_BUS.register(new KeybindRenderEntity(ItemRenderConfig.gridEntitySize.get(), "_grid", Keyboard.KEY_APOSTROPHE, I18n.format("itemrender.key.entity", gridEntitySize)));
        //            MinecraftForge.EVENT_BUS.register(defaultRender);
        //            MinecraftForge.EVENT_BUS.register(new KeybindRenderInventoryBlock(ItemRenderConfig.gridBlockSize, "_grid", Keyboard.KEY_RBRACKET, I18n.format("itemrender.key.block", gridBlockSize)));
        //            MinecraftForge.EVENT_BUS.register(new KeybindToggleRender());
        //            MinecraftForge.EVENT_BUS.register(new KeybindRenderCurrentPlayer(ItemRenderConfig.playerSize.get()));
    }
}
