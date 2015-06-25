package elec332.craftingtableiv.api;

import elec332.craftingtableiv.handler.WrappedRecipe;
import net.minecraft.item.crafting.IRecipe;

/**
 * Created by Elec332 on 24-6-2015.
 */
public abstract class AbstractRecipeHandler<R extends IRecipe> implements IRecipeHandler<R> {

    @Override
    public WrappedRecipe getWrappedRecipe(R recipe) {
        Object[] ingredients = getIngredients(recipe);
        if (ingredients == null || ingredients.length == 0)
            return null;
        return new WrappedRecipe(recipe, ingredients);
    }

}
