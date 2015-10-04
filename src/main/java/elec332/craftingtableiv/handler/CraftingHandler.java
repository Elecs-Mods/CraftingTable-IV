package elec332.craftingtableiv.handler;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.helper.OredictHelper;
import elec332.core.minetweaker.MineTweakerHelper;
import elec332.core.util.DoubleInventory;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.network.PacketCraft;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class CraftingHandler {

    public static ArrayList<ItemStack> validOutputs = Lists.newArrayList();
    public static ArrayList<WrappedRecipe> recipeList = Lists.newArrayList();
    public static ArrayList<StackComparator> syncedRecipeOutput = Lists.newArrayList();
    public static ArrayList<RecipeStackComparator> stackDataList = Lists.newArrayList();
    public static Map<String, Map<ItemComparator, List<WrappedRecipe>>> recipeHash = Maps.newHashMap();
    public static Map<String, Map<StackComparator, RecipeStackComparator>> rcMap = Maps.newHashMap();

    private static void addToRecipeHash(ItemStack stack, WrappedRecipe recipe){
        ItemComparator itemComparator = new ItemComparator(stack);
        String s = identifier(stack);
        if (recipeHash.get(s) == null)
            recipeHash.put(s, Maps.<ItemComparator, List<WrappedRecipe>>newHashMap());
        if (recipeHash.get(s).get(itemComparator) == null)
            recipeHash.get(s).put(itemComparator, new ArrayList<WrappedRecipe>());
        recipeHash.get(s).get(itemComparator).add(recipe);
    }

    private static void addToRCMap(StackComparator stackComparator, RecipeStackComparator recipeStackComparator){
        String s = identifier(stackComparator.getStack());
        if (rcMap.get(s) == null)
            rcMap.put(s, new HashMap<StackComparator, RecipeStackComparator>());
        rcMap.get(s).put(stackComparator, recipeStackComparator);
    }

    public static String identifier(ItemStack stack){
        return MineTweakerHelper.getItemRegistryName(stack).replace(":", " ").split(" ")[0];
    }

    @SuppressWarnings("unchecked")
    public static boolean canPlayerCraft(EntityPlayer player, TileEntityCraftingTableIV craftingTableIV, WrappedRecipe recipe, FastRecipeList canCraft, boolean executeCrafting){
        InventoryPlayer fakeInventoryPlayer = new InventoryPlayer(player);
        fakeInventoryPlayer.copyInventory(player.inventory);
        TileEntityCraftingTableIV fakeCraftingInventory = craftingTableIV.getCopy();
        boolean ret = canPlayerCraft(new DoubleInventory(fakeInventoryPlayer, fakeCraftingInventory), recipe, 0, canCraft, executeCrafting);
        if (executeCrafting && ret && isServer(player)){
            player.inventory.copyInventory(fakeInventoryPlayer);
            for (int i = 0; i < craftingTableIV.getSizeInventory(); i++) {
                craftingTableIV.setInventorySlotContents(i, fakeCraftingInventory.getStackInSlot(i));
            }
        }
        return ret;
    }

    @SuppressWarnings("all")
    public static boolean canPlayerCraft(DoubleInventory<InventoryPlayer, IInventory> inventory, WrappedRecipe recipe, int i, FastRecipeList check, boolean executeCrafting){
        //if (executeCrafting)
        //    System.out.println("Executing crafting for "+recipe.getOutputItemName());
        if (i > CraftingTableIV.recursionDepth)
            return false;

        if (inventory != null && recipe != null) {
            inputLoop:
            for (Object obj : recipe.getInput()) {
                if (obj == null)
                    continue;
                if (obj instanceof ItemStack) {
                    ItemStack itemStack = (ItemStack) obj;
                    int slotID = inventory.getFirstSlotWithItemStackNoNBT(itemStack);//CraftingHandler.getFirstInventorySlotWithItemStack(fakeInventoryPlayer, fakeCraftingInventory, itemStack);
                    if (slotID > -1) {
                        if (!handleStuff(inventory, itemStack))
                            return false;
                    } else if (i < CraftingTableIV.recursionDepth){
                        List<WrappedRecipe> valid = check.getCraftingRecipe(itemStack);
                        if (valid.isEmpty())
                            return false;
                        WrappedRecipe wrappedRecipe = canPlayerCraftAnyOf(inventory, check, i, executeCrafting, valid);
                        if (wrappedRecipe != null){
                            if (!handleStuff(inventory, wrappedRecipe.getRecipeOutput().getStack()))
                                return false;
                            continue inputLoop;
                        } else {
                            return false;
                        }
                    } else return false;
                } else if (obj instanceof List && !((List)obj).isEmpty()){
                    @SuppressWarnings("unchecked")
                    List<ItemStack> stacks = (List<ItemStack>) obj;
                    for (ItemStack itemStack : stacks) {
                        int p = inventory.getFirstSlotWithItemStackNoNBT(itemStack);//CraftingHandler.getFirstInventorySlotWithItemStack(fakeInventoryPlayer, fakeCraftingInventory, itemStack);
                        if (p >= 0) {
                            if (!handleStuff(inventory, itemStack))
                                return false;
                            continue inputLoop;
                        }
                    }
                    if (i < CraftingTableIV.recursionDepth){
                        List<WrappedRecipe> valid = check.getCraftingRecipe(stacks);
                        if (valid.isEmpty())
                            return false;
                        WrappedRecipe wrappedRecipe = canPlayerCraftAnyOf(inventory, check, i, executeCrafting, valid);
                        if (wrappedRecipe != null){
                            if (!handleStuff(inventory, wrappedRecipe.getRecipeOutput().getStack()))
                                return false;
                            continue inputLoop;
                        } else {
                            return false;
                        }
                    } else return false;
                }
            }
            if (executeCrafting && inventory.getFirstInventory().player.getEntityWorld().isRemote) {
                CraftingTableIV.networkHandler.getNetworkWrapper().sendToServer(new PacketCraft(recipe));
            }
            return inventory.addItemToInventory(recipe.getRecipeOutput().getStack().copy());
        } else return false;
    }

    private static WrappedRecipe canPlayerCraftAnyOf(DoubleInventory<InventoryPlayer, IInventory> inventory, FastRecipeList check, int i, boolean execute, List<WrappedRecipe> recipes){
        for (WrappedRecipe recipe : recipes){
            DoubleInventory<InventoryPlayer, IInventory> copy = DoubleInventory.fromInventory(new InventoryPlayer(inventory.getFirstInventory().player), new TileEntityCraftingTableIV(), inventory);
            if (canPlayerCraft(copy, recipe, i+1, check, execute)){
                inventory.copyFrom(copy);
                return recipe;
            }
        }
        return null;
    }

    private static boolean handleStuff(DoubleInventory inventory, ItemStack stack){
        int s = inventory.getFirstSlotWithItemStackNoNBT(stack.copy());
        if (s == -1)
            return false;
        if (stack.getItem().hasContainerItem(stack)){
            ItemStack itemStack = stack.getItem().getContainerItem(inventory.getStackInSlot(s));
            if (itemStack != null && itemStack.isItemStackDamageable() && itemStack.getItemDamage() > itemStack.getMaxDamage())
                itemStack = null;
            if (itemStack != null && !inventory.addItemToInventory(itemStack))
                return false;
        }
        return inventory.decrStackSize(s, 1) != null;
    }

    public static boolean isServer(EntityPlayer player){
        return !player.worldObj.isRemote;
    }


    //Validate all recipes, excluding all firework recipes, colouring recipes, ect.
    public static void InitRecipes() {
        validOutputs.clear();
        recipeList.clear();
        recipeHash.clear();
        stackDataList.clear();
        syncedRecipeOutput.clear();
        rcMap.clear();
        SortedMap<String, List<IRecipe>> namedList = Maps.newTreeMap();
        Map<String, List<IRecipe>> entries = Maps.newHashMap();
        for (Object object : CraftingManager.getInstance().getRecipeList()){
            if (object instanceof IRecipe){
                IRecipe recipe = (IRecipe) object;
                ItemStack output = recipe.getRecipeOutput();
                if (output != null && output.getItem() != null) {
                    if (Strings.isNullOrEmpty(MineTweakerHelper.getItemRegistryName(output))){
                        CraftingTableIV.instance.error("A mod has registered an invalid recipe, this is a severe error.");
                        continue;
                    }
                    if (recipe instanceof ShapelessOreRecipe && !isOreValid(((ShapelessOreRecipe)recipe).getInput()))
                        continue;
                    if (recipe instanceof ShapedOreRecipe && !isOreValid(Lists.newArrayList(((ShapedOreRecipe)recipe).getInput())))
                        continue;
                    output = output.copy();
                    if (CraftingTableIV.nuggetFilter && (MineTweakerHelper.getItemRegistryName(output).contains("nugget") || OredictHelper.getOreName(output).contains("nugget"))) // || recipeList.contains(recipe))
                        continue;
                    String[] s = MineTweakerHelper.getItemRegistryName(output).replace(":", " ").split(" ");
                    boolean b = false;
                    for (String s1 : CraftingTableIV.disabledMods) {
                        if (s1.equalsIgnoreCase(s[0])) {
                            b = true;
                        }
                    }
                    if (b)
                        continue;
                    if (s[0].contains("minecraft")) {
                        if (entries.get("minecraft") == null)
                            entries.put("minecraft", new ArrayList<IRecipe>());
                        entries.get("minecraft").add(recipe);
                    } else {
                        if (namedList.get(s[0]) == null)
                            namedList.put(s[0], new ArrayList<IRecipe>());
                        namedList.get(s[0]).add(recipe);
                    }
                }
            }
        }
        entries.putAll(namedList);
        for (List<IRecipe> recipes : entries.values()){
            for (IRecipe iRecipe : recipes){
                WrappedRecipe recipe = getWrappedRecipe(iRecipe);
                if (recipe != null) {
                    ItemStack output = recipe.getRecipeOutput().getStack();
                    validOutputs.add(output.copy());
                    recipeList.add(recipe);
                    syncedRecipeOutput.add(new StackComparator(output.copy()));
                    stackDataList.add(new RecipeStackComparator(output.copy()));
                    addToRCMap(new StackComparator(output.copy()), new RecipeStackComparator(output.copy()));
                    addToRecipeHash(output.copy(), recipe);
                }
            }
        }
    }

    private static boolean isOreValid(List list){
        if (!list.isEmpty()){
            for (Object o : list){
                if (o instanceof List){
                    if (((List) o).isEmpty())
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    public static List<WrappedRecipe> getCraftingRecipe(ItemStack stack) {
        if (stack == null || stack.getItem() == null)
            return Lists.newArrayList();
        List<WrappedRecipe> possRet;
        try {
            possRet = recipeHash.get(identifier(stack)).get(new ItemComparator(stack));
        } catch (Exception e) {
            return Lists.newArrayList();
        }
        if (possRet == null || possRet.isEmpty())
            return Lists.newArrayList();
        if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
            return possRet;
        List<WrappedRecipe> ret = Lists.newArrayList();
        for (WrappedRecipe recipe : possRet) {
            ItemStack out = recipe.getRecipeOutput().getStack();
            if (out.getItemDamage() == stack.getItemDamage() || (!out.getHasSubtypes() && !stack.getHasSubtypes()))
                ret.add(recipe);
        }
        return ret;
    }

    public static WrappedRecipe getWrappedRecipe(IRecipe irecipe) {
        try {
            if (irecipe == null)
                return null;
            else if (irecipe instanceof ShapelessRecipes) {
                return new WrappedRecipe((ShapelessRecipes)irecipe);
            } else if (irecipe instanceof ShapedRecipes) {
                return new WrappedRecipe((ShapedRecipes)irecipe);
            } else if (irecipe instanceof ShapedOreRecipe) {
                return new WrappedRecipe((ShapedOreRecipe) irecipe);
            } else if (irecipe instanceof ShapelessOreRecipe) {
                return new WrappedRecipe((ShapelessOreRecipe) irecipe);
            } else if (irecipe instanceof RecipesArmorDyes || irecipe instanceof RecipeFireworks || irecipe instanceof RecipeBookCloning || irecipe instanceof RecipesMapCloning) {
                return null;
            } else if (RecipeHandler.getCompatHandler().isDisabled(irecipe)) {
                return null;
            }else if (RecipeHandler.getCompatHandler().hasHandler(irecipe.getClass())){
                return RecipeHandler.getCompatHandler().getHandler(irecipe).getWrappedRecipe(irecipe);
            } else {
                if (irecipe.getRecipeOutput() != null)
                    CraftingTableIV.instance.error("ERROR FINDING HANDLER FOR RECIPE CLASS: " + irecipe.toString());
                else CraftingTableIV.instance.error("ERROR: THE OUTPUT FOR THIS RECIPE IS NULL! " + irecipe.toString());
                return null;
            }
        } catch (NullPointerException e) {
            /*if (ElecCore.proxy.isClient()) {
                ElecCore.tickHandler.registerCall(new IRunOnce() {
                    @Override
                    public void run() {
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("There was an error loading some recipes, the error will be printed in the game log.");
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("Please report this to Elec332 with the entire gamelog here: https://github.com/Elecs-Mods/CraftingTable-IV/issues");
                    }
                });
            }*/
            CraftingTableIV.instance.error("Something went wrong while trying to acquire recipe ingredients!");
            CraftingTableIV.instance.error(irecipe.toString()+" with output "+irecipe.getRecipeOutput());
            CraftingTableIV.instance.error(e);
            return null;
        }
    }

}
