package elec332.craftingtableiv.inventory;

import elec332.core.inventory.widget.slot.WidgetSlot;
import elec332.core.util.ItemStackHelper;
import elec332.craftingtableiv.util.WrappedRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

/**
 * Created by Elec332 on 1-1-2017.
 */
public class WidgetCraftSlot extends WidgetSlot {

    public WidgetCraftSlot(IItemHandler inventory, int index, int x, int y, BooleanSupplier recipeSize) {
        super(inventory, index, x, y);
        this.window = recipeSize;
    }

    private BooleanSupplier window;
    private WrappedRecipe recipe;
    private int amt;
    private static final AchievementHandler achievementHandler;

    public void clearRecipe() {
        setIRecipe(null, 0);
    }

    public void setIRecipe(WrappedRecipe theIRecipe, int amt) {
        this.recipe = theIRecipe;
        this.amt = amt;
    }

    public WrappedRecipe getIRecipe() {
        return recipe;
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getStack() {
        return recipe == null ? ItemStackHelper.NULL_STACK : recipe.getRecipeOutput(window.getAsBoolean() ? amt : recipe.getOutputSize());
    }

    public int getAmount() {
        return this.amt;
    }

    @Nonnull
    @Override
    public ItemStack onTake(PlayerEntity player, @Nonnull ItemStack stack) {
        stack.onCrafting(player.getEntityWorld(), player, 1);
        achievementHandler.onCrafting(stack);
        return super.onTake(player, stack);
    }

    static {
        achievementHandler = new AchievementHandler();
    }

    private static class AchievementHandler extends CraftingResultSlot {

        @SuppressWarnings("all")
        public AchievementHandler() {
            super(null, null, null, 0, 0, 0);
        }

        @Override
        public void onCrafting(ItemStack stack) {
            super.onCrafting(stack);
        }

    }

}
