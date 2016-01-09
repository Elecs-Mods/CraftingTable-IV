package elec332.craftingtableiv.api;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 24-6-2015.
 */
public interface IRecipeHandler {

    public boolean canHandleRecipe(IRecipe recipe);

    @Nonnull
    public Object[] getIngredients(IRecipe recipe);

    public boolean isValidIngredientFor(IRecipe recipe, ItemStack recipeStack, ItemStack inventoryStack);

    @Nullable
    public ItemStack getCraftingResult(IRecipe recipe, ItemStack[] usedStacks);

}
