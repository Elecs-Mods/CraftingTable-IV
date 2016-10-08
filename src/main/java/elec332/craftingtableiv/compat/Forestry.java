package elec332.craftingtableiv.compat;

import elec332.core.module.ElecModule;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.api.AbstractRecipeHandler;
import elec332.craftingtableiv.api.CraftingTableIVAPI;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.lepidopterology.recipes.MatingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 4-10-2015.
 */
@ElecModule(owner = CraftingTableIV.ModID, name = "ForestryCompat", modDependencies = "forestry@[5.2.9.241,)")
public class Forestry  {

    @ElecModule.EventHandler
    public void init(FMLInitializationEvent event) {
        CraftingTableIVAPI.getAPI().registerHandler(new AbstractRecipeHandler() {

            @Override
            public boolean canHandleRecipe(IRecipe recipe) {
                return recipe instanceof ShapedRecipeCustom;
            }

            @Nonnull
            @Override
            public Object[] getIngredients(IRecipe recipe) {
                return ((ShapedRecipeCustom) recipe).getIngredients();
            }

        });
        CraftingTableIVAPI.getAPI().registerDisabledRecipe(MatingRecipe.class);
    }

}
