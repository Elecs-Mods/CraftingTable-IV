package elec332.craftingtableiv.compat.handlers;

import elec332.craftingtableiv.api.AbstractRecipeHandler;
import elec332.craftingtableiv.compat.AbstractCompatModule;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.core.recipes.ShapelessRecipeCustom;
import forestry.lepidopterology.recipes.MatingRecipe;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nonnull;

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
        registerHandler(new AbstractRecipeHandler() {

            @Override
            public boolean canHandleRecipe(IRecipe recipe) {
                return recipe instanceof ShapedRecipeCustom;
            }

            @Override
            @Nonnull
            public Object[] getIngredients(IRecipe recipe) {
                return ((ShapedRecipeCustom) recipe).getIngredients();
            }

        });
        registerHandler(new AbstractRecipeHandler() {

            @Override
            public boolean canHandleRecipe(IRecipe recipe) {
                return recipe instanceof ShapelessRecipeCustom;
            }

            @Override
            @Nonnull
            public Object[] getIngredients(IRecipe recipe) {
                return ((ShapelessRecipeCustom) recipe).getIngredients().toArray();
            }

        });
        registerDisabledRecipe(MatingRecipe.class);
    }

}
