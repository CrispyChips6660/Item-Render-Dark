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

import java.util.ArrayList;
import java.util.List;

import itemrender.ItemRender;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class ItemDataStandard implements ItemData
{
    private String name;
    private String englishName;
    private String registerName;
    private int metadata;
    private String[] OredictList;
    private String CreativeTabName;
    private String type;
    private int maxStackSize;
    private int maxDurability;
    private String smallIcon;
    private String largeIcon;
    private transient ItemStack itemStack;

    @Override
    public void setItem(ItemStack itemStack)
    {
        if (ItemRender.debugMode)
            ItemRender.instance.log.info(I18n.format("itemrender.msg.processing", itemStack.getTranslationKey() + "@" + itemStack.getMetadata()));
        name = null;
        englishName = null;
        registerName = itemStack.getItem().getRegistryName().toString();
        metadata = itemStack.getMetadata();
        List<String> list = new ArrayList<String>();
        if (!itemStack.isEmpty())
        {
            for (int i : OreDictionary.getOreIDs(itemStack))
            {
                String ore = OreDictionary.getOreName(i);
                list.add(ore);
            }
            OredictList = list.toArray(new String[0]);
        }
        CreativeTabName = null;
        type = ExportUtils.INSTANCE.getType(itemStack);
        maxStackSize = itemStack.getMaxStackSize();
        maxDurability = itemStack.getMaxDamage() + 1;
        smallIcon = ExportUtils.INSTANCE.getSmallIcon(itemStack);
        largeIcon = ExportUtils.INSTANCE.getLargeIcon(itemStack);

        this.itemStack = itemStack;
    }

    @Override
    public ItemStack getItem()
    {
        return itemStack;
    }

    @Override
    public void setCreativeName(String name)
    {
        this.CreativeTabName = name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public void setEnglishName(String englishName)
    {
        this.englishName = englishName;
    }

}
