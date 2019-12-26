package elec332.craftingtableiv.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import elec332.core.client.ClientHelper;
import elec332.core.client.RenderHelper;
import elec332.core.client.util.GuiDraw;
import elec332.core.inventory.BasicItemHandler;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.widget.Widget;
import elec332.core.inventory.widget.WidgetButton;
import elec332.core.inventory.widget.slot.WidgetSlot;
import elec332.core.inventory.window.Window;
import elec332.core.util.ItemStackHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import elec332.craftingtableiv.util.FastRecipeList;
import elec332.craftingtableiv.util.RecipeCache;
import elec332.craftingtableiv.util.WrappedRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by Elec332 on 1-1-2017.
 */
public class WindowCraftingTableIV extends Window {

    public WindowCraftingTableIV(TileEntityCraftingTableIV theTile) {
        super(-1, 234);
        this.theTile = theTile;
        craftableRecipes = new RecipeCache();
    }

    private IItemHandlerModifiable inventory = new BasicItemHandler(8 * 5);
    private IItemHandlerModifiable recipeItems = new BasicItemHandler(9);
    private final TileEntityCraftingTableIV theTile;
    private float scroll;
    @SuppressWarnings("all")
    private boolean shaped, hovering;
    private TextFieldWidget textField;
    private CTIVThread currentThread;
    private float scrollValue = 0.0F;
    private final RecipeCache craftableRecipes;
    private long lastTextTime = -1;

