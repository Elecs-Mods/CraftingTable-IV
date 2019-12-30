package elec332.craftingtableiv.inventory;

import com.google.common.collect.Lists;
import elec332.core.client.ClientHelper;
import elec332.core.client.RenderHelper;
import elec332.core.inventory.BasicItemHandler;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.widget.Widget;
import elec332.core.inventory.widget.WidgetButton;
import elec332.core.inventory.widget.WidgetScrollArea;
import elec332.core.inventory.widget.WidgetTextField;
import elec332.core.inventory.widget.slot.WidgetSlot;
import elec332.core.inventory.window.Window;
import elec332.core.util.ItemStackHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import elec332.craftingtableiv.util.CTIVConfig;
import elec332.craftingtableiv.util.FastRecipeList;
import elec332.craftingtableiv.util.RecipeCache;
import elec332.craftingtableiv.util.WrappedRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
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

    private WidgetScrollArea scrollArea;
    private boolean shaped, hovering;
    private WidgetTextField textField;
    private CTIVThread currentThread;
    private float scrollValue = 0.0F;
    private final RecipeCache craftableRecipes;
    private long lastTextTime = -1;

    @Override
    protected void initWindow() {
        super.initWindow();
        for (int l2 = 0; l2 < 5; l2++) {
            for (int j3 = 0; j3 < 8; j3++) {
                addWidget(new WidgetCraftSlot(inventory, j3 + l2 * 8, 8 + j3 * 18, 18 + l2 * 18, this::recipeSize));
            }
        }

        for (int a = 0; a < 2; a++) {
            for (int i = 0; i < 9; i++) {
                addWidget(new WidgetSlot(theTile, i + (a * 9), 8 + i * 18, 112 + (18 * a)).addChangeListener(s -> this.onSlotChanged()));
            }
        }

        IItemHandler playerInv = new PlayerMainInvWrapper(getPlayer().inventory);

        for (int j = 0; j < 3; j++) {
            for (int i1 = 0; i1 < 9; i1++) {
                addWidget(new WidgetSlot(playerInv, i1 + j * 9 + 9, 8 + i1 * 18, 152 + j * 18).addChangeListener(s -> this.onSlotChanged()));
            }
        }

        for (int i3 = 0; i3 < 9; i3++) {
            addWidget(new WidgetSlot(playerInv, i3, 8 + i3 * 18, 211).addChangeListener(s -> this.onSlotChanged()));
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
                    public void draw(Window window, int guiX, int guiY, double mouseX, double mouseY, float partialTicks) {
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
                public void draw(Window window, int guiX, int guiY, double mouseX, double mouseY, float partialTicks) {
                }

            });
        }
        final ToolTip rSTT = new ToolTip(Lists.newArrayList("Toggles whether to", "show the max amount", "of craftable items.")).setMouseOffset(0, 10);
        final ToolTip shTT = new ToolTip(Lists.newArrayList("Toggles whether to", "show shaped recipes", "on the right side."));
        addWidget(new WidgetButton(xSize + 2, 2, 12, 12)).setDisplayString("n").setToolTip(rSTT).addButtonEventListener(button -> {
            if (getPlayer().getEntityWorld().isRemote) {
                theTile.showRecipeSize = !recipeSize();
            }
        });
        addWidget(new WidgetButton(xSize + 2, 15, 12, 12)).setDisplayString("s").setToolTip(shTT).addButtonEventListener(button -> {
            if (getPlayer().getEntityWorld().isRemote) {
                theTile.showShaped = !showShaped();
            }
        });

        this.scrollArea = addWidget(new WidgetScrollArea(155, 17, 14, 90, () -> (craftableRecipes.getShownSize() / 8 - 4) + 1));
        this.scrollArea.addListener(() -> updateVisibleSlots(scrollArea.getScroll()));
        this.scrollArea.setScroll(0);
        this.textField = addWidget(new WidgetTextField(102, 5, 103 / 2, 10, ""));
        this.textField.setTextColor(-1);
        this.textField.setDisabledTextColour(-1);
        this.textField.setEnableBackgroundDrawing(true);
        this.textField.setMaxStringLength(12);
        this.textField.addListener(s -> lastTextTime = System.currentTimeMillis());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void initClient() {
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

    private boolean recipeSize() {
        return theTile.showRecipeSize;
    }

    private boolean showShaped() {
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
    protected void handleSlotClick(WidgetSlot slot, int slotId, int mouseButton, @Nonnull ClickType type) {
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
            super.handleSlotClick(slot, slotId, mouseButton, type);
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

    private void onSlotChanged() {
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
                for (int i = 0; i < CTIVConfig.recursionDepth; i++) {
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
                if (CTIVConfig.Debug.debugTimings) {
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

    private static Predicate<WrappedRecipe> toPattern(WidgetTextField textField) {
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
