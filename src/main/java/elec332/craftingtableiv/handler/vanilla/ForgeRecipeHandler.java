package elec332.craftingtableiv.handler.vanilla;

import elec332.craftingtableiv.api.IRecipeHandler;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.crafting.IShapedRecipe;

/**
 * Created by Elec332 on 7-1-2016.
 */
public class ForgeRecipeHandler implements IRecipeHandler {

    @Override
    public boolean canHandleRecipe(IRecipe recipe) {
        return recipe instanceof IShapedRecipe<?>;
    }

    @Override
    public int getRecipeWidth(IRecipe recipe) {
        return ((IShapedRecipe) recipe).getRecipeWidth();
    }

}
