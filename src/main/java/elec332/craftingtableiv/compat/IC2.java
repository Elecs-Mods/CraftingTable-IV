package elec332.craftingtableiv.compat;

import elec332.core.api.module.ElecModule;
import elec332.craftingtableiv.CraftingTableIV;

/**
 * Created by Elec332 on 4-10-2015.
 */
@ElecModule(owner = CraftingTableIV.MODID, name = "IC2Compat", modDependencies = "ic2")
public class IC2 {
/*
    @ElecModule.EventHandler
    public void init(FMLCommonSetupEvent event) {
        CraftingTableIV.logger.info("Registering IC2 recipe handlers...");
        CraftingTableIVAPI.getAPI().registerHandler(new IRecipeHandler() {

            @Override
            public boolean canHandleRecipe(IRecipe recipe) {
                return recipe instanceof AdvRecipe && ((AdvRecipe) recipe).canShow();
            }

            @Override
            public int getRecipeWidth(IRecipe recipe) {
                return ((AdvRecipe) recipe).inputWidth;
            }

        });
        CraftingTableIVAPI.getAPI().registerHandler(new IRecipeHandler() {

            @Override
            public boolean canHandleRecipe(IRecipe recipe) {
                return recipe instanceof AdvShapelessRecipe && ((AdvShapelessRecipe) recipe).canShow();
            }

        });
    }*/

}
