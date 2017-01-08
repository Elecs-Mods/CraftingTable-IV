package elec332.craftingtableiv.api;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

/**
 * Created by Elec332 on 24-6-2015.
 */
public class CraftingTableIVAPI {

    static final String owner = "CraftingTableIV";
    static final String version = "1.1.0";
    static final String provides = "CraftingTableIV-API";

    private static final ICraftingTableIVAPI api;

    public static ICraftingTableIVAPI getAPI(){
        return api;
    }

    public static class NullAPI implements ICraftingTableIVAPI {

        private NullAPI(){
        }

        @Override
        public void registerHandler(IRecipeHandler handler) throws IllegalStateException {
            System.out.println("[CraftingTable-API] CraftingTableIV mod not loaded, not registering handler " + handler.getClass().getCanonicalName());
        }

        @Override
        public void registerDisabledRecipe(Class<? extends IRecipe> recipe) {
            System.out.println("[CraftingTableIV-API] CraftingTableIV mod not loaded, not disabling: " + recipe.toString());
        }

        @Override
        public List<IRecipeHandler> getRegistry() throws IllegalAccessError {
            return ImmutableList.of();
        }

        @Override
        public boolean isRecipeDisabled(IRecipe recipe) {
            return false;
        }

        @Override
        public boolean isRecipeDisabled(Class<? extends IRecipe> recipeClass) {
            return false;
        }

    }

    static {
        ICraftingTableIVAPI a;
        try {
            a = (ICraftingTableIVAPI) Class.forName("elec332.craftingtableiv.handler.RecipeHandler").getDeclaredMethod("getCompatHandler").invoke(null);
        } catch (Exception e){
            System.out.println("CraftingTableIV mod not found, registering handlers will not work!");
            a = new NullAPI();
        }
        api = a;
    }

}
