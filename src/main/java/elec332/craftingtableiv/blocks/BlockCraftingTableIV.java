package elec332.craftingtableiv.blocks;

import com.google.common.base.Preconditions;
import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.api.client.model.template.IMutableModelTemplate;
import elec332.core.block.AbstractBlock;
import elec332.core.client.model.loading.INoJsonBlock;
import elec332.core.client.model.loading.INoJsonItem;
import elec332.core.inventory.window.WindowManager;
import elec332.core.util.BlockProperties;
import elec332.core.util.ItemStackHelper;
import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class BlockCraftingTableIV extends AbstractBlock implements INoJsonBlock, INoJsonItem {

    public BlockCraftingTableIV(ResourceLocation name) {
        super(Properties.create(Material.WOOD));
        setRegistryName(name);
    }

    @OnlyIn(Dist.CLIENT)
    private IBakedModel model;
    private static final VoxelShape BOUNDING_BOX = VoxelShapes.create(1d / 16, 0, 1d / 16, 15d / 16, 1, 15d / 16);

    @Override
    public void onBlockHarvested(World worldIn, @Nonnull BlockPos pos, BlockState state, @Nonnull PlayerEntity player) {
        TileEntityCraftingTableIV te = (TileEntityCraftingTableIV) WorldHelper.getTileAt(worldIn, pos);
        for (int i = 0; i < te.getSlots(); ++i) {
            ItemStack stack = te.getStackInSlot(i);
            if (!ItemStackHelper.isStackValid(stack)) {
                WorldHelper.dropStack(worldIn, pos, stack);
            }
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext selectionContext) {
        return BOUNDING_BOX;
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public IBakedModel getBlockModel(BlockState state) {
        return this.model;
    }

    @Override
    public IBakedModel getItemModel(ItemStack stack, World world, LivingEntity entity) {
        return this.model;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            WindowManager.openWindow(player, CraftingTableIV.proxy, world, elecByteBuf -> elecByteBuf.writeBlockPos(pos));
        }
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IElecQuadBakery quadBakery, IElecModelBakery modelBakery, IElecTemplateBakery templateBakery) {
        IMutableModelTemplate t = templateBakery.newDefaultBlockTemplate();
        t.setBuiltIn(true);
        this.model = modelBakery.itemModelForTextures(t);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerTextures(IIconRegistrar registrar) {
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(BlockProperties.FACING_NORMAL, Preconditions.checkNotNull(context.getPlayer()).getHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(BlockProperties.FACING_NORMAL);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityCraftingTableIV();
    }

}
