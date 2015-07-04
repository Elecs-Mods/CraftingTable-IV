package elec332.craftingtableiv.blocks.container;

import com.google.common.collect.Lists;
import elec332.core.player.PlayerHelper;
import elec332.core.util.Constants;
import elec332.core.util.NBTHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.blocks.inv.InventoryCraftingTableIV;
import elec332.craftingtableiv.blocks.slot.InterceptSlot;
import elec332.craftingtableiv.blocks.slot.SlotCrafter;
import elec332.craftingtableiv.blocks.slot.SlotStorage;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.handler.RecipeStackComparator;
import elec332.craftingtableiv.handler.StackComparator;
import elec332.craftingtableiv.handler.WrappedRecipe;
import elec332.craftingtableiv.network.PacketSyncRecipes;
import elec332.craftingtableiv.network.PacketSyncScroll;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 23-3-2015.
 */

public class CraftingTableIVContainer extends Container {

    public InventoryBasic inventory = new InventoryBasic("tmp",true , 8*5);
    public InventoryBasic recipeItems = new InventoryBasic("tmp2",true, 9);

    public InventoryCrafting craftMatrix;
    public InventoryCraftingTableIV craftableRecipes;

    private String textField = "";

    //private List recipeList;
    private Runnable getRunnable(){
        return new Runnable() {
            @Override
            public void run() {
                populateSlotsWithRecipes();
            }
        };
    }

    private CTIVThread currentThread;

    private EntityPlayer thePlayer;
    public TECraftingTableIV theTile;
    public float ScrollValue = 0.0F;
    private boolean playerHasWood = false;
    //private boolean busy = false;

    public CraftingTableIVContainer(EntityPlayer aPlayer, TECraftingTableIV tile) {
        theTile = tile;
        thePlayer = aPlayer;
        craftMatrix = new InventoryCrafting(this, 3, 3);
        craftableRecipes = new InventoryCraftingTableIV();
        //recipeList = Collections.unmodifiableList(CraftingManager.getInstance().getRecipeList());

        for(int l2 = 0; l2 < 5; l2++) {
            for(int j3 = 0; j3 < 8; j3++) {
                addSlotToContainer(new SlotCrafter(inventory, craftMatrix, j3 + l2 * 8, 8 + j3 * 18, 18 + l2 * 18, this));
            }
        }

        for(int a = 0; a < 2; a++) {
            for(int i = 0; i < 9; i++) {
                addSlotToContainer(new SlotStorage(theTile, i + (a * 9), 8 + i * 18, 112 + (18 * a)));
            }
        }

        for(int j = 0; j < 3; j++) {
            for(int i1 = 0; i1 < 9; i1++) {
                addSlotToContainer(new Slot(thePlayer.inventory, i1 + j * 9 + 9, 8 + i1 * 18, 152 + j * 18));
            }
        }

        for(int i3 = 0; i3 < 9; i3++) {
            addSlotToContainer(new Slot(thePlayer.inventory, i3, 8 + i3 * 18, 211));
        }

        addSlotToContainer(new InterceptSlot(recipeItems, 0, -18, 34, this));
        addSlotToContainer(new InterceptSlot(recipeItems, 1, -18, 52, this));
        addSlotToContainer(new InterceptSlot(recipeItems, 2, -18, 70, this));
        addSlotToContainer(new InterceptSlot(recipeItems, 3, -18, 88, this));
        addSlotToContainer(new InterceptSlot(recipeItems, 4, -18, 106, this));
        addSlotToContainer(new InterceptSlot(recipeItems, 5, -18, 124, this));
        addSlotToContainer(new InterceptSlot(recipeItems, 6, -18, 142, this));
        addSlotToContainer(new InterceptSlot(recipeItems, 7, -18, 160, this));
        addSlotToContainer(new InterceptSlot(recipeItems, 8, -18, 178, this));
        //CraftingHandler.InitRecipes();
        updateRecipes();
    }


    private void stopThread(){
        if (currentThread != null)
            currentThread.killSafe();
    }

    public void setText(String text){
        this.textField = text;
        updateRecipes();
    }

