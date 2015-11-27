package elec332.craftingtableiv.handler;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import elec332.core.util.MineTweakerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Elec332 on 21-6-2015.
 */
public class WrappedRecipe {

    public WrappedRecipe(ShapelessRecipes shapelessRecipe){
        this(shapelessRecipe.recipeItems.toArray(), shapelessRecipe);
    }

    public WrappedRecipe(ShapelessOreRecipe shapelessOreRecipe){
        this(shapelessOreRecipe.getInput().toArray(), shapelessOreRecipe);
    }

    public WrappedRecipe(ShapedRecipes shapedRecipe){
        this(shapedRecipe.recipeItems, shapedRecipe);
    }

    public WrappedRecipe(ShapedOreRecipe shapedOreRecipe){
        this(shapedOreRecipe.getInput(), shapedOreRecipe);
    }

    public WrappedRecipe(IRecipe recipe, Object[] input){
        this(input, recipe, true);
    }

    private WrappedRecipe(Object[] input, IRecipe recipe){
        this(input, recipe, false);
    }

    private WrappedRecipe(Object[] input, IRecipe recipe, boolean b){
        if (b){
            for (Object obj : input){
                if (obj instanceof ItemStack || obj == null)
                    continue;
                if (obj instanceof List){
                    if (!((List) obj).isEmpty() && ((List) obj).get(0) instanceof ItemStack)
                        continue;
                }
                System.out.println("ERROR: "+recipe.getRecipeOutput().toString()+" ... "+recipe.toString());
            }
        }
        this.input = input;
        this.outPut = new RecipeStackComparator(recipe.getRecipeOutput().copy());
        this.outputItemName = MineTweakerHelper.getItemRegistryName(recipe.getRecipeOutput());
        this.recipe = recipe;
        this.identifier = MineTweakerHelper.getItemRegistryName(recipe.getRecipeOutput().copy()).replace(":", " ").split(" ")[0];
        this.hash = new Random().nextInt(999999);
        this.key = UUID.randomUUID();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            registerItemName();
        }
    }

    @SideOnly(Side.CLIENT)
    private void registerItemName(){
        StringBuilder stringBuilder = new StringBuilder();
        List tooltip = recipe.getRecipeOutput().getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        boolean appendH = false;
        for (Object o : tooltip){
            stringBuilder.append(o);
            if (appendH){
                stringBuilder.append("#");
            } else appendH = true;
        }
        this.itemName = stringBuilder.toString().toLowerCase();
    }

    final int hash;
    final UUID key;
    final IRecipe recipe;
    final Object[] input;
    final RecipeStackComparator outPut;
    final String outputItemName;
    final String identifier;
    private String itemName;

    @SideOnly(Side.CLIENT)
    public String itemIdentifierClientName(){
        return itemName;
    }

    public Object[] getInput() {
        return input;
    }

    public RecipeStackComparator getRecipeOutput() {
        return outPut;
    }

    public String getOutputItemName() {
        return outputItemName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public IRecipe getRecipe() {
        return recipe;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    //TODO: keep at UUID or switch back to recipe.equals()?
    @Override
    public boolean equals(Object obj) {
        return obj instanceof WrappedRecipe && ((WrappedRecipe) obj).key.equals(key);//((WrappedRecipe) obj).recipe.equals(recipe);
    }
}
