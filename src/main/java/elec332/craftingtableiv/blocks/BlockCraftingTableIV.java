package elec332.craftingtableiv.blocks;

import elec332.core.client.model.ElecModelBakery;
import elec332.core.client.model.ElecQuadBakery;
import elec332.core.client.model.INoJsonBlock;
import elec332.core.client.model.RenderingRegistry;
import elec332.core.client.model.model.IBlockModel;
import elec332.core.client.model.model.TESRItemModel;
import elec332.core.client.model.template.ElecTemplateBakery;
import elec332.craftingtableiv.client.CraftingTableIVRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import elec332.core.tile.BlockTileBase;
import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class BlockCraftingTableIV extends BlockTileBase implements INoJsonBlock {

    public BlockCraftingTableIV() {
        super(Material.wood, TileEntityCraftingTableIV.class, "craftingtableiv", CraftingTableIV.ModID);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1F, 1.0F);
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
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 2;
    }

    /**
     * This method is used when a model is requested to render the block in a world.
     *
     * @param state The current BlockState.
     * @param iba   The IBlockAccess the block is in.
     * @param pos   The position of the block.
     * @return The model to render for this block for the given arguments.
     */
    @Override
    public IBlockModel getBlockModel(IBlockState state, IBlockAccess iba, BlockPos pos) {
        return null;
    }

    /**
     * This method is used when a model is requested when its not placed, so for an item.
     *
     * @param item
     * @param meta
     * @return The model to render when the block is not placed.
     */
    @Override
    public IBakedModel getBlockModel(Item item, int meta) {
        return this.model;
    }

    /**
     * A helper method to prevent you from having to hook into the event,
     * use this to make your quads. (This always comes AFTER the textures are loaded)
     *
     * @param quadBakery     The QuadBakery.
     * @param modelBakery
     * @param templateBakery
     */
    @Override
    public void registerModels(ElecQuadBakery quadBakery, ElecModelBakery modelBakery, ElecTemplateBakery templateBakery) {
        this.model = new TESRItemModel(new CraftingTableIVRenderer());
    }

    /**
     * A helper method to prevent you from having to hook into the event,
     * use this to register your textures.
     *
     * @param textureMap The TextureMap.
     */
    @Override
    public void registerTextures(TextureMap textureMap) {
    }

}
