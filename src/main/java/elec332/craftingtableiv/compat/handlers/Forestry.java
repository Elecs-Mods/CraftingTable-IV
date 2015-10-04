package elec332.craftingtableiv.compat.handlers;

import elec332.craftingtableiv.api.AbstractRecipeHandler;
import elec332.craftingtableiv.compat.AbstractCompatModule;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.lepidopterology.MatingRecipe;

/**
 * Created by Elec332 on 4-10-2015.
 */
public class Forestry extends AbstractCompatModule {

    @Override
    public String getName() {
        return "Forestry";
    }

    @Override
    public void init() {
        registerHandler(ShapedRecipeCustom.class, new AbstractRecipeHandler<ShapedRecipeCustom>() {
            @Override
            public Object[] getIngredients(ShapedRecipeCustom recipe) {
                return recipe.getIngredients();
            }
        });
        registerDisabledRecipe(MatingRecipe.class);
    }

}
