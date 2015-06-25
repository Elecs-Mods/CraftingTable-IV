package elec332.craftingtableiv.api;

import elec332.craftingtableiv.handler.WrappedRecipe;
import net.minecraft.item.crafting.IRecipe;

/**
 * Created by Elec332 on 24-6-2015.
 */
public abstract class AbstractRecipeHandler<R extends IRecipe> implements IRecipeHandler<R> {

    @Override
    public WrappedRecipe getWrappedRecipe(R recipe) {
        return new WrappedRecipe(recipe, getIngredients(recipe));
    }

}
