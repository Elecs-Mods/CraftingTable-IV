package elec332.craftingtableiv.handler;

import com.google.common.collect.Lists;
import elec332.core.helper.OredictHelper;
import elec332.core.main.ElecCore;
import elec332.core.minetweaker.MineTweakerHelper;
import elec332.core.player.InventoryHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.blocks.container.ContainerNull;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class CraftingHandler {
    //public static int MaxLevel = 20;
    public static ArrayList<ItemStack> validOutputs = new ArrayList<ItemStack>();
    public static ArrayList<IRecipe> recipeList = new ArrayList<IRecipe>();


    //public static Object[] canPlayerCraft(InventoryPlayer ThePlayer, ItemStack TheItem, IInventory Internal, IRecipe ForcedIndex)
    //{
        //return canPlayerCraft(ThePlayer, ((TECraftingTableIV)Internal).getCopy(), TheItem, 0, null, null, ForcedIndex);
    //}
    /*
     * Only time this is called with -1 is when we
     * are trying to make an item for a request recipe
     *
     * Otherwise ForcedIndex should always be a value!
     * If not dupe item will show on the table!
     */
    /*public static Object[] canPlayerCraft(InventoryPlayer inventoryPlayer, IInventory Internal, ItemStack TheItem, int Level, ItemStack Item1, ItemStack Item2, IRecipe recipe)
    {
        int SlotCount = 0;

        if (Level > MaxLevel)
            return new Object[] {false, inventoryPlayer, SlotCount, Internal};

        //Copys to prevent bugs
        InventoryPlayer ThePlayerBefore = new InventoryPlayer(inventoryPlayer.player);
        ThePlayerBefore.copyInventory(inventoryPlayer);
        TECraftingTableIV InternalBefore = ((TECraftingTableIV)Internal).getCopy();

        IRecipe recipeIndex = recipe;

        if (recipe == null) {
            recipeIndex = RecipeHelper.getCraftingRecipe(TheItem);
            if (recipeIndex == null)
                return new Object[] {false, inventoryPlayer, SlotCount, Internal};
        }

        boolean playerHasAllItems = false;

        while (recipeIndex != null)
        {
            boolean playerHasAllItemsForThis = true;
            ArrayList<ItemStack> recipeIngredients = new ArrayList<ItemStack>();
            for (ItemStack stack : getRecipeIngredients(RecipeHelper.getCraftingRecipe(TheItem), inventoryPlayer)){
                recipeIngredients.add(stack);
            }

            for (ItemStack itemStack : recipeIngredients)
            {
                if (itemStack == null)
                    continue;
                if (ItemHelper.areItemsEqual(itemStack, Item1))
                    return new Object[] {false, ThePlayerBefore, SlotCount, InternalBefore};
                if (ItemHelper.areItemsEqual(itemStack, Item2))
                    return new Object[] {false, ThePlayerBefore, SlotCount, InternalBefore};

                int SlotIndex = getFirstInventorySlotWithItemStack(inventoryPlayer, Internal, itemStack);
                if (SlotIndex > -1)
                {
                    decrStackSize(inventoryPlayer, Internal, SlotIndex, itemStack.stackSize); //inventoryPlayer.decrStackSize(SlotIndex, recipeIngredients.get(i).StackSize);
                } else {
                    Object[] Result = canPlayerCraft(inventoryPlayer, Internal, itemStack, Level+1, itemStack, TheItem, null);
                    inventoryPlayer = (InventoryPlayer) Result[1];
                    Internal = (IInventory) Result[3];
                    if ((Boolean)Result[0] != true)
                    {
                        playerHasAllItemsForThis = false;
                        break;
                    }
                    SlotIndex = getFirstInventorySlotWithItemStack(inventoryPlayer, Internal, itemStack);
                    if(SlotIndex != -1) {
                        decrStackSize(inventoryPlayer, Internal, SlotIndex, itemStack.stackSize); //inventoryPlayer.decrStackSize(SlotIndex, recipeIngredients.get(i).StackSize);
                    }

                }
            }

            if (playerHasAllItemsForThis)
            {
                playerHasAllItems = true;
                break;
            } else
            {
                //Reset the item to before trying this recipe
                inventoryPlayer.copyInventory(ThePlayerBefore);
                Internal = InternalBefore.getCopy();
            }
            if (recipe != null)
                break;
            recipeIndex = RecipeHelper.getCraftingRecipe(TheItem);
        }

        if (playerHasAllItems)
        {
            TheItem = recipe.getRecipeOutput(); //Fixes damage values and set the proper item stack size
            //Object[] iTemp = addItemStackPlayer(inventoryPlayer, Internal, TheItem);
            //if ((Boolean)iTemp[0] == false) //ThePlayer.addItemStackToInventory(TheItem.toItemStack());
            {
                return new Object[] {false, ThePlayerBefore, SlotCount, InternalBefore}; //Look into this effecting player in some recipes
            }
            //Internal = (IInventory) iTemp[1];
            //InventoryCrafting TempMatrix =generateCraftingMatrix(getRecipeIngredients(RecipeHelper.getCraftingRecipe(TheItem)));
            //TheItem.onCrafting(inventoryPlayer.player.worldObj, inventoryPlayer.player, 1);
               //ModLoader.takenFromCrafting(ThePlayer.player, TheItem.toItemStack(), TempMatrix);
               //ForgeHooks.onTakenFromCrafting(ThePlayer.player, TheItem.toItemStack(), TempMatrix);
            //handleCraftingMatrix(TempMatrix, inventoryPlayer);
        }

        /*if (playerHasAllItems)
        {
            ItemStack theItem = CraftingHandler.validOutputs.get(recipeIndex); //Fixes damage values and set the proper item stack size
            boolean flag = AddItemStackPlayer(inventoryPlayer, (TECraftingTableIV)Internal, theItem);
            if (!flag) {
                return new Object[] {false, ThePlayerBefore, SlotCount, InternalBefore}; //Look into this effecting player in some recipes
            }
            InventoryCrafting TempMatrix = generateCraftingMatrix(getRecipeIngredients(RecipeHelper.getCraftingRecipe(TheItem)));
            TheItem.onCrafting(inventoryPlayer.player.worldObj, inventoryPlayer.player, 1);
            //ModLoader.takenFromCrafting(inventoryPlayer.player, TheItem.toItemStack(), TempMatrix);
            //ForgeHooks.onTakenFromCrafting(inventoryPlayer.player, TheItem.toItemStack(), TempMatrix);
            handleCraftingMatrix(TempMatrix, inventoryPlayer);
            }
        }
        return new Object[] {playerHasAllItems, inventoryPlayer, SlotCount, Internal};
    }*/


    public static boolean addItemStackPlayer(InventoryPlayer inventoryPlayer, TECraftingTableIV internal, ItemStack b) {
        return internal.addItemStackToInventory(b.copy()) || inventoryPlayer.addItemStackToInventory(b.copy());
    }
/*
    public static Object[] addItemStackPlayer(InventoryPlayer a, IInventory Internal, ItemStack b) {
        TECraftingTableIV TheInternal = (TECraftingTableIV) Internal;
        if (TheInternal.addItemStackToInventory(b.copy())) {
            return new Object[]{true, TheInternal};
        } else {
            return new Object[]{a.addItemStackToInventory(b.copy()), TheInternal};
        }
    }*/


    public static void decrStackSize(InventoryPlayer inventoryPlayer, IInventory internal, int slot, int amount) {
        if (slot < 18)
            internal.decrStackSize(slot, amount);
        else
            inventoryPlayer.decrStackSize(slot-18, amount);
    }

    public static void handleCraftingMatrix(InventoryCrafting craftingMatrix, InventoryPlayer inventoryPlayer) {
        //ItemStack output = CraftingManager.getInstance().findMatchingRecipe(craftingMatrix, inventoryPlayer.player.worldObj).copy();
        //PlayerEvent.ItemCraftedEvent thisEvent = new PlayerEvent.ItemCraftedEvent(inventoryPlayer.player, output, craftingMatrix);
        //MinecraftForge.EVENT_BUS.post(thisEvent);
        //if (//!thisEvent.isCanceled()) {
        //true) {
            for (int i = 0; i < craftingMatrix.getSizeInventory(); i++) {
                ItemStack stackInSlot = craftingMatrix.getStackInSlot(i);
                if (stackInSlot != null) {
                    craftingMatrix.decrStackSize(i, 1);
                    if (stackInSlot.getItem().hasContainerItem(stackInSlot)) {
                        ItemStack containerItem = new ItemStack(stackInSlot.getItem().getContainerItem());
                        craftingMatrix.setInventorySlotContents(i, containerItem);
                    }
                }
            }
            for (int i = 0; i < craftingMatrix.getSizeInventory(); i++) {
                ItemStack stackInSlot = craftingMatrix.getStackInSlot(i);
                if (stackInSlot != null) {
                    if (!inventoryPlayer.addItemStackToInventory(stackInSlot.copy()))
                        throw new RuntimeException("EY!");
                    craftingMatrix.setInventorySlotContents(i, null);
                }
            }
            //ItemStack selectedStack = inventoryPlayer.getCurrentItem();
            //output.onCrafting(inventoryPlayer.player.worldObj, inventoryPlayer.player, 1);
            /*if (selectedStack == null)
                inventoryPlayer.setItemStack(output);
            else if (ItemHelper.areItemsEqual(output, selectedStack) && selectedStack.stackSize < selectedStack.getMaxStackSize())
                selectedStack.stackSize++;*/
        //}
    }

    public static InventoryCrafting generateCraftingMatrix(ItemStack[] items) {
        InventoryCrafting ret = new InventoryCrafting(new ContainerNull(), 3, 3);
        for (int i=0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item != null) {
                item.stackSize = 1;
                ret.setInventorySlotContents(i, item);
            }
        }
        return ret;
    }

    public static int getFirstInventorySlotWithItemStack(InventoryPlayer inventoryPlayer, IInventory internal, ItemStack itemStack) {
        int i = InventoryHelper.getFirstSlotWithItemStack(internal, itemStack);
        if (i > -1)
            return i;

        int q = InventoryHelper.getFirstSlotWithItemStack(inventoryPlayer, itemStack);
        if (q > -1)
            return q + 18;

        return -1;
    }

    //Validate all recipes, excluding all firework recipes, colouring recipes, ect.
    public static void InitRecipes() {
        validOutputs.clear();
        recipeList.clear();
        for (Object object : CraftingManager.getInstance().getRecipeList()){
            if (object instanceof IRecipe){
                ItemStack output = ((IRecipe) object).getRecipeOutput();
                if (output != null && output.getItem() != null && ((object instanceof ShapelessOreRecipe && isOreValid(((ShapelessOreRecipe)object).getInput())) || (object instanceof ShapedOreRecipe && isOreValid(Lists.newArrayList(((ShapedOreRecipe)object).getInput()))) || object instanceof ShapedRecipes || object instanceof ShapelessRecipes)) {
                    if (CraftingTableIV.nuggetFilter && (MineTweakerHelper.getItemRegistryName(output.copy()).contains("nugget") || OredictHelper.getOreName(output.copy()).contains("nugget")))
                        continue;
                    validOutputs.add(output);
                    recipeList.add((IRecipe) object);
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

    public static List<IRecipe> getCraftingRecipe(ItemStack stack) {
        if (!isStackValid(stack))
            return null;
        List<IRecipe> ret = Lists.newArrayList();
        for(IRecipe recipe : recipeList) {
            if(recipe != null && (recipe instanceof ShapelessOreRecipe || recipe instanceof ShapedOreRecipe || recipe instanceof ShapedRecipes || recipe instanceof ShapelessRecipes)) {
                ItemStack output = recipe.getRecipeOutput();
                if (!isStackValid(output))
                    continue;
                if(output.getItem() == stack.getItem()) {
                    if(output.getItemDamage() == stack.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                        ret.add(recipe);
                        continue;
                    }
                    if(!output.getItem().getHasSubtypes() && !stack.getItem().getHasSubtypes()) {
                        ret.add(recipe);
                    }
                }
            }
        }

        return ret;
    }

    private static boolean isStackValid(ItemStack stack){
        return stack != null && stack.getItem() != null;
    }

    @SuppressWarnings("unchecked")
    public static ItemStack[] getRecipeIngredients(IRecipe irecipe, InventoryPlayer inventoryPlayerNotUse) {
        InventoryPlayer inventoryPlayer = new InventoryPlayer(inventoryPlayerNotUse.player);
        inventoryPlayer.copyInventory(inventoryPlayerNotUse);
        try {
            if (irecipe == null)
                return null;
            else if (irecipe instanceof ShapelessRecipes) {
                ArrayList<ItemStack> recipeItems = new ArrayList<ItemStack>(((ShapelessRecipes) irecipe).recipeItems);
                return recipeItems.toArray(new ItemStack[recipeItems.size()]);
            } else if (irecipe instanceof ShapedRecipes) {
                return ((ShapedRecipes) irecipe).recipeItems;
            } else if (irecipe instanceof ShapedOreRecipe) {
                Object[] input= ((ShapedOreRecipe) irecipe).getInput();
                ArrayList<ItemStack> toRet = new ArrayList<ItemStack>();
                for (Object object : input){
                    if (object instanceof ItemStack) {
                        toRet.add((ItemStack) object);
                        int i = InventoryHelper.getFirstSlotWithItemStack(inventoryPlayer, (ItemStack) object);
                        if (i >= 0)
                            inventoryPlayer.decrStackSize(i, 1);
                    }else if (object instanceof ArrayList) {
                        ItemStack s = getBestItem((ArrayList<ItemStack>) object, inventoryPlayer);
                        int i = InventoryHelper.getFirstSlotWithItemStack(inventoryPlayer, s);
                        if (i >= 0)
                            inventoryPlayer.decrStackSize(i, 1);
                        toRet.add(s);
                    }else if (object == null)
                        toRet.add(null);
                }
                return toRet.toArray(new ItemStack[toRet.size()]);
            } else if (irecipe instanceof ShapelessOreRecipe) {
                ArrayList<Object> input= ((ShapelessOreRecipe) irecipe).getInput();
                ArrayList<ItemStack> toRet = new ArrayList<ItemStack>();
                for (Object object : input){
                    if (object instanceof ItemStack) {
                        toRet.add((ItemStack) object);
                        int i = InventoryHelper.getFirstSlotWithItemStack(inventoryPlayer, (ItemStack) object);
                        if (i >= 0)
                            inventoryPlayer.decrStackSize(i, 1);
                    }else if (object instanceof ArrayList) {
                        ItemStack s = getBestItem((ArrayList<ItemStack>) object, inventoryPlayer);
                        int i = InventoryHelper.getFirstSlotWithItemStack(inventoryPlayer, s);
                        if (i >= 0)
                            inventoryPlayer.decrStackSize(i, 1);
                        toRet.add(s);
                    }
                }
                return toRet.toArray(new ItemStack[toRet.size()]);
            } else if (irecipe instanceof RecipesArmorDyes || irecipe instanceof RecipeFireworks || irecipe instanceof RecipeBookCloning || irecipe instanceof RecipesMapCloning) {
                return null;
            } else {
                if (irecipe.getRecipeOutput() != null)
                    CraftingTableIV.instance.error("ERROR FINDING RECIPE CLASS FOR: " + irecipe.getRecipeOutput().getItem().getUnlocalizedName());
                else CraftingTableIV.instance.error("ERROR: THE OUTPUT FOR THIS RECIPE IS NULL! " + irecipe.toString());
                return null;
            }
        } catch (NullPointerException e) {
            if (ElecCore.proxy.isClient()) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("There was an error loading some recipes, the error will be printed in the game log.");
                Minecraft.getMinecraft().thePlayer.sendChatMessage("Please report this to Elec332 with the entire gamelog here: https://github.com/Elecs-Mods/CraftingTable-IV/issues");
            }
            CraftingTableIV.instance.error("Something went wrong while trying to aquire recipe ingredients!");
            CraftingTableIV.instance.error(e);
            return null;
        }
    }

    private static ItemStack getBestItem(List<ItemStack> itemStacks, InventoryPlayer inventoryPlayer){
        for (ItemStack itemStack : itemStacks)
            if (InventoryHelper.getFirstSlotWithItemStack(inventoryPlayer, itemStack) >= 0)
                return itemStack;
        return itemStacks.get(0);
    }
}