    public void populateSlotsWithRecipes() {
        long l = System.currentTimeMillis();
        if (!thePlayer.worldObj.isRemote) {
            cannotCraft.clear();
            craftableRecipes.clearRecipes();
            syncRecipes();
            //int i = 0;
            /*for (Map.Entry<ItemComparator, List<IRecipe>> entry : CraftingHandler.recipeHash.entrySet()) {
                for (IRecipe recipe : entry.getValue()) {
                    i++;
                    System.out.println("Starting for "+ MineTweakerHelper.getItemRegistryName(recipe.getRecipeOutput()));
                    if (//craftableRecipes.canAdd(recipe) &&
                            canPlayerCraft(thePlayer, theTile, recipe, false)) {
                        craftableRecipes.forceAddRecipe(recipe);
                        syncRecipes();
                    }
                    System.out.println("Done with "+i+" out of "+CraftingHandler.recipeList.size());
                }
            }*/
            for (WrappedRecipe recipe : CraftingHandler.recipeList) {
                //i++;
                //System.out.println("Starting for "+ MineTweakerHelper.getItemRegistryName(recipe.getRecipeOutput()));
                if (recipe.getOutputItemName().contains(textField) && canPlayerCraft(thePlayer, theTile, recipe, false)) {
                    craftableRecipes.forceAddRecipe(recipe);
                    syncRecipes();
                } /*else {
                    cannotCraft.add(new StackComparator(recipe.getRecipeOutput()));
                    String s = OredictHelper.getOreName(recipe.getRecipeOutput());
                    if (!Strings.isNullOrEmpty(s))
                        canAlsoNotCraft.add(s);
                }*/
                //System.out.println("Done with " + i + " out of " + CraftingHandler.recipeList.size());
            }
            updateVisibleSlots(ScrollValue);
            syncRecipes();
            //System.out.println("Done with entirely");
        }
        CraftingTableIV.instance.info("Loaded all recipes for CTIV Gui in "+(System.currentTimeMillis()-l)+" ms");
    }

    private void syncRecipes(){
        NBTTagCompound tagCompound = new NBTTagCompound();
        craftableRecipes.writeToNBT(tagCompound);
        CraftingTableIV.networkHandler.getNetworkWrapper().sendTo(new PacketSyncRecipes(tagCompound), (EntityPlayerMP)thePlayer);
    }

