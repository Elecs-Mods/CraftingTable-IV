package elec332.craftingtableiv.blocks.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class ContainerNull extends Container {

    private static InventoryBasic inventory = new InventoryBasic("tmp", true, 8*5);


    public ContainerNull()
    {
    }




    //static InventoryBasic getInventory()
    {
        //return inventory;
    }


    public void updateVisibleSlots(float f)
    {
    }

    public ItemStack slotClick(int slotIndex, int mouseButton, boolean shiftIsDown, EntityPlayer entityplayer)
    {
        return null;
    }



    private void onCraftMatrixChanged(ItemStack recipeOutputStack)
    {
    }


    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }

}