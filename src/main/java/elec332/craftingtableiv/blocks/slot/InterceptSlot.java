package elec332.craftingtableiv.blocks.slot;

import elec332.craftingtableiv.blocks.container.CraftingTableIVContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class InterceptSlot extends Slot {
    public InterceptSlot(IInventory par1iInventory, int par2, int par3, int par4, CraftingTableIVContainer cont) {
        super(par1iInventory, par2, par3, par4);
    }
    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return false;
    }

}