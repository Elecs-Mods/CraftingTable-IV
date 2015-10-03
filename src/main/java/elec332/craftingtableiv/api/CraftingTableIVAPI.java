package elec332.craftingtableiv.api;

import net.minecraft.item.crafting.IRecipe;

/**
 * Created by Elec332 on 24-6-2015.
 */
public class CraftingTableIVAPI {

    protected static final String owner = "CraftingTableIV";
    protected static final String version = "1.0.2";
    protected static final String provides = "CraftingTableIV-API";

    private static final ICraftingTableIVAPI api;

    public static ICraftingTableIVAPI getAPI(){
        return api;
    }

    public static class NullAPI implements ICraftingTableIVAPI {

        private NullAPI(){
        }

        @Override
        public <C extends IRecipe> void registerHandler(Class<C> clazz, IRecipeHandler<C> handler) {
            System.out.println("[CraftingTable-API] CraftingTableIV mod not loaded, not registering handler for " + clazz.toString());
        }

        @Override
        public void registerDisabledRecipe(Class<? extends IRecipe> recipe) {
            System.out.println("[CraftingTableIV-API] CraftingTableIV mod not loaded, not disabling: " + recipe.toString());
        }

    }

    static {
        ICraftingTableIVAPI a;
        try {
            a = (ICraftingTableIVAPI) Class.forName("elec332.craftingtableiv.handler.Compat").getDeclaredMethod("getCompatHandler").invoke(null);
        } catch (Exception e){
            System.out.println("CraftingTableIV mod not found, registering handlers will not work!");
            a = new NullAPI();
        }
        api = a;
    }

}
