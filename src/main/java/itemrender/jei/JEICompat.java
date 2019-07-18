package itemrender.jei;

import java.util.List;
import java.util.stream.Collectors;

import itemrender.ItemRender;
import itemrender.export.IItemList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEICompat implements IModPlugin
{
    private static IJeiHelpers helper;
    private static IIngredientManager ingredients;
    public static final ResourceLocation UID = new ResourceLocation(ItemRender.MODID, "export");

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        helper = registration.getJeiHelpers();
        ingredients = registration.getIngredientManager();
    }

    @Override
    public ResourceLocation getPluginUid()
    {
        return UID;
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
            // TODO: blacklist
            return ingredients.getAllIngredients(VanillaTypes.ITEM).stream().collect(Collectors.toList());
        }
    }
}
