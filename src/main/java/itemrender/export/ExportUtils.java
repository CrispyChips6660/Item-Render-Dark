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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import itemrender.ItemRender;
import itemrender.ItemRenderConfig;
import itemrender.rendering.FBOHelper;
import itemrender.rendering.Renderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class ExportUtils
{
    // Hardcoded value for mcmod.cn only, don't change this unless the website updates
    private static FBOHelper fboSmall = new FBOHelper(32);
    private static FBOHelper fboLarge = new FBOHelper(128);
    private static FBOHelper fboEntity = new FBOHelper(200);
    private static ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

    private ExportUtils()
    {
    }

    public static String getType(ItemStack itemStack)
    {
        return Block.getBlockFromItem(itemStack.getItem()) != null ? "Block" : "Item";
    }

    public static String getSmallIcon(ItemStack itemStack)
    {
        return Renderer.getItemBase64(itemStack, fboSmall, itemRenderer);
    }

    public static String getLargeIcon(ItemStack itemStack)
    {
        return Renderer.getItemBase64(itemStack, fboLarge, itemRenderer);
    }

    public static String getEntityIcon(EntityType Entitymob)
    {
        return Renderer.getEntityBase64(Entitymob, fboEntity);
    }

    private static String getItemOwner(ItemStack itemStack)
    {
        return itemStack.getItem().getCreatorModId(itemStack);
    }

    private static String getEntityOwner(EntityType Entitymob)
    {
        ResourceLocation registryName = Entitymob.getRegistryName();
        return registryName == null ? "unnamed" : registryName.getNamespace();
    }

    public static int exportMods(Pattern pattern) throws IOException
    {
        int count = 0;
        Multimap<String, ItemData> itemDataList = LinkedListMultimap.create();
        Multimap<String, MobData> mobDataList = LinkedListMultimap.create();
        Minecraft mc = Minecraft.getInstance();

        Language lang = mc.getLanguageManager().getCurrentLanguage();

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        ItemData itemData;
        MobData mobData;

        boolean standard = ItemRenderConfig.format.get() == ExportFormat.STANDARD;

        for (ItemStack itemStack : ItemRender.itemList.getItems())
        {
            if (itemStack == null)
                continue;
            Matcher matcher = pattern.matcher(itemStack.getItem().getRegistryName().toString());
            if (!matcher.find())
                continue;

            if (standard)
            {
                itemData = new ItemDataStandard();
            }
            else
            {
                itemData = new ItemDataMCMOD();
            }
            itemData.setItem(itemStack);
            itemDataList.put(getItemOwner(itemStack), itemData);
        }
        for (EntityType entity : ForgeRegistries.ENTITIES)
        {
            if (entity == null)
                continue;
            Matcher matcher = pattern.matcher(entity.getRegistryName().toString());
            if (!matcher.find())
                continue;

            mobData = new MobData(entity);
            mobDataList.put(getEntityOwner(entity), mobData);
        }

        //boolean reloadstate = ForgeMod.selectiveResourceReloadEnabled;
        //ForgeMod.selectiveResourceReloadEnabled = true;

        // Since refreshResources takes a long time, only refresh once for all the items
        refreshLanguage(mc, "zh_cn");

        for (ItemData data : itemDataList.values())
        {
            data.setName(data.getItem().getDisplayName().getString());
            data.setCreativeName(getCreativeTabName(data));
        }
        for (MobData data : mobDataList.values())
        {
            data.setName(data.getMob().getName().getString());
        }

        refreshLanguage(mc, "en_us");

        for (ItemData data : itemDataList.values())
        {
            data.setEnglishName(data.getItem().getDisplayName().getString());
        }

        for (MobData data : mobDataList.values())
        {
            data.setEnglishname(data.getMob().getName().getString());
        }

        File export;
        PrintWriter pw;
        String comma = standard ? "," : "";
        for (String modid : itemDataList.keySet())
        {
            export = new File(mc.gameDir, String.format("export/" + modid + "_item.json", modid.replaceAll("[^A-Za-z0-9()\\[\\]]", "")));
            if (!export.getParentFile().exists())
                export.getParentFile().mkdirs();
            if (!export.exists())
                export.createNewFile();
            pw = new PrintWriter(export, "UTF-8");

            boolean flag = false;
            for (ItemData data : itemDataList.get(modid))
            {
                if (flag)
                {
                    pw.println(comma);
                }
                flag = true;
                ++count;
                pw.print(gson.toJson(data));
            }
            pw.close();
        }
        for (String modid : mobDataList.keySet())
        {
            export = new File(mc.gameDir, String.format("export/" + modid + "_entity.json", modid.replaceAll("[^A-Za-z0-9()\\[\\]]", "")));
            if (!export.getParentFile().exists())
                export.getParentFile().mkdirs();
            if (!export.exists())
                export.createNewFile();
            pw = new PrintWriter(export, "UTF-8");

            boolean flag = false;
            for (MobData data : mobDataList.get(modid))
            {
                if (flag)
                {
                    pw.println(comma);
                }
                flag = true;
                ++count;
                pw.print(gson.toJson(data));
            }
            pw.close();
        }

        refreshLanguage(mc, lang.getCode());
        //ForgeMod.selectiveResourceReloadEnabled = reloadstate;

        return count;
    }

    private static void refreshLanguage(Minecraft mc, String lang)
    {
        if (!mc.gameSettings.language.equals(lang))
        {
            mc.getLanguageManager().setCurrentLanguage(new Language(lang, "", "", false));
            mc.gameSettings.language = lang;
            mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
            //FMLClientHandler.instance().refreshResources(VanillaResourceType.LANGUAGES);
        }
    }

    private static String getCreativeTabName(ItemData data)
    {
        ItemGroup tab = data.getItem().getItem().getGroup();
        if (tab != null)
        {
            return I18n.format(tab.getTranslationKey());
        }
        else
        {
            return null;
        }
    }
}
