package elec332.craftingtableiv.handler;

import elec332.core.minetweaker.MineTweakerHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.List;
import java.util.Random;
import java.util.UUID;

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

    public WrappedRecipe(IRecipe recipe, Object[] input){
        this(input, recipe, true);
    }

    private WrappedRecipe(Object[] input, IRecipe recipe){
        this(input, recipe, false);
    }

    private WrappedRecipe(Object[] input, IRecipe recipe, boolean b){
        if (b){
            for (Object obj : input){
                if (obj instanceof ItemStack || obj == null)
                    continue;
                if (obj instanceof List){
                    if (!((List) obj).isEmpty() && ((List) obj).get(0) instanceof ItemStack)
                        continue;
                }
                System.out.println("ERROR: "+recipe.getRecipeOutput().toString()+" ... "+recipe.toString());
            }
        }
        this.input = input;
        this.outPut = new RecipeStackComparator(recipe.getRecipeOutput().copy());
        this.outputItemName = MineTweakerHelper.getItemRegistryName(recipe.getRecipeOutput());
        this.recipe = recipe;
        this.identifier = MineTweakerHelper.getItemRegistryName(recipe.getRecipeOutput().copy()).replace(":", " ").split(" ")[0];
        this.hash = new Random().nextInt(999999);
        this.key = UUID.randomUUID();
    }

    final int hash;
    final UUID key;
    final IRecipe recipe;
    final Object[] input;
    final RecipeStackComparator outPut;
    final String outputItemName;
    final String identifier;

    public Object[] getInput() {
        return input;
    }

    public RecipeStackComparator getRecipeOutput() {
        return outPut;
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

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WrappedRecipe && ((WrappedRecipe) obj).key.equals(key);//((WrappedRecipe) obj).recipe.equals(recipe);
    }
}
