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

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class ItemDataMCMOD implements ItemData
{
    private String Name;
    private String EnglishName;
    private String RegistryName;
    private String ItemTags;
    private String BlockTags;
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
        ItemTags = itemStack.getItem().getTags().toString();
        if (ItemTags.equals("[]"))
        {
            ItemTags = null;
        }
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block != null)
        {
            Set<ResourceLocation> tags = block.getTags();
            if (!tags.isEmpty())
            {
                BlockTags = tags.toString();
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
