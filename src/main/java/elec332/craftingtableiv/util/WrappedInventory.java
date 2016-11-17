package elec332.craftingtableiv.util;

import elec332.core.api.inventory.IDefaultInventory;
import elec332.core.util.InventoryHelper;
import elec332.core.util.ItemStackHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-9-2015.
 */
public class WrappedInventory<I extends IInventory> implements IDefaultInventory {

    public static <I extends IInventory> WrappedInventory<I> of(I inventory){
        if (inventory == null)
            throw new IllegalArgumentException();
        return new WrappedInventory<I>(inventory);
    }

    private WrappedInventory(I inventory){
        this.inventory = inventory;
    }

    private I inventory;

    @Nonnull
    @Override
    public I getInventory(){
        return this.inventory;
    }

    public boolean addItemToInventory(ItemStack stack) {
        return InventoryHelper.addItemToInventory(this.inventory, stack);
    }

    public int getFirstSlotWithItemStackNoNBT(ItemStack stack) {
        return InventoryHelper.getFirstSlotWithItemStackNoNBT(this.inventory, stack);
    }

    public void copyContentsFrom(WrappedInventory<I> inventory){
        copyContentsFrom(inventory.getInventory());
    }

    public void copyContentsFrom(I otherInventory){
        for (int i = 0; i < otherInventory.getSizeInventory(); i++) {
            setInventorySlotContents(i, ItemStackHelper.copyItemStack(otherInventory.getStackInSlot(i)));
        }
    }

    public ItemStack[] getCopyOfContents(){
        ItemStack[] ret = new ItemStack[getSizeInventory()];
        for (int i = 0; i < getSizeInventory(); i++) {
            ret[i] = ItemStackHelper.copyItemStack(getStackInSlot(i));
        }
        return ret;
    }

    public void setContents(ItemStack[] contents){
        if (contents.length != getSizeInventory())
            throw new IllegalArgumentException();
        for (int i = 0; i < contents.length; i++) {
            setInventorySlotContents(i, ItemStackHelper.copyItemStack(contents[i]));
        }
    }

}
