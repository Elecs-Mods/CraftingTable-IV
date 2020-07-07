package elec332.craftingtableiv.client;

import elec332.core.api.client.IRenderMatrix;
import elec332.core.api.client.ITextureLocation;
import elec332.core.client.RenderHelper;
import elec332.core.client.model.legacy.AbstractLegacyTileEntityRenderer;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 24-3-2015.
 */
public class CraftingTableIVRenderer extends AbstractLegacyTileEntityRenderer<TileEntityCraftingTableIV> {

    public CraftingTableIVRenderer() {
        modelCraftingTable = new ModelCraftingTableIV();
    }

    private final ModelCraftingTableIV modelCraftingTable;
    private static final ITextureLocation rl;

    @Override
    public void render(TileEntityCraftingTableIV tile, float partialTicks, IRenderMatrix renderMatrix) {
        float doorRotation = 0;
        int facing = 0;
        if (tile != null) {
            doorRotation = tile.doorAngle;
            facing = tile.getFacing();
        }
        float r = facing * 90f;

        renderMatrix.push();
        renderMatrix.translate(0.5, 1, 0.5);
        renderMatrix.rotateDegrees(270 - r, 0, 1, 0);
        renderMatrix.bindTexture(rl);
        renderMatrix.scale(-1, -1, 1);

        modelCraftingTable.setDoorRotation(doorRotation);
        modelCraftingTable.render(renderMatrix, renderMatrix.getLight(), renderMatrix.getOverlay(), 1, 1, 1, 1);

        renderMatrix.pop();
    }

    static {
        rl = RenderHelper.createTextureLocation(new ResourceLocation("craftingtableiv", "blocktextures/ctiv.png"));
    }

}