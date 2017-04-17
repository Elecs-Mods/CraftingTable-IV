package elec332.craftingtableiv.inventory;

import com.google.common.collect.Lists;
import elec332.core.client.RenderHelper;
import elec332.core.client.util.GuiDraw;
import elec332.core.client.util.KeyHelper;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.widget.Widget;
import elec332.core.inventory.widget.WidgetButton;
import elec332.core.inventory.widget.slot.WidgetSlot;
import elec332.core.inventory.window.Window;
import elec332.core.util.BasicItemHandler;
import elec332.core.util.Constants;
import elec332.core.util.ItemStackHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import elec332.craftingtableiv.util.FastRecipeList;
import elec332.craftingtableiv.util.RecipeCache;
import elec332.craftingtableiv.util.WrappedRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Elec332 on 1-1-2017.
 */
public class WindowCraftingTableIV extends Window {

    public WindowCraftingTableIV(TileEntityCraftingTableIV theTile) {
        super(-1, 234);
        this.theTile = theTile;
    }

    private IItemHandlerModifiable inventory = new BasicItemHandler(8*5);
    private IItemHandlerModifiable recipeItems = new BasicItemHandler(9);
    private final TileEntityCraftingTableIV theTile;
    private float scroll;
    @SuppressWarnings("all")
    private boolean shaped, hovering;
    private GuiTextField textField;
    private CTIVThread currentThread;
    private float scrollValue = 0.0F;
    private RecipeCache craftableRecipes;
    private static final StackMatcher ALWAYS_TRUE;

    @Override
    protected void initWindow() {
        super.initWindow();
        for(int l2 = 0; l2 < 5; l2++) {
            for(int j3 = 0; j3 < 8; j3++) {
                addWidget(new WidgetCraftSlot(inventory,  j3 + l2 * 8, 8 + j3 * 18, 18 + l2 * 18, this));
            }
        }

        for(int a = 0; a < 2; a++) {
            for(int i = 0; i < 9; i++) {
                addWidget(new WidgetCTIVSlot(theTile, i + (a * 9), 8 + i * 18, 112 + (18 * a), this));
            }
        }

        IItemHandler playerInv = new PlayerMainInvWrapper(getPlayer().inventory);

        for(int j = 0; j < 3; j++) {
            for(int i1 = 0; i1 < 9; i1++) {
                addWidget(new WidgetCTIVSlot(playerInv, i1 + j * 9 + 9, 8 + i1 * 18, 152 + j * 18, this));
            }
        }

        for(int i3 = 0; i3 < 9; i3++) {
            addWidget(new WidgetCTIVSlot(playerInv, i3, 8 + i3 * 18, 211, this));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                addWidget(new WidgetSlot(recipeItems, j * 3 + i, (3 - i) * -18, 34 + j * 18) {

                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean isHidden() {
                        return x != -18 && !shaped || !hovering;
                    }

                    @Override
                    public int getSlotIndex() {
                        return shaped ? super.getSlotIndex() : (y - 34) / 18;
                    }

                    @Override
                    @SideOnly(Side.CLIENT)
                    public void draw(Window window, int guiX, int guiY, int mouseX, int mouseY) {
                    }

                });
            }
        }
        for (int i = 3; i < 9; i++) {
            addWidget(new WidgetSlot(recipeItems, i, -18, 34 + i * 18){

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean isHidden() {
                    return shaped || !hovering;
                }

                @Override
                @SideOnly(Side.CLIENT)
                public void draw(Window window, int guiX, int guiY, int mouseX, int mouseY) {
                }

            });
        }
        final ToolTip rSTT = new ToolTip(Lists.newArrayList("Toggles whether to", "show the max amount", "of craftable items.")){

            @Override
            @SideOnly(Side.CLIENT)
            public void renderTooltip(int mouseX, int mouseY, int guiLeft, int guiTop) {
                mouseY += 10;
                super.renderTooltip(mouseX, mouseY, guiLeft, guiTop);
            }

        };
        final ToolTip shTT = new ToolTip(Lists.newArrayList("Toggles whether to", "show shaped recipes", "on the right side."));
        addWidget(new WidgetButton(xSize + 2, 2, 12, 12){

            @Override
            public ToolTip getToolTip() {
                return rSTT;
            }

            @Override
            public void onButtonClicked() {
                if (getPlayer().getEntityWorld().isRemote) {
                    theTile.showRecipeSize = !recipeSize();
                }
            }

        }).setDisplayString("n");
        addWidget(new WidgetButton(xSize + 2, 15, 12, 12){

            @Override
            public ToolTip getToolTip() {
                return shTT;
            }

            @Override
            public void onButtonClicked() {
                if (getPlayer().getEntityWorld().isRemote) {
                    theTile.showShaped = !showShaped();
                }
            }

        }).setDisplayString("s");


