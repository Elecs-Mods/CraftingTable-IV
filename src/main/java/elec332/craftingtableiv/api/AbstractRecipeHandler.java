package elec332.craftingtableiv.api;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Elec332 on 24-6-2015.
 */
public abstract class AbstractRecipeHandler implements IRecipeHandler {

    @Override
    public boolean isValidIngredientFor(IRecipe recipe, ItemStack recipeStack, ItemStack inventoryStack) {
        return recipeStack.getItem() == inventoryStack.getItem() && (recipeStack.getItemDamage() == inventoryStack.getItemDamage() || recipeStack.getItemDamage() == OreDictionary.WILDCARD_VALUE || !recipeStack.getHasSubtypes() && !inventoryStack.getHasSubtypes());
    }

    @Override
    public ItemStack getCraftingResult(IRecipe recipe, ItemStack[] usedStacks) {
        return recipe.getRecipeOutput().copy();
    }

}
