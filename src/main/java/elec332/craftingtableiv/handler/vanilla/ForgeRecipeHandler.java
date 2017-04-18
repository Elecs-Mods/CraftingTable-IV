package elec332.craftingtableiv.handler.vanilla;

import elec332.core.util.recipes.RecipeHelper;
import elec332.craftingtableiv.api.IRecipeHandler;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

/**
 * Created by Elec332 on 7-1-2016.
 */
public class ForgeRecipeHandler implements IRecipeHandler {

    @Override
    public boolean canHandleRecipe(IRecipe recipe) {
        return recipe instanceof ShapedOreRecipe || recipe instanceof ShapelessOreRecipe;
    }

    @Nonnull
    @Override
    public Object[] getIngredients(IRecipe recipe) {
        if (recipe instanceof ShapelessOreRecipe){
            return RecipeHelper.getRecipeInput((ShapelessOreRecipe) recipe).toArray();
        }
        return ((ShapedOreRecipe)recipe).getInput();
    }

    @Override
    public int getRecipeWidth(IRecipe recipe) {
        return recipe instanceof ShapelessOreRecipe ? -1 : getWidth((ShapedOreRecipe) recipe);
    }

    private static int getWidth(ShapedOreRecipe recipe){
        try {
            return f.getInt(recipe);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static final Field f;

    static {
        try {
            f = ShapedOreRecipe.class.getDeclaredField("width");
            f.setAccessible(true);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
