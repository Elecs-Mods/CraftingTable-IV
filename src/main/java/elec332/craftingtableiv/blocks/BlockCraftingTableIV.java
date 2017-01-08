package elec332.craftingtableiv.blocks;

import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.client.RenderHelper;
import elec332.core.client.model.loading.INoJsonBlock;
import elec332.core.client.model.model.TESRItemModel;
import elec332.core.tile.BlockTileBase;
import elec332.core.util.BlockStateHelper;
import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.client.CraftingTableIVRenderer;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class BlockCraftingTableIV extends BlockTileBase implements INoJsonBlock {

    public BlockCraftingTableIV() {
        super(Material.WOOD, TileEntityCraftingTableIV.class, "craftingtableiv", CraftingTableIV.ModID);
        setDefaultState(BlockStateHelper.FACING_NORMAL.setDefaultMetaState(this));
        setLightOpacity(0);
    }

    @SideOnly(Side.CLIENT)
    private IBakedModel model;

    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        WorldHelper.dropInventoryItems(worldIn, pos, (TileEntityCraftingTableIV) WorldHelper.getTileAt(worldIn, pos));
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public IBakedModel getBlockModel(IBlockState state) {
        return RenderHelper.getMissingModel();
    }

    @Override
    public IBakedModel getItemModel(ItemStack stack, World world, EntityLivingBase entity) {
        return this.model;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IElecQuadBakery quadBakery, IElecModelBakery modelBakery, IElecTemplateBakery templateBakery) {
        this.model = new TESRItemModel(new CraftingTableIVRenderer());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTextures(IIconRegistrar registrar) {
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return BlockStateHelper.FACING_NORMAL.getStateForMeta(this, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return BlockStateHelper.FACING_NORMAL.getMetaForState(state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return BlockStateHelper.FACING_NORMAL.createMetaBlockState(this);
    }

}
