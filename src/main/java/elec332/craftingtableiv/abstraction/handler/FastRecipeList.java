package elec332.craftingtableiv.abstraction.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.util.ItemStackHelper;
import elec332.craftingtableiv.abstraction.CraftingTableIVAbstractionLayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 5-7-2015.
 */
public class FastRecipeList {

    public FastRecipeList(List<WrappedRecipe> recipes){
        this();
        for (WrappedRecipe recipe : recipes)
            addRecipe(recipe);
    }

    public FastRecipeList(){
        recipeHash = Maps.newHashMap();
    }

    Map<String, Map<ItemComparator, List<WrappedRecipe>>> recipeHash;

    public void addRecipe(WrappedRecipe recipe){
        ItemComparator itemComparator = new ItemComparator(recipe.getRecipe().getRecipeOutput().copy());
        String s = identifier(itemComparator.getStack());
        if (recipeHash.get(s) == null)
            recipeHash.put(s, Maps.<ItemComparator, List<WrappedRecipe>>newHashMap());
        if (recipeHash.get(s).get(itemComparator) == null)
            recipeHash.get(s).put(itemComparator, new ArrayList<WrappedRecipe>());
        recipeHash.get(s).get(itemComparator).add(recipe);
    }

    public List<WrappedRecipe> getCraftingRecipe(ItemStack stack) {
        if (!ItemStackHelper.isStackValid(stack)) {
            return Lists.newArrayList();
        }
        List<WrappedRecipe> possRet;
        try {
            possRet = recipeHash.get(identifier(stack)).get(new ItemComparator(stack));
        } catch (Exception e) {
            return Lists.newArrayList();
        }
        if (possRet == null || possRet.isEmpty()) {
            return Lists.newArrayList();
        }
        if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
            return possRet;
        }
        List<WrappedRecipe> ret = Lists.newArrayList();
        for (WrappedRecipe recipe : possRet) {
            ItemStack out = recipe.getRecipeOutput();
            if (out.getItemDamage() == stack.getItemDamage() || (!out.getHasSubtypes() && !stack.getHasSubtypes())) {
                ret.add(recipe);
            }
        }
        return ret;
    }

    public List<WrappedRecipe> getCraftingRecipe(List<ItemStack> stacks){
        List<WrappedRecipe> ret = Lists.newArrayList();
        for (ItemStack stack : stacks)
            ret.addAll(getCraftingRecipe(stack));
        return ret;
    }

    public boolean removeRecipe(WrappedRecipe recipe){
        return recipeHash.get(recipe.getIdentifier()).get(new ItemComparator(recipe.getRecipeOutput())).remove(recipe);
    }

    public void removeAllRecipes(List<WrappedRecipe> recipes){
        for (WrappedRecipe recipe : recipes)
            removeRecipe(recipe);
    }

    public FastRecipeList copyOf(){
        FastRecipeList ret = new FastRecipeList();
        for (Map.Entry<String, Map<ItemComparator, List<WrappedRecipe>>> entry : recipeHash.entrySet()){
            ret.recipeHash.put(entry.getKey(), Maps.<ItemComparator, List<WrappedRecipe>>newHashMap());
            for (Map.Entry<ItemComparator, List<WrappedRecipe>> comparatorListEntry : recipeHash.get(entry.getKey()).entrySet()){
                ret.recipeHash.get(entry.getKey()).put(new ItemComparator(comparatorListEntry.getKey().getStack().copy()), Lists.<WrappedRecipe>newArrayList());
                for (WrappedRecipe recipe : recipeHash.get(entry.getKey()).get(comparatorListEntry.getKey())){
                    ret.recipeHash.get(entry.getKey()).get(comparatorListEntry.getKey()).add(recipe);
                }
            }
        }
        return ret;
    }

    private static String identifier(ItemStack stack){
        return CraftingTableIVAbstractionLayer.instance.mod.getItemRegistryName(stack).replace(":", " ").split(" ")[0];
    }

}
