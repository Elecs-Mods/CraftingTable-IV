package elec332.craftingtableiv.compat;

import elec332.core.util.AbstractCompatHandler;
import elec332.craftingtableiv.api.CraftingTableIVAPI;
import elec332.craftingtableiv.api.IRecipeHandler;
import net.minecraft.item.crafting.IRecipe;

/**
 * Created by Elec332 on 4-10-2015.
 */
public abstract class AbstractCompatModule extends AbstractCompatHandler.ICompatHandler {

    public <C extends IRecipe> void registerHandler(Class<C> clazz, IRecipeHandler<C> handler){
        CraftingTableIVAPI.getAPI().registerHandler(clazz, handler);
    }

    public void registerDisabledRecipe(Class<? extends IRecipe> recipe){
        CraftingTableIVAPI.getAPI().registerDisabledRecipe(recipe);
    }

}
