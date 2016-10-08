package elec332.craftingtableiv.util;

import elec332.core.util.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

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
    public ItemStack removeStackFromSlot(int slot) {
        return inventory.removeStackFromSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inventory.hasCustomName();
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
    public void openInventory(EntityPlayer player) {
        inventory.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        inventory.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack Stack) {
        return inventory.isItemValidForSlot(slot, Stack);
    }

    @Override
    public ITextComponent getDisplayName() {
        return inventory.getDisplayName();
    }

    @Override
    public int getField(int id) {
        return inventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inventory.getFieldCount();
    }

    @Override
    public void clear() {
        inventory.clear();
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
