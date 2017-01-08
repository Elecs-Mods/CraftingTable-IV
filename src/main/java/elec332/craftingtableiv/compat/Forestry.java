package elec332.craftingtableiv.compat;

import elec332.core.api.module.ElecModule;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.api.CraftingTableIVAPI;
import elec332.craftingtableiv.api.IRecipeHandler;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.lepidopterology.recipes.MatingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Elec332 on 4-10-2015.
 */
@ElecModule(owner = CraftingTableIV.ModID, name = "ForestryCompat", modDependencies = "forestry@[5.2.9.241,)")
public class Forestry  {

    @ElecModule.EventHandler
    public void init(FMLInitializationEvent event) {
        CraftingTableIVAPI.getAPI().registerHandler(new IRecipeHandler() {

            @Override
            public boolean canHandleRecipe(IRecipe recipe) {
                return recipe instanceof ShapedRecipeCustom;
            }

            @Nonnull
            @Override
            public Object[] getIngredients(IRecipe recipe) {
                try {
                    return ((List) ingredients.invoke(recipe)).toArray();
                } catch (Exception e){
                    throw new RuntimeException();
                }
            }

            @Override
            public int getRecipeWidth(IRecipe recipe) {
                return ((ShapedRecipeCustom) recipe).getWidth();
            }

            @Override
            public boolean logHandlerErrors() {
                return false;
            }

        });
        CraftingTableIVAPI.getAPI().registerDisabledRecipe(MatingRecipe.class);
    }

    private static final Method ingredients;

    static {
        try {
            ingredients = ShapedRecipeCustom.class.getDeclaredMethod("getIngredients");
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
