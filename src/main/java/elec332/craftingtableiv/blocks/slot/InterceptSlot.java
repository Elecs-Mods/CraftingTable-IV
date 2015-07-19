package elec332.craftingtableiv.blocks.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class InterceptSlot extends Slot {

    public InterceptSlot(IInventory inventory, int i1, int i2, int i3) {
        super(inventory, i1, i2, i3);
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
    }

}