package elec332.craftingtableiv.handler.vanilla;

import elec332.craftingtableiv.api.IRecipeHandler;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 6-1-2016.
 */
public class VanillaRecipeHandler implements IRecipeHandler {

    @Override
    public boolean canHandleRecipe(IRecipe recipe) {
        return recipe instanceof ShapedRecipes || recipe instanceof ShapelessRecipes;
    }

    @Override
    @Nonnull
    public Object[] getIngredients(IRecipe recipe) {
        if (recipe instanceof ShapelessRecipes){
            return ((ShapelessRecipes) recipe).recipeItems.toArray();
        }
        return ((ShapedRecipes) recipe).recipeItems;
    }

    @Override
    public int getRecipeWidth(IRecipe recipe) {
        return recipe instanceof ShapedRecipes ? ((ShapedRecipes) recipe).recipeWidth : -1;
    }

}