        if (getPlayer().getEntityWorld().isRemote) {
            scroll = 0.0F;
            craftableRecipes = new RecipeCache();
            Keyboard.enableRepeatEvents(true);
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            this.textField = new GuiTextField(3, RenderHelper.getMCFontrenderer(), i + 102, j + 5, 103/2, 10);
            this.textField.setTextColor(-1);
            this.textField.setDisabledTextColour(-1);
            this.textField.setEnableBackgroundDrawing(true);
            this.textField.setMaxStringLength(12);
            new Thread(() -> {

				try {
					Thread.sleep(120);
				} catch (Exception e) {
					//Nope
				} finally {
					updateRecipes();
					if (craftableRecipes.getAllRecipes().size() == 0){
					    updateRecipes(); // retry
                    }
				}

			}).start();
        }

    }

    public boolean recipeSize(){
        return theTile.showRecipeSize;
    }

    public boolean showShaped(){
        return theTile.showShaped;
    }

    @Override
    @Nonnull
    public ItemStack slotClick(int slotIndex, int dragType, ClickType clickType, EntityPlayer player) {
        if(slotIndex >= 0 && getSlot(slotIndex) != null && getSlot(slotIndex) instanceof WidgetCraftSlot) {
            return ItemStackHelper.NULL_STACK;
        }
        return super.slotClick(slotIndex, dragType, clickType, player);
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        WidgetSlot slot = getSlot(i);
        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
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
        return true;
    }

    //Client part

    private void stopThread(){
        if (currentThread != null) {
            currentThread.killSafe();
        }
    }

    @Override
    public void onWindowClosed(EntityPlayer playerIn) {
        super.onWindowClosed(playerIn);
        stopThread();
    }

    private void updateVisibleSlots(float f) {
        if(f < 0.0F) {
            f = 0.0F;
        }
        if(f > 1.0F) {
            f = 1.0F;
        }
        scrollValue = f;
        int numberOfRecipes = craftableRecipes.getShownSize();
        int i = (numberOfRecipes / 8 - 4) + 1;
        int j = (int)((double)(f * (float)i) + 0.5D);
        if(j < 0) {
            j = 0;
        }
        for(int k = 0; k < 5; k++) {
            for(int l = 0; l < 8; l++) {
                int i1 = l + (k + j) * 8;
                WidgetSlot slot = getSlot(l + k * 8);
                if(i1 >= 0 && i1 < numberOfRecipes) {
                    ItemStack recipeOutput = craftableRecipes.getRecipeOutput(i1);
                    if(recipeOutput != null) {
                        if(slot instanceof WidgetCraftSlot) {
                            RecipeCache.Entry e = craftableRecipes.getShownRecipe(i1);
                            ((WidgetCraftSlot)slot).setIRecipe(e.recipe, e.amount);
                        }
                    } else {
                        if(slot instanceof WidgetCraftSlot) {
                            ((WidgetCraftSlot)slot).clearRecipe();
                        }
                    }
                } else {
                    if(slot instanceof WidgetCraftSlot) {
                        ((WidgetCraftSlot)slot).clearRecipe();
                    }
                }
            }
        }
    }

    private void updateRecipes(){
        updateRecipes(false);
    }

    private void updateRecipes(boolean txt){
        if (txt){
            craftableRecipes.updateVisual(getCurrentPattern());
            updateVisibleSlots(scrollValue);
        } else {
            stopThread();
            currentThread = new CTIVThread();
            currentThread.start();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void handleMouseClick(WidgetSlot slot, int slotId, int mouseButton, @Nonnull ClickType type) {
        if(slot instanceof WidgetCraftSlot) {
            if(mouseButton == Constants.Mouse.MOUSE_RIGHT) {
                updateRecipes();
            } else if(KeyHelper.isShiftDown()) {
                onRequestMaximumRecipeOutput((WidgetCraftSlot) slot);
                updateRecipes();
            } else if (mouseButton == Constants.Mouse.MOUSE_LEFT){
                onRequestSingleRecipeOutput((WidgetCraftSlot) slot);
                updateRecipes();
            } else {
                CraftingTableIV.logger.info("Received mouse event with ID: " + mouseButton + " I cannot process this button");
            }
        } else {
            super.handleMouseClick(slot, slotId, mouseButton, type);
            updateRecipes();
        }
    }

    private boolean onRequestSingleRecipeOutput(WidgetCraftSlot slot) {
        WrappedRecipe recipe = slot.getIRecipe();
        return recipe != null && CraftingHandler.canCraft(CraftingHandler.forCraftingTableIV(getPlayer(), theTile), recipe, new FastRecipeList(craftableRecipes.getAllRecipes()), true, recipe.getOutputSize()) > 0;
    }

    private void onRequestMaximumRecipeOutput(WidgetCraftSlot slot) {
        WrappedRecipe recipe = slot.getIRecipe();
        if (recipe == null) {
            return;
        }
        CraftingHandler.IWorldAccessibleInventory<?> wrappedInventory = CraftingHandler.forCraftingTableIV(getPlayer(), theTile);
        FastRecipeList recipeList = new FastRecipeList(craftableRecipes.getAllRecipes());
        int i = CraftingHandler.canCraft(wrappedInventory, recipe, recipeList, false, slot.getAmount());
        CraftingHandler.canCraft(wrappedInventory, recipe, recipeList, true, i);
    }

    /**
     * Actual GUI stuff
     */

    @Override
    @SideOnly(Side.CLIENT)
    protected boolean keyTyped(char c, int i) {
        if (textField.textboxKeyTyped(c, i)) {
            updateRecipes(true);
            return true;
        } else {
            return super.keyTyped(c, i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected boolean mouseClicked(int i1, int i2, int i3) throws IOException {
        if (super.mouseClicked(i1, i2, i3)){
            return true;
        }
        textField.mouseClicked(i1, i2, i3);
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawScreen(int i, int j, float f) {

        for (int b = 0; b < 9; b++) {
            recipeItems.setStackInSlot(b, ItemStackHelper.NULL_STACK);
        }

        boolean work = false;

        for (int a = 0; a < inventory.getSlots(); a++) {
            WidgetSlot slot = getSlot(a);
            if (slot instanceof WidgetCraftSlot && getIsMouseOverSlot(slot, i, j)) {
                WidgetCraftSlot theSlot = (WidgetCraftSlot) getSlot(a);
                WrappedRecipe recipe = theSlot.getIRecipe();
                if (recipe != null) {
                    this.hovering = true;
                    this.shaped = recipe.isShaped() && showShaped();
                    ItemStack[] theRecipe = getIngredients(recipe, this.shaped);
                    for (int b = 0; b < 9; b++) {
                        ItemStack stack = theRecipe[b];
                        recipeItems.setStackInSlot(b, stack);
                        work = true;
                    }
                }
            }

        }
        super.drawScreen(i, j, f);

        if (!work && this.hovering){
            this.hovering = false;
        }

    }

    private ItemStack[] getIngredients(WrappedRecipe recipe, boolean shaped){
        ItemStack[] ret = new ItemStack[9];
        int counter = 0;
        for (int j = 0; j < 9; j++) {
            int i = shaped ? j : counter;
            Object obj = j >= recipe.getInput().length ? ItemStackHelper.NULL_STACK : recipe.getInput()[j];
            ItemStack stack;
            if (obj == null){
                stack = ItemStackHelper.NULL_STACK;
            } else if (obj instanceof ItemStack) {
                stack = ((ItemStack) obj).copy();
            } else if (obj instanceof List && !((List) obj).isEmpty()) {
                stack = ((ItemStack) ((List) obj).get(0)).copy();
            } else {
                stack = ItemStackHelper.NULL_STACK;
            }
            if (!ItemStackHelper.isStackValid(stack)){
                if (shaped) {
                    ret[i] = ItemStackHelper.NULL_STACK;
                }
            } else {
                ret[i] = stack;
                counter++;
            }
        }
        for (int i = 0; i < ret.length; i++) {
            ItemStack stack = ret[i];
            if (stack != null) {
                if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    stack.setItemDamage(0);
                }
            } else {
                ret[i] = ItemStackHelper.NULL_STACK;
            }
        }
        return ret;
    }

    private boolean getIsMouseOverSlot(Widget slot, int i, int j) {
        int k = guiLeft;
        int l = guiTop;
        i -= k;
        j -= l;
        return i >= slot.x - 1 && i < slot.x + 16 + 1 && j >= slot.y - 1 && j < slot.y + 16 + 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void drawGuiContainerForegroundLayer(int i, int i1) {
        Minecraft.getMinecraft().fontRendererObj.drawString("Crafting Table IV", 8, 6, 0x404040);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.bindTexture(new ResourceLocation("craftingtableiv", "gui/crafttableii.png"));
        int l = guiLeft;
        int i1 = guiTop;
        //Background
        GuiDraw.drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        //int j1 = l + 155;
        int k1 = i1 + 17;
        int l1 = k1 + 88 + 2;
        //Scrolly bar
        GuiDraw.drawTexturedModalRect(l + 154, i1 + 17 + (int)((float)(l1 - k1 - 17) * scroll), 0, 240, 16, 16);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.textField.drawTextBox();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleMouseInput() throws IOException{
        int m = Mouse.getEventDWheel();
        float oldScroll = scroll;
        if(m != 0) {
            int j = (craftableRecipes.getShownSize() / 8 - 4) + 1;
            if(m > 0) {
                m = 1;
            }
            if(m < 0) {
                m = -1;
            }
            scroll -= (double)m / (double)j;
        }
        if (Mouse.isButtonDown(0)){
            Minecraft mc = Minecraft.getMinecraft();
            final ScaledResolution scaledresolution = new ScaledResolution(mc);
            int s1 = scaledresolution.getScaledWidth();
            int s2 = scaledresolution.getScaledHeight();
            final int i = Mouse.getX() * s1 / mc.displayWidth;
            final int j = s2 - Mouse.getY() * s2 / mc.displayHeight - 1;
            int k = guiLeft;
            int l = guiTop;
            int i1 = k + 155;
            int j1 = l + 17;
            int k1 = i1 + 14;
            int l1 = j1 + 88 + 2;

            if(i >= i1 && j >= j1 && i < k1 && j < l1) {
                oldScroll = scroll;
                scroll = (float)(j - (j1 + 8)) / ((float)(l1 - j1) - 16F);
            }
        }
        if(scroll < 0.0F) {
            scroll = 0.0F;
        }
        if(scroll > 1.0F) {
            scroll = 1.0F;
        }
        if (scroll != oldScroll) {
            updateVisibleSlots(scroll);
        }
        super.handleMouseInput();
    }

    void onSlotChanged() {
        updateRecipes();
    }

    @SuppressWarnings("deprecation")
    private class CTIVThread extends Thread {

        CTIVThread(){
            super("CraftingHandler");
        }

        @Override
        public void run() {
            int times = 64;
            long l = System.currentTimeMillis();
            if (getPlayer().getEntityWorld().isRemote) {
                craftableRecipes.clearRecipes();
                List<WrappedRecipe> validRecipes = Lists.newArrayList(CraftingHandler.getAllRecipes());
                List<WrappedRecipe> canCraft = Lists.newArrayList();
                StackMatcher matcher = getCurrentPattern();
                CraftingHandler.IWorldAccessibleInventory<?> wrappedInventory = CraftingHandler.forCraftingTableIV(getPlayer(), theTile);
                for (WrappedRecipe recipe : validRecipes){
                    checkStopThread();
                    int a = CraftingHandler.canCraft(wrappedInventory, recipe, null, false, times);
                    if (a > 0) {
                        checkStopThread();
                        craftableRecipes.addRecipe(recipe, a, matcher);
                        canCraft.add(recipe);
                    }
                }
                updateVisibleSlots(scrollValue);
                validRecipes.removeAll(canCraft);
                for (int i = 0; i < CraftingTableIV.recursionDepth; i++) {
                    checkStopThread();
                    List<WrappedRecipe> pcc = Lists.newArrayList(canCraft);
                    FastRecipeList recipeList = new FastRecipeList(canCraft);
                    for (WrappedRecipe recipe : validRecipes){
                        checkStopThread();
                        int a = CraftingHandler.canCraft(wrappedInventory, recipe, recipeList, false, times);
                        if (a > 0) {
                            checkStopThread();
                            craftableRecipes.addRecipe(recipe, a, matcher);
                            canCraft.add(recipe);
                            updateVisibleSlots(scrollValue);
                        }
                    }
                    validRecipes.removeAll(canCraft);
                    checkStopThread();
                    if (pcc.size() == canCraft.size()) {
                        break;
                    }
                }
                updateRecipes(true);
                if (CraftingTableIV.debugTimings) {
                    CraftingTableIV.logger.info("Loaded all recipes for CTIV Gui in " + (System.currentTimeMillis() - l) + " ms");
                }
            }
        }

        private void killSafe(){
            stopThread = true;
        }

        private boolean stopThread = false;

        private void checkStopThread(){
            if (stopThread) {
                stop();
            }
        }

    }

    private StackMatcher getCurrentPattern(){
        return toPattern(textField);
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
        return ALWAYS_TRUE;
    }

    public static class StackMatcher {

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

    static {
        ALWAYS_TRUE = new StackMatcher();
    }

}
