package elec332.craftingtableiv.blocks.inv;

import elec332.craftingtableiv.abstraction.handler.WrappedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Created by Elec332 on 5-7-2015.
 */
public class RecipeData{

    public RecipeData(WrappedRecipe recipe){
        this();
        output = recipe.getRecipeOutput();
        for (int i = 0; i < recipe.getInput().length; i++) {
            if (recipe.getInput()[i] instanceof ItemStack) {
                input[i] = (ItemStack) recipe.getInput()[i];
            } else if (recipe.getInput()[i] != null){
                input[i] = (ItemStack) ((List) recipe.getInput()[i]).get(0);
            }
        }
    }

    private RecipeData(){
        input = new ItemStack[9];
    }

    private ItemStack[] input;
    private ItemStack output;

    public ItemStack getOutput(){
        return output;
    }

    public NBTTagCompound writeToNBT(){
        NBTTagCompound mainTag = new NBTTagCompound();
        for (int i = 0; i < 9; i++) {
            mainTag.setTag("input"+i, input[i].writeToNBT(new NBTTagCompound()));
        }
        mainTag.setTag("output", output.writeToNBT(new NBTTagCompound()));
        return mainTag;
    }

    public static RecipeData fromNBT(NBTTagCompound tagCompound){
        RecipeData ret = new RecipeData();
        for (int i = 0; i < 9; i++) {
            ret.input[i] = ItemStack.loadItemStackFromNBT(tagCompound.getCompoundTag("input"+1));
        }
        ret.output = ItemStack.loadItemStackFromNBT(tagCompound.getCompoundTag("output"));
        return ret;
    }
}
