package elec332.craftingtableiv.blocks.slot;

import elec332.craftingtableiv.blocks.container.CraftingTableIVContainer;
import elec332.craftingtableiv.handler.WrappedRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class SlotCrafter extends Slot {

    private WrappedRecipe recipe;
    public SlotCrafter(IInventory craftableRecipes, int i, int j, int k) {
        super(craftableRecipes, i, j, k);
    }

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
        if(itemstack.getItem() == Item.getItemFromBlock(Blocks.crafting_table)) {
            player.addStat(AchievementList.buildWorkBench, 1);
        } else if(itemstack.getItem() == Items.wooden_pickaxe) {
            player.addStat(AchievementList.buildPickaxe, 1);
        } else if(itemstack.getItem() == Item.getItemFromBlock(Blocks.furnace)) {
            player.addStat(AchievementList.buildFurnace, 1);
        } else if(itemstack.getItem() == Items.wooden_hoe) {
            player.addStat(AchievementList.buildHoe, 1);
        } else if(itemstack.getItem() == Items.bread) {
            player.addStat(AchievementList.makeBread, 1);
        } else if(itemstack.getItem() == Items.cake) {
            player.addStat(AchievementList.bakeCake, 1);
        } else if(itemstack.getItem() == Items.stone_pickaxe) {
            player.addStat(AchievementList.buildBetterPickaxe, 1);
        } else if(itemstack.getItem() == Items.wooden_sword) {
            player.addStat(AchievementList.buildSword, 1);
        } else if(itemstack.getItem() == Item.getItemFromBlock(Blocks.enchanting_table)) {
            player.addStat(AchievementList.enchantments, 1);
        } else if(itemstack.getItem() == Item.getItemFromBlock(Blocks.bookshelf)) {
            player.addStat(AchievementList.bookcase, 1);
        }
    }
}