package elec332.craftingtableiv.api;

import net.minecraft.item.crafting.IRecipe;

/**
 * Created by Elec332 on 24-6-2015.
 */
public interface ICraftingTableIVAPI {

    public <C extends IRecipe> void registerHandler(Class<C> clazz, IRecipeHandler<C> handler);

    public void registerDisabledRecipe(Class<? extends IRecipe> recipe);

}
