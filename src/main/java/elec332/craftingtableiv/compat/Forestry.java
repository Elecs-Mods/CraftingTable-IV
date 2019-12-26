package elec332.craftingtableiv.compat;

import elec332.core.api.module.ElecModule;
import elec332.craftingtableiv.CraftingTableIV;

/**
 * Created by Elec332 on 4-10-2015.
 */
@ElecModule(owner = CraftingTableIV.MODID, name = "ForestryCompat", modDependencies = "forestry@[5.2.9.241,)")
public class Forestry {

    /*
    @ElecModule.EventHandler
    public void init(FMLCommonSetupEvent event) {
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
    }*/

}
