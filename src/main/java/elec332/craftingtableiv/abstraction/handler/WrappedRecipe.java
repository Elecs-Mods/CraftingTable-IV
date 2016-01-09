package elec332.craftingtableiv.abstraction.handler;

import elec332.craftingtableiv.abstraction.CraftingTableIVAbstractionLayer;
import elec332.craftingtableiv.api.IRecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * Created by Elec332 on 21-6-2015.
 */
public class WrappedRecipe {

    public static WrappedRecipe of(Object[] input, IRecipe recipe, IRecipeHandler handler){
        for (Object obj : input){
            if (obj instanceof ItemStack || obj == null)
                continue;
            if (obj instanceof List){
                if (!((List) obj).isEmpty() && ((List) obj).get(0) instanceof ItemStack)
                    continue;
            }
            System.out.println("ERROR: "+recipe.getRecipeOutput().toString()+" ... "+recipe.toString());
            throw new IllegalArgumentException();
        }
        return new WrappedRecipe(input, recipe, handler);
    }

    private WrappedRecipe(Object[] input, IRecipe recipe, IRecipeHandler handler){
        this.input = input;
        this.outPut = recipe.getRecipeOutput().copy();
        this.outputItemName = CraftingTableIVAbstractionLayer.instance.mod.getItemRegistryName(recipe.getRecipeOutput());
        this.recipe = recipe;
        this.identifier = CraftingTableIVAbstractionLayer.instance.mod.getItemRegistryName(recipe.getRecipeOutput().copy()).replace(":", " ").split(" ")[0];
        this.recipeHandler = handler;
        if (CraftingTableIVAbstractionLayer.instance.mod.isEffectiveSideClient()){
            this.itemName = CraftingTableIVAbstractionLayer.instance.mod.getFullItemName(recipe.getRecipeOutput().copy());
        }
    }

    private final IRecipe recipe;
    private final Object[] input;
    private final ItemStack outPut;
    private final String outputItemName;
    private final String identifier;
    private String itemName;
    private final IRecipeHandler recipeHandler;

    public String itemIdentifierClientName(){
        return itemName;
    }

    public Object[] getInput() {
        return input;
    }

    public ItemStack getRecipeOutput() {
        return outPut.copy();
    }

    public String getOutputItemName() {
        return outputItemName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public IRecipe getRecipe() {
        return recipe;
    }

    public IRecipeHandler getRecipeHandler() {
        return recipeHandler;
    }
}
