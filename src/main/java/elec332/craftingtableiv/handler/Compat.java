package elec332.craftingtableiv.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.Loader;
import elec332.craftingtableiv.api.AbstractRecipeHandler;
import elec332.craftingtableiv.api.ICraftingTableIVAPI;
import elec332.craftingtableiv.api.IRecipeHandler;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.lepidopterology.MatingRecipe;
import ic2.core.AdvRecipe;
import ic2.core.AdvShapelessRecipe;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 24-6-2015.
 */
public class Compat implements ICraftingTableIVAPI {

    private static final Compat instance = new Compat();

    private Compat() {
        registry = Maps.newHashMap();
        disabledRecipes = Lists.newArrayList();
    }

    public static Compat getCompatHandler() {
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

    static {
        if (Loader.isModLoaded("IC2")) {
            instance.registerHandler(AdvRecipe.class, new AbstractRecipeHandler<AdvRecipe>() {
                @Override
                public Object[] getIngredients(AdvRecipe recipe) {
                    Object[] items = new Object[9];
                    int ret = 0;
                    for (int j = 0; ret < 9; ++ret) {
                        if ((recipe.masks[0] & 1 << 8 - ret) != 0) {
                            List inputs = AdvRecipe.expand(recipe.input[j]);
                            if (inputs.isEmpty()) {
                                return null;
                            }

                            items[ret] = inputs;
                            ++j;
                        }
                    }
                    return items;
                }
            });
            instance.registerHandler(AdvShapelessRecipe.class, new AbstractRecipeHandler<AdvShapelessRecipe>() {
                @Override
                public Object[] getIngredients(AdvShapelessRecipe recipe) {
                    List[] items = AdvRecipe.expandArray(recipe.input);
                    for (List item : items) {
                        if (item != null && item.isEmpty()) {
                            return null;
                        }
                    }
                    return items;
                }
            });
        }
        if (Loader.isModLoaded("Forestry")) {
            instance.registerHandler(ShapedRecipeCustom.class, new AbstractRecipeHandler<ShapedRecipeCustom>() {
                @Override
                public Object[] getIngredients(ShapedRecipeCustom recipe) {
                    return recipe.getIngredients();
                }
            });
            instance.registerDisabledRecipe(MatingRecipe.class);
        }
    }

}
