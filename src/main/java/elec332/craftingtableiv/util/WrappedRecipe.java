package elec332.craftingtableiv.util;

import elec332.core.util.InventoryHelper;
import elec332.core.util.ItemStackHelper;
import elec332.core.util.MineTweakerHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.api.IRecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 21-6-2015.
 */
public class WrappedRecipe {

    @Nullable
    @SuppressWarnings("all")
    public static WrappedRecipe of(IRecipe recipe, IRecipeHandler handler){
        if (!handler.canHandleRecipe(recipe)){
            throw new IllegalArgumentException("Invalid RecipeHandler.");
        }
        Object[] input = handler.getIngredients(recipe);
        if (input == null){
            System.out.println(new RuntimeException("Found null input for recipe: "+recipe));
        }
        boolean list = false, stack = false, one = true;
        try {
            for (Object obj : input) {
                if (obj instanceof ItemStack || obj == null){
                    if (stack){
                        one = false;
                    }
                    stack = true;
                    continue;
                } else if (obj instanceof List) {
                    if (((List) obj).isEmpty()) {
                        return null;
                    } else if (((List) obj).get(0) instanceof ItemStack) {
                        if (list){
                            one = false;
                        }
                        list = true;
                        continue;
                    }
                }
                System.out.println("ERROR: " + recipe.getRecipeOutput().toString() + " ... " + recipe.toString());
                throw new IllegalArgumentException();
            }
            return new WrappedRecipe(input, recipe, handler, list != stack, one);
        } catch (Exception e){
            e.printStackTrace();
            CraftingTableIV.logger.error(recipe.getRecipeOutput());
            CraftingTableIV.logger.error("A weird error occurred, please report this on the CraftingTable-IV github issue tracker, with the full log!");
            return null;
        }
    }

    private WrappedRecipe(Object[] input, IRecipe recipe, IRecipeHandler handler, boolean sameType, boolean one){
        this.outPut = recipe.getRecipeOutput().copy();
        this.outputItemName = MineTweakerHelper.getItemRegistryName(recipe.getRecipeOutput());
        this.recipe = recipe;
        this.identifier = CraftingTableIV.getItemIdentifier(recipe.getRecipeOutput());
        this.recipeHandler = handler;
        int width = handler.getRecipeWidth(recipe);
        this.shaped = width != -1;
        if (shaped){
            this.input = makeFit(input, width);
        } else {
            this.input = input;
        }
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()){
            this.itemName = CraftingTableIV.instance.getFullItemName(recipe.getRecipeOutput().copy());
        }
        this.one = one;
        if (!sameType){
            sameItems = false;
            return;
        }
        boolean list = false;
        for (Object o : input){
            if (o instanceof List){
                list = true;
                break;
            } else if (o instanceof ItemStack){
                break;
            }
        }
        if (list){
            List f = null;
            for (Object o : input){
                if (o != null){
                    if (f != null){
                        if (f.equals(o)){
                            continue;
                        }
                        sameItems = false;
                        return;
                    } else {
                        f = (List) o;
                    }
                }
            }
            sameItems = true;
        } else {
            ItemStack f = null;
            for (Object o : input){
                if (o != null){
                    ItemStack s = (ItemStack) o;
                    if (f != null){
                        if (InventoryHelper.areEqualNoSizeNoNBT(f, s)){
                            continue;
                        }
                        sameItems = false;
                        return;
                    } else {
                        f = s;
                    }
                }
            }
            sameItems = true;
        }
    }

    private final IRecipe recipe;
    private final Object[] input;
    private final ItemStack outPut;
    private final String outputItemName;
    private final String identifier;
    private final boolean shaped, sameItems, one;
    @SideOnly(Side.CLIENT)
    private String itemName;
    private final IRecipeHandler recipeHandler;

    @SideOnly(Side.CLIENT)
    public String itemIdentifierClientName(){
        return itemName;
    }

    public Object[] getInput() {
        return input;
    }

    public boolean isShaped(){
        return this.shaped;
    }

    public ItemStack getRecipeOutput() {
        return outPut.copy();
    }

    public ItemStack getRecipeOutput(int size) {
        ItemStack ret = outPut.copy();
        ret.stackSize = size;
        return ret;
    }

    public int getOutputSize() {
        return outPut.stackSize;
    }

    public boolean sameItems() {
        return this.sameItems;
    }

    public boolean oneItem() {
        return this.one;
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

    private static Object[] makeFit(Object[] ingredients, int width){
        Object[] ret = new Object[9];
        if (width == 1){
            for (int i = 0; i < 9; i++) {
                Object stack = ItemStackHelper.NULL_STACK;
                if (i == 0 || i == 3 || i == 6){
                    int j = i / 3;
                    stack = ingredients.length <= j ? ItemStackHelper.NULL_STACK : ingredients[j];
                }
                ret[i] = stack;
            }
        } else if (width == 2){
            int counter = 0;
            for (int i = 0; i < 9; i++) {
                Object stack = ItemStackHelper.NULL_STACK;
                if (!(i == 2 || i == 5 || i == 8)){
                    stack = counter >= ingredients.length ? ItemStackHelper.NULL_STACK : ingredients[counter];
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
