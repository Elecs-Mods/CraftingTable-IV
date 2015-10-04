package elec332.craftingtableiv.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.craftingtableiv.api.ICraftingTableIVAPI;
import elec332.craftingtableiv.api.IRecipeHandler;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 24-6-2015.
 */
public class RecipeHandler implements ICraftingTableIVAPI {

    private static final RecipeHandler instance = new RecipeHandler();

    private RecipeHandler() {
        registry = Maps.newHashMap();
        disabledRecipes = Lists.newArrayList();
    }

    public static RecipeHandler getCompatHandler() {
        return instance;
    }

    private Map<Class<? extends IRecipe>, IRecipeHandler<? extends IRecipe>> registry;
    private List<Class<? extends IRecipe>> disabledRecipes;

    public <C extends IRecipe> void registerHandler(Class<C> clazz, IRecipeHandler<C> handler){
        if (hasHandler(clazz))
            throw new IllegalArgumentException("handler for class "+clazz.toString()+" has already been registered!");
        registry.put(clazz, handler);
    }

    public boolean hasHandler(Class clazz){
        return registry.keySet().contains(clazz);
    }

    public void registerDisabledRecipe(Class<? extends IRecipe> recipe){
        if (!disabledRecipes.contains(recipe))
            disabledRecipes.add(recipe);
    }

    public boolean isDisabled(IRecipe recipe){
        return disabledRecipes.contains(recipe.getClass());
    }

    @SuppressWarnings("unchecked")
    public <R extends IRecipe> IRecipeHandler<R> getHandler(R recipe){
        return (IRecipeHandler<R>) registry.get(recipe.getClass());
    }

}
