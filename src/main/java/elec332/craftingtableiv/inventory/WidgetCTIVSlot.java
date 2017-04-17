package elec332.craftingtableiv.inventory;

import elec332.core.inventory.widget.slot.WidgetSlot;
import elec332.core.inventory.window.Window;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

/**
 * Created by Elec332 on 1-1-2017.
 */
public class WidgetCTIVSlot extends WidgetSlot {

    public WidgetCTIVSlot(IItemHandler inventory, int index, int x, int y, WindowCraftingTableIV listener) {
        super(inventory, index, x, y);
        this.listener = listener;
    }

    private final WindowCraftingTableIV listener;

    @Override
    public void onSlotChanged() {
        listener.onSlotChanged();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(Window window, int guiX, int guiY, int mouseX, int mouseY) {
    }

}
