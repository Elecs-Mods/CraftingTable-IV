package elec332.craftingtableiv.compat;

import elec332.core.api.module.ElecModule;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.api.CraftingTableIVAPI;
import elec332.craftingtableiv.api.IRecipeHandler;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.lepidopterology.recipes.MatingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Elec332 on 4-10-2015.
 */
@ElecModule(owner = CraftingTableIV.ModID, name = "ForestryCompat", modDependencies = "forestry@[5.2.9.241,)")
public class Forestry  {

    @ElecModule.EventHandler
    public void init(FMLInitializationEvent event) {
        CraftingTableIV.logger.info("Registering Forestry recipe handlers...");
        CraftingTableIVAPI.getAPI().registerHandler(new IRecipeHandler() {

            @Override
            public boolean canHandleRecipe(IRecipe recipe) {
                return recipe instanceof IDescriptiveRecipe;
            }

            @Override
            public int getRecipeWidth(IRecipe recipe) {
                return ((IDescriptiveRecipe) recipe).getWidth();
            }

            @Nonnull
            @Override
            public ItemStack[][] getIngredientStacks(IRecipe recipe) {
                return ((IDescriptiveRecipe) recipe).getRawIngredients().stream().map(itemStacks -> itemStacks.toArray(new ItemStack[0])).toArray(ItemStack[][]::new);
            }

        });
        CraftingTableIVAPI.getAPI().registerDisabledRecipe(MatingRecipe.class);
    }

}
