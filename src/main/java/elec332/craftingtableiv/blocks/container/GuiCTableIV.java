package elec332.craftingtableiv.blocks.container;

import com.google.common.collect.Lists;
import elec332.craftingtableiv.blocks.slot.SlotCrafter;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class GuiCTableIV extends GuiContainer {

    private float scroll;
    private boolean field_35313_h;
    private boolean field_35314_i;
    int RecipeType = 0;


    public GuiCTableIV(EntityPlayer entityplayer, TECraftingTableIV tile) {
        super(new CraftingTableIVContainer(entityplayer, tile));
        scroll = 0.0F;
        field_35313_h = false;
        allowUserInput = true;
        ySize = 234;
    }

    @Override
    protected void handleMouseClick(Slot slot, int i, int j, int flag) {
        if (slot != null)
            if (slot.slotNumber < 94)
                super.handleMouseClick(slot, i, j, flag);
        //inventorySlots.slotClick(i, j, flag, mc.thePlayer);
    }


    @Override
    public void drawScreen(int i, int j, float f) {
        boolean flag = Mouse.isButtonDown(0);
        int k = guiLeft;
        int l = guiTop;
        int i1 = k + 155;
        int j1 = l + 17;
        int k1 = i1 + 14;
        int l1 = j1 + 88 + 2;

        if(!field_35314_i && flag && i >= i1 && j >= j1 && i < k1 && j < l1) {
            field_35313_h = true;
        }
        if(!flag) {
            field_35313_h = false;
        }
        field_35314_i = flag;
        if(field_35313_h) {
            scroll = (float)(j - (j1 + 8)) / ((float)(l1 - j1) - 16F);
            if(scroll < 0.0F) {
                scroll = 0.0F;
            }
            if(scroll > 1.0F) {
                scroll = 1.0F;
            }
            ((CraftingTableIVContainer)inventorySlots).updateVisibleSlots(scroll);
        }

        for (int b=0; b<9; b++) {
            ((CraftingTableIVContainer)inventorySlots).recipeItems.setInventorySlotContents(b, null);
        }

        for (int a = 0; a < ((CraftingTableIVContainer)inventorySlots).inventory.getSizeInventory(); a++) {
            if (this.inventorySlots.inventorySlots.get(a) instanceof SlotCrafter) {
                SlotCrafter theSlot = (SlotCrafter) this.inventorySlots.inventorySlots.get(a);
                if (theSlot.getIRecipe() != null){
                theSlot.inventory.setInventorySlotContents(theSlot.getSlotIndex(), theSlot.getIRecipe().getRecipeOutput());
                if (getIsMouseOverSlot(theSlot, i, j)) {
                    try {
                        List<ItemStack> theRecipe = getIngredients(theSlot.getIRecipe());//Lists.newArrayList(CraftingHandler.getRecipeIngredients(theSlot.getIRecipe(), Minecraft.getMinecraft().thePlayer.inventory));
                        int Counter = 0;
                        for (int b = 0; b < theRecipe.size(); b++) {
                            if (RecipeType == 1) {
                                if (theRecipe.get(b) != null)
                                    ((CraftingTableIVContainer) inventorySlots).recipeItems.setInventorySlotContents(b, theRecipe.get(b));
                                else
                                    ((CraftingTableIVContainer) inventorySlots).recipeItems.setInventorySlotContents(b, null);
                            } else if (RecipeType == 0) {
                                if (theRecipe.get(b) != null) {
                                    ((CraftingTableIVContainer) inventorySlots).recipeItems.setInventorySlotContents(Counter, theRecipe.get(b));
                                    Counter++;
                                }
                            }
                        }
                    } catch (Throwable throwable){
                        throwable.printStackTrace();
                    }
                }
            }}

        }
        super.drawScreen(i, j, f);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2896 /*GL_LIGHTING*/);
    }

    private List<ItemStack> getIngredients(IRecipe recipe){
        List<ItemStack> ret = Lists.newArrayList();
        for (Object obj : CraftingHandler.getRecipeIngredients(recipe, null)){
            if (obj instanceof ItemStack)
                ret.add((ItemStack) obj);
            else if (obj instanceof List && !((List) obj).isEmpty())
                ret.add((ItemStack) ((List) obj).get(0));
        }
        return ret;
    }

    private boolean getIsMouseOverSlot(Slot slot, int i, int j) {
        int k = guiLeft;
        int l = guiTop;
        i -= k;
        j -= l;
        return i >= slot.xDisplayPosition - 1 && i < slot.xDisplayPosition + 16 + 1 && j >= slot.yDisplayPosition - 1 && j < slot.yDisplayPosition + 16 + 1;
    }

    protected void drawGuiContainerForegroundLayer(int i, int i1) {
        Minecraft.getMinecraft().fontRenderer.drawString("Crafting Table IV", 8, 6, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("craftingtableiv", "gui/crafttableii.png"));
        int l = guiLeft;
        int i1 = guiTop;
        //Background
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        //int j1 = l + 155;
        int k1 = i1 + 17;
        int l1 = k1 + 88 + 2;
        //Scrolly bar
        drawTexturedModalRect(l + 154, i1 + 17 + (int)((float)(l1 - k1 - 17) * scroll), 0, 240, 16, 16);
    }

    public void handleMouseInput() {
        int i = Mouse.getEventDWheel();
        CraftingTableIVContainer container = (CraftingTableIVContainer)inventorySlots;
        if(i != 0) {
            int j = (container.craftableRecipes.getSize() / 8 - 4) + 1;
            if(i > 0) {
                i = 1;
            }
            if(i < 0) {
                i = -1;
            }
            scroll -= (double)i / (double)j;
            if(scroll < 0.0F) {
                scroll = 0.0F;
            }
            if(scroll > 1.0F) {
                scroll = 1.0F;
            }
            container.updateVisibleSlots(scroll);
        }
        super.handleMouseInput();
    }

    public void resetScroll() {
        scroll = 0.0F;
        ((CraftingTableIVContainer)inventorySlots).updateVisibleSlots(scroll);
    }
}