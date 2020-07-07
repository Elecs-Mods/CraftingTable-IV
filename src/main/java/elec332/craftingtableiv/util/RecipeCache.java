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

    public RecipeCache() {
        recipes = Lists.newArrayList();
        shownRecipes = Lists.newArrayList();
    }

    private final List<Entry> recipes, shownRecipes;

    public List<WrappedRecipe> getAllRecipes() {
        synchronized (recipes) {
            return recipes.stream().map(e -> e.recipe).collect(Collectors.toList());
        }
    }

    public int getShownSize() {
        return shownRecipes.size();
    }

    public void addRecipe(WrappedRecipe recipe, int amt, Predicate<WrappedRecipe> matcher) {
        Entry entry = new Entry(recipe, amt);
        synchronized (recipes) {
            if (matcher.test(recipe)) {
                shownRecipes.add(entry);
            }
            recipes.add(entry);
        }
    }

    public Entry getShownRecipe(int i) {
        return shownRecipes.get(i);
    }

    public ItemStack getRecipeOutput(int i) {
        synchronized (recipes) {
            Entry e = getShownRecipe(i);
            if (e == null) {
                return ItemStackHelper.NULL_STACK;
            }
            return e.recipe.getRecipeOutput();
        }
    }

    public void updateVisual(Predicate<WrappedRecipe> stackMatcher) {
        synchronized (recipes) {
            shownRecipes.clear();
            for (Entry wrappedRecipe : recipes) {
                if (stackMatcher.test(wrappedRecipe.recipe)) {
                    shownRecipes.add(wrappedRecipe);
                }
            }
        }
    }

    public void clearRecipes() {
        synchronized (recipes) {
            recipes.clear();
            shownRecipes.clear();
        }
    }

    public static class Entry {

        private Entry(WrappedRecipe recipe, int amount) {
            this.amount = amount;
            this.recipe = recipe;
        }

        public final int amount;
        public final WrappedRecipe recipe;

    }

}