    private List<StackComparator> cannotCraft = Lists.newArrayList();
    //private List<String> canAlsoNotCraft = Lists.newArrayList();

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        stopThread();
    }

    public boolean canPlayerCraft(EntityPlayer player, TECraftingTableIV craftingTableIV, WrappedRecipe recipe, boolean b){
        InventoryPlayer fakeInventoryPlayer = new InventoryPlayer(player);
        fakeInventoryPlayer.copyInventory(player.inventory);
        TECraftingTableIV fakeCraftingInventory = craftingTableIV.getCopy();
        boolean ret = canPlayerCraft(fakeInventoryPlayer, fakeCraftingInventory, recipe, 0, new ArrayList<RecipeStackComparator>());
        if (b && ret){
            player.inventory.copyInventory(fakeInventoryPlayer);
            for (int i = 0; i < craftingTableIV.getSizeInventory(); i++) {
                craftingTableIV.setInventorySlotContents(i, fakeCraftingInventory.getStackInSlot(i));
            }
        }
        return ret;
    }

    public boolean canPlayerCraft(InventoryPlayer fakeInventoryPlayer, TECraftingTableIV fakeCraftingInventory, WrappedRecipe recipe, int i, List<RecipeStackComparator> no){
        if (i > CraftingTableIV.recursionDepth)
            return false;
        if (i > 0 && compareStacks(recipe.getRecipe(), no))
            return false;
        if (fakeInventoryPlayer != null && recipe != null) {
            for (Object obj : recipe.getInput()) {
                if (obj == null)
                    continue;
                if (obj instanceof ItemStack) {
                    ItemStack itemStack = (ItemStack) obj;
                       //if (cannotCraft.contains(new StackComparator(itemStack)) )
                       //    return false;
                       //String s = OredictHelper.getOreName(recipe.getRecipeOutput());
                       //if (!Strings.isNullOrEmpty(s) && canAlsoNotCraft.contains(s))
                       //    return false;
                    int slotID = CraftingHandler.getFirstInventorySlotWithItemStack(fakeInventoryPlayer, fakeCraftingInventory, itemStack);
                    if (slotID > -1) {
                        if (itemStack.getItem().hasContainerItem(itemStack) && !CraftingHandler.addItemStackPlayer(fakeInventoryPlayer, fakeCraftingInventory, itemStack.getItem().getContainerItem(itemStack)))
                            return false;
                        CraftingHandler.decrStackSize(fakeInventoryPlayer, fakeCraftingInventory, slotID, 1);
                    } else if (i != CraftingTableIV.recursionDepth && canPlayerCraftAnyOf(fakeInventoryPlayer, fakeCraftingInventory, CraftingHandler.getCraftingRecipe(itemStack), i, addToList(no, CraftingHandler.getStackComparator(itemStack)))) {
                        if (!handleStuff(fakeInventoryPlayer, fakeCraftingInventory, itemStack))
                            return false;
                    } else {
                        return false;
                    }
                } else if (obj instanceof List && !((List)obj).isEmpty()){
                    boolean done = false;
                    @SuppressWarnings("unchecked")
                    List<ItemStack> stacks = (List<ItemStack>) obj;
                    for (ItemStack itemStack : stacks) {
                        int p = CraftingHandler.getFirstInventorySlotWithItemStack(fakeInventoryPlayer, fakeCraftingInventory, itemStack);
                        if (p >= 0) {
                            if (!handleStuff(fakeInventoryPlayer, fakeCraftingInventory, itemStack))
                                return false;
                            done = true;
                            break;
                        }
                    }
                    if (done)
                        continue;
                    if (i == CraftingTableIV.recursionDepth)
                        return false;
                    ItemStack stack = canPlayerCraftAnyOf(fakeInventoryPlayer, fakeCraftingInventory, stacks, addToList(no, recipe.getRecipeOutput()), i);
                    if (stack != null){
                       if (!handleStuff(fakeInventoryPlayer, fakeCraftingInventory, stack))
                           return false;
                    } else {
                        return false;
                    }
                }
            }
            return CraftingHandler.addItemStackPlayer(fakeInventoryPlayer, fakeCraftingInventory, recipe.getRecipeOutput().getStack().copy());
        } else return false;
    }

    private boolean handleStuff(InventoryPlayer fakeInventoryPlayer, TECraftingTableIV fakeCraftingInventory, ItemStack stack){
        int s = CraftingHandler.getFirstInventorySlotWithItemStack(fakeInventoryPlayer, fakeCraftingInventory, stack.copy());
        if (stack.getItem().hasContainerItem(stack)){
            ItemStack itemStack = stack.getItem().getContainerItem(CraftingHandler.getStackInSlot(fakeInventoryPlayer, fakeCraftingInventory, s));
            if (itemStack != null && itemStack.isItemStackDamageable() && itemStack.getItemDamage() > itemStack.getMaxDamage())
                itemStack = null;
            if (itemStack != null && !CraftingHandler.addItemStackPlayer(fakeInventoryPlayer, fakeCraftingInventory, itemStack))
                return false;
        }
        CraftingHandler.decrStackSize(fakeInventoryPlayer, fakeCraftingInventory, s, 1);
        return true;
    }



    private ItemStack canPlayerCraftAnyOf(InventoryPlayer fakeInventoryPlayer, TECraftingTableIV fakeCraftingInventory, List<ItemStack> stacks, List<RecipeStackComparator> no, int i){
        /*List<IRecipe> recipes = Lists.newArrayList();
        for (ItemStack stack : stacks){
            recipes.addAll(CraftingHandler.getCraftingRecipe(stack));
        }
        return canPlayerCraftAnyOf(fakeInventoryPlayer, fakeCraftingInventory, recipes, i, no);*/
        for (ItemStack itemStack : stacks){
            if (canPlayerCraftAnyOf(fakeInventoryPlayer, fakeCraftingInventory, CraftingHandler.getCraftingRecipe(itemStack), i, no))
                return itemStack;
        }
        return null;
    }

    private boolean canPlayerCraftAnyOf(InventoryPlayer fakeInventoryPlayer, TECraftingTableIV fakeCraftingInventory, List<WrappedRecipe> recipes, int i, List<RecipeStackComparator> no){
        if (recipes == null || recipes.isEmpty())
            return false;

        for (WrappedRecipe recipe : recipes) {
            InventoryPlayer fake = new InventoryPlayer(fakeInventoryPlayer.player);
            fake.copyInventory(fakeInventoryPlayer);
            TECraftingTableIV copy = fakeCraftingInventory.getCopy();
            if (canPlayerCraft(fake, copy, recipe, i+1, no)){
                fakeInventoryPlayer.copyInventory(fake);
                for (int q = 0; q < fakeCraftingInventory.getSizeInventory(); q++) {
                    fakeCraftingInventory.setInventorySlotContents(q, copy.getStackInSlot(q));
                }
                return true;
            }
        }
        return false;
    }

    private <T> List<T> addToList(List<T> list, T t){
        List<T> newList = Lists.newArrayList();
        for (T t1 : list)
            newList.add(t1);
        newList.add(t);
        return newList;
      /*
        list.add(t);
        return list;*/
    }

    private static boolean isValidForCrafting(ItemStack stack){
        for (ItemStack itemStack : CraftingHandler.validOutputs){
            if(itemStack.getItem() == stack.getItem()) {
                if(itemStack.getItemDamage() == stack.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    return true;
                }
                if(!itemStack.getItem().getHasSubtypes() && !stack.getItem().getHasSubtypes()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean compareStacks(IRecipe r, RecipeStackComparator[] s){
        return compareStacks(r, Lists.newArrayList(s));
    }

    private static boolean compareStacks(IRecipe r, List<RecipeStackComparator> s){
        if (s.isEmpty())
            return false;
        for (RecipeStackComparator s1 : s){
            if (CraftingHandler.isStackValid(s1) && compareStacks(r, s1))
                return true;
        }
        return false;
    }

    private static boolean compareStacks(IRecipe r, RecipeStackComparator sc){
        if (r == null || r.getRecipeOutput() == null || sc == null)
            return false;
        if (sc.equals(CraftingHandler.getStackComparator(r.getRecipeOutput())))
            return true;
        /*System.out.println("Not equal");
        ItemStack s1 = r.getRecipeOutput().copy();
        ItemStack s2 = sc.getStack().copy();
        if(s1.getItem() == s2.getItem()) {
            if(s1.getItemDamage() == s2.getItemDamage() || s2.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                throw new RuntimeException();
                //return true;
            }
            if(!s1.getItem().getHasSubtypes() && !s2.getItem().getHasSubtypes()) {
                throw new RuntimeException();
                //return true;
            }
            if (s2.getItem() == Items.dye || s2.getItem() == Item.getItemFromBlock(Blocks.wool))
                throw new RuntimeException();
                //return true;
        }*/
        return false;
    }

    public void updateVisibleSlots(float f) {
        ScrollValue = f;
        if (thePlayer.getEntityWorld().isRemote)
            CraftingTableIV.networkHandler.getNetworkWrapper().sendToServer(new PacketSyncScroll(new NBTHelper().addToTag(ScrollValue, "scroll").toNBT()));
        int numberOfRecipes = craftableRecipes.getSize();
        int i = (numberOfRecipes / 8 - 4) + 1;
        int j = (int)((double)(f * (float)i) + 0.5D);
        if(j < 0)
            j = 0;
        for(int k = 0; k < 5; k++) {
            for(int l = 0; l < 8; l++) {
                int i1 = l + (k + j) * 8;
                Slot slot = (Slot)inventorySlots.get(l + k * 8);
                if(i1 >= 0 && i1 < numberOfRecipes) {
                    ItemStack recipeOutput = craftableRecipes.getRecipeOutput(i1);
                    if(recipeOutput != null) {
                        //if (thePlayer instanceof EntityClientPlayerMP)
                            //inventory.setInventorySlotContents(l + k * 8, recipeOutput);
                        //else
                        inventory.setInventorySlotContents(l + k * 8, null);
                        if(slot instanceof SlotCrafter) {
                            ((SlotCrafter)slot).setIRecipe(craftableRecipes.getIRecipe(i1));
                        }
                    } else {
                        inventory.setInventorySlotContents(l + k * 8, null);
                        if(slot instanceof SlotCrafter) {
                            ((SlotCrafter)slot).setIRecipe(null);
                        }
                    }
                } else {
                    inventory.setInventorySlotContents(l + k * 8, null);
                    if(slot instanceof SlotCrafter) {
                        ((SlotCrafter)slot).setIRecipe(null);
                    }
                }
            }
        }
        //System.out.println("Updated visible slots");
    }

    public ItemStack transferStackInSlot(int par1) {
        ItemStack var2 = null;
        Slot var3 = (Slot)this.inventorySlots.get(par1);

        if (var3 != null && var3.getHasStack()) {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();

            if (par1 < 58 && par1 > 39) {
                if (!this.mergeItemStack(var4, 58, 94, true)) {
                    return null;
                }
            } else if (par1 > 57)
                if (!this.mergeItemStack(var4, 40, 58, false)) {
                    return null;
                }

            if (var4.stackSize == 0) {
                var3.putStack(null);
            } else {
                var3.onSlotChanged();
            }
        }
        return var2;
    }



    @Override
    public ItemStack slotClick(int slotIndex, int mouseButton, int flag, EntityPlayer entityplayer) {
        //PlayerHelper.addPersonalMessageToClient("Clicked slot: "+slotIndex);
        //if (this.busy)
        //    return null;
        //this.busy = true;
        if(slotIndex >= 0 && inventorySlots.get(slotIndex) != null && inventorySlots.get(slotIndex) instanceof SlotCrafter) {

            // Check if the currently held itemstack is different to the clicked itemstack.
            //if(!ItemHelper.areItemsEqual(inventory.getStackInSlot(slotIndex), entityplayer.inventory.getItemStack()))
                //return null;

            // Ignore right click.
            if(mouseButton == Constants.Mouse.MOUSE_RIGHT) {
                updateRecipes();
                return null;
            } else if(flag == 1) {
                onRequestMaximumRecipeOutput((SlotCrafter) inventorySlots.get(slotIndex));
                updateRecipes();
                return null;
            } else if (mouseButton == Constants.Mouse.MOUSE_LEFT){
                onRequestSingleRecipeOutput((SlotCrafter)inventorySlots.get(slotIndex));
                updateRecipes();
                return null;
            } else CraftingTableIV.instance.info("Received mouse event with ID: "+mouseButton+" I cannot process this button");
        }
        /*if(KeyHelper.isShiftDown()) {
            //transferStackInSlot(slotIndex);
            ItemStack itemstack = super.slotClick(slotIndex, mouseButton, flag, entityplayer);
            populateSlotsWithRecipes();
            updateVisibleSlots(ScrollValue);
            return itemstack;
        } else {
            ItemStack itemstack = super.slotClick(slotIndex, mouseButton, flag, entityplayer);
            populateSlotsWithRecipes();
            updateVisibleSlots(ScrollValue);
            return itemstack;
        }*/
        ItemStack itemstack = super.slotClick(slotIndex, mouseButton, flag, entityplayer);
        updateRecipes();
        return itemstack;
    }

    public void updateRecipes(){
        //this.busy = true;
        stopThread();
        currentThread = new CTIVThread();
        currentThread.start();
        //updateVisibleSlots(ScrollValue);
        //this.busy = false;
    }

    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
        return transferStackInSlot(p_82846_2_);
    }

    public boolean onRequestSingleRecipeOutput(SlotCrafter slot) {
        WrappedRecipe recipe = slot.getIRecipe();
        return recipe != null && canPlayerCraft(thePlayer, theTile, recipe, true); //onRequestSingleRecipeOutput(thePlayer.inventory, irecipe, theTile, true);
    }

    public boolean onRequestSingleRecipeOutput(InventoryPlayer thePlayerInventory, IRecipe irecipe, TECraftingTableIV internal, boolean b) {
        TECraftingTableIV internalCopy = internal.getCopy();
        InventoryPlayer fakeInventory = new InventoryPlayer(thePlayerInventory.player);
        fakeInventory.copyInventory(thePlayerInventory);
        try {
            craftRecipe(irecipe, fakeInventory, internalCopy);
            thePlayerInventory.copyInventory(fakeInventory);
            for (int i = 0; i < internal.getSizeInventory(); i++) {
                internal.setInventorySlotContents(i, internalCopy.getStackInSlot(i));
            }
            //internal.inv = internalCopy.theInventory;
            return true;
        } catch (RuntimeException t){
            if (b)
                PlayerHelper.addPersonalMessageToClient("Something went wrong while trying to process your crafting request");
            return false;
        }
    }

    public void craftRecipe(IRecipe recipe, InventoryPlayer inventoryPlayer, TECraftingTableIV internalInventory) {
        /*ItemStack[] ingredients = CraftingHandler.getRecipeIngredients(recipe, inventoryPlayer);
        for (ItemStack itemStack : ingredients) {
            CraftingHandler.decrStackSize(inventoryPlayer, internalInventory, CraftingHandler.getFirstInventorySlotWithItemStack(inventoryPlayer, internalInventory, itemStack), 1);
        }
        InventoryCrafting craftingMatrix = CraftingHandler.generateCraftingMatrix(ingredients);
        CraftingHandler.handleCraftingMatrix(craftingMatrix, inventoryPlayer);
        if (!CraftingHandler.addItemStackPlayer(inventoryPlayer, internalInventory, recipe.getRecipeOutput().copy())) {
            PlayerHelper.addPersonalMessageToClient("Something went wrong while trying to process your crafting request");
            throw new RuntimeException("EY!");
        }*/
    }

    private void onRequestMaximumRecipeOutput(SlotCrafter slot) {
        WrappedRecipe recipe = slot.getIRecipe();
        if(recipe == null)
            return;
        onRequestMaximumRecipeOutput(thePlayer, recipe, theTile);
    }

    public static void onRequestMaximumRecipeOutput(EntityPlayer thePlayer, WrappedRecipe irecipe, TECraftingTableIV Internal) {
        InventoryPlayer Temp = new InventoryPlayer( thePlayer );
        Temp.copyInventory(thePlayer.inventory);

        InventoryPlayer inventoryPlayer = thePlayer.inventory;
        int GoTo = 64;
        if (irecipe.getRecipeOutput().getStack().getMaxStackSize() > 1) {
            GoTo = irecipe.getRecipeOutput().getStack().getMaxStackSize() / irecipe.getRecipeOutput().getStack().stackSize ;
        }
        for (int i=0; i<GoTo; i++) {
            Temp.copyInventory(thePlayer.inventory);
            //if ((Boolean)CraftingHandler.canPlayerCraft(Temp, irecipe.getRecipeOutput(), Internal, RecipeIndex)[0]) {
            //    Object[] iTemp = CraftingHandler.canPlayerCraft(inventoryPlayer, Internal, irecipe.getRecipeOutput(), 0, null, null, RecipeIndex);
             //   Internal.theInventory = ((TECraftingTableIV)iTemp[3]).theInventory;
            //    thePlayer.inventory.copyInventory((InventoryPlayer) iTemp[1]);
           // } else {
                break;
            //}
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return this.theTile.isUseableByPlayer(entityplayer);
    }

    @SuppressWarnings("deprecation")
    private class CTIVThread extends Thread{

        public CTIVThread(){
            super("CraftingHandler");
        }

        @Override
        public void run() {
            long l = System.currentTimeMillis();
            if (!thePlayer.worldObj.isRemote) {
                cannotCraft.clear();
                craftableRecipes.clearRecipes();
                List<ItemStack> checkWood = Lists.newArrayList(OreDictionary.getOres("logWood"));
                checkWood.addAll(OreDictionary.getOres("plankWood"));
                InventoryPlayer check = new InventoryPlayer(thePlayer);
                check.copyInventory(thePlayer.inventory);
                for (ItemStack stack : checkWood){
                    if (CraftingHandler.getFirstInventorySlotWithItemStack(check, theTile.getCopy(), stack) >= 0){
                        CraftingTableIVContainer.this.playerHasWood = true;
                        break;
                    }
                }
                syncRecipes();
                for (WrappedRecipe recipe : CraftingHandler.recipeList) {
                    if (stopThread)
                        stop();
                    if (!CraftingTableIVContainer.this.playerHasWood && recipe.hasWood())
                        continue;
                    if (recipe.getOutputItemName().contains(CraftingTableIVContainer.this.textField) && canPlayerCraft(thePlayer, theTile, recipe, false)) {
                        craftableRecipes.forceAddRecipe(recipe);
                        syncRecipes();
                    }

                }
                updateVisibleSlots(ScrollValue);
                syncRecipes();
                CraftingTableIV.instance.info("Loaded all recipes for CTIV Gui in " + (System.currentTimeMillis() - l) + " ms");
            }
        }

        public void killSafe(){
            stopThread = true;
        }

        private boolean stopThread = false;

    }
}
