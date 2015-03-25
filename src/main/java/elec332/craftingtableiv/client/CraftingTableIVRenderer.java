package elec332.core.craftingtableiv.client;

import elec332.core.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Elec332 on 24-3-2015.
 */
public class CraftingTableIVRenderer extends TileEntitySpecialRenderer {

    private ModelCraftingTableIV modelCraftingTable;

    public CraftingTableIVRenderer() {
        modelCraftingTable = new ModelCraftingTableIV();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {

        TECraftingTableIV craftingtable = (TECraftingTableIV)tileentity;
        float doorRotation = craftingtable.doorAngle;
        int facing = craftingtable.getFacing();
        float r = facing * 90F;

        GL11.glPushMatrix();
        GL11.glTranslatef((float)d+0.5F, (float)d1+1F, (float)d2+0.5F);
        GL11.glRotatef(270F - r, 0.0F, 1.0F, 0.0F);

        bindTexture(new ResourceLocation("craftingtableiv", "blocktextures/ctiv.png"));
        GL11.glScalef(-1F, -1F, 1.0F);
        modelCraftingTable.render(null, doorRotation, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();

    }
}