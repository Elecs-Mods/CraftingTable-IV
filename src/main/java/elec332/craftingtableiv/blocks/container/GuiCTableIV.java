package elec332.craftingtableiv.blocks.container;

import com.google.common.collect.Lists;
import elec332.core.util.Constants;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.blocks.inv.InventoryCraftingTableIV;
import elec332.craftingtableiv.blocks.slot.SlotCrafter;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.handler.FastRecipeList;
import elec332.craftingtableiv.handler.WrappedRecipe;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.image.ImagingOpException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class GuiCTableIV extends GuiContainer implements ISlotChangeableGUI{

    private float scroll;
    private boolean field_35313_h;  //???
    private boolean field_35314_i;  //???
    int RecipeType = 0;
    private GuiTextField textField;
    private CraftingTableIVContainer container;
    private CTIVThread currentThread;
    public float ScrollValue = 0.0F;
    private EntityPlayer thePlayer;
    private TileEntityCraftingTableIV theTile;
    public InventoryCraftingTableIV craftableRecipes;
    private InventoryBasic inventory;

    public GuiCTableIV(EntityPlayer entityplayer, TileEntityCraftingTableIV tile) {
        super(new CraftingTableIVContainer(entityplayer, tile));
        scroll = 0.0F;
        field_35313_h = false;
        allowUserInput = true;
        ySize = 234;
        thePlayer = entityplayer;
        theTile = tile;
        craftableRecipes = new InventoryCraftingTableIV();
        container = (CraftingTableIVContainer) inventorySlots;
        inventory = container.inventory;
        container.setGui(this);
        updateRecipes();
    }

    private void stopThread(){
        if (currentThread != null)
            currentThread.killSafe();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        stopThread();
    }

    public void updateVisibleSlots(float f) {
        ScrollValue = f;
        int numberOfRecipes = craftableRecipes.getSize();
        int i = (numberOfRecipes / 8 - 4) + 1;
        int j = (int)((double)(f * (float)i) + 0.5D);
        if(j < 0)
            j = 0;
        for(int k = 0; k < 5; k++) {
            for(int l = 0; l < 8; l++) {
                int i1 = l + (k + j) * 8;
                Slot slot = container.getSlot(l + k * 8);
                if(i1 >= 0 && i1 < numberOfRecipes) {
                    ItemStack recipeOutput = craftableRecipes.getRecipeOutput(i1);
                    if(recipeOutput != null) {
                        inventory.setInventorySlotContents(l + k * 8, null);
                        if(slot instanceof SlotCrafter) {
                            ((SlotCrafter)slot).setIRecipe(craftableRecipes.getIRecipe(i1));
                        }
                    } else {
                        inventory.setInventorySlotContents(l + k * 8, null);
                        if(slot instanceof SlotCrafter) {
                            ((SlotCrafter)slot).setIRecipe(null);
                        }
                    }
                } else {
                    inventory.setInventorySlotContents(l + k * 8, null);
                    if(slot instanceof SlotCrafter) {
                        ((SlotCrafter)slot).setIRecipe(null);
                    }
                }
            }
        }
    }

    public void updateRecipes(){
        stopThread();
        currentThread = new CTIVThread();
        currentThread.start();
    }

    @Override
    public void handleMouseClick(Slot slot, int slotIndex, int mouseButton, int flag) {
        if(slot instanceof SlotCrafter) {
            if(mouseButton == Constants.Mouse.MOUSE_RIGHT) {
                updateRecipes();
            } else if(flag == 1) {
                onRequestMaximumRecipeOutput((SlotCrafter) slot);
                updateRecipes();
            } else if (mouseButton == Constants.Mouse.MOUSE_LEFT){
                onRequestSingleRecipeOutput((SlotCrafter) slot);
                updateRecipes();
            } else CraftingTableIV.instance.info("Received mouse event with ID: "+mouseButton+" I cannot process this button");
        } else {
            super.handleMouseClick(slot, slotIndex, mouseButton, flag);
            updateRecipes();
        }
    }

    public boolean onRequestSingleRecipeOutput(SlotCrafter slot) {
        WrappedRecipe recipe = slot.getIRecipe();
        return recipe != null && CraftingHandler.canPlayerCraft(thePlayer, theTile, recipe, new FastRecipeList(craftableRecipes.getAllRecipes()), true);
    }

    //TODO START

    /*public boolean onRequestSingleRecipeOutput(InventoryPlayer thePlayerInventory, IRecipe irecipe, TileEntityCraftingTableIV internal, boolean b) {
        TileEntityCraftingTableIV internalCopy = internal.getCopy();
        InventoryPlayer fakeInventory = new InventoryPlayer(thePlayerInventory.player);
        fakeInventory.copyInventory(thePlayerInventory);
        try {
            craftRecipe(irecipe, fakeInventory, internalCopy);
            thePlayerInventory.copyInventory(fakeInventory);
            for (int i = 0; i < internal.getSizeInventory(); i++) {
                internal.setInventorySlotContents(i, internalCopy.getStackInSlot(i));
            }
            //internal.inv = internalCopy.theInventory;
            return true;
        } catch (RuntimeException t){
            if (b)
                PlayerHelper.addPersonalMessageToClient("Something went wrong while trying to process your crafting request");
            return false;
        }
    }

    public void craftRecipe(IRecipe recipe, InventoryPlayer inventoryPlayer, TileEntityCraftingTableIV internalInventory) {
        ItemStack[] ingredients = CraftingHandler.getRecipeIngredients(recipe, inventoryPlayer);
        for (ItemStack itemStack : ingredients) {
            CraftingHandler.decreaseStackSize(inventoryPlayer, internalInventory, CraftingHandler.getFirstInventorySlotWithItemStack(inventoryPlayer, internalInventory, itemStack), 1);
        }
        InventoryCrafting craftingMatrix = CraftingHandler.generateCraftingMatrix(ingredients);
        CraftingHandler.handleCraftingMatrix(craftingMatrix, inventoryPlayer);
        if (!CraftingHandler.addItemStackPlayer(inventoryPlayer, internalInventory, recipe.getRecipeOutput().copy())) {
            PlayerHelper.addPersonalMessageToClient("Something went wrong while trying to process your crafting request");
            throw new RuntimeException("EY!");
        }
    }*/

    private void onRequestMaximumRecipeOutput(SlotCrafter slot) {
        /*WrappedRecipe recipe = slot.getIRecipe();
        if(recipe == null)
            return;
        onRequestMaximumRecipeOutput(thePlayer, recipe, theTile);*/
    }
/*
    public static void onRequestMaximumRecipeOutput(EntityPlayer thePlayer, WrappedRecipe irecipe, TileEntityCraftingTableIV Internal) {
        InventoryPlayer Temp = new InventoryPlayer( thePlayer );
        Temp.copyInventory(thePlayer.inventory);

        InventoryPlayer inventoryPlayer = thePlayer.inventory;
        int GoTo = 64;
        if (irecipe.getRecipeOutput().getStack().getMaxStackSize() > 1) {
            GoTo = irecipe.getRecipeOutput().getStack().getMaxStackSize() / irecipe.getRecipeOutput().getStack().stackSize ;
        }
        for (int i=0; i<GoTo; i++) {
            Temp.copyInventory(thePlayer.inventory);
            //if ((Boolean)CraftingHandler.canPlayerCraft(Temp, irecipe.getRecipeOutput(), Internal, RecipeIndex)[0]) {
            //    Object[] iTemp = CraftingHandler.canPlayerCraft(inventoryPlayer, Internal, irecipe.getRecipeOutput(), 0, null, null, RecipeIndex);
            //   Internal.theInventory = ((TECraftingTableIV)iTemp[3]).theInventory;
            //    thePlayer.inventory.copyInventory((InventoryPlayer) iTemp[1]);
            // } else {
            break;
            //}
        }
    }*/
    //TODO END


    /**
     * Actual GUI stuff
     */

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.textField = new GuiTextField(3, this.fontRendererObj, i + 102, j + 5, 103/2, 10);
        this.textField.setTextColor(-1);
        this.textField.setDisabledTextColour(-1);
        this.textField.setEnableBackgroundDrawing(true);
        this.textField.setMaxStringLength(12);
    }

    @Override
    protected void keyTyped(char c, int i) throws IOException{
        if (textField.textboxKeyTyped(c, i)) {
            updateRecipes();
        } else super.keyTyped(c, i);
    }

    @Override
    protected void mouseClicked(int i1, int i2, int i3) throws IOException{
        super.mouseClicked(i1, i2, i3);
        textField.mouseClicked(i1, i2, i3);
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
        }

        for (int b=0; b<9; b++) {
            ((CraftingTableIVContainer)inventorySlots).recipeItems.setInventorySlotContents(b, null);
        }

        for (int a = 0; a < ((CraftingTableIVContainer)inventorySlots).inventory.getSizeInventory(); a++) {
            if (this.inventorySlots.inventorySlots.get(a) instanceof SlotCrafter) {
                SlotCrafter theSlot = (SlotCrafter) this.inventorySlots.inventorySlots.get(a);
                if (theSlot.getIRecipe() != null){
                theSlot.inventory.setInventorySlotContents(theSlot.getSlotIndex(), theSlot.getIRecipe().getRecipeOutput().getStack());
                if (getIsMouseOverSlot(theSlot, i, j)) {
                    try {
                        List<ItemStack> theRecipe = getIngredients(theSlot.getIRecipe());
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
        GL11.glDisable(GL11.GL_BLEND);
        this.textField.drawTextBox();
    }

    private List<ItemStack> getIngredients(WrappedRecipe recipe){
        List<ItemStack> ret = Lists.newArrayList();
        for (Object obj : recipe.getInput()){
            if (obj instanceof ItemStack)
                ret.add((ItemStack) obj);
            else if (obj instanceof List && !((List) obj).isEmpty())
                ret.add((ItemStack) ((List) obj).get(0));
        }
        for (ItemStack stack : ret){
            if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
                stack.setItemDamage(0);
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

    @Override
    protected void drawGuiContainerForegroundLayer(int i, int i1) {
        Minecraft.getMinecraft().fontRendererObj.drawString("Crafting Table IV", 8, 6, 0x404040);
    }

    @Override
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

    @Override
    public void handleMouseInput() throws IOException{
        int i = Mouse.getEventDWheel();
        if(i != 0) {
            int j = (craftableRecipes.getSize() / 8 - 4) + 1;
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
            updateVisibleSlots(scroll);
        }
        super.handleMouseInput();
    }

    @Override
    public void onSlotChanged() {
        updateRecipes();
    }

    @SuppressWarnings("deprecation")
    private class CTIVThread extends Thread{

        public CTIVThread(){
            super("CraftingHandler");
        }

        @Override
        public void run() {
            long l = System.currentTimeMillis();
            if (thePlayer.worldObj.isRemote) {
                craftableRecipes.clearRecipes();
                List<WrappedRecipe> validRecipes = Lists.newArrayList();
                List<WrappedRecipe> canCraft = Lists.newArrayList();
                StackMatcher matcher = toPattern(textField);
                for (WrappedRecipe recipe : CraftingHandler.recipeList) {
                    if (matcher.canAdd(recipe)){
                        validRecipes.add(recipe);
                    }
                }
                for (WrappedRecipe recipe : validRecipes){
                    checkStopThread();
                    if (CraftingHandler.canPlayerCraft(thePlayer, theTile, recipe, new FastRecipeList(), false)) {
                        checkStopThread();
                        craftableRecipes.forceAddRecipe(recipe);
                        canCraft.add(recipe);
                    }
                }
                updateVisibleSlots(ScrollValue);
                validRecipes.removeAll(canCraft);
                for (int i = 0; i < CraftingTableIV.recursionDepth; i++) {
                    checkStopThread();
                    List<WrappedRecipe> pcc = Lists.newArrayList(canCraft);
                    FastRecipeList recipeList = new FastRecipeList(canCraft);
                    for (WrappedRecipe recipe : validRecipes){
                        checkStopThread();
                        if (CraftingHandler.canPlayerCraft(thePlayer, theTile, recipe, recipeList, false)) {
                            checkStopThread();
                            craftableRecipes.forceAddRecipe(recipe);
                            canCraft.add(recipe);
                            updateVisibleSlots(ScrollValue);
                        }
                    }
                    validRecipes.removeAll(canCraft);
                    if (pcc.size() == canCraft.size())
                        break;
                }
                updateVisibleSlots(ScrollValue);
                if (CraftingTableIV.debugTimings)
                    CraftingTableIV.instance.info("Loaded all recipes for CTIV Gui in " + (System.currentTimeMillis() - l) + " ms");
            }
        }

        public void killSafe(){
            stopThread = true;
        }

        private boolean stopThread = false;

        private void checkStopThread(){
            if (stopThread)
                stop();
        }

    }

    private static StackMatcher toPattern(GuiTextField textField){
        String txt = null;
        if (textField != null){
            txt = textField.getText();
        }
        if (txt == null)
            txt = "";
        txt = txt.toLowerCase().replace(".", "").replace("?", ".").replace("*", ".+?");
        Pattern pattern;
        try {
            pattern = Pattern.compile(txt);
        } catch (Exception e){
            pattern = null;
        }
        if (pattern != null)
            return new PatternStackMatcher(pattern);
        return new StackMatcher();
    }

    private static class StackMatcher {

        public boolean canAdd(WrappedRecipe recipe){
            return true;
        }

    }

    private static class PatternStackMatcher extends StackMatcher {

        private PatternStackMatcher(Pattern pattern){
            this.pattern = pattern;
        }

        Pattern pattern;

        public boolean canAdd(WrappedRecipe recipe){
            return pattern.matcher(recipe.itemIdentifierClientName()).find();
        }

    }

}