    @Override
    protected void initWindow() {
        super.initWindow();
        for (int l2 = 0; l2 < 5; l2++) {
            for (int j3 = 0; j3 < 8; j3++) {
                addWidget(new WidgetCraftSlot(inventory, j3 + l2 * 8, 8 + j3 * 18, 18 + l2 * 18, this));
            }
        }

        for (int a = 0; a < 2; a++) {
            for (int i = 0; i < 9; i++) {
                addWidget(new WidgetCTIVSlot(theTile, i + (a * 9), 8 + i * 18, 112 + (18 * a), this));
            }
        }

        IItemHandler playerInv = new PlayerMainInvWrapper(getPlayer().inventory);

        for (int j = 0; j < 3; j++) {
            for (int i1 = 0; i1 < 9; i1++) {
                addWidget(new WidgetCTIVSlot(playerInv, i1 + j * 9 + 9, 8 + i1 * 18, 152 + j * 18, this));
            }
        }

        for (int i3 = 0; i3 < 9; i3++) {
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
                    @OnlyIn(Dist.CLIENT)
                    public void draw(Window window, int guiX, int guiY, double mouseX, double mouseY) {
                    }

                });
            }
        }
        for (int i = 3; i < 9; i++) {
            addWidget(new WidgetSlot(recipeItems, i, -18, 34 + i * 18) {

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean isHidden() {
                    return shaped || !hovering;
                }

                @Override
                @OnlyIn(Dist.CLIENT)
                public void draw(Window window, int guiX, int guiY, double mouseX, double mouseY) {
                }

            });
        }
        final ToolTip rSTT = new ToolTip(Lists.newArrayList("Toggles whether to", "show the max amount", "of craftable items.")) {

            @Override
            @OnlyIn(Dist.CLIENT)
            public void renderTooltip(int mouseX, int mouseY, int guiLeft, int guiTop) {
                mouseY += 10;
                super.renderTooltip(mouseX, mouseY, guiLeft, guiTop);
            }

        };
        final ToolTip shTT = new ToolTip(Lists.newArrayList("Toggles whether to", "show shaped recipes", "on the right side."));
        addWidget(new WidgetButton(xSize + 2, 2, 12, 12) {

            @Override
            public ToolTip getToolTip(double mouseX, double mouseY) {
                return rSTT;
            }

            @Override
            public void onButtonClicked(int mouseBttn) {
                if (getPlayer().getEntityWorld().isRemote) {
                    theTile.showRecipeSize = !recipeSize();
                }
            }

        }).setDisplayString("n");
        addWidget(new WidgetButton(xSize + 2, 15, 12, 12) {

            @Override
            public ToolTip getToolTip(double mouseX, double mouseY) {
                return shTT;
            }

            @Override
            public void onButtonClicked(int mouseBttn) {
                if (getPlayer().getEntityWorld().isRemote) {
                    theTile.showShaped = !showShaped();
                }
            }

        }).setDisplayString("s");


        if (getPlayer().getEntityWorld().isRemote) { //client
            scroll = 0.0F;
            ClientHelper.getKeyboardListener().enableRepeatEvents(true);
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            this.textField = new TextFieldWidget(RenderHelper.getMCFontrenderer(), i + 102, j + 5, 103 / 2, 10, "");
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
                    if (craftableRecipes.getAllRecipes().size() == 0) {
                        updateRecipes(); // retry
                    }
                }

            }).start();
        }

    }

    public boolean recipeSize() {
        return theTile.showRecipeSize;
    }

    public boolean showShaped() {
        return theTile.showShaped;
    }

    @Override
    @Nonnull
    public ItemStack slotClick(int slotIndex, int dragType, ClickType clickType, PlayerEntity player) {
        if (slotIndex >= 0 && getSlot(slotIndex) != null && getSlot(slotIndex) instanceof WidgetCraftSlot) {
            return ItemStackHelper.NULL_STACK;
        }
        return super.slotClick(slotIndex, dragType, clickType, player);
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(PlayerEntity player, int i) {
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
            if (stackInSlot.getCount() == 0) {
                slot.putStack(ItemStackHelper.NULL_STACK);
            } else {
                slot.onSlotChanged();
            }
        }
        return ItemStackHelper.NULL_STACK;
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity PlayerEntity) {
        return true;
    }

    //Client part

    private void stopThread() {
        if (currentThread != null) {
            currentThread.killSafe();
        }
    }

    @Override
    public void onWindowClosed(PlayerEntity playerIn) {
        super.onWindowClosed(playerIn);
        stopThread();
    }

    private void updateVisibleSlots(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }
        if (f > 1.0F) {
            f = 1.0F;
        }
        scrollValue = f;
        int numberOfRecipes = craftableRecipes.getShownSize();
        int i = (numberOfRecipes / 8 - 4) + 1;
        int j = (int) ((double) (f * (float) i) + 0.5D);
        if (j < 0) {
            j = 0;
        }
        for (int k = 0; k < 5; k++) {
            for (int l = 0; l < 8; l++) {
                int i1 = l + (k + j) * 8;
                WidgetSlot slot = getSlot(l + k * 8);
                if (i1 >= 0 && i1 < numberOfRecipes) {
                    ItemStack recipeOutput = craftableRecipes.getRecipeOutput(i1);
                    if (recipeOutput != null) {
                        if (slot instanceof WidgetCraftSlot) {
                            RecipeCache.Entry e = craftableRecipes.getShownRecipe(i1);
                            ((WidgetCraftSlot) slot).setIRecipe(e.recipe, e.amount);
                        }
                    } else {
                        if (slot instanceof WidgetCraftSlot) {
                            ((WidgetCraftSlot) slot).clearRecipe();
                        }
                    }
                } else {
                    if (slot instanceof WidgetCraftSlot) {
                        ((WidgetCraftSlot) slot).clearRecipe();
                    }
                }
            }
        }
    }

    private void updateRecipes() {
        updateRecipes(false);
    }

    private void updateRecipes(boolean txt) {
        if (txt) {
            synchronized (craftableRecipes) {
                craftableRecipes.updateVisual(getCurrentPattern());
            }
            updateVisibleSlots(scrollValue);
        } else {
            stopThread();
            currentThread = new CTIVThread();
            currentThread.start();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void handleMouseClick(WidgetSlot slot, int slotId, int mouseButton, @Nonnull ClickType type) {
        if (slot instanceof WidgetCraftSlot) {
            if (mouseButton == 1) { //mouse right
                updateRecipes();
            } else if (ClientHelper.isShiftKeyDown()) {
                onRequestMaximumRecipeOutput((WidgetCraftSlot) slot);
                updateRecipes();
            } else if (mouseButton == 0) { //mouse left
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
    @OnlyIn(Dist.CLIENT)
    protected boolean keyTyped(char c, int i) {
        if (textField.charTyped(c, i)) {
            lastTextTime = System.currentTimeMillis();
            return true;
        } else {
            return super.keyTyped(c, i);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0) {
            float oldScroll = scroll;
            Minecraft mc = Minecraft.getInstance();
            //final ScaledResolution scaledresolution = new ScaledResolution(mc);
            //int s1 = scaledresolution.getScaledWidth();
            //int s2 = scaledresolution.getScaledHeight();
            //final int i = Mouse.getX() * s1 / mc.displayWidth;
            //final int j = s2 - Mouse.getY() * s2 / mc.displayHeight - 1;
            double i = mouseX;
            double j = mouseY;
            int k = guiLeft;
            int l = guiTop;
            int i1 = k + 155;
            int j1 = l + 17;
            int k1 = i1 + 14;
            int l1 = j1 + 88 + 2;

            if (i >= i1 && j >= j1 && i < k1 && j < l1) {
                scroll = (float) (j - (j1 + 8)) / ((float) (l1 - j1) - 16F);
            }
            return postMouseEvent(oldScroll);
        }
        textField.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    protected void drawScreenPost(int mouseX, int mouseY, float partialTicks) {
        for (int b = 0; b < 9; b++) {
            recipeItems.setStackInSlot(b, ItemStackHelper.NULL_STACK);
        }

        boolean work = false;

        for (int a = 0; a < inventory.getSlots(); a++) {
            WidgetSlot slot = getSlot(a);
            if (slot instanceof WidgetCraftSlot && getIsMouseOverSlot(slot, mouseX, mouseY)) {
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
        super.drawScreenPost(mouseX, mouseY, partialTicks);

        if (!work && this.hovering) {
            this.hovering = false;
        }

        if (lastTextTime == -1) {
            return;
        }

        if (System.currentTimeMillis() - lastTextTime > 300) {
            updateRecipes(true);
            lastTextTime = -1;
        }

    }

    private ItemStack[] getIngredients(WrappedRecipe recipe, boolean shaped) {
        ItemStack[] ret = new ItemStack[9];
        int i = 0;
        for (int j = 0; j < 9; j++) {
            ItemStack stack = ItemStackHelper.NULL_STACK;
            if (recipe.getIngredientItems().length > j) {
                ItemStack[] s = recipe.getIngredientItems()[j];
                if (s != null && s.length > 0) {
                    stack = s[0].copy();
                }
            }
            if (stack.getDamage() == Short.MAX_VALUE) {
                stack.setDamage(0);
            }
            if (!shaped) {
                if (ItemStackHelper.isStackValid(stack)) {
                    ret[i] = stack;
                    i++;
                }
            } else {
                ret[j] = stack;
            }
        }

        if (!shaped) { //When not shaped, fill all empty slots with empty stacks
            for (int j = i; j < 9; j++) {
                ret[j] = ItemStack.EMPTY;
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
    @OnlyIn(Dist.CLIENT)
    protected void drawGuiContainerForegroundLayer(int i, int i1) {
        RenderHelper.getMCFontrenderer().drawString("Crafting Table IV", 8, 6, 0x404040);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.bindTexture(new ResourceLocation("craftingtableiv", "gui/crafttableii.png"));
        int l = guiLeft;
        int i1 = guiTop;
        //Background
        GuiDraw.drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        //int j1 = l + 155;
        int k1 = i1 + 17;
        int l1 = k1 + 88 + 2;
        //Scrolly bar
        GuiDraw.drawTexturedModalRect(l + 154, i1 + 17 + (int) ((float) (l1 - k1 - 17) * scroll), 0, 240, 16, 16);

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.textField.renderButton(mouseX, mouseY, partialTicks);
    }

    @Override
    protected boolean handleMouseWheel(double m, double translatedMouseX, double translatedMouseY) {
        float oldScroll = scroll;
        if (m != 0) {
            int j = (craftableRecipes.getShownSize() / 8 - 4) + 1;
            if (m > 0) {
                m = 1;
            }
            if (m < 0) {
                m = -1;
            }
            scroll -= m / (double) j;
        }
        return postMouseEvent(oldScroll);
    }


    private boolean postMouseEvent(float oldScroll) {
        if (scroll < 0.0F) {
            scroll = 0.0F;
        }
        if (scroll > 1.0F) {
            scroll = 1.0F;
        }
        if (scroll != oldScroll) {
            updateVisibleSlots(scroll);
            return true;
        }
        return false;
    }

    void onSlotChanged() {
        updateRecipes();
    }

    @SuppressWarnings("deprecation")
    private class CTIVThread extends Thread {

        CTIVThread() {
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
                Predicate<WrappedRecipe> matcher = getCurrentPattern();
                CraftingHandler.IWorldAccessibleInventory<?> wrappedInventory = CraftingHandler.forCraftingTableIV(getPlayer(), theTile);
                for (WrappedRecipe recipe : validRecipes) {
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
                    for (WrappedRecipe recipe : validRecipes) {
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

        private void killSafe() {
            stopThread = true;
        }

        private boolean stopThread = false;

        private void checkStopThread() {
            if (stopThread) {
                stop();
            }
        }

    }

    private Predicate<WrappedRecipe> getCurrentPattern() {
        return toPattern(textField);
    }

    private static Predicate<WrappedRecipe> toPattern(TextFieldWidget textField) {
        String txt = null;
        if (textField != null) {
            txt = textField.getText();
        }
        if (txt == null) {
            txt = "";
        }
        txt = txt.toLowerCase().replace(".", "").replace("?", ".").replace("*", ".+?");
        try {
            Pattern pattern = Pattern.compile(txt);
            return recipe -> pattern.matcher(recipe.itemIdentifierClientName()).find();
        } catch (Exception e) {
            return wrappedRecipe -> true;
        }
    }

}
