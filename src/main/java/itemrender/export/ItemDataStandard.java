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

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class ItemDataStandard implements ItemData
{
    private String Name;
    private String EnglishName;
    private String RegistryName;
    private String[] ItemTags;
    private String[] BlockTags;
    private String Group;
    private String Type;
    private int MaxStackSize;
    private int MaxDurability;
    private String SmallIcon;
    private String LargeIcon;
    private transient ItemStack itemStack;

    @Override
    public void setItem(ItemStack itemStack)
    {
        RegistryName = itemStack.getItem().getRegistryName().toString();
        List<String> itemTags = itemStack.getItem().getTags().stream().map(Object::toString).collect(Collectors.toList());
        if (!itemTags.isEmpty())
        {
            ItemTags = itemTags.toArray(new String[0]);
        }
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block != null)
        {
            List<String> blockTags = block.getTags().stream().map(Object::toString).collect(Collectors.toList());
            if (!blockTags.isEmpty())
            {
                BlockTags = blockTags.toArray(new String[0]);
            }
        }
        Type = ExportUtils.getType(itemStack);
        MaxStackSize = itemStack.getMaxStackSize();
        MaxDurability = itemStack.getMaxDamage() + 1;
        SmallIcon = ExportUtils.getSmallIcon(itemStack);
        LargeIcon = ExportUtils.getLargeIcon(itemStack);

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
        this.Group = name;
    }

    @Override
    public void setName(String name)
    {
        this.Name = name;
    }

    @Override
    public void setEnglishName(String englishName)
    {
        this.EnglishName = englishName;
    }

}
