package elec332.craftingtableiv.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 24-6-2015.
 */
public interface IRecipeHandler {

    public boolean canHandleRecipe(IRecipe recipe);

    default public List<Ingredient> getIngredients(IRecipe recipe) {
        return recipe.getIngredients();
    }

    @Nonnull
    default public ItemStack[][] getIngredientStacks(IRecipe recipe) {
        ItemStack[][] ret = getIngredients(recipe).stream().map(ing -> getMatchingStacks(recipe, ing)).toArray(ItemStack[][]::new);
        Lists.newArrayList(ret);
        return ret;
    }

    default public ItemStack[] getMatchingStacks(IRecipe recipe, Ingredient ingredient) {
        return Preconditions.checkNotNull(ingredient.getMatchingStacks());
    }

    /**
     * Returns the recipe width, return -1 for shapeless recipes
     *
     * @param recipe The recipe
     * @return The recipe width, or -1 for shapeless recipes
     */
    default public int getRecipeWidth(IRecipe recipe) {
        return -1;
    }

    default public boolean isValidIngredientFor(IRecipe recipe, Ingredient ingredient, ItemStack inventoryStack) {
        return ingredient.test(inventoryStack);
    }

    @Nullable
    default public ItemStack getCraftingResult(IRecipe recipe, CraftingInventory usedStacks) {
        return recipe.getCraftingResult(usedStacks);
    }

    default public boolean logHandlerErrors() {
        return true;
    }

}
