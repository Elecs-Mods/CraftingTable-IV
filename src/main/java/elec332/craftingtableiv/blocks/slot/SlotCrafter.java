package elec332.craftingtableiv.blocks.slot;

import elec332.craftingtableiv.blocks.container.CraftingTableIVContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.AchievementList;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class SlotCrafter extends Slot {

    private EntityPlayer thePlayer;
    public IInventory craftMatrix;
    private IRecipe irecipe;
    public int myIndex;
    public CraftingTableIVContainer theCont;
    public SlotCrafter(EntityPlayer entityplayer, IInventory craftableRecipes, IInventory matrix, int i, int j, int k, CraftingTableIVContainer cont)
    {
        super(craftableRecipes, i, j, k);
        thePlayer = entityplayer;
        craftMatrix = matrix;
        theCont = cont;
    }

    public void setIRecipe(IRecipe theIRecipe, int theIndex)
    {
        irecipe = theIRecipe;
        myIndex = theIndex;
    }

    public IRecipe getIRecipe()
    {
        return irecipe;
    }

    public boolean isItemValid(ItemStack itemstack)
    {
        return false;
    }

    public void onPickupFromSlot(ItemStack itemstack)
    {
        itemstack.onCrafting(thePlayer.worldObj, thePlayer, 1);

        if(itemstack.getItem() == Item.getItemFromBlock(Blocks.crafting_table))
        {
            thePlayer.addStat(AchievementList.buildWorkBench, 1);
        } else
        if(itemstack.getItem() == Items.wooden_pickaxe)
        {
            thePlayer.addStat(AchievementList.buildPickaxe, 1);
        } else
        if(itemstack.getItem() == Item.getItemFromBlock(Blocks.furnace))
        {
            thePlayer.addStat(AchievementList.buildFurnace, 1);
        } else
        if(itemstack.getItem() == Items.wooden_hoe)
        {
            thePlayer.addStat(AchievementList.buildHoe, 1);
        } else
        if(itemstack.getItem() == Items.bread)
        {
            thePlayer.addStat(AchievementList.makeBread, 1);
        } else
        if(itemstack.getItem() == Items.cake)
        {
            thePlayer.addStat(AchievementList.bakeCake, 1);
        } else
        if(itemstack.getItem() == Items.stone_pickaxe)
        {
            thePlayer.addStat(AchievementList.buildBetterPickaxe, 1);
        } else
        if(itemstack.getItem() == Items.wooden_sword)
        {
            thePlayer.addStat(AchievementList.buildSword, 1);
        } else
        if(itemstack.getItem() == Item.getItemFromBlock(Blocks.enchanting_table))
        {
            thePlayer.addStat(AchievementList.enchantments, 1);
        } else
        if(itemstack.getItem() == Item.getItemFromBlock(Blocks.bookshelf))
        {
            thePlayer.addStat(AchievementList.bookcase, 1);
        }
    }

}