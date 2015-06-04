package elec332.craftingtableiv.blocks.container;

import elec332.core.client.KeyHelper;
import elec332.core.helper.RecipeHelper;
import elec332.core.minetweaker.MineTweakerHelper;
import elec332.core.player.PlayerHelper;
import elec332.core.util.Constants;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.blocks.inv.InventoryCraftingTableIV;
import elec332.craftingtableiv.blocks.slot.InterceptSlot;
import elec332.craftingtableiv.blocks.slot.SlotCrafter;
import elec332.craftingtableiv.blocks.slot.SlotStorage;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class CraftingTableIVContainer extends Container {

    public InventoryBasic inventory = new InventoryBasic("tmp",true , 8*5);
    public InventoryBasic recipeItems = new InventoryBasic("tmp2",true, 9);

    public InventoryCrafting craftMatrix;
    public InventoryCraftingTableIV craftableRecipes;
    //private List recipeList;
    private EntityPlayer thePlayer;
    public TECraftingTableIV theTile;
    public float ScrollValue = 0.0F;
    private boolean busy = false;

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

    public void populateSlotsWithRecipes() {
        craftableRecipes.clearRecipes();
        for(ItemStack stack : CraftingHandler.validOutputs) {
            if (canPlayerCraft(thePlayer, theTile, stack, false)) {
                craftableRecipes.addRecipe(RecipeHelper.getCraftingRecipe(stack));
            }
        }
        updateVisibleSlots(ScrollValue);
    }

    public boolean canPlayerCraft(EntityPlayer player, TECraftingTableIV craftingTableIV,  ItemStack stack, boolean b){
        InventoryPlayer fakeInventoryPlayer = new InventoryPlayer(player);
        fakeInventoryPlayer.copyInventory(player.inventory);
        TECraftingTableIV fakeCraftingInventory = craftingTableIV.getCopy();
        boolean ret = canPlayerCraft(fakeInventoryPlayer, fakeCraftingInventory, stack, 0, stack);
        if (b && ret){
            player.inventory.copyInventory(fakeInventoryPlayer);
            for (int i = 0; i < craftingTableIV.getSizeInventory(); i++) {
                craftingTableIV.setInventorySlotContents(i, fakeCraftingInventory.getStackInSlot(i));
            }
        }
        return ret;
    }

    public boolean canPlayerCraft(InventoryPlayer fakeInventoryPlayer, TECraftingTableIV fakeCraftingInventory,  ItemStack stack, int i, ItemStack no){
        if (i > CraftingTableIV.recursionDepth)
            return false;
        if (fakeInventoryPlayer != null && stack != null && !(i > 0 && compareStacks(stack, no))){
            IRecipe theRecipe = CraftingHandler.getCraftingRecipe(stack);
            if (theRecipe == null){
                CraftingTableIV.instance.error("Cannot find recipe for: "+ MineTweakerHelper.getItemRegistryName(stack));
                return false;
            }

            for (ItemStack itemStack : CraftingHandler.getRecipeIngredients(theRecipe, fakeInventoryPlayer)){
                if (itemStack == null) {
                    //CraftingTableIV.instance.error("Null item in recipe for: "+MineTweakerHelper.getItemRegistryName(theRecipe.getRecipeOutput()));
                    continue;
                }
                int slotID = CraftingHandler.getFirstInventorySlotWithItemStack(fakeInventoryPlayer, fakeCraftingInventory, itemStack);
                if (slotID > -1){
                    CraftingHandler.decrStackSize(fakeInventoryPlayer, fakeCraftingInventory, slotID, 1);
                } else if (isValidForCrafting(itemStack) && canPlayerCraft(fakeInventoryPlayer, fakeCraftingInventory, itemStack.copy(), i+1, no)) {
                    CraftingHandler.decrStackSize(fakeInventoryPlayer, fakeCraftingInventory, CraftingHandler.getFirstInventorySlotWithItemStack(fakeInventoryPlayer, fakeCraftingInventory, itemStack), 1);
                } else return false;
            }
            return CraftingHandler.addItemStackPlayer(fakeInventoryPlayer, fakeCraftingInventory, theRecipe.getRecipeOutput().copy());
        } else return false;
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

    private static boolean compareStacks(ItemStack s1, ItemStack s2){
        if(s1.getItem() == s2.getItem()) {
            if(s1.getItemDamage() == s2.getItemDamage() || s2.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                return true;
            }
            if(!s1.getItem().getHasSubtypes() && !s2.getItem().getHasSubtypes()) {
                return true;
            }
        }
        return false;
    }

    public void updateVisibleSlots(float f) {
        ScrollValue = f;
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
        if (this.busy)
            return null;
        this.busy = true;
        if(slotIndex >= 0 && inventorySlots.get(slotIndex) != null && inventorySlots.get(slotIndex) instanceof SlotCrafter) {

            // Check if the currently held itemstack is different to the clicked itemstack.
            //if(!ItemHelper.areItemsEqual(inventory.getStackInSlot(slotIndex), entityplayer.inventory.getItemStack()))
                //return null;

            // Ignore right click.
            if(mouseButton == Constants.Mouse.MOUSE_RIGHT) {
                updateRecipes();
                return null;
            } else if(KeyHelper.isShiftDown()) {
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
        this.busy = true;
        populateSlotsWithRecipes();
        //updateVisibleSlots(ScrollValue);
        this.busy = false;
    }

    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
        return transferStackInSlot(p_82846_2_);
    }

    public boolean onRequestSingleRecipeOutput(SlotCrafter slot) {
        IRecipe irecipe = slot.getIRecipe();
        return irecipe != null && canPlayerCraft(thePlayer, theTile, irecipe.getRecipeOutput(), true); //onRequestSingleRecipeOutput(thePlayer.inventory, irecipe, theTile, true);
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
        ItemStack[] ingredients = CraftingHandler.getRecipeIngredients(recipe, inventoryPlayer);
        for (ItemStack itemStack : ingredients) {
            CraftingHandler.decrStackSize(inventoryPlayer, internalInventory, CraftingHandler.getFirstInventorySlotWithItemStack(inventoryPlayer, internalInventory, itemStack), 1);
        }
        InventoryCrafting craftingMatrix = CraftingHandler.generateCraftingMatrix(ingredients);
        CraftingHandler.handleCraftingMatrix(craftingMatrix, inventoryPlayer);
        if (!CraftingHandler.addItemStackPlayer(inventoryPlayer, internalInventory, recipe.getRecipeOutput().copy())) {
            PlayerHelper.addPersonalMessageToClient("Something went wrong while trying to process your crafting request");
            throw new RuntimeException("EY!");
        }
    }

    private void onRequestMaximumRecipeOutput(SlotCrafter slot) {
        IRecipe irecipe = slot.getIRecipe();
        if(irecipe == null)
            return;
        onRequestMaximumRecipeOutput(thePlayer, irecipe, theTile, slot.getIRecipe());
    }

    public static void onRequestMaximumRecipeOutput(EntityPlayer thePlayer, IRecipe irecipe, TECraftingTableIV Internal, IRecipe RecipeIndex) {
        InventoryPlayer Temp = new InventoryPlayer( thePlayer );
        Temp.copyInventory(thePlayer.inventory);

        InventoryPlayer inventoryPlayer = thePlayer.inventory;
        int GoTo = 64;
        if (irecipe.getRecipeOutput().getMaxStackSize() > 1) {
            GoTo = irecipe.getRecipeOutput().getMaxStackSize() / irecipe.getRecipeOutput().stackSize ;
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
}
