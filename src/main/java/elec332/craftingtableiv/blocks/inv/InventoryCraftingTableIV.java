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

    private List<WrappedRecipe> recipes;
    //private List<RecipeData> synced;

    public InventoryCraftingTableIV() {
        recipes = Lists.newArrayList();
        //synced = Lists.newArrayList();
    }

    public List<WrappedRecipe> getAllRecipes(){
        return Lists.newArrayList(recipes);
    }

    public int getSize() {
        return recipes.size();
    }

    public boolean addRecipe(WrappedRecipe recipe, GuiCTableIV.StackMatcher stackMatcher){
        if (stackMatcher.canAdd(recipe)){
            return forceAddRecipe(recipe);
        }
        return false;
    }

    public boolean forceAddRecipe(WrappedRecipe recipe){
        //synced.add(new RecipeData(recipe));
        return recipes.add(recipe);
    }

    public boolean addRecipe(WrappedRecipe recipe) {
        return canAdd(recipe) && forceAddRecipe(recipe);
    }

    public WrappedRecipe getIRecipe(int i) {
        return recipes.get(i);
    }

    public boolean canAdd(WrappedRecipe recipe){
        return !recipes.contains(recipe);
    }

    public ItemStack getRecipeOutput(int i) {
        return recipes.get(i).getRecipeOutput().getStack().copy();//return synced.get(i).getOutput();//getIRecipe(i).getRecipeOutput().copy();
    }

    public void clearRecipes() {
        recipes.clear();
    }
/*
    public void writeToNBT(NBTTagCompound tagCompound){
        NBTTagList list = new NBTTagList();
        for (RecipeData data : synced) {
            list.appendTag(data.writeToNBT());
        }
        tagCompound.setTag("data", list);
    }

    public void readFromNBT(NBTTagCompound tagCompound){
        if (tagCompound == null)
            return;
        NBTTagList tagList = tagCompound.getTagList("data", 10);
        synced.clear();
        for (int i = 0; i < tagList.tagCount(); i++) {
            synced.add(RecipeData.fromNBT(tagList.getCompoundTagAt(i)));
        }
    }*/
}