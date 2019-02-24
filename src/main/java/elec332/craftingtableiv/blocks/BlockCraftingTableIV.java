package elec332.craftingtableiv.blocks;

import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.api.client.model.template.IMutableModelTemplate;
import elec332.core.block.AbstractBlock;
import elec332.core.client.model.loading.INoJsonBlock;
import elec332.core.client.model.loading.INoJsonItem;
import elec332.core.inventory.window.WindowManager;
import elec332.core.util.IBlockStateHelper;
import elec332.core.util.ItemStackHelper;
import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 23-3-2015.
 */
@SuppressWarnings("deprecation")
public class BlockCraftingTableIV extends AbstractBlock implements INoJsonBlock, INoJsonItem, ITileEntityProvider {

    public BlockCraftingTableIV(ResourceLocation name) {
        super(Material.WOOD);
        setRegistryName(name);
        setDefaultState(IBlockStateHelper.FACING_NORMAL.setDefaultMetaState(this));
        setLightOpacity(0);
    }

    @SideOnly(Side.CLIENT)
    private IBakedModel model;
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(1d/16, 0, 1d/16, 15d/16, 1, 15d/16);

    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntityCraftingTableIV te = (TileEntityCraftingTableIV) WorldHelper.getTileAt(worldIn, pos);
        for(int i = 0; i < te.getSlots(); ++i) {
            ItemStack stack = te.getStackInSlot(i);
            if (!ItemStackHelper.isStackValid(stack)) {
                WorldHelper.dropStack(worldIn, pos, stack);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
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
        return this.model;
    }

    @Override
    public IBakedModel getItemModel(ItemStack stack, World world, EntityLivingBase entity) {
        return this.model;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, RayTraceResult hit) {
        if (!world.isRemote) {
            WindowManager.openWindow(player, CraftingTableIV.proxy, world, pos, CraftingTableIV.guiID);
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IElecQuadBakery quadBakery, IElecModelBakery modelBakery, IElecTemplateBakery templateBakery) {
        IMutableModelTemplate t = templateBakery.newDefaultBlockTemplate();
        t.setBuiltIn(true);
        this.model = modelBakery.itemModelForTextures(t);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTextures(IIconRegistrar registrar) {
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(IBlockStateHelper.FACING_NORMAL.getProperty(), placer.getHorizontalFacing().getOpposite());
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return IBlockStateHelper.FACING_NORMAL.getStateForMeta(this, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return IBlockStateHelper.FACING_NORMAL.getMetaForState(state);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return IBlockStateHelper.FACING_NORMAL.createMetaBlockState(this);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileEntityCraftingTableIV();
    }

}
