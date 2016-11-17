package elec332.craftingtableiv.blocks.container;

import elec332.core.util.ItemStackHelper;
import elec332.craftingtableiv.blocks.slot.CTIVSlot;
import elec332.craftingtableiv.blocks.slot.InterceptSlot;
import elec332.craftingtableiv.blocks.slot.SlotCrafter;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 23-3-2015.
 */

public class CraftingTableIVContainer extends Container {

    public InventoryBasic inventory = new InventoryBasic("tmp",true , 8*5);
    public InventoryBasic recipeItems = new InventoryBasic("tmp2",true, 9);
    public EntityPlayer thePlayer;
    public TileEntityCraftingTableIV theTile;
    private ISlotChangeableGUI slotChangeableGUI;

    public CraftingTableIVContainer(EntityPlayer aPlayer, TileEntityCraftingTableIV tile) {
        theTile = tile;
        thePlayer = aPlayer;
        for(int l2 = 0; l2 < 5; l2++) {
            for(int j3 = 0; j3 < 8; j3++) {
                addSlotToContainer(new SlotCrafter(inventory,  j3 + l2 * 8, 8 + j3 * 18, 18 + l2 * 18, this));
            }
        }

        for(int a = 0; a < 2; a++) {
            for(int i = 0; i < 9; i++) {
                addSlotToContainer(new CTIVSlot(theTile, i + (a * 9), 8 + i * 18, 112 + (18 * a), this));
            }
        }

        for(int j = 0; j < 3; j++) {
            for(int i1 = 0; i1 < 9; i1++) {
                addSlotToContainer(new CTIVSlot(thePlayer.inventory, i1 + j * 9 + 9, 8 + i1 * 18, 152 + j * 18, this));
            }
        }

        for(int i3 = 0; i3 < 9; i3++) {
            addSlotToContainer(new CTIVSlot(thePlayer.inventory, i3, 8 + i3 * 18, 211, this));
        }

        addSlotToContainer(new InterceptSlot(recipeItems, 0, -18, 34));
        addSlotToContainer(new InterceptSlot(recipeItems, 1, -18, 52));
        addSlotToContainer(new InterceptSlot(recipeItems, 2, -18, 70));
        addSlotToContainer(new InterceptSlot(recipeItems, 3, -18, 88));
        addSlotToContainer(new InterceptSlot(recipeItems, 4, -18, 106));
        addSlotToContainer(new InterceptSlot(recipeItems, 5, -18, 124));
        addSlotToContainer(new InterceptSlot(recipeItems, 6, -18, 142));
        addSlotToContainer(new InterceptSlot(recipeItems, 7, -18, 160));
        addSlotToContainer(new InterceptSlot(recipeItems, 8, -18, 178));
    }

    public void setGui(ISlotChangeableGUI gui){
        this.slotChangeableGUI = gui;
    }

    public void onSlotChanged(){
        if (slotChangeableGUI != null) {
            slotChangeableGUI.onSlotChanged();
        }
    }

    @Override
    @Nonnull
    public ItemStack slotClick(int slotIndex, int dragType, ClickType clickType, EntityPlayer player) {
        if(slotIndex >= 0 && inventorySlots.get(slotIndex) != null && inventorySlots.get(slotIndex) instanceof SlotCrafter) {
            return ItemStackHelper.NULL_STACK;
        }
        return super.slotClick(slotIndex, dragType, clickType, player);
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        ItemStack stack = null;
        Slot slot = this.inventorySlots.get(i);
        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();

            if (i < 58 && i > 39) {
                if (!this.mergeItemStack(stackInSlot, 58, 94, true)) {
                    return ItemStackHelper.NULL_STACK;
                }
            } else if (i > 57) {
                if (!this.mergeItemStack(stackInSlot, 40, 58, false)) {
                    return ItemStackHelper.NULL_STACK;
                }
            }
            if (stackInSlot.stackSize == 0) {
                slot.putStack(ItemStackHelper.NULL_STACK);
            } else {
                slot.onSlotChanged();
            }
        }
        return ItemStackHelper.NULL_STACK;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer entityplayer) {
        return this.theTile.isUseableByPlayer(entityplayer);
    }

}
