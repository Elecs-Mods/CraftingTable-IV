package elec332.craftingtableiv.blocks.container;

import com.google.common.collect.Lists;
import elec332.core.player.PlayerHelper;
import elec332.core.util.Constants;
import elec332.core.util.NBTHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.blocks.inv.InventoryCraftingTableIV;
import elec332.craftingtableiv.blocks.slot.SlotCrafter;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.handler.FastRecipeList;
import elec332.craftingtableiv.handler.RecipeStackComparator;
import elec332.craftingtableiv.handler.WrappedRecipe;
import elec332.craftingtableiv.network.PacketCraft;
import elec332.craftingtableiv.network.PacketSyncRecipes;
import elec332.craftingtableiv.network.PacketSyncScroll;
import elec332.craftingtableiv.network.PacketSyncText;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class GuiCTableIV extends GuiContainer {

    private float scroll;
    private boolean field_35313_h;
    private boolean field_35314_i;
    int RecipeType = 0;
    private GuiTextField textField;
    private CraftingTableIVContainer container;


    public GuiCTableIV(EntityPlayer entityplayer, TECraftingTableIV tile) {
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
    }
    //////////////////////////**/

    private CTIVThread currentThread;
    public float ScrollValue = 0.0F;
    private EntityPlayer thePlayer;
    private TECraftingTableIV theTile;
    public InventoryCraftingTableIV craftableRecipes;
    private InventoryBasic inventory;


    private void stopThread(){
        if (currentThread != null)
            currentThread.killSafe();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        stopThread();
    }

    public static boolean canPlayerCraft(EntityPlayer player, TECraftingTableIV craftingTableIV, WrappedRecipe recipe, FastRecipeList canCraft, boolean b){
        InventoryPlayer fakeInventoryPlayer = new InventoryPlayer(player);
        fakeInventoryPlayer.copyInventory(player.inventory);
        TECraftingTableIV fakeCraftingInventory = craftingTableIV.getCopy();
        boolean ret = canPlayerCraft(fakeInventoryPlayer, fakeCraftingInventory, recipe, 0, canCraft, b);
        if (b && ret){
            player.inventory.copyInventory(fakeInventoryPlayer);
            for (int i = 0; i < craftingTableIV.getSizeInventory(); i++) {
                craftingTableIV.setInventorySlotContents(i, fakeCraftingInventory.getStackInSlot(i));
            }
        }
        return ret;
    }

    private static void yew(WrappedRecipe recipe){

    }

    public static boolean canPlayerCraft(InventoryPlayer fakeInventoryPlayer, TECraftingTableIV fakeCraftingInventory, WrappedRecipe recipe, int i, FastRecipeList check, boolean r){
        if (fakeInventoryPlayer != null && recipe != null) {
            inputLoop:
            for (Object obj : recipe.getInput()) {
                if (obj == null)
                    continue;
                if (obj instanceof ItemStack) {
                    ItemStack itemStack = (ItemStack) obj;
                    int slotID = CraftingHandler.getFirstInventorySlotWithItemStack(fakeInventoryPlayer, fakeCraftingInventory, itemStack);
                    if (slotID > -1) {
                        if (!handleStuff(fakeInventoryPlayer, fakeCraftingInventory, itemStack, r))
                            return false;
                    } else if (i != CraftingTableIV.recursionDepth){
                        List<WrappedRecipe> valid = check.getCraftingRecipe(itemStack);
                        if (valid.isEmpty())
                            return false;
                        for (WrappedRecipe wrappedRecipe : valid){
                            if (canPlayerCraft(fakeInventoryPlayer, fakeCraftingInventory, wrappedRecipe, i+1, check, r)) {
                                if (!handleStuff(fakeInventoryPlayer, fakeCraftingInventory, itemStack, r))
                                    return false;
                                yew(wrappedRecipe);
                                continue inputLoop;
                            }
                        }
                        return false;
                    } else return false;
                } else if (obj instanceof List && !((List)obj).isEmpty()){
                    @SuppressWarnings("unchecked")
                    List<ItemStack> stacks = (List<ItemStack>) obj;
                    for (ItemStack itemStack : stacks) {
                        int p = CraftingHandler.getFirstInventorySlotWithItemStack(fakeInventoryPlayer, fakeCraftingInventory, itemStack);
                        if (p >= 0) {
                            if (!handleStuff(fakeInventoryPlayer, fakeCraftingInventory, itemStack, r))
                                return false;
                            continue inputLoop;
                        }
                    }
                    if (i != CraftingTableIV.recursionDepth){
                        List<WrappedRecipe> valid = check.getCraftingRecipe(stacks);
                        if (valid.isEmpty())
                            return false;
                        for (WrappedRecipe wrappedRecipe : valid){
                            if (canPlayerCraft(fakeInventoryPlayer, fakeCraftingInventory, wrappedRecipe, i+1, check, r)) {
                                if (!handleStuff(fakeInventoryPlayer, fakeCraftingInventory, wrappedRecipe.getRecipeOutput().getStack(), r))
                                    return false;
                                yew(wrappedRecipe);
                                continue inputLoop;
                            }
                        }
                        return false;
                    } else return false;
                    /*
                    if (i == CraftingTableIV.recursionDepth)
                        return false;
                    ItemStack stack = canPlayerCraftAnyOf(fakeInventoryPlayer, fakeCraftingInventory, stacks, addToList(check, recipe.getRecipeOutput()), i);
                    if (stack != null){
                        if (!handleStuff(fakeInventoryPlayer, fakeCraftingInventory, stack))
                            return false;
                    } else {
                        return false;
                    }*/
                }
            }
            if (r && fakeInventoryPlayer.player.getEntityWorld().isRemote)
                CraftingTableIV.networkHandler.getNetworkWrapper().sendToServer(new PacketCraft(recipe));
            return CraftingHandler.addItemStackPlayer(fakeInventoryPlayer, fakeCraftingInventory, recipe.getRecipeOutput().getStack().copy(), r);
        } else return false;
    }

    private List<WrappedRecipe> valid(List<WrappedRecipe> recipes, List<ItemStack> oreDict){
        List<WrappedRecipe> ret = Lists.newArrayList();
        for (ItemStack stack : oreDict){
            ret.addAll(valid(recipes, stack));
        }
        return ret;
    }

    private List<WrappedRecipe> valid(List<WrappedRecipe> recipes, ItemStack stack){
        List<WrappedRecipe> ret = Lists.newArrayList();
        if (recipes.isEmpty())
            return ret;
        for (WrappedRecipe recipe : recipes){
            if (stacksEqual(stack, recipe.getRecipeOutput().getStack()))
                ret.add(recipe);
        }
        return ret;
    }

    public boolean stacksEqual(ItemStack stack, ItemStack recipe){
        if(recipe.getItem() == stack.getItem()) {
            if(recipe.getItemDamage() == stack.getItemDamage() || recipe.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                return true;
            }
            if(!recipe.getItem().getHasSubtypes() && !stack.getItem().getHasSubtypes()) {
                return true;
            }
        }
        return false;
    }

    private static boolean handleStuff(InventoryPlayer fakeInventoryPlayer, TECraftingTableIV fakeCraftingInventory, ItemStack stack, boolean h){
        int s = CraftingHandler.getFirstInventorySlotWithItemStack(fakeInventoryPlayer, fakeCraftingInventory, stack.copy());
        if (s == -1)
            return false;
        if (stack.getItem().hasContainerItem(stack)){
            ItemStack itemStack = stack.getItem().getContainerItem(CraftingHandler.getStackInSlot(fakeInventoryPlayer, fakeCraftingInventory, s));
            if (itemStack != null && itemStack.isItemStackDamageable() && itemStack.getItemDamage() > itemStack.getMaxDamage())
                itemStack = null;
            if (itemStack != null && !CraftingHandler.addItemStackPlayer(fakeInventoryPlayer, fakeCraftingInventory, itemStack, h))
                return false;
        }
        return CraftingHandler.decrStackSize(fakeInventoryPlayer, fakeCraftingInventory, s, 1, h);
    }

    public void updateVisibleSlots(float f) {
        ScrollValue = f;
        if (thePlayer.getEntityWorld().isRemote)
            CraftingTableIV.networkHandler.getNetworkWrapper().sendToServer(new PacketSyncScroll(new NBTHelper().addToTag(ScrollValue, "scroll").toNBT()));
        int numberOfRecipes = craftableRecipes.getSize();
        int i = (numberOfRecipes / 8 - 4) + 1;
        int j = (int)((double)(f * (float)i) + 0.5D);
        if(j < 0)
            j = 0;
        for(int k = 0; k < 5; k++) {
            for(int l = 0; l < 8; l++) {
                int i1 = l + (k + j) * 8;
                Slot slot = (Slot)container.getSlots().get(l + k * 8);
                if(i1 >= 0 && i1 < numberOfRecipes) {
                    ItemStack recipeOutput = craftableRecipes.getRecipeOutput(i1);
                    if(recipeOutput != null) {
                        //if (thePlayer instanceof EntityClientPlayerMP)
                        //inventory.setInventorySlotContents(l + k * 8, recipeOutput);
                        //else
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
        //System.out.println("Updated visible slots");
    }

    public void updateRecipes(){
        //this.busy = true;
        stopThread();
        currentThread = new CTIVThread();
        currentThread.start();
        //updateVisibleSlots(ScrollValue);
        //this.busy = false;
    }

    @Override
    public void handleMouseClick(Slot slot, int slotIndex, int mouseButton, int flag) {
        //PlayerHelper.addPersonalMessageToClient("Clicked slot: "+slotIndex);
        //if (this.busy)
        //    return null;
        //this.busy = true;
        if(slot instanceof SlotCrafter) {

            // Check if the currently held itemstack is different to the clicked itemstack.
            //if(!ItemHelper.areItemsEqual(inventory.getStackInSlot(slotIndex), entityplayer.inventory.getItemStack()))
            //return null;

            // Ignore right click.
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
        /*if(KeyHelper.isShiftDown()) {
            //transferStackInSlot(slotIndex);
            ItemStack itemstack = super.slotClick(slotIndex, mouseButton, flag, entityplayer);
            populateSlotsWithRecipes();
            updateVisibleSlots(ScrollValue);
            return itemstack;
        } else {
            ItemStack itemstack = super.slotClick(slotIndex, mouseButton, flag, entityplayer);
            populateSlotsWithRecipes();
            updateVisibleSlots(ScrollValue);
            return itemstack;
        }
        ItemStack itemstack = super.slotClick(slotIndex, mouseButton, flag, entityplayer);
        updateRecipes();
        return itemstack;*/
    }


    public boolean onRequestSingleRecipeOutput(SlotCrafter slot) {
        WrappedRecipe recipe = slot.getIRecipe();
        return recipe != null && canPlayerCraft(thePlayer, theTile, recipe, new FastRecipeList(craftableRecipes.getAllRecipes()), true); //onRequestSingleRecipeOutput(thePlayer.inventory, irecipe, theTile, true);
    }

    public boolean onRequestSingleRecipeOutput(InventoryPlayer thePlayerInventory, IRecipe irecipe, TECraftingTableIV internal, boolean b) {
        TECraftingTableIV internalCopy = internal.getCopy();
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

    public void craftRecipe(IRecipe recipe, InventoryPlayer inventoryPlayer, TECraftingTableIV internalInventory) {
        /*ItemStack[] ingredients = CraftingHandler.getRecipeIngredients(recipe, inventoryPlayer);
        for (ItemStack itemStack : ingredients) {
            CraftingHandler.decrStackSize(inventoryPlayer, internalInventory, CraftingHandler.getFirstInventorySlotWithItemStack(inventoryPlayer, internalInventory, itemStack), 1);
        }
        InventoryCrafting craftingMatrix = CraftingHandler.generateCraftingMatrix(ingredients);
        CraftingHandler.handleCraftingMatrix(craftingMatrix, inventoryPlayer);
        if (!CraftingHandler.addItemStackPlayer(inventoryPlayer, internalInventory, recipe.getRecipeOutput().copy())) {
            PlayerHelper.addPersonalMessageToClient("Something went wrong while trying to process your crafting request");
            throw new RuntimeException("EY!");
        }*/
    }

    private void onRequestMaximumRecipeOutput(SlotCrafter slot) {
        WrappedRecipe recipe = slot.getIRecipe();
        if(recipe == null)
            return;
        onRequestMaximumRecipeOutput(thePlayer, recipe, theTile);
    }

    public static void onRequestMaximumRecipeOutput(EntityPlayer thePlayer, WrappedRecipe irecipe, TECraftingTableIV Internal) {
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
                for (WrappedRecipe recipe : CraftingHandler.recipeList) {
                    if (recipe.getOutputItemName().contains(GuiCTableIV.this.textField.getText())){
                        validRecipes.add(recipe);
                    }
                    /*
                    if (recipe.getOutputItemName().contains(CraftingTableIVContainer.this.textField) && canPlayerCraft(thePlayer, theTile, recipe, false)) {
                        craftableRecipes.forceAddRecipe(recipe);
                        syncRecipes();
                    }*/
                }
                for (WrappedRecipe recipe : validRecipes){
                    checkStopThread();
                    if (canPlayerCraft(thePlayer, theTile, recipe, new FastRecipeList(), false)) {
                        checkStopThread();
                        craftableRecipes.forceAddRecipe(recipe);
                        canCraft.add(recipe);
                    }
                }
                validRecipes.removeAll(canCraft);
                for (int i = 0; i < CraftingTableIV.recursionDepth; i++) {
                    checkStopThread();
                    List<WrappedRecipe> pcc = Lists.newArrayList(canCraft);
                    FastRecipeList recipeList = new FastRecipeList(canCraft);
                    for (WrappedRecipe recipe : validRecipes){
                        checkStopThread();
                        if (canPlayerCraft(thePlayer, theTile, recipe, recipeList, false)) {
                            checkStopThread();
                            craftableRecipes.forceAddRecipe(recipe);
                            canCraft.add(recipe);
                        }
                    }
                    validRecipes.removeAll(canCraft);
                    if (pcc.size() == canCraft.size())
                        break;
                }
                updateVisibleSlots(ScrollValue);
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









    /////////////////////////**/

    /*@Override
    protected void handleMouseClick(Slot slot, int i, int j, int flag) {
        if (slot != null)
            if (slot.slotNumber < 94)
                super.handleMouseClick(slot, i, j, flag);
        //inventorySlots.slotClick(i, j, flag, mc.thePlayer);
    }*/

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.textField = new GuiTextField(this.fontRendererObj, i + 102, j + 5, 103/2, 10);
        this.textField.setTextColor(-1);
        this.textField.setDisabledTextColour(-1);
        this.textField.setEnableBackgroundDrawing(true);
        this.textField.setMaxStringLength(12);
    }

    @Override
    protected void keyTyped(char c, int i) {
        if (textField.textboxKeyTyped(c, i)){
            syncText();
        } else super.keyTyped(c, i);
    }

    @Override
    protected void mouseClicked(int i1, int i2, int i3) {
        super.mouseClicked(i1, i2, i3);
        textField.mouseClicked(i1, i2, i3);
    }

    private void syncText(){
        CraftingTableIV.networkHandler.getNetworkWrapper().sendToServer(new PacketSyncText(textField.getText()));
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
            //((CraftingTableIVContainer)inventorySlots).updateVisibleSlots(scroll);
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

    @Override
    public void handleMouseInput() {
        int i = Mouse.getEventDWheel();
        //CraftingTableIVContainer container = (CraftingTableIVContainer)inventorySlots;
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
            //container.updateVisibleSlots(scroll);
        }
        super.handleMouseInput();
    }

    /*public void resetScroll() {
        scroll = 0.0F;
        ((CraftingTableIVContainer)inventorySlots).updateVisibleSlots(scroll);
    }*/
}