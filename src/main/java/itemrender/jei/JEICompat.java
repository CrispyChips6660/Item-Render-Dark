package itemrender.jei;

import java.util.List;
import java.util.stream.Collectors;

import itemrender.export.IItemList;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEICompat implements IModPlugin
{
    private static IJeiHelpers helper;
    private static IIngredientRegistry ingredients;

    @Override
    public void register(IModRegistry registry)
    {
        helper = registry.getJeiHelpers();
        ingredients = registry.getIngredientRegistry();
    }

    public static class JEIItemList implements IItemList
    {
        public static JEIItemList INSTANCE = new JEIItemList();

        private JEIItemList()
        {
        }

        @Override
        public List<ItemStack> getItems()
        {
            return ingredients.getAllIngredients(VanillaTypes.ITEM).stream().filter(s -> !helper.getIngredientBlacklist().isIngredientBlacklisted(s)).collect(Collectors.toList());
        }
    }
}
