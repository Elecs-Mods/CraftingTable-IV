package elec332.craftingtableiv.compat;

import com.google.common.base.Preconditions;
import elec332.core.api.module.ElecModule;
import elec332.core.util.FMLUtil;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.api.CraftingTableIVAPI;
import elec332.craftingtableiv.api.IRecipeHandler;
import ic2.core.recipe.AdvRecipe;
import ic2.core.recipe.AdvShapelessRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by Elec332 on 4-10-2015.
 */
@ElecModule(owner = CraftingTableIV.ModID, name = "IC2Compat", modDependencies = "ic2")
public class IC2 {

    @ElecModule.EventHandler
    public void init(FMLInitializationEvent event) {
        if (Preconditions.checkNotNull(FMLUtil.getModContainer(ic2.core.IC2.getInstance())).getName().toLowerCase().contains("classic")) {
            CraftingTableIV.logger.info("IC2 Classic detected, not registering IC2 recipe handlers.");
        } else {
            registerIC2RecipeHandlers();
        }
    }

    private void registerIC2RecipeHandlers() {
        CraftingTableIV.logger.info("Registering IC2 recipe handlers...");
        CraftingTableIVAPI.getAPI().registerHandler(new IRecipeHandler() {

            @Override
            public boolean canHandleRecipe(IRecipe recipe) {
                if (recipe.isDynamic() && recipe.getIngredients().isEmpty()){
                    return false;
                }
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
                if (recipe.isDynamic() && recipe.getIngredients().isEmpty()){
                    return false;
                }
                return recipe instanceof AdvShapelessRecipe && ((AdvShapelessRecipe) recipe).canShow();
            }

        });
    }

}
