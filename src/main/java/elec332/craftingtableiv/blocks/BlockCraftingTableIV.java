package elec332.craftingtableiv.blocks;

import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class BlockCraftingTableIV extends BlockContainer {

    public BlockCraftingTableIV() {
        super(Material.wood);
        this.setBlockName("craftingtableiv");
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1F, 1.0F);
        setLightOpacity(0);
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        entityplayer.openGui(CraftingTableIV.instance, CraftingTableIV.guiID, world, i, j, k);
        return true;
    }

    @Override
    public void onBlockPreDestroy(World world, int par2, int par3, int par4, int par5) {
        TECraftingTableIV theTile = (TECraftingTableIV) world.getTileEntity(par2, par3, par4);
        for (int i=0; i < theTile.getSizeInventory(); i++) {
            if (theTile.getStackInSlot(i) != null) {
                WorldHelper.dropStack(world, par2, par3, par4, theTile.getStackInSlot(i));
            }

        }
        super.onBlockPreDestroy(world, par2, par3, par4, par5);
    }

    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityLiving, ItemStack p_149689_6_) {
        int i1 = MathHelper.floor_double((double) ((entityLiving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(i, j, k, i1, 2);
        super.onBlockPlacedBy(world, i, j, k, entityLiving, p_149689_6_);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TECraftingTableIV();
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public int getRenderType() {
        return -1;
    }
}
