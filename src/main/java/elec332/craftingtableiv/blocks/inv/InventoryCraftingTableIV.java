package elec332.craftingtableiv.blocks.inv;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class InventoryCraftingTableIV {

    private IRecipe[] recipes;
    public int[] listIndex;
    private int recipesLength;

    public InventoryCraftingTableIV(int i)
    {
        recipesLength = i;
        recipes = new IRecipe[recipesLength];
        listIndex = new int[recipesLength];
    }

    public int getSize()
    {
        for(int i = 0; i < recipes.length; i++) {
            if(recipes[i] == null)
                return i;
        }

        return 0;
    }

    public boolean addRecipe(IRecipe irecipe, int theIndex)
    {
        int size = getSize();
        if(size >= recipesLength || irecipe == null)
            return false;

        recipes[size] = irecipe;
        listIndex[size] = theIndex;
        return true;
    }

    public IRecipe getIRecipe(int i)
    {
        return recipes[i];
    }
    public int getListIndex(int i)
    {
        return listIndex[i];
    }

    public ItemStack getRecipeOutput(int i)
    {
        if(recipes[i] != null)
            return recipes[i].getRecipeOutput().copy();
        else
            return null;
    }

    public void clearRecipes() {
        recipes = null;
        recipes = new IRecipe[recipesLength];
    }
}