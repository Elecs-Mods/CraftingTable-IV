package elec332.craftingtableiv.util;

import elec332.core.player.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 16-9-2015.
 */
public class WrappedInventory<I extends IInventory> implements IInventory{

    public static <I extends IInventory> WrappedInventory<I> of(I inventory){
        if (inventory == null)
            throw new IllegalArgumentException();
        return new WrappedInventory<I>(inventory);
    }

    private WrappedInventory(I inventory){
        this.inventory = inventory;
    }

    private I inventory;

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return inventory.decrStackSize(slot, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return inventory.getStackInSlotOnClosing(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getInventoryName() {
        return inventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return inventory.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
        inventory.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {
        inventory.openInventory();
    }

    @Override
    public void closeInventory() {
        inventory.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack Stack) {
        return inventory.isItemValidForSlot(slot, Stack);
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
            setInventorySlotContents(i, ItemStack.copyItemStack(otherInventory.getStackInSlot(i)));
        }
    }

    public ItemStack[] getCopyOfContents(){
        ItemStack[] ret = new ItemStack[getSizeInventory()];
        for (int i = 0; i < getSizeInventory(); i++) {
            ret[i] = ItemStack.copyItemStack(getStackInSlot(i));
        }
        return ret;
    }

    public void setContents(ItemStack[] contents){
        if (contents.length != getSizeInventory())
            throw new IllegalArgumentException();
        for (int i = 0; i < contents.length; i++) {
            setInventorySlotContents(i, ItemStack.copyItemStack(contents[i]));
        }
    }

    public I getInventory(){
        return this.inventory;
    }
}
