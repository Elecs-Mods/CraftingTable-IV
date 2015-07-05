package elec332.craftingtableiv.handler;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.registry.GameData;
import elec332.core.helper.OredictHelper;
import elec332.core.main.ElecCore;
import elec332.core.minetweaker.MineTweakerHelper;
import elec332.core.util.IRunOnce;
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

import java.util.*;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class CraftingHandler {
    //public static int MaxLevel = 20;
    public static ArrayList<ItemStack> validOutputs = Lists.newArrayList();
    public static ArrayList<WrappedRecipe> recipeList = Lists.newArrayList();
    public static ArrayList<StackComparator> syncedRecipeOutput = Lists.newArrayList();
    public static ArrayList<RecipeStackComparator> stackDataList = Lists.newArrayList();
    public static Map<String, Map<ItemComparator, List<WrappedRecipe>>> recipeHash = Maps.newHashMap();
    //public static Map<String, List<WrappedRecipe>> oreDictRecipeHash = Maps.newHashMap();
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

    /*private static void addToOreRecipeHash(String s, WrappedRecipe recipe){
        if (oreDictRecipeHash.get(s) == null)
            oreDictRecipeHash.put(s, new ArrayList<WrappedRecipe>());
        oreDictRecipeHash.get(s).add(recipe);
    }*/

    private static void addToRCMap(StackComparator stackComparator, RecipeStackComparator recipeStackComparator){
        String s = identifier(stackComparator.getStack());
        if (rcMap.get(s) == null)
            rcMap.put(s, new HashMap<StackComparator, RecipeStackComparator>());
        rcMap.get(s).put(stackComparator, recipeStackComparator);
    }

    public static RecipeStackComparator g(StackComparator s){
        try {
            return get(identifier(s.getStack())).get(s);
        } catch (Exception e){
            return null;
        }
    }

    public static RecipeStackComparator getStackComparator(ItemStack stack){
       return g(new StackComparator(stack)); //null;//rcMap.get(stack);//new RecipeStackComparator(stack) ; //stackDataList.get(syncedRecipeOutput.indexOf(new StackComparator(stack)));
    }

    private static Map<StackComparator, RecipeStackComparator> get(String s){
        return rcMap.get(s);
    }

    private static String identifier(ItemStack stack){
        return MineTweakerHelper.getItemRegistryName(stack).replace(":", " ").split(" ")[0];
    }

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


    public static boolean decrStackSize(InventoryPlayer inventoryPlayer, IInventory internal, int slot, int amount) {
        if (slot < 18)
            return internal.decrStackSize(slot, amount) != null;
        else
            return inventoryPlayer.decrStackSize(slot-18, amount) != null;
    }

    public static ItemStack getStackInSlot(InventoryPlayer inventoryPlayer, IInventory internal, int slot) {
        if (slot < 18)
            return internal.getStackInSlot(slot);
        else
            return inventoryPlayer.getStackInSlot(slot - 18);
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
        int i = getFirstSlotWithItemStack(internal, itemStack);
        if (i > -1)
            return i;

        int q = getFirstSlotWithItemStack(inventoryPlayer, itemStack);
        if (q > -1)
            return q + 18;

        return -1;
    }

    public static int getFirstSlotWithItemStack(IInventory inventory, ItemStack stack){
        for(int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stackInSlot = inventory.getStackInSlot(i);
            if(stackInSlot != null && stack != null && stackInSlot.getItem() == stack.getItem()) {
                if(stackInSlot.getItemDamage() == stack.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    return i;
                }
                if(!stackInSlot.getItem().getHasSubtypes() && !stack.getItem().getHasSubtypes()) {
                    return i;
                }
            }
        }
        return -1;
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
                ItemStack output = ((IRecipe) object).getRecipeOutput();
                if (output != null && output.getItem() != null) {
                    if (object instanceof ShapelessOreRecipe && !isOreValid(((ShapelessOreRecipe)object).getInput()))
                        continue;
                    if (object instanceof ShapedOreRecipe && !isOreValid(Lists.newArrayList(((ShapedOreRecipe)object).getInput())))
                        continue;
                    output = output.copy();
                    if (CraftingTableIV.nuggetFilter && (MineTweakerHelper.getItemRegistryName(output).contains("nugget") || OredictHelper.getOreName(output).contains("nugget")) || recipeList.contains((IRecipe)object))
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
                        entries.get("minecraft").add((IRecipe)object);
                    } else {
                        if (namedList.get(s[0]) == null)
                            namedList.put(s[0], new ArrayList<IRecipe>());
                        namedList.get(s[0]).add((IRecipe)object);
                    }
                    /*validOutputs.add(output);
                    recipeList.add((IRecipe) object);
                    syncedRecipeOutput.add(new StackComparator(output));
                    stackDataList.add(new RecipeStackComparator(output));
                    rcMap.put(new StackComparator(output), new RecipeStackComparator(output));
                    addToRecipeHash(output, (IRecipe) object);
                    String oreName = OredictHelper.getOreName(output);
                    if (!Strings.isNullOrEmpty(oreName))
                        addToOreRecipeHash(oreName, (IRecipe)object);*/
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
                    String oreName = OredictHelper.getOreName(output.copy());
                    //if (!Strings.isNullOrEmpty(oreName))
                        //addToOreRecipeHash(oreName, recipe);
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
        if (!isStackValid(stack))
            return Lists.newArrayList();
        List<WrappedRecipe> possRet;
        try {
            possRet = recipeHash.get(identifier(stack)).get(new ItemComparator(stack));
        } catch (Exception e){
            return Lists.newArrayList();
        }
        if (possRet == null || possRet.isEmpty())
            return Lists.newArrayList();
        if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
            return possRet;
        List<WrappedRecipe> ret = Lists.newArrayList();
        for (WrappedRecipe recipe : possRet){
            ItemStack out = recipe.getRecipeOutput().getStack();
            if (out.getItemDamage() == stack.getItemDamage() || (!out.getHasSubtypes() && !stack.getHasSubtypes()))
                ret.add(recipe);
        }
        return ret;
        /*List<IRecipe> ret = Lists.newArrayList();
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

        return ret;*/
    }

    public static boolean isStackValid(RecipeStackComparator rc){
        return isStackValid(rc.getStack());
    }

    public static boolean isStackValid(ItemStack stack){
        return stack != null && stack.getItem() != null;
    }


    public static WrappedRecipe getWrappedRecipe(IRecipe irecipe) {
        /*InventoryPlayer inventoryPlayer = new InventoryPlayer(inventoryPlayerNotUse.player);
        inventoryPlayer.copyInventory(inventoryPlayerNotUse);*/
        try {
            if (irecipe == null)
                return null;
            else if (irecipe instanceof ShapelessRecipes) {
                return new WrappedRecipe((ShapelessRecipes)irecipe);
                //return recipeItems.toArray(new ItemStack[recipeItems.size()]);
            } else if (irecipe instanceof ShapedRecipes) {
                return new WrappedRecipe((ShapedRecipes)irecipe);
            } else if (irecipe instanceof ShapedOreRecipe) {
                return new WrappedRecipe((ShapedOreRecipe) irecipe);
                /*Object[] input= ((ShapedOreRecipe) irecipe).getInput();
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
                return toRet.toArray(new ItemStack[toRet.size()]);*/
            } else if (irecipe instanceof ShapelessOreRecipe) {
                return new WrappedRecipe((ShapelessOreRecipe) irecipe);
                /*ArrayList<Object> input= ((ShapelessOreRecipe) irecipe).getInput();
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
                return toRet.toArray(new ItemStack[toRet.size()]);*/
            } else if (irecipe instanceof RecipesArmorDyes || irecipe instanceof RecipeFireworks || irecipe instanceof RecipeBookCloning || irecipe instanceof RecipesMapCloning) {
                return null;
            } else if (Compat.getCompatHandler().isDisabled(irecipe)) {
                return null;
            }else if (Compat.getCompatHandler().hasHandler(irecipe.getClass())){
                return Compat.getCompatHandler().getHandler(irecipe).getWrappedRecipe(irecipe);
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
            CraftingTableIV.instance.error(irecipe.toString()+" with output "+irecipe.getRecipeOutput().toString());
            CraftingTableIV.instance.error(e);
            return null;
        }
    }

    /*@SuppressWarnings("unchecked")
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
            CraftingTableIV.instance.error("Something went wrong while trying to acquire recipe ingredients!");
            CraftingTableIV.instance.error(e);
            return null;
        }
    }

    private static ItemStack getBestItem(List<ItemStack> itemStacks, InventoryPlayer inventoryPlayer){
        for (ItemStack itemStack : itemStacks)
            if (InventoryHelper.getFirstSlotWithItemStack(inventoryPlayer, itemStack) >= 0)
                return itemStack;
        for (ItemStack itemStack : itemStacks) //Yes, this must be run after the loop above
            if (itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
                return itemStack;
        Object o = oreDictRecipeHash.get(OredictHelper.getOreName(itemStacks.get(0)));
        if (o != null && !((List) o).isEmpty())
            return ((IRecipe)((List) o).get(0)).getRecipeOutput().copy();
        return itemStacks.get(0);
    }*/
}
