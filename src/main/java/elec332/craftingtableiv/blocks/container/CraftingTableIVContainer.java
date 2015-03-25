package elec332.craftingtableiv.blocks.container;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapedRecipeHandler;
import codechicken.nei.recipe.ShapelessRecipeHandler;
import elec332.core.client.KeyHelper;
import elec332.core.main.ElecCore;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.blocks.inv.InventoryCraftingTableIV;
import elec332.craftingtableiv.blocks.slot.InterceptSlot;
import elec332.craftingtableiv.blocks.slot.SlotCrafter;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.handler.ItemDetail;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class CraftingTableIVContainer extends Container {

    public InventoryBasic inventory = new InventoryBasic("tmp",true , 8*5);
    public InventoryBasic recipeItems = new InventoryBasic("tmp2",true, 9);

    public InventoryCrafting craftMatrix;
    public InventoryCraftingTableIV craftableRecipes;
    private List recipeList;
    private World worldObj;
    private EntityPlayer thePlayer;
    private Timer timer;
    public int MaxLevel = 3; //4 Runs
    public TECraftingTableIV theTile;
    public float ScrollValue = 0.0F;

    public CraftingTableIVContainer(EntityPlayer aPlayer, TECraftingTableIV tile)
    {
        worldObj = tile.getWorldObj();
        int RecipeType = 0;
        theTile = tile;
        thePlayer = aPlayer;
        craftMatrix = new InventoryCrafting(this, 3, 3);
        craftableRecipes = new InventoryCraftingTableIV(1000);
        recipeList = Collections.unmodifiableList(CraftingManager.getInstance().getRecipeList());

        for(int l2 = 0; l2 < 5; l2++)
        {
            for(int j3 = 0; j3 < 8; j3++)
            {
                addSlotToContainer(new SlotCrafter(thePlayer, inventory, craftMatrix, j3 + l2 * 8, 8 + j3 * 18, 18 + l2 * 18, this));
            }
        }
        for(int a = 0; a < 2; a++)
        {
            for(int i = 0; i < 9; i++)
            {
                addSlotToContainer(new Slot(theTile, i + (a * 9), 8 + i * 18, 112 + (18 * a)));
            }
        }

        for(int j = 0; j < 3; j++)
        {
            for(int i1 = 0; i1 < 9; i1++)
            {
                addSlotToContainer(new Slot(thePlayer.inventory, i1 + j * 9 + 9, 8 + i1 * 18, 152 + j * 18));
            }
        }

        for(int i3 = 0; i3 < 9; i3++)
        {
            addSlotToContainer(new Slot(thePlayer.inventory, i3, 8 + i3 * 18, 211));
        }


        if (RecipeType == 0)
        {
            addSlotToContainer(new InterceptSlot(recipeItems, 0, -18, 34, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 1, -18, 52, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 2, -18, 70, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 3, -18, 88, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 4, -18, 106, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 5, -18, 124, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 6, -18, 142, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 7, -18, 160, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 8, -18, 178, this));

        } else if (RecipeType == 1)
        {
            addSlotToContainer(new InterceptSlot(recipeItems, 2, -18, 34, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 8, -18, 70, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 1, -36, 34, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 4, -36, 52, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 7, -36, 70, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 0, -54, 34, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 3, -54, 52, this));
            addSlotToContainer(new InterceptSlot(recipeItems, 6, -54, 70, this));
        }





        if(ElecCore.proxy.isClient()) {
            timer = new Timer();
            //timer.schedule(new RemindTask(), mod_CraftingTableIII.SyncWaitTime);
            populateSlotsWithRecipes();
        }

    }

    class RemindTask extends TimerTask {
        public void run() {
            slotClick(-999, 5, 5, null); //Throw a bad click to force it to bring items up
        }
    }



    //public IInventory getInventory()
    {
        //return inventory;
    }
    public void populateSlotsWithRecipes()
    {
        CraftingHandler.InitRecipes();
        if (ElecCore.proxy.isClient()) {
            long StartTime = new Date().getTime();
            craftableRecipes.clearRecipes();
            recipeList = Collections.unmodifiableList(recipeList);
            InventoryPlayer Temp = new InventoryPlayer( thePlayer );

            for(int i = 0; i < CraftingHandler.ValidOutput.size(); i++) { // Zeldo.ValidOutput.size()
                Temp.copyInventory(thePlayer.inventory);
                //System.out.println("RecipeCheck: " + i + "/" + Zeldo.ValidOutput.size() + " - " + Zeldo.ValidOutput.get(i).ItemID + "@" + Zeldo.ValidOutput.get(i).ItemDamage);
                if ((Boolean)CraftingHandler.canPlayerCraft(Temp, (ItemDetail)CraftingHandler.ValidOutput.get(i), theTile, i)[0])
                {
                    craftableRecipes.addRecipe(((ItemDetail)CraftingHandler.ValidOutput.get(i)).iRecipe, i);
                }
            }

            //if (mod_CraftingTableIII.ShowTimings)
            //    System.out.println("Calculation Time: " + (new Date().getTime() - StartTime));
        }
    }




    // Check InventorPlayer contains the ItemStack.
    private int getFirstInventoryPlayerSlotWithItemStack(InventoryPlayer inventory, ItemStack itemstack)
    {
        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemstack1 = inventory.getStackInSlot(i);
            if(itemstack1 != null
                    && itemstack1.getItem() == itemstack.getItem()
                    && (itemstack1.getItemDamage() == itemstack.getItemDamage() || itemstack.getItemDamage() == -1)) {
                return i;
            }
        }

        return -1;
    }
    public static int getRecipeIngredients(ItemDetail theItem)
    {
        return getRecipeIngredients(theItem, 0);
    }
    // Get a list of ingredient required to craft the recipe item.
    public static int getRecipeIngredients(ItemDetail theItem, int offset)
    {
        if (CraftingHandler.ValidOutput.size() <= offset)
            return -1;
        for (int i=offset; i<CraftingHandler.ValidOutput.size(); i++)
            if (CraftingHandler.ValidOutput.get(i) != null && theItem != null)
                if (CraftingHandler.ValidOutput.get(i).equals(theItem))
                    return i;

        return -1;
    }

    @SuppressWarnings("unchecked")
    public static ItemStack[] getRecipeIngredientsOLD(IRecipe irecipe)
    {
        /*try {
            if (irecipe == null)
                return null;
            if(irecipe instanceof ShapedRecipes) {
                //return (ItemStack[])ModLoader.getPrivateValue(ShapedRecipes.class, (ShapedRecipes)irecipe, 2);
            } else if(irecipe instanceof ShapelessRecipes) {
                //if (irecipe.getRecipeOutput().getItem().getUnlocalizedName() != null)
                //{
                    //if (irecipe.getRecipeOutput().getItem().getItemName().equalsIgnoreCase("tile.rpwire"))
                    //{
                        //return null;
                    //}
                //}
                //ArrayList recipeItems = new ArrayList((List)ModLoader.getPrivateValue(ShapelessRecipes.class, (ShapelessRecipes)irecipe, 1));
                ArrayList recipeItems = new ArrayList(((ShapelessRecipes) irecipe).recipeItems);
                return (ItemStack[])recipeItems.toArray(new ItemStack[recipeItems.size()]);
            } else {
                /*String className = irecipe.getClass().getName();
                if(className.equals("ic2.common.AdvRecipe")) {
                    //return (ItemStack[]) ModLoader.getPrivateValue((Class)irecipe.getClass(), (Object)irecipe, "input");
                } else if(className.equals("ic2.common.AdvShapelessRecipe")) {
                    //return (ItemStack[]) ModLoader.getPrivateValue((Class)irecipe.getClass(), (Object)irecipe, "input");
                } else {
                    if (mod_CraftingTableIII.ShowTimings)
                        System.out.println("Invalid Recipe Class: " + className);
                    return null;
                }
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }*/
        if (irecipe == null)
            return null;
        else if (irecipe instanceof ShapelessRecipes) {
            ArrayList recipeItems = new ArrayList(((ShapelessRecipes) irecipe).recipeItems);
            return (ItemStack[]) recipeItems.toArray(new ItemStack[recipeItems.size()]);
        } else if(irecipe instanceof ShapedRecipes) {
            return ((ShapedRecipes) irecipe).recipeItems;
        } else if (irecipe instanceof ShapedOreRecipe){
            ArrayList<PositionedStack> t = new ShapedRecipeHandler().forgeShapedRecipe((ShapedOreRecipe) irecipe).ingredients;
            ArrayList<ItemStack> q = new ArrayList<ItemStack>();
            for (PositionedStack positionedStack : t){
                q.add(positionedStack.item);
            }
            return q.toArray(new ItemStack[q.size()]);
        } else if (irecipe instanceof ShapelessOreRecipe) {
            ArrayList<PositionedStack> t = new ShapelessRecipeHandler().forgeShapelessRecipe((ShapelessOreRecipe) irecipe).ingredients;
            ArrayList<ItemStack> q = new ArrayList<ItemStack>();
            for (PositionedStack positionedStack : t){
                q.add(positionedStack.item);
            }
            return q.toArray(new ItemStack[q.size()]);
        } else if (irecipe instanceof RecipesArmorDyes || irecipe instanceof RecipeFireworks || irecipe instanceof RecipeBookCloning || irecipe instanceof RecipesMapCloning){
            return null;
        }
        else {
            if (irecipe.getRecipeOutput() != null)
                CraftingTableIV.instance.error("ERROR FINDING RECIPE CLASS FOR: " + irecipe.getRecipeOutput().getItem().getUnlocalizedName());
            else CraftingTableIV.instance.error("ERROR: THE OUTPUT FOR THIS RECIPE IS NULL! " + irecipe.toString());
            return null;
        }
    }

    public void updateVisibleSlots(float f)
    {
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
                        inventory.setInventorySlotContents(l + k * 8, recipeOutput);
                        if(slot instanceof SlotCrafter) {
                            ((SlotCrafter)slot).setIRecipe( craftableRecipes.getIRecipe(i1),  craftableRecipes.getListIndex(i1));
                        }
                    } else {
                        inventory.setInventorySlotContents(l + k * 8, null);
                        if(slot instanceof SlotCrafter) {
                            ((SlotCrafter)slot).setIRecipe(null, -1);
                        }
                    }
                } else {
                    inventory.setInventorySlotContents(l + k * 8, null);
                    if(slot instanceof SlotCrafter) {
                        ((SlotCrafter)slot).setIRecipe(null, -1);
                    }
                }
            }
        }
    }
    public ItemStack transferStackInSlot(int par1)
    {
        ItemStack var2 = null;
        Slot var3 = (Slot)this.inventorySlots.get(par1);

        if (var3 != null && var3.getHasStack())
        {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();

            if (par1 < 58 && par1 > 39)
            {
                if (!this.mergeItemStack(var4, 58, 94, true))
                {
                    return null;
                }
            }
            else if (par1 > 57)
                if (!this.mergeItemStack(var4, 40, 58, false))
                {
                    return null;
                }

            if (var4.stackSize == 0)
            {
                var3.putStack((ItemStack)null);
            }
            else
            {
                var3.onSlotChanged();
            }
        }

        return var2;
    }

    @Override
    public ItemStack slotClick(int slotIndex, int mouseButton, int flag, EntityPlayer entityplayer)
    {
        if(slotIndex != -999
                && inventorySlots.size() > slotIndex
                && slotIndex >= 0
                && inventorySlots.get(slotIndex) != null
                && inventorySlots.get(slotIndex) instanceof SlotCrafter) {

            // Check if the currently held itemstack is different to the clicked itemstack.
            ItemStack itemstack = inventory.getStackInSlot(slotIndex);
            ItemStack playerItemStack = entityplayer.inventory.getItemStack();
            boolean currentItemStackIsDifferent = false;
            if(playerItemStack != null && itemstack != null) {
                if(playerItemStack.getItem() == itemstack.getItem()
                        && (itemstack.getItemDamage() == -1 || itemstack.getItemDamage() == playerItemStack.getItemDamage())) {
                    currentItemStackIsDifferent = false;
                } else {
                    currentItemStackIsDifferent = true;
                }
            }

            if(currentItemStackIsDifferent)
                return null;

            // Ignore right click.
            if(mouseButton == 1) {
                return null;
            } else if(KeyHelper.isShiftDown()) {
                onRequestMaximumRecipeOutput( (SlotCrafter)inventorySlots.get(slotIndex) );
                populateSlotsWithRecipes();
                updateVisibleSlots(ScrollValue);
                return null;
            } else {
                if( !onRequestSingleRecipeOutput( (SlotCrafter)inventorySlots.get(slotIndex) ) )
                    populateSlotsWithRecipes();
                updateVisibleSlots(ScrollValue);
                return null;
            }
        }

        if(KeyHelper.isShiftDown()) {
            transferStackInSlot(slotIndex);
            populateSlotsWithRecipes();
            updateVisibleSlots(ScrollValue);
            return null;
        } else {
            ItemStack itemstack = super.slotClick(slotIndex, mouseButton, flag, entityplayer);
            populateSlotsWithRecipes();
            updateVisibleSlots(ScrollValue);
            return itemstack;
        }
    }
    public boolean onRequestSingleRecipeOutput( SlotCrafter slot )
    {
        IRecipe irecipe = slot.getIRecipe();
        if(irecipe == null)
            return false;

        return onRequestSingleRecipeOutput(thePlayer, irecipe, theTile, slot.myIndex);
    }

    public static boolean onRequestSingleRecipeOutput(EntityPlayer thePlayer, IRecipe irecipe, TECraftingTableIV Internal, int RecipeIndex)
    {

        ItemStack recipeOutputStack = irecipe.getRecipeOutput().copy();

        if (ElecCore.proxy.isClient())
        {
            //mod_CraftingTableIII.getInstance().SendCraftingPacket(irecipe.getRecipeOutput().copy(), false, Internal.xCoord, Internal.yCoord, Internal.zCoord, RecipeIndex);
        }

        InventoryPlayer Temp = new InventoryPlayer( thePlayer );
        Temp.copyInventory(thePlayer.inventory);

        InventoryPlayer inventoryPlayer = thePlayer.inventory;
        Object[] iTemp = CraftingHandler.canPlayerCraft(inventoryPlayer, Internal, new ItemDetail(irecipe.getRecipeOutput()), 0, true, null, null, RecipeIndex);
        Internal.theInventory = ((TECraftingTableIV)iTemp[3]).theInventory;
        thePlayer.inventory.copyInventory((InventoryPlayer) iTemp[1]) ;

        //onCraftMatrixChanged(recipeOutputStack);
        return false;
    }
    private void onRequestMaximumRecipeOutput( SlotCrafter slot )
    {
        IRecipe irecipe = slot.getIRecipe();
        if(irecipe == null)
            return;

        onRequestMaximumRecipeOutput(thePlayer, irecipe, theTile, slot.myIndex);
    }
    public static void onRequestMaximumRecipeOutput(EntityPlayer thePlayer, IRecipe irecipe, TECraftingTableIV Internal, int RecipeIndex)
    {


        ItemStack recipeOutputStack = irecipe.getRecipeOutput().copy();
        //addFavouriteRecipe(irecipe);

        if (ElecCore.proxy.isClient())
        {
            //mod_CraftingTableIII.getInstance().SendCraftingPacket(irecipe.getRecipeOutput().copy(), true, Internal.xCoord, Internal.yCoord, Internal.zCoord, RecipeIndex);
        }

        InventoryPlayer Temp = new InventoryPlayer( thePlayer );
        Temp.copyInventory(thePlayer.inventory);

        InventoryPlayer inventoryPlayer = thePlayer.inventory;
        int GoTo = 64;
        if (irecipe.getRecipeOutput().getMaxStackSize() > 1) {
            GoTo = irecipe.getRecipeOutput().getMaxStackSize() / irecipe.getRecipeOutput().stackSize ;
        }
        for (int i=0; i<GoTo; i++)
        {
            Temp.copyInventory(thePlayer.inventory);
            if ((Boolean)CraftingHandler.canPlayerCraft(Temp, new ItemDetail(irecipe.getRecipeOutput()), Internal, RecipeIndex)[0])
            {
                Object[] iTemp = CraftingHandler.canPlayerCraft(inventoryPlayer, Internal, new ItemDetail(irecipe.getRecipeOutput()), 0, true, null, null, RecipeIndex);
                Internal.theInventory = ((TECraftingTableIV)iTemp[3]).theInventory;
                thePlayer.inventory.copyInventory((InventoryPlayer) iTemp[1]) ;
            } else {
                break;
            }
        }

        //onCraftMatrixChanged(recipeOutputStack);
    }

    private void onCraftMatrixChanged(ItemStack recipeOutputStack)
    {
        InventoryPlayer inventoryPlayer = thePlayer.inventory;
        // Call custom hooks.
        //ModLoader.takenFromCrafting(thePlayer, recipeOutputStack, craftMatrix);
        //ForgeHooks.onTakenFromCrafting(thePlayer, recipeOutputStack, craftMatrix);
        // Remove items from the craftMatrix and replace container items.
        for(int i = 0; i < craftMatrix.getSizeInventory(); i++)
        {
            ItemStack itemstack1 = craftMatrix.getStackInSlot(i);
            if(itemstack1 != null)
            {
                craftMatrix.decrStackSize(i, 1);
                if(itemstack1.getItem().hasContainerItem())
                {
                    craftMatrix.setInventorySlotContents(i, new ItemStack(itemstack1.getItem().getContainerItem()));
                }
            }
        }
        // Transfer any remaining items in the craft matrix to the player.
        for(int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            ItemStack itemstack = craftMatrix.getStackInSlot(i);
            if(itemstack != null) {
                inventoryPlayer.addItemStackToInventory(itemstack);
                craftMatrix.setInventorySlotContents(i, null);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }
    public boolean isUsableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    public boolean UpdateInventory()
    {
        for (int var1 = 0; var1 < this.inventorySlots.size(); ++var1)
        {
            ItemStack var2 = ((Slot)this.inventorySlots.get(var1)).getStack();
            ItemStack var3 = (ItemStack)this.inventoryItemStacks.get(var1);

            if (!ItemStack.areItemStacksEqual(var3, var2))
            {
                var3 = var2 == null ? null : var2.copy();
                this.inventoryItemStacks.set(var1, var3);

                for (int var4 = 0; var4 < this.crafters.size(); ++var4)
                {
                    //((ICrafting)this.crafters.get(var4)).sendProgressBarUpdate(this, var1, var3);  //.updateCraftingInventorySlot(this, var1, var3);
                }
            }
        }
        return true;
    }
    public void StartTimer() {
        //timer = new Timer();
        //timer.schedule(new RemindTask(), mod_CraftingTableIII.SyncWaitTime);
    }
}
