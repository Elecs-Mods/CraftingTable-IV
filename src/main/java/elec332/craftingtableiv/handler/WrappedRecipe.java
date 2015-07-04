package elec332.craftingtableiv.handler;

import elec332.core.minetweaker.MineTweakerHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.List;

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
        for (Object obj : input) {
            if (obj != null) {
                if (obj instanceof ItemStack){
                    if (isWood((ItemStack) obj)) {
                        hasWood = true;
                        break;
                    }
                }
                if (obj instanceof List){
                    if (!((List) obj).isEmpty() && ((List) obj).get(0) instanceof ItemStack && isWood((ItemStack) ((List) obj).get(0))) {
                        hasWood = true;
                        break;
                    }
                }
            }
        }
    }

    private boolean isWood(ItemStack stack){
        String s = MineTweakerHelper.getItemRegistryName(stack);
        String o = MineTweakerHelper.getItemRegistryName(stack);
        return o.contains("wood") || s.contains("wood") || o.contains("Wood") || s.contains("Wood");
    }

    IRecipe recipe;
    Object[] input;
    RecipeStackComparator outPut;
    String outputItemName;
    boolean hasWood;

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

    public boolean hasWood(){
        return hasWood;
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
