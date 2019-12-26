package elec332.craftingtableiv.util;

import elec332.core.util.InventoryHelper;
import elec332.core.util.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-9-2015.
 */
public class WrappedItemHandler<I extends IItemHandlerModifiable> implements IItemHandlerModifiable {

    public static <I extends IItemHandlerModifiable> WrappedItemHandler<I> of(I inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException();
        }
        return new WrappedItemHandler<I>(inventory);
    }

    private WrappedItemHandler(I inventory) {
        this.inventory = inventory;
    }

    private final I inventory;

    @Nonnull
    public I getInventory() {
        return this.inventory;
    }

    public boolean addItemToInventory(ItemStack stack, boolean simulate) {
        return InventoryHelper.addItemToInventory(this.inventory, stack, simulate);
    }

    public void copyContentsFrom(WrappedItemHandler<I> inventory) {
        copyContentsFrom(inventory.getInventory());
    }

    public void copyContentsFrom(I otherInventory) {
        for (int i = 0; i < otherInventory.getSlots(); i++) {
            setStackInSlot(i, ItemStackHelper.copyItemStack(otherInventory.getStackInSlot(i)));
        }
    }

    public ItemStack[] getCopyOfContents() {
        ItemStack[] ret = new ItemStack[getSlots()];
        for (int i = 0; i < getSlots(); i++) {
            ret[i] = ItemStackHelper.copyItemStack(getStackInSlot(i));
        }
        return ret;
    }

    public void setContents(ItemStack[] contents) {
        if (contents.length != getSlots()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < contents.length; i++) {
            setStackInSlot(i, ItemStackHelper.copyItemStack(contents[i]));
        }
    }

    @Override
    public int getSlots() {
        return inventory.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return inventory.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return inventory.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory.getSlotLimit(slot);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        inventory.setStackInSlot(slot, stack);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return inventory.isItemValid(slot, stack);
    }

}
