package elec332.craftingtableiv.api;

import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * Created by Elec332 on 24-6-2015.
 */
public interface ICraftingTableIVAPI {

    public void registerHandler(IRecipeHandler handler) throws IllegalStateException;

    public void registerDisabledRecipe(Class<? extends IRecipe> recipe) throws IllegalStateException;

    public List<IRecipeHandler> getRegistry() throws IllegalAccessError;

    public boolean isRecipeDisabled(IRecipe recipe);

    public boolean isRecipeDisabled(Class<? extends IRecipe> recipeClass);

}
