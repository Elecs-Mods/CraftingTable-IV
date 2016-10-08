package elec332.craftingtableiv.blocks.slot;

import elec332.craftingtableiv.abstraction.handler.WrappedRecipe;
import elec332.craftingtableiv.blocks.container.CraftingTableIVContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class SlotCrafter extends CTIVSlot {

    public SlotCrafter(IInventory craftableRecipes, int i, int j, int k, CraftingTableIVContainer container) {
        super(craftableRecipes, i, j, k, container);
    }

    private WrappedRecipe recipe;
    private static final AchievementHandler achievementHandler;

    public void setIRecipe(WrappedRecipe theIRecipe) {
        recipe = theIRecipe;
    }

    public WrappedRecipe getIRecipe() {
        return recipe;
    }

    public boolean isItemValid(ItemStack itemstack) {
        return false;
    }

    public void onPickupFromSlot(EntityPlayer player, ItemStack itemstack) {
        itemstack.onCrafting(player.worldObj, player, 1);
        achievementHandler.onCrafting(itemstack);
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