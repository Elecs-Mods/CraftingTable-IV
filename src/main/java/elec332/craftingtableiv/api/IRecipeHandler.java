package elec332.craftingtableiv.api;

import elec332.craftingtableiv.handler.WrappedRecipe;
import net.minecraft.item.crafting.IRecipe;

/**
 * Created by Elec332 on 24-6-2015.
 */
public interface IRecipeHandler<R extends IRecipe> {

    public Object[] getIngredients(R recipe);

    public WrappedRecipe getWrappedRecipe(R recipe);

}
