package elec332.core.craftingtableiv.handler;

import elec332.core.craftingtableiv.blocks.container.ContainerNull;
import elec332.core.craftingtableiv.blocks.container.CraftingTableIVContainer;
import elec332.core.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class CraftingHandler {
    public static int MaxLevel = 20;
    public static ArrayList<ArrayList<ItemDetail>> ValidRecipes;
    public static ArrayList<ItemDetail> ValidOutput;
    public static boolean RecipesInit = false;
    public static IRecipe getCraftingRecipe(ItemStack item)
    {
        return getCraftingRecipe(item, new ArrayList());
    }
    public static IRecipe getCraftingRecipe(ItemStack item, ArrayList BlackList)
    {
        for(int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); i++) {
            ItemStack CurItem = ((IRecipe)CraftingManager.getInstance().getRecipeList().get(i)).getRecipeOutput();
            if (TestItems(CurItem,item))
            {
                return (IRecipe)CraftingManager.getInstance().getRecipeList().get(i);
            }
        }
        return null;
    }

    public static boolean TestItems(ItemStack i1, ItemStack i2)
    {
        return i1.getItem() == i2.getItem() && (i1.getItemDamage() == i2.getItemDamage() || i1.getItemDamage() == -1 || i2.getItemDamage() == -1);
    }

    public static Object[] canPlayerCraft(InventoryPlayer ThePlayer, ItemDetail TheItem, IInventory Internal, int ForcedIndex)
    {
        return canPlayerCraft(ThePlayer, ((TECraftingTableIV)Internal).getCopy(), TheItem, 0, false, null, null, ForcedIndex);
    }
    /*
     * Only time this is called with -1 is when we
     * are trying to make an item for a request recipe
     *
     * Otherwise ForcedIndex should always be a value!
     * If not dupe items will show on the table!
     */
    public static Object[] canPlayerCraft(InventoryPlayer ThePlayer, IInventory Internal, ItemDetail TheItem, int Level, boolean UpdateWorld, ItemDetail Item1, ItemDetail Item2, int ForcedIndex)
    {
        int SlotCount = 0;

        if (Level > MaxLevel)
            return new Object[] {false, ThePlayer, SlotCount, Internal};

        //Copys to prevent bugs
        InventoryPlayer ThePlayerBefore = new InventoryPlayer(ThePlayer.player);
        ThePlayerBefore.copyInventory(ThePlayer);
        TECraftingTableIV InternalBefore = ((TECraftingTableIV)Internal).getCopy();

        int recipeIndex = ForcedIndex;

        if (recipeIndex == -1) {
            recipeIndex = CraftingTableIVContainer.getRecipeIngredients(TheItem);
            if (recipeIndex == -1)
                return new Object[] {false, ThePlayer, SlotCount, Internal};
        }

        boolean playerHasAllItems = false;

        while (recipeIndex > -1)
        {
            boolean playerHasAllItemsForThis = true;
            ArrayList<ItemDetail> recipeIngredients = (ArrayList<ItemDetail>) CraftingHandler.ValidRecipes.get(recipeIndex);

            for (int i=0; i<recipeIngredients.size(); i++)
            {
                if (recipeIngredients.get(i) == null)
                    continue;
                if (recipeIngredients.get(i).equalsForceIgnore(Item1))
                    return new Object[] {false, ThePlayerBefore, SlotCount, InternalBefore};
                if (recipeIngredients.get(i).equalsForceIgnore(Item2))
                    return new Object[] {false, ThePlayerBefore, SlotCount, InternalBefore};

                int SlotIndex = getFirstInventoryPlayerSlotWithItemStack(ThePlayer, Internal, recipeIngredients.get(i).toItemStack());
                if (SlotIndex > -1)
                {
                    DecItemStackPlayer(ThePlayer, Internal, SlotIndex, recipeIngredients.get(i).StackSize, UpdateWorld); //ThePlayer.decrStackSize(SlotIndex, recipeIngredients.get(i).StackSize);
                } else {
                    Object[] Result = canPlayerCraft(ThePlayer, Internal, recipeIngredients.get(i), Level+1, UpdateWorld, recipeIngredients.get(i), TheItem, -1);
                    ThePlayer = (InventoryPlayer) Result[1];
                    Internal = (IInventory) Result[3];
                    if ((Boolean)Result[0] != true)
                    {
                        playerHasAllItemsForThis = false;
                        break;
                    }
                    SlotIndex = getFirstInventoryPlayerSlotWithItemStack(ThePlayer, Internal, recipeIngredients.get(i).toItemStack());
                    if(SlotIndex != -1) {
                        DecItemStackPlayer(ThePlayer, Internal, SlotIndex, recipeIngredients.get(i).StackSize, UpdateWorld); //ThePlayer.decrStackSize(SlotIndex, recipeIngredients.get(i).StackSize);
                    }

                }
            }

            if (playerHasAllItemsForThis == true)
            {
                playerHasAllItems = true;
                break;
            } else
            {
                //Reset the items to before trying this recipe
                ThePlayer.copyInventory(ThePlayerBefore);
                Internal = InternalBefore.getCopy();
            }
            if (ForcedIndex != -1)
                break;
            recipeIndex = CraftingTableIVContainer.getRecipeIngredients(TheItem, recipeIndex+1);
        }


        if (playerHasAllItems)
        {
            TheItem = CraftingHandler.ValidOutput.get(recipeIndex); //Fixes damage values and set the proper item stack size
            Object[] iTemp = AddItemStackPlayer(ThePlayer, Internal, TheItem.toItemStack(), UpdateWorld);
            if ((Boolean)iTemp[0] == false) //ThePlayer.addItemStackToInventory(TheItem.toItemStack());
            {
                return new Object[] {false, ThePlayerBefore, SlotCount, InternalBefore}; //Look into this effecting player in some recipes
            }
            Internal = (IInventory) iTemp[1];
            if (UpdateWorld){
                InventoryCrafting TempMatrix =GenCraftingMatrix(CraftingTableIVContainer.getRecipeIngredientsOLD(TheItem.iRecipe));
                TheItem.toItemStack().onCrafting(ThePlayer.player.worldObj, ThePlayer.player, 1);
                //ModLoader.takenFromCrafting(ThePlayer.player, TheItem.toItemStack(), TempMatrix);
                //ForgeHooks.onTakenFromCrafting(ThePlayer.player, TheItem.toItemStack(), TempMatrix);
                HandleCraftingMaxtrix(TempMatrix, ThePlayer);
            }
        }
        return new Object[] {playerHasAllItems, ThePlayer, SlotCount, Internal};
    }
    public static Object[] AddItemStackPlayer(InventoryPlayer a, IInventory Internal, ItemStack b, boolean Update)
    {
        //if (!Update || (Proxy.IsClient() && !Proxy.isMutiplayer()) || !Proxy.IsClient())
        //{
            TECraftingTableIV TheInternal = (TECraftingTableIV)Internal;
            if (TheInternal.addItemStackToInventory(b.copy()))
            {
                return new Object[] {true, TheInternal};
            } else {
                return new Object[] {a.addItemStackToInventory(b.copy()), TheInternal};
            }
        //}
        //return new Object[] {true, Internal};

    }
    public static void DecItemStackPlayer(InventoryPlayer a, IInventory Internal, int Slot, int Amount, boolean Update)
    {
        //if (!Update || (Proxy.IsClient() && !Proxy.isMutiplayer()) || !Proxy.IsClient())
        //{
            if (Slot < 18)
                Internal.decrStackSize(Slot, Amount);
            else
                a.decrStackSize(Slot-18, Amount);
        //}
    }
    public static void HandleCraftingMaxtrix(InventoryCrafting CraftingMatrix, InventoryPlayer thePlayer)
    {
        for (int i=0; i<CraftingMatrix.getSizeInventory(); i++)
        {
            ItemStack CurStack = CraftingMatrix.getStackInSlot(i);
            if (CurStack != null)
            {
                CraftingMatrix.decrStackSize(i, 1);

                if(CurStack.getItem().hasContainerItem())
                {
                    ItemStack item2 = new ItemStack(CurStack.getItem().getContainerItem());
                    CraftingMatrix.setInventorySlotContents(i, item2);
                }


            }
        }
        for(int i = 0; i <CraftingMatrix.getSizeInventory(); i++) {
            ItemStack itemstack = CraftingMatrix.getStackInSlot(i);
            if(itemstack != null) {
                thePlayer.addItemStackToInventory(itemstack.copy());
                CraftingMatrix.setInventorySlotContents(i, null);
            }
        }
    }
    public static InventoryCrafting GenCraftingMatrix(ItemStack[] Items)
    {
        InventoryCrafting Temp = new InventoryCrafting(new ContainerNull(), 3, 3);
        for (int i=0; i<Items.length; i++)
        {
            if (Items[i] != null) {
                Items[i].stackSize = 1;
                Temp.setInventorySlotContents(i, Items[i]);
            }
        }
        return Temp;
    }

    public static int getFirstInventoryPlayerSlotWithItemStack(InventoryPlayer inventory, IInventory Internal, ItemStack itemstack)
    {
        for(int i = 0; i < Internal.getSizeInventory()-1; i++) {
            ItemStack itemstack1 = Internal.getStackInSlot(i);
            if(itemstack1 != null && itemstack1.getItem() == itemstack.getItem()) {
                if (itemstack1.getItemDamage() == itemstack.getItemDamage() || itemstack.getItemDamage() == -1 || itemstack1.getItem().getHasSubtypes() == false)
                    return i;
            }
        }
        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemstack1 = inventory.getStackInSlot(i);
            if(itemstack1 != null && itemstack1.getItem() == itemstack.getItem()) {
                if (itemstack1.getItemDamage() == itemstack.getItemDamage() || itemstack.getItemDamage() == -1)
                    return i+18;
                if (itemstack1.getItem().getHasSubtypes() == false) //Damageable so ignore
                    return i+18;
            }
        }
        return -1;
    }
    public static void InitRecipes() {
        if (RecipesInit) return;
        ValidRecipes = new ArrayList();
        ValidOutput = new ArrayList();
        //Get a list of the recipes in my form
        for(int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); i++) {

            ItemStack[] recipeIngredients = CraftingTableIVContainer.getRecipeIngredientsOLD((IRecipe)CraftingManager.getInstance().getRecipeList().get(i));


            if (recipeIngredients != null) {
                ArrayList Temp = new ArrayList();
                for (int a=0; a<recipeIngredients.length; a++)
                {
                    if (recipeIngredients[a] == null)
                        Temp.add(null);
                    else
                    {
                        if (recipeIngredients[a].getItem() == Item.getItemFromBlock(Blocks.log) || recipeIngredients[a].getItem() == Item.getItemFromBlock(Blocks.log2))
                            Temp.add(new ItemDetail(recipeIngredients[a].getItem(), -1, 1, (IRecipe)CraftingManager.getInstance().getRecipeList().get(i)));
                        else if (recipeIngredients[a].getItem() == Item.getItemFromBlock(Blocks.planks))
                            Temp.add(new ItemDetail(recipeIngredients[a].getItem(), -1, 1, (IRecipe)CraftingManager.getInstance().getRecipeList().get(i)));
                        else
                            Temp.add(new ItemDetail(recipeIngredients[a].getItem(), recipeIngredients[a].getItemDamage(), 1, (IRecipe)CraftingManager.getInstance().getRecipeList().get(i)));

                    }
                }
                ValidRecipes.add(Temp);
                ItemStack Temp2 = ((IRecipe)CraftingManager.getInstance().getRecipeList().get(i)).getRecipeOutput();
                ValidOutput.add(new ItemDetail(Temp2.getItem(), Temp2.getItemDamage(), Temp2.stackSize, (IRecipe)CraftingManager.getInstance().getRecipeList().get(i), true));
            }
        }

    }
    public static int FindRecipe(ArrayList<ItemDetail> TheIngrediants, ItemDetail Output)
    {
        int RecipeIndex = CraftingTableIVContainer.getRecipeIngredients(Output);
        while (RecipeIndex > -1)
        {
            ArrayList<ItemDetail> recipeIngredients = (ArrayList<ItemDetail>) CraftingHandler.ValidRecipes.get(RecipeIndex);
            if (recipeIngredients.size() != TheIngrediants.size())
            {
                RecipeIndex = CraftingTableIVContainer.getRecipeIngredients(Output, RecipeIndex+1);
                continue; //Not the recipe...
            }
            boolean Valid = true;
            for (int i=0; i<recipeIngredients.size(); i++)
            {
                if (recipeIngredients.get(i) != null)
                    if (!recipeIngredients.get(i).equalsForceIgnore(TheIngrediants.get(i)))
                    {
                        Valid = false;
                        break;
                    }
            }
            if (Valid)
                return RecipeIndex;
            RecipeIndex = CraftingTableIVContainer.getRecipeIngredients(Output, RecipeIndex + 1);
        }
        return -1;
    }
}
