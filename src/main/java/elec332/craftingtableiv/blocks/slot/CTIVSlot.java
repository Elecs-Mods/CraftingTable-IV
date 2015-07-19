package elec332.craftingtableiv.blocks.slot;

import elec332.craftingtableiv.blocks.container.CraftingTableIVContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Created by Elec332 on 6-7-2015.
 */
public class CTIVSlot extends Slot {

    public CTIVSlot(IInventory inventory, int i1, int i2, int i3, CraftingTableIVContainer container) {
        super(inventory, i1, i2, i3);
        this.container = container;
    }

    private final CraftingTableIVContainer container;

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        container.onSlotChanged();
    }
}
