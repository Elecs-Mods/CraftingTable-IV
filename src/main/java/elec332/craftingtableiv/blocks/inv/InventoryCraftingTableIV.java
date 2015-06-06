package elec332.craftingtableiv.blocks.inv;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class InventoryCraftingTableIV {

    private List<IRecipe> recipes;

    public InventoryCraftingTableIV() {
        recipes = Lists.newArrayList();
    }

    public int getSize() {
        return recipes.size();
    }

    public boolean addRecipe(IRecipe irecipe) {
        return !recipes.contains(irecipe) && recipes.add(irecipe);
    }

    public IRecipe getIRecipe(int i) {
        return recipes.get(i);
    }

    public ItemStack getRecipeOutput(int i) {
        return getIRecipe(i).getRecipeOutput().copy();
    }

    public void clearRecipes() {
        recipes.clear();
    }
}