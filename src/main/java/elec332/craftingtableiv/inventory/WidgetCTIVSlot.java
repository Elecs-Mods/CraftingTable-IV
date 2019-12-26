package elec332.craftingtableiv.inventory;

import elec332.core.inventory.widget.slot.WidgetSlot;
import elec332.core.inventory.window.Window;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    @OnlyIn(Dist.CLIENT)
    public void draw(Window window, int guiX, int guiY, double mouseX, double mouseY) {
    }

}
