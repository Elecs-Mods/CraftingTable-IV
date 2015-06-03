package elec332.craftingtableiv.blocks.container;

import elec332.craftingtableiv.blocks.slot.SlotCrafter;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class GuiCTableIV extends GuiContainer {

    private float scroll;
    private boolean field_35313_h;
    private boolean field_35314_i;
    int RecipeType = 0;
    private boolean shouldShowDescriptions;


    public GuiCTableIV(EntityPlayer entityplayer, TECraftingTableIV tile) {
        super(new CraftingTableIVContainer(entityplayer, tile));
        scroll = 0.0F;
        field_35313_h = false;
        allowUserInput = true;
        ySize = 234;
        //((CraftingTableIVContainer)inventorySlots).updateVisibleSlots(0.0F);
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
                //drawStuff(theSlot);
                //inventorySlots.putStackInSlot(theSlot.slotNumber, theSlot.getIRecipe().getRecipeOutput());
                /*if (theSlot.getIRecipe() != null && theSlot.getIRecipe().getRecipeOutput() != null)*/
                if (theSlot.getIRecipe() != null){
                theSlot.inventory.setInventorySlotContents(theSlot.getSlotIndex(), theSlot.getIRecipe().getRecipeOutput());
                //renderSlotResult(theSlot);*/
                if ( //theSlot.myIndex > -1 &&
                 getIsMouseOverSlot(theSlot, i, j)) {
                    try {


                    //ArrayList<ItemStack> theRecipe = CraftingHandler.ValidRecipes.get(theSlot.myIndex);
                        ArrayList<ItemStack> theRecipe = new ArrayList<ItemStack>();
                        for (ItemStack stack : CraftingHandler.getRecipeIngredients(theSlot.getIRecipe(), Minecraft.getMinecraft().thePlayer.inventory)){
                            theRecipe.add(stack);
                        }
                    int Counter = 0;
                    for (int b=0; b<theRecipe.size(); b++)
                    {
                        if (RecipeType == 1)
                        {
                            if (theRecipe.get(b) != null)
                                ((CraftingTableIVContainer)inventorySlots).recipeItems.setInventorySlotContents(b, theRecipe.get(b));
                            else
                                ((CraftingTableIVContainer)inventorySlots).recipeItems.setInventorySlotContents(b, null);
                        } else if (RecipeType == 0)
                        {
                            if (theRecipe.get(b) != null)
                            {
                                ((CraftingTableIVContainer)inventorySlots).recipeItems.setInventorySlotContents(Counter, theRecipe.get(b));
                                Counter++;
                            }
                        }
                    }
                    //break; // Exit the loop, no need to waste cycles checking the rest
                }catch (Throwable throwable){
                        throwable.printStackTrace();
                    }
                }
                //if (theSlot.getIRecipe() != null && theSlot.getIRecipe().getRecipeOutput() != null)
                    //theSlot.inventory.setInventorySlotContents(theSlot.getSlotIndex(), theSlot.getIRecipe().getRecipeOutput());
                    //renderSlotResult(theSlot);
            }}

        }


        super.drawScreen(i, j, f);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2896 /*GL_LIGHTING*/);
    }

    private void drawStuff(SlotCrafter slot) {
        int i = slot.xDisplayPosition;
        int j = slot.yDisplayPosition;
        ItemStack itemstack = slot.getIRecipe().getRecipeOutput();
        boolean flag = false;
        boolean flag1 = false;//slot == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
        ItemStack itemstack1 = this.mc.thePlayer.inventory.getItemStack();
        String s = null;

        /*if (slot == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && itemstack != null)
        {
            itemstack = itemstack.copy();
            itemstack.stackSize /= 2;
        }*/
        //else
        if (this.field_147007_t && this.field_147008_s.contains(slot) && itemstack1 != null)
        {
            if (this.field_147008_s.size() == 1)
            {
                return;
            }

            if (Container.func_94527_a(slot, itemstack1, true) && this.inventorySlots.canDragIntoSlot(slot))
            {
                itemstack = itemstack1.copy();
                flag = true;
                Container.func_94525_a(this.field_147008_s, this.field_146987_F, itemstack, slot.getStack() == null ? 0 : slot.getStack().stackSize);

                if (itemstack.stackSize > itemstack.getMaxStackSize())
                {
                    s = EnumChatFormatting.YELLOW + "" + itemstack.getMaxStackSize();
                    itemstack.stackSize = itemstack.getMaxStackSize();
                }

                if (itemstack.stackSize > slot.getSlotStackLimit())
                {
                    s = EnumChatFormatting.YELLOW + "" + slot.getSlotStackLimit();
                    itemstack.stackSize = slot.getSlotStackLimit();
                }
            }
            else
            {
                this.field_147008_s.remove(slot);
                this.func_146980_g();
            }
        }

        this.zLevel = 100.0F;
        itemRender.zLevel = 100.0F;

        if (itemstack == null)
        {
            IIcon iicon = slot.getBackgroundIconIndex();

            if (iicon != null)
            {
                GL11.glDisable(GL11.GL_LIGHTING);
                this.mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
                this.drawTexturedModelRectFromIcon(i, j, iicon, 16, 16);
                GL11.glEnable(GL11.GL_LIGHTING);
                flag1 = true;
            }
        }

        if (!flag1)
        {
            if (flag)
            {
                drawRect(i, j, i + 16, j + 16, -2130706433);
            }

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack, i, j);
            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack, i, j, s);
        }

        itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    private void func_146980_g()
    {
        ItemStack itemstack = this.mc.thePlayer.inventory.getItemStack();

        if (itemstack != null && this.field_147007_t)
        {
            this.field_146996_I = itemstack.stackSize;
            ItemStack itemstack1;
            int i;

            for (Iterator iterator = this.field_147008_s.iterator(); iterator.hasNext(); this.field_146996_I -= itemstack1.stackSize - i)
            {
                Slot slot = (Slot)iterator.next();
                itemstack1 = itemstack.copy();
                i = slot.getStack() == null ? 0 : slot.getStack().stackSize;
                Container.func_94525_a(this.field_147008_s, this.field_146987_F, itemstack1, i);

                if (itemstack1.stackSize > itemstack1.getMaxStackSize())
                {
                    itemstack1.stackSize = itemstack1.getMaxStackSize();
                }

                if (itemstack1.stackSize > slot.getSlotStackLimit())
                {
                    itemstack1.stackSize = slot.getSlotStackLimit();
                }
            }
        }
    }

    private boolean getIsMouseOverSlot(Slot slot, int i, int j)
    {
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
        //int k = mc.renderEngine.getTexture();
        mc.renderEngine.bindTexture(new ResourceLocation("craftingtableiv", "gui/crafttableii.png"));
        int l = guiLeft;
        int i1 = guiTop;
        //Background
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        int j1 = l + 155;
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