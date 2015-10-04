package elec332.craftingtableiv.compat.handlers;

import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.api.AbstractRecipeHandler;
import elec332.craftingtableiv.compat.AbstractCompatModule;
import ic2.core.AdvRecipe;
import ic2.core.AdvShapelessRecipe;

import java.util.List;

/**
 * Created by Elec332 on 4-10-2015.
 */
public class IC2 extends AbstractCompatModule {

    @Override
    public String getName() {
        return "IC2";
    }

    private boolean normal;
    private boolean classic;

    @Override
    public void init() {
        identifyTypes();
        if (normal){
            registerHandler(AdvRecipe.class, new AbstractRecipeHandler<AdvRecipe>() {
                @Override
                public Object[] getIngredients(AdvRecipe recipe) {
                    Object[] items = new Object[9];
                    int ret = 0;
                    for (int j = 0; ret < 9; ++ret) {
                        if ((recipe.masks[0] & 1 << 8 - ret) != 0) {
                            List inputs = AdvRecipe.expand(recipe.input[j]);
                            if (inputs.isEmpty()) {
                                return null;
                            }

                            items[ret] = inputs;
                            ++j;
                        }
                    }
                    return items;
                }
            });
            registerHandler(AdvShapelessRecipe.class, new AbstractRecipeHandler<AdvShapelessRecipe>() {
                @Override
                public Object[] getIngredients(AdvShapelessRecipe recipe) {
                    List[] items = AdvRecipe.expandArray(recipe.input);
                    for (List item : items) {
                        if (item != null && item.isEmpty()) {
                            return null;
                        }
                    }
                    return items;
                }
            });
        } else if (classic){
            registerHandler(AdvRecipe.class, new AbstractRecipeHandler<AdvRecipe>() {
                @Override
                public Object[] getIngredients(AdvRecipe recipe) {
                    return recipe.input;
                }
            });
            registerHandler(AdvShapelessRecipe.class, new AbstractRecipeHandler<AdvShapelessRecipe>() {
                @Override
                public Object[] getIngredients(AdvShapelessRecipe recipe) {
                    return recipe.input;
                }
            });
        }
    }

    private void identifyTypes(){
        try{
            Class.forName("ic2.api.info.IC2Classic");
            classic = true;
        }catch(ClassNotFoundException e1){
            try{
                Class.forName("ic2classic.core.IC2");
                CraftingTableIV.logger.info("There is no compat for Immibis-IC2Classic yet, not loading compat.");
            }catch(ClassNotFoundException e2){
                normal = true;
            }
        }
    }

}
