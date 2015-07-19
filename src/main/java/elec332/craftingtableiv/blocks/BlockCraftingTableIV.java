package elec332.craftingtableiv.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.baseclasses.tileentity.BlockTileBase;
import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class BlockCraftingTableIV extends BlockTileBase {

    public BlockCraftingTableIV() {
        super(Material.wood, TECraftingTableIV.class, "craftingtableiv", CraftingTableIV.ModID);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1F, 1.0F);
        setLightOpacity(0);
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        //Nope
    }

    /*@Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityLiving, ItemStack p_149689_6_) {
        int i1 = MathHelper.floor_double((double) ((entityLiving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(i, j, k, i1, 2);
        super.onBlockPlacedBy(world, i, j, k, entityLiving, p_149689_6_);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TECraftingTableIV();
    }*/

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

}
