package elec332.craftingtableiv.api;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 24-6-2015.
 */
public interface IRecipeHandler {

    public boolean canHandleRecipe(IRecipe recipe);

    @Nonnull
    public Object[] getIngredients(IRecipe recipe);

    /**
     * Returns the recipe width, return -1 for shapeless recipes
     *
     * @param recipe The recipe
     * @return The recipe width, or -1 for shapeless recipes
     */
    default public int getRecipeWidth(IRecipe recipe){
        return -1;
    }

    default public boolean isValidIngredientFor(IRecipe recipe, ItemStack recipeStack, ItemStack inventoryStack) {
        return recipeStack.getItem() == inventoryStack.getItem() && (recipeStack.getItemDamage() == inventoryStack.getItemDamage() || recipeStack.getItemDamage() == OreDictionary.WILDCARD_VALUE || !recipeStack.getHasSubtypes() && !inventoryStack.getHasSubtypes());
    }

    @Nullable
    default public ItemStack getCraftingResult(IRecipe recipe, InventoryCrafting usedStacks) {
        return recipe.getCraftingResult(usedStacks);
    }

    default public boolean logHandlerErrors(){
        return true;
    }

}
