package itemrender.export;

import net.minecraft.item.ItemStack;

public interface ItemData
{
    void setItem(ItemStack stack);

    ItemStack getItem();

    void setCreativeName(String name);

    void setName(String name);

    void setEnglishName(String englishName);
}
