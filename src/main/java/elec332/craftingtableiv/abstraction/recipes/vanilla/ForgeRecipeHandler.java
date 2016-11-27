package elec332.craftingtableiv.abstraction.recipes.vanilla;

import elec332.core.util.recipes.RecipeHelper;
import elec332.craftingtableiv.api.AbstractRecipeHandler;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 7-1-2016.
 */
public class ForgeRecipeHandler extends AbstractRecipeHandler {

    @Override
    public boolean canHandleRecipe(IRecipe recipe) {
        return recipe instanceof ShapedOreRecipe || recipe instanceof ShapelessOreRecipe;
    }

    @Nonnull
    @Override
    public Object[] getIngredients(IRecipe recipe) {
        if (recipe instanceof ShapelessOreRecipe){
            return RecipeHelper.getRecipeOutput((ShapelessOreRecipe) recipe).toArray();
        }
        return ((ShapedOreRecipe)recipe).getInput();
    }

}
