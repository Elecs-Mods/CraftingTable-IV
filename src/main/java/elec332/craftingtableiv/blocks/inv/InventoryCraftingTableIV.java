package elec332.craftingtableiv.blocks.inv;

import com.google.common.collect.Lists;
import elec332.craftingtableiv.abstraction.handler.WrappedRecipe;
import elec332.craftingtableiv.blocks.container.GuiCTableIV;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class InventoryCraftingTableIV {

    private List<WrappedRecipe> recipes, allRecipes;

    public InventoryCraftingTableIV() {
        recipes = Lists.newArrayList();
        allRecipes = Lists.newArrayList();
    }

    public List<WrappedRecipe> getAllRecipes(){
        return Lists.newArrayList(allRecipes);
    }

    public int getVisibleSize() {
        return recipes.size();
    }

    public void addRecipe(WrappedRecipe recipe, GuiCTableIV.StackMatcher stackMatcher){
        if (stackMatcher.canAdd(recipe)){
            recipes.add(recipe);
        }
        allRecipes.add(recipe);
    }

    public void updateVisual(GuiCTableIV.StackMatcher stackMatcher){
        recipes.clear();
        for (WrappedRecipe wrappedRecipe : allRecipes){
            if (stackMatcher.canAdd(wrappedRecipe)){
                recipes.add(wrappedRecipe);
            }
        }
    }

    public WrappedRecipe getRecipe(int i) {
        return recipes.get(i);
    }

    public ItemStack getRecipeOutput(int i) {
        return getRecipe(i).getRecipeOutput().copy();
    }

    public void clearRecipes() {
        recipes.clear();
        allRecipes.clear();
    }

}