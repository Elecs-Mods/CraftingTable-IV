package elec332.craftingtableiv.util;

import com.google.common.collect.Lists;
import elec332.craftingtableiv.inventory.WindowCraftingTableIV;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class RecipeCache {

    private List<WrappedRecipe> recipes, shownRecipes;

    public RecipeCache() {
        recipes = Lists.newArrayList();
        shownRecipes = Lists.newArrayList();
    }

    public List<WrappedRecipe> getAllRecipes(){
        return Lists.newArrayList(recipes);
    }

    public int getShownSize() {
        return shownRecipes.size();
    }

    public void addRecipe(WrappedRecipe recipe, WindowCraftingTableIV.StackMatcher matcher){
        if (matcher.canAdd(recipe)){
            shownRecipes.add(recipe);
        }
        recipes.add(recipe);
    }

    public WrappedRecipe getShownRecipe(int i) {
        return shownRecipes.get(i);
    }

    public ItemStack getRecipeOutput(int i) {
        return getShownRecipe(i).getRecipeOutput();
    }

    public void updateVisual(WindowCraftingTableIV.StackMatcher stackMatcher){
        shownRecipes.clear();
        for (WrappedRecipe wrappedRecipe : recipes){
            if (stackMatcher.canAdd(wrappedRecipe)){
                shownRecipes.add(wrappedRecipe);
            }
        }
    }

    public void clearRecipes() {
        recipes.clear();
        shownRecipes.clear();
    }

}