package elec332.craftingtableiv.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.util.ItemStackHelper;
import elec332.craftingtableiv.CraftingTableIV;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 5-7-2015.
 */
public class FastRecipeList {

    public FastRecipeList(List<WrappedRecipe> recipes){
        this();
        for (WrappedRecipe recipe : recipes) {
            addRecipe(recipe);
        }
    }

    public FastRecipeList(){
        recipeHash = Maps.newHashMap();
    }

    private Map<String, Map<ItemComparator, List<WrappedRecipe>>> recipeHash;

    public void addRecipe(WrappedRecipe recipe){
        ItemComparator itemComparator = new ItemComparator(recipe.getRecipe().getRecipeOutput());
        String s = CraftingTableIV.getItemIdentifier(itemComparator.getStack());
        recipeHash.
                computeIfAbsent(s, k -> Maps.<ItemComparator, List<WrappedRecipe>>newHashMap()).
                computeIfAbsent(itemComparator, k -> Lists.newArrayList()).
                add(recipe);
    }

    public List<WrappedRecipe> getCraftingRecipe(ItemStack stack) {
        if (!ItemStackHelper.isStackValid(stack)) {
            return ImmutableList.of();
        }
        List<WrappedRecipe> possRet;
        try {
            possRet = recipeHash.get(CraftingTableIV.getItemIdentifier(stack)).get(new ItemComparator(stack));
        } catch (Exception e) {
            return ImmutableList.of();
        }
        if (possRet == null || possRet.isEmpty()) {
            return ImmutableList.of();
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
        for (WrappedRecipe recipe : recipes) {
            removeRecipe(recipe);
        }
    }

    public FastRecipeList copyOf(){
        FastRecipeList ret = new FastRecipeList();
        for (Map.Entry<String, Map<ItemComparator, List<WrappedRecipe>>> entry : recipeHash.entrySet()){
            ret.recipeHash.put(entry.getKey(), Maps.<ItemComparator, List<WrappedRecipe>>newHashMap());
            for (Map.Entry<ItemComparator, List<WrappedRecipe>> comparatorListEntry : recipeHash.get(entry.getKey()).entrySet()){
                ret.recipeHash.get(entry.getKey()).put(comparatorListEntry.getKey().copy(), Lists.<WrappedRecipe>newArrayList());
                for (WrappedRecipe recipe : recipeHash.get(entry.getKey()).get(comparatorListEntry.getKey())){
                    ret.recipeHash.get(entry.getKey()).get(comparatorListEntry.getKey()).add(recipe);
                }
            }
        }
        return ret;
    }

}
