package elec332.craftingtableiv.blocks;

import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.client.RenderHelper;
import elec332.core.client.model.ElecModelBakery;
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
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
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
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntityCraftingTableIV theTile = (TileEntityCraftingTableIV) WorldHelper.getTileAt(worldIn, pos);
        for (int i=0; i < theTile.getSizeInventory(); i++) {
            if (theTile.getStackInSlot(i) != null) {
                WorldHelper.dropStack(worldIn, pos, theTile.getStackInSlot(i));
            }
        }
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
        this.model = /*new AbstractItemModel() {

            CraftingTableIVRenderer renderer = new CraftingTableIVRenderer(){

                @Override
                protected void init() {
                    setRendererDispatcher(TileEntityRendererDispatcher.instance);
                }

            };

            ModelCraftingTableIV modelCraftingTable = new ModelCraftingTableIV();

            @Override
            public List<BakedQuad> getGeneralQuads() {
                Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer vb = tessellator.getBuffer();
                boolean draw = RenderHelper.isBufferDrawing(vb);
                int mode = 7;
                VertexFormat format = null;
                if (draw){
                    mode = vb.getDrawMode();
                    format = vb.getVertexFormat();
                    tessellator.draw();
                }
                GlStateManager.pushMatrix();
                //GlStateManager.translate(0.5D, 0.5D, 0.5D);
                //GlStateManager.scale(-1.0F, -1.0F, 1.0F);
                //GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                //GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                //GlStateManager.enableRescaleNormal();

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableRescaleNormal();
                GlStateManager.depthMask(true);
                //TileEntityRendererDispatcher.instance.renderTileEntity();
                renderer.renderTileEntityAt(null, 0, 0, 0, -1, 0);
                //modelCraftingTable.render(null, 2, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                GlStateManager.popMatrix();
                if (draw){
                    vb.begin(mode, format);
                }
                return EMPTY_LIST;
            }

            @Override
            public ResourceLocation getTextureLocation() {
                return new ResourceLocation("craftingtableiv", "blocktextures/ctiv.png");
            }

            @Override
            public boolean isAmbientOcclusion() {
                return true;
            }

            @Override
            public boolean isGui3d() {
                return true;
            }

            @Override
            public ItemCameraTransforms getItemCameraTransforms() {
                return ItemCameraTransforms.DEFAULT;
            }
        };*new TESRItemModel(new CraftingTableIVRenderer()){

            @Override
            public boolean isItemTESR() {
                return true;
            }

            @Override
            public boolean isGui3d() {
                return true;
            }

            @Override
            public ItemCameraTransforms getItemCameraTransforms() {
                return ItemCameraTransforms.DEFAULT;
            }

            @Override
            public void renderTesr() {
                TileEntitySpecialRenderer r = new CraftingTableIVRenderer();
                r.setRendererDispatcher(TileEntityRendererDispatcher.instance);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                ///////
                //GlStateManager.depthMask(false);
               // GlStateManager.depthFunc(514);
//GlStateManager.disableRescaleNormal();
                //////
                GlStateManager.scale(0.5, .5, .5);
//                TileEntityRendererDispatcher.instance.renderTileEntityAt(new TileEntityCraftingTableIV(), 0.0D, 0.0D, 0.0D, 0.0F);
                r.renderTileEntityAt(null, 0, 0, 0, -1, 0);
               // GlStateManager.depthFunc(515);
               // GlStateManager.depthMask(true);
            }

        };*/
        new TESRItemModel(new CraftingTableIVRenderer()) {
            /*@Override
            public List<BakedQuad> getGeneralQuads() {
                return EMPTY_LIST;
            }
*/
            @Override
            public ResourceLocation getTextureLocation() {
                return null;
            }

            @Override
            public boolean isItemTESR() {
                return true;
            }

            @Override
            public boolean isGui3d() {
                return true;
            }

            @Override
            public ItemCameraTransforms getItemCameraTransforms() {
                return ElecModelBakery.DEFAULT_ITEM;
            }

            @Override
            public void renderTesr() {
                super.renderTesr();
            }
        };
        //ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(this), 0, TileEntityCraftingTableIV.class);
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
