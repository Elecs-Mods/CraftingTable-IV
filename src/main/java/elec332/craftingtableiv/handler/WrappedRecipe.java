package elec332.craftingtableiv.handler;

import elec332.core.minetweaker.MineTweakerHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Created by Elec332 on 21-6-2015.
 */
public class WrappedRecipe {

    public WrappedRecipe(ShapelessRecipes shapelessRecipe){
        this(shapelessRecipe.recipeItems.toArray(), shapelessRecipe);
    }

    public WrappedRecipe(ShapelessOreRecipe shapelessOreRecipe){
        this(shapelessOreRecipe.getInput().toArray(), shapelessOreRecipe);
    }

    public WrappedRecipe(ShapedRecipes shapedRecipe){
        this(shapedRecipe.recipeItems, shapedRecipe);
    }

    public WrappedRecipe(ShapedOreRecipe shapedOreRecipe){
        this(shapedOreRecipe.getInput(), shapedOreRecipe);
    }

    private WrappedRecipe(Object[] input, IRecipe recipe){
        this.input = input;
        this.outPut = new RecipeStackComparator(recipe.getRecipeOutput().copy());
        this.outputItemName = MineTweakerHelper.getItemRegistryName(recipe.getRecipeOutput());
        this.recipe = recipe;
    }

    IRecipe recipe;
    Object[] input;
    RecipeStackComparator outPut;
    String outputItemName;

    public Object[] getInput() {
        return input;
    }

    public RecipeStackComparator getRecipeOutput() {
        return outPut;
    }

    public String getOutputItemName() {
        return outputItemName;
    }

    public IRecipe getRecipe() {
        return recipe;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WrappedRecipe && ((WrappedRecipe) obj).recipe.equals(recipe);
    }
}
