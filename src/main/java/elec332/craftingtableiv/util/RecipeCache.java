package elec332.craftingtableiv.util;

import com.google.common.collect.Lists;
import elec332.core.util.ItemStackHelper;
import elec332.craftingtableiv.inventory.WindowCraftingTableIV;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class RecipeCache {

    private List<Entry> recipes, shownRecipes;

    public RecipeCache() {
        recipes = Lists.newArrayList();
        shownRecipes = Lists.newArrayList();
    }

    public List<WrappedRecipe> getAllRecipes(){
        return recipes.stream().map(e -> e.recipe).collect(Collectors.toList());
    }

    public int getShownSize() {
        return shownRecipes.size();
    }

    public void addRecipe(WrappedRecipe recipe, int amt, Predicate<WrappedRecipe> matcher){
        Entry entry = new Entry(recipe, amt);
        if (matcher.test(recipe)){
            shownRecipes.add(entry);
        }
        recipes.add(entry);
    }

    public Entry getShownRecipe(int i) {
        return shownRecipes.get(i);
    }

    public ItemStack getRecipeOutput(int i) {
        Entry e = getShownRecipe(i);
        if (e == null){
            return ItemStackHelper.NULL_STACK;
        }
        return e.recipe.getRecipeOutput();
    }

    public void updateVisual(Predicate<WrappedRecipe> stackMatcher){
        shownRecipes.clear();
        for (Entry wrappedRecipe : recipes){
            if (stackMatcher.test(wrappedRecipe.recipe)){
                shownRecipes.add(wrappedRecipe);
            }
        }
    }

    public void clearRecipes() {
        recipes.clear();
        shownRecipes.clear();
    }

    public class Entry {

        Entry(WrappedRecipe recipe, int amount) {
            this.amount = amount;
            this.recipe = recipe;
        }

        public final int amount;
        public final WrappedRecipe recipe;

    }

}