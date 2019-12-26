package elec332.craftingtableiv.handler.vanilla;

import elec332.craftingtableiv.api.IRecipeHandler;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;

/**
 * Created by Elec332 on 6-1-2016.
 */
public class VanillaRecipeHandler implements IRecipeHandler {

    @Override
    public boolean canHandleRecipe(IRecipe recipe) {
        return recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe;
    }

    @Override
    public int getRecipeWidth(IRecipe recipe) {
        return recipe instanceof ShapedRecipe ? ((ShapedRecipe) recipe).getWidth() : -1;
    }

}
