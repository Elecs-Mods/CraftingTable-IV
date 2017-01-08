package elec332.craftingtableiv.inventory;

import elec332.core.inventory.widget.slot.WidgetSlot;
import elec332.core.util.ItemStackHelper;
import elec332.craftingtableiv.util.WrappedRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 1-1-2017.
 */
public class WidgetCraftSlot extends WidgetSlot {

    public WidgetCraftSlot(IItemHandler inventory, int index, int x, int y, WindowCraftingTableIV listener) {
        super(inventory, index, x, y);
    }

    private WrappedRecipe recipe;
    private static final AchievementHandler achievementHandler;

    public void setIRecipe(WrappedRecipe theIRecipe) {
        recipe = theIRecipe;
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
        return recipe == null ? ItemStackHelper.NULL_STACK : recipe.getRecipeOutput();
    }

    @Nonnull
    @Override
    public ItemStack onTake(EntityPlayer player, @Nonnull ItemStack stack) {
        stack.onCrafting(player.getEntityWorld(), player, 1);
        achievementHandler.onCrafting(stack);
        return super.onTake(player, stack);
    }

    static {
        achievementHandler = new AchievementHandler();
    }

    private static class AchievementHandler extends SlotCrafting {

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
