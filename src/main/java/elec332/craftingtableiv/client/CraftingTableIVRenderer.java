package elec332.craftingtableiv.client;

import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Elec332 on 24-3-2015.
 */
public class CraftingTableIVRenderer extends TileEntitySpecialRenderer<TileEntityCraftingTableIV> {

    public CraftingTableIVRenderer() {
        modelCraftingTable = new ModelCraftingTableIV();
    }

    private final ModelCraftingTableIV modelCraftingTable;
    private static final ResourceLocation rl;


    @Override
    public void render(TileEntityCraftingTableIV tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        float doorRotation = 0;
        int facing = 0;
        if (tile != null) {
            doorRotation = tile.doorAngle;
            facing = tile.getFacing();
        }
        float r = facing * 90F;

        GL11.glPushMatrix();
        GL11.glTranslatef((float)x+0.5F, (float)y+1F, (float)z+0.5F);
        GL11.glRotatef(270F - r, 0.0F, 1.0F, 0.0F);

        bindTexture(rl);
        GL11.glScalef(-1F, -1F, 1.0F);
        modelCraftingTable.render(null, doorRotation, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    static {
        rl = new ResourceLocation("craftingtableiv", "blocktextures/ctiv.png");
    }

}