package elec332.craftingtableiv.api;

import net.minecraft.item.crafting.IRecipe;

/**
 * Created by Elec332 on 24-6-2015.
 */
public class CraftingTableIVAPI {

    protected static final String owner = "CraftingTableIV";
    protected static final String version = "1.0.0Alpha";
    protected static final String provides = "CraftingTableIV-API";

    public static <R extends IRecipe> void registerHandler(Class<R> clazz, IRecipeHandler<R> recipeHandler){
        try {
            ((ICTIVCompatRegistry)Class.forName("elec332.craftingtableiv.handler.Compat").getDeclaredMethod("getCompatHandler").invoke(null)).registerHandler(clazz, recipeHandler);
        } catch (IllegalArgumentException e){
            throw new RuntimeException(e);
        } catch (Exception e){
            System.out.println("[CTIV-API] CraftingTableIV mod not loaded, not registering handler for ");
        }
    }

}
