package elec332.craftingtableiv.blocks.inv;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.util.ArrayList;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class InventoryCraftingTableIV {

    private ArrayList<IRecipe> recipes;


    public InventoryCraftingTableIV() {
        recipes = new ArrayList<IRecipe>();
    }

    public int getSize() {
        return recipes.size();
    }

    public boolean addRecipe(IRecipe irecipe) {
        return recipes.add(irecipe);
    }

    public IRecipe getIRecipe(int i) {
        return recipes.get(i);
    }

    public ItemStack getRecipeOutput(int i) {
        //if(getIRecipe(i) != null)
            return getIRecipe(i).getRecipeOutput().copy();
        //else
           // return null;
    }

    public void clearRecipes() {
        recipes.clear();
    }
}