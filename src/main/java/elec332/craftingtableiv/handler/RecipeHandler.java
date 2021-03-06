package elec332.craftingtableiv.handler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.craftingtableiv.api.ICraftingTableIVAPI;
import elec332.craftingtableiv.api.IRecipeHandler;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 24-6-2015.
 */
public class RecipeHandler implements ICraftingTableIVAPI {

    private static final RecipeHandler instance = new RecipeHandler();

    private RecipeHandler() {
        registry = Sets.newHashSet();
        disabledRecipes = Lists.newArrayList();
    }

    public static RecipeHandler getCompatHandler() {
        return instance;
    }

    private Set<IRecipeHandler> registry;
    private List<Class<? extends IRecipe>> disabledRecipes;
    private boolean closed;

    @Override
    public void registerHandler(IRecipeHandler handler) {
        checkClosed();
        if (handler == null) {
            return;
        }
        registry.add(handler);
    }

    @Override
    public void registerDisabledRecipe(Class<? extends IRecipe> recipe) {
        checkClosed();
        if (!disabledRecipes.contains(recipe)) {
            disabledRecipes.add(recipe);
        }
    }

    @Override
    public Set<IRecipeHandler> getRegistry() {
        if (!closed) {
            throw new IllegalAccessError("Cannot provide registry before it's been closed!");
        }
        return registry;
    }

    @Override
    public boolean isRecipeDisabled(IRecipe recipe) {
        return isRecipeDisabled(recipe.getClass());
    }

    @Override
    public boolean isRecipeDisabled(Class<? extends IRecipe> recipeClass) {
        return disabledRecipes.contains(recipeClass);
    }

    public void closeRegistry() {
        checkClosed();
        registry = ImmutableSet.copyOf(registry);
        disabledRecipes = ImmutableList.copyOf(disabledRecipes);
        this.closed = true;
    }

    private void checkClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Registry has already been closed!");
        }
    }

}
