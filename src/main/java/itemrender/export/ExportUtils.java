/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */

package itemrender.export;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import itemrender.ItemRender;
import itemrender.rendering.FBOHelper;
import itemrender.rendering.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class ExportUtils
{
    public static ExportUtils INSTANCE;

    private FBOHelper fboSmall;
    private FBOHelper fboLarge;
    private FBOHelper fboEntity;
    private RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

    public ExportUtils()
    {
        // Hardcoded value for mcmod.cn only, don't change this unless the website updates
        fboSmall = new FBOHelper(32);
        fboLarge = new FBOHelper(128);
        fboEntity = new FBOHelper(200);
    }

    public String getLocalizedName(ItemStack itemStack)
    {
        return itemStack.getDisplayName();
    }

    public String getType(ItemStack itemStack)
    {
        return (itemStack.getItem() instanceof ItemBlock) ? "Block" : "Item";
    }

    public String getSmallIcon(ItemStack itemStack)
    {
        return Renderer.getItemBase64(itemStack, fboSmall, itemRenderer);
    }

    public String getLargeIcon(ItemStack itemStack)
    {
        return Renderer.getItemBase64(itemStack, fboLarge, itemRenderer);
    }

    public String getEntityIcon(EntityEntry Entitymob)
    {
        return Renderer.getEntityBase64(Entitymob, fboEntity);
    }

    private String getItemOwner(ItemStack itemStack)
    {
        return itemStack.getItem().getCreatorModId(itemStack);
    }

    private String getEntityOwner(EntityEntry Entitymob)
    {
        ResourceLocation registryName = Entitymob.getRegistryName();
        return registryName == null ? "unnamed" : registryName.getNamespace();
    }

    public void exportMods(String pattern) throws IOException
    {
        Multimap<String, ItemData> itemDataList = LinkedListMultimap.create();
        Multimap<String, MobData> mobDataList = LinkedListMultimap.create();
        long ms = Minecraft.getSystemTime();
        Minecraft mc = Minecraft.getMinecraft();

        Language lang = mc.getLanguageManager().getCurrentLanguage();

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        ItemData itemData;
        MobData mobData;

        for (ItemStack itemStack : ItemRender.itemList.getItems())
        {
            if (itemStack == null)
                continue;
            if (!itemStack.getItem().getRegistryName().toString().matches(pattern))
                continue;

            itemData = new ItemData(itemStack);
            itemDataList.put(getItemOwner(itemStack), itemData);
        }
        for (EntityEntry entity : ForgeRegistries.ENTITIES)
        {
            if (entity == null)
                continue;
            if (!entity.getRegistryName().toString().matches(pattern))
                continue;

            mobData = new MobData(entity);
            mobDataList.put(getEntityOwner(entity), mobData);
        }

        boolean reloadstate = ForgeModContainer.selectiveResourceReloadEnabled;
        boolean unicodeState = mc.fontRenderer.getUnicodeFlag();
        ForgeModContainer.selectiveResourceReloadEnabled = true;

        // Since refreshResources takes a long time, only refresh once for all the items
        refreshLanguage(mc, "zh_CN");

        for (ItemData data : itemDataList.values())
        {
            if (ItemRender.debugMode)
                ItemRender.instance.log.info(I18n.format("itemrender.msg.addCN", data.getItemStack().getTranslationKey() + "@" + data.getItemStack().getMetadata()));
            data.setName(data.getItemStack().getDisplayName());
            data.setCreativeName(getCreativeTabName(data));
        }
        for (MobData data : mobDataList.values())
        {
            if (ItemRender.debugMode)
                ItemRender.instance.log.info(I18n.format("itemrender.msg.addCN", data.getMob().getRegistryName()));
            data.setName(I18n.format("entity." + data.getMob().getName() + ".name"));
        }

        mc.fontRenderer.setUnicodeFlag(false);
        refreshLanguage(mc, "en_US");

        for (ItemData data : itemDataList.values())
        {
            if (ItemRender.debugMode)
                ItemRender.instance.log.info(I18n.format("itemrender.msg.addEN", data.getItemStack().getTranslationKey() + "@" + data.getItemStack().getMetadata()));
            data.setEnglishName(this.getLocalizedName(data.getItemStack()));
        }

        for (MobData data : mobDataList.values())
        {
            if (ItemRender.debugMode)
                ItemRender.instance.log.info(I18n.format("itemrender.msg.addEN", data.getMob().getRegistryName()));
            data.setEnglishname(new TextComponentTranslation("entity." + data.getMob().getName() + ".name", new Object[0]).getFormattedText());
        }

        File export;
        File export1;
        for (String modid : itemDataList.keySet())
        {
            export = new File(mc.gameDir, String.format("export/" + modid + "_item.json", modid.replaceAll("[^A-Za-z0-9()\\[\\]]", "")));
            if (!export.getParentFile().exists())
                export.getParentFile().mkdirs();
            if (!export.exists())
                export.createNewFile();
            PrintWriter pw = new PrintWriter(export, "UTF-8");

            for (ItemData data : itemDataList.get(modid))
            {
                pw.println(gson.toJson(data));
            }
            pw.close();

        }
        for (String modid : mobDataList.keySet())
        {
            export1 = new File(mc.gameDir, String.format("export/" + modid + "_entity.json", modid.replaceAll("[^A-Za-z0-9()\\[\\]]", "")));
            if (!export1.getParentFile().exists())
                export1.getParentFile().mkdirs();
            if (!export1.exists())
                export1.createNewFile();
            PrintWriter pw1 = new PrintWriter(export1, "UTF-8");

            for (MobData data : mobDataList.get(modid))
            {
                pw1.println(gson.toJson(data));
            }
            pw1.close();
        }

        refreshLanguage(mc, lang.getLanguageCode());
        ForgeModContainer.selectiveResourceReloadEnabled = reloadstate;
        mc.fontRenderer.setUnicodeFlag(unicodeState);

        String output = String.format("导出完毕。耗时%ss", (Minecraft.getSystemTime() - ms) / 1000f);
        mc.player.sendMessage(new TextComponentString(output));
    }

    private static void refreshLanguage(Minecraft mc, String lang)
    {
        if (!mc.gameSettings.language.equals(lang))
        {
            mc.getLanguageManager().setCurrentLanguage(new Language(lang, "", "", false));
            mc.gameSettings.language = lang;
            FMLClientHandler.instance().refreshResources(VanillaResourceType.LANGUAGES);
        }
    }

    private String getCreativeTabName(ItemData data)
    {
        CreativeTabs tab = data.getItemStack().getItem().getCreativeTab();
        if (tab != null)
        {
            return I18n.format(tab.getTranslationKey());
        }
        else
        {
            return "";
        }
    }
}
