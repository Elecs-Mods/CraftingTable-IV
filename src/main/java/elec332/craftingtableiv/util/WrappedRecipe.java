package elec332.craftingtableiv.util;

import com.google.common.collect.Lists;
import elec332.core.util.RegistryHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.api.IRecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.IntFunction;

/**
 * Created by Elec332 on 21-6-2015.
 */
public class WrappedRecipe {

    @Nullable
    @SuppressWarnings("all")
    public static WrappedRecipe of(IRecipe recipe, IRecipeHandler handler){
        if (!handler.canHandleRecipe(recipe)){
            throw new IllegalArgumentException("Invalid RecipeHandler");
        }
        List<Ingredient> input_ = handler.getIngredients(recipe);
        ItemStack[][] items = null;
        String errMsg = null;

        if (input_ == null){
            errMsg = "Found null input";
        } else if (input_.isEmpty()){
            errMsg = "Found empty input";
        }

        if (errMsg == null) {
            items = handler.getIngredientStacks(recipe);
            if (items == null){
                errMsg = "Found empty compiled Item list";
            } else if (items.length != input_.size()){
                errMsg = "Found mismatch between ingredient size and comiled Item list size";
            }
        }

        if (errMsg != null){
            throw new RuntimeException(errMsg + " for recipe: " + recipe.getClass().getCanonicalName() + " " + recipe);
        }

        Ingredient[] input = input_.toArray(new Ingredient[0]);
        int width = handler.getRecipeWidth(recipe);
        boolean shaped = width != -1;
        boolean one = input.length == 1;
        boolean same = true;
        ItemStack rfi = null;
        for (int i = 0; i < input.length; i++) {
            Ingredient ing = input[i];
            if (ing != Ingredient.EMPTY){
                if (rfi == null){
                    if (items[i].length == 0){ //Can happen with ore recipes that have no items registered to the specified ore
                        return null;
                    }
                    rfi = items[i][0];
                }
                if (!ing.apply(rfi)){
                    same = false;
                    break;
                }
            }
        }
        if (shaped){
            input = makeFit(input, width, Ingredient[]::new, Ingredient.EMPTY);
            ItemStack[] EMPTY = new ItemStack[0];
            items = makeFit(items, width, ItemStack[][]::new, EMPTY);
        }
        if (!one){
            rfi = null;
        }
        return new WrappedRecipe(input, items, shaped, same, one, rfi, recipe, handler);
    }

    private WrappedRecipe(Ingredient[] ing, ItemStack[][] input, boolean shaped, boolean sameItems, boolean oneItem, ItemStack oneI, IRecipe recipe, IRecipeHandler handler){
        this.outPut = recipe.getRecipeOutput().copy();
        this.outputItemName = RegistryHelper.getItemRegistry().getKey(recipe.getRecipeOutput().getItem()).toString();
        this.recipe = recipe;
        this.identifier = CraftingTableIV.getItemIdentifier(recipe.getRecipeOutput());
        this.recipeHandler = handler;
        this.shaped = shaped;
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()){
            this.itemName = CraftingTableIV.instance.getFullItemName(recipe.getRecipeOutput().copy());
        }
        this.ingredients = ing;
        this.items = input;
        this.sameItems = sameItems;
        this.oneItem = oneItem;
        this.oneI = oneI;
    }

    private final IRecipe recipe;
    private final Ingredient[] ingredients;
    private final ItemStack[][] items;
    private final ItemStack outPut, oneI;
    private final String outputItemName;
    private final String identifier;
    private final boolean shaped, sameItems, oneItem;
    @SideOnly(Side.CLIENT)
    private String itemName;
    private final IRecipeHandler recipeHandler;

    @SideOnly(Side.CLIENT)
    public String itemIdentifierClientName(){
        return itemName;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public ItemStack[][] getIngredientItems() {
        return items;
    }

    public boolean isShaped(){
        return this.shaped;
    }

    public boolean oneItem() {
        return oneItem;
    }

    @Nullable
    public ItemStack getOneItem() {
        return oneI;
    }

    public boolean sameItems() {
        return sameItems;
    }

    public ItemStack getRecipeOutput() {
        return outPut.copy();
    }

    public ItemStack getRecipeOutput(int size) {
        ItemStack ret = outPut.copy();
        ret.setCount(size);
        return ret;
    }

    public int getOutputSize() {
        return outPut.getCount();
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

    private static <O> O[] makeFit(O[] ingredients, int width, IntFunction<O[]> arrayGen, O empty){
        O[] ret = arrayGen.apply(9);
        if (width == 1){
            for (int i = 0; i < 9; i++) {
                O stack = empty;
                if (i == 0 || i == 3 || i == 6){
                    int j = i / 3;
                    stack = ingredients.length <= j ? empty : ingredients[j];
                }
                ret[i] = stack;
            }
        } else if (width == 2){
            int counter = 0;
            for (int i = 0; i < 9; i++) {
                O stack = empty;
                if (!(i == 2 || i == 5 || i == 8)){
                    stack = counter >= ingredients.length ? empty : ingredients[counter];
                    counter++;
                }
                ret[i] = stack;

            }
        } else {
            ret = ingredients;
        }
        return ret;
    }

}
