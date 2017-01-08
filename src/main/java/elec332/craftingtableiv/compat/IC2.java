package elec332.craftingtableiv.compat;

import com.google.common.collect.Lists;
import elec332.core.api.module.ElecModule;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.api.CraftingTableIVAPI;
import elec332.craftingtableiv.api.IRecipeHandler;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.IRecipeInput;
import ic2.core.recipe.AdvRecipe;
import ic2.core.recipe.AdvShapelessRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 4-10-2015.
 */
@ElecModule(owner = CraftingTableIV.ModID, name = "IC2Compat", modDependencies = "IC2")
public class IC2  {

    private boolean normal;
    private boolean classic;

    @ElecModule.EventHandler
    public void init(FMLInitializationEvent event) {
        identifyTypes();
        if (normal){
            CraftingTableIVAPI.getAPI().registerHandler(new IRecipeHandler() {

                @Override
                public boolean canHandleRecipe(IRecipe recipe) {
                    return recipe instanceof AdvRecipe;
                }

                @Override
                @Nonnull
                @SuppressWarnings("all")
                public Object[] getIngredients(IRecipe recipe_) {
                    AdvRecipe recipe = (AdvRecipe) recipe_;
                    if (!isRecipeValid(recipe)){
                        return null;
                    }
                    int mask = recipe.masks[0];
                    int itemIndex = 0;
                    List<IRecipeInput> ret = Lists.newArrayList();

                    for(int i = 0; i < 9; ++i) {
                        if(i % 3 < recipe.inputWidth && i / 3 < recipe.inputHeight) {
                            if((mask >>> 8 - i & 1) != 0) {
                                ret.add(recipe.input[itemIndex++]);
                            } else {
                                ret.add(null);
                            }
                        }
                    }
                    List<List<ItemStack>> list = replaceRecipeInputs(ret);
                    return list == null ? null : list.toArray();
                }

                @Override
                public int getRecipeWidth(IRecipe recipe) {
                    return ((AdvRecipe) recipe).inputWidth;
                }

            });
            CraftingTableIVAPI.getAPI().registerHandler(new IRecipeHandler() {

                @Override
                public boolean canHandleRecipe(IRecipe recipe) {
                    return recipe instanceof AdvShapelessRecipe;
                }

                @Override
                @Nonnull
                @SuppressWarnings("all")
                public Object[] getIngredients(IRecipe recipe_) {
                    AdvShapelessRecipe recipe = (AdvShapelessRecipe) recipe_;
                    if (!isRecipeValid(recipe)){
                        return null;
                    }
                    List<List<ItemStack>> ret = Lists.newArrayList();
                    IRecipeInput[] var2 = recipe.input;
                    for (IRecipeInput input : var2) {
                        ret.add(input.getInputs());
                    }
                    return ret.toArray();
                }

            });
        } else if (classic){

            CraftingTableIVAPI.getAPI().registerHandler(new IRecipeHandler() {

                @Override
                public boolean canHandleRecipe(IRecipe recipe) {
                    return recipe instanceof AdvRecipe;
                }

                @Override
                @Nonnull
                public Object[] getIngredients(IRecipe recipe) {
                    return ((AdvRecipe) recipe).input;
                }

                @Override
                public int getRecipeWidth(IRecipe recipe) {
                    return ((AdvRecipe) recipe).inputWidth;
                }
            });
            CraftingTableIVAPI.getAPI().registerHandler(new IRecipeHandler() {

                @Override
                public boolean canHandleRecipe(IRecipe recipe) {
                    return recipe instanceof AdvShapelessRecipe;
                }

                @Override
                @Nonnull
                public Object[] getIngredients(IRecipe recipe) {
                    return ((AdvShapelessRecipe) recipe).input;
                }

            });
        }
    }

    private boolean isRecipeValid(@Nonnull AdvRecipe recipe) {
        return !recipe.hidden && !hidden(recipe.input);
    }

    private boolean isRecipeValid(@Nonnull AdvShapelessRecipe recipe) {
        return !recipe.hidden && !hidden(recipe.input);
    }

    private boolean hidden(IRecipeInput[] inputs){
        for (IRecipeInput input : inputs) {
            if (input.getInputs().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static List<List<ItemStack>> replaceRecipeInputs(List<IRecipeInput> list) {
       List<List<ItemStack>> out = Lists.newArrayList();

        for (IRecipeInput recipe : list) {
            if (recipe == null) {
                out.add(null);
            } else {
                List<ItemStack> replace = recipe.getInputs();
                for (int i = 0; i < replace.size(); ++i) {
                    ItemStack stack = replace.get(i);
                    if (stack != null && stack.getItem() instanceof IElectricItem) {
                        return null;
                    }
                }
                out.add(replace);
            }
        }
        return out;
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
