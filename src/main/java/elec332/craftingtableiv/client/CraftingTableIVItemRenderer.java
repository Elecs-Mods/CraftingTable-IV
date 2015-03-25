package elec332.core.craftingtableiv.client;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by Elec332 on 24-3-2015.
 */
public class CraftingTableIVItemRenderer implements IItemRenderer {
    private ModelCraftingTableIV modelCraftingTableIV;

    public CraftingTableIVItemRenderer() {
        modelCraftingTableIV = new ModelCraftingTableIV();
    }

    //@Override
    //public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    //return true;
    //}

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        switch (type)
        {
            case ENTITY:
                return true;
            case EQUIPPED:
                return true;
            case EQUIPPED_FIRST_PERSON:
                return true;
            case INVENTORY:
                return true;
            default:
                return false;
        }
    }

    private void render(RenderBlocks render, ItemStack item, float x, float y, float z, float scaleq) {
        //this.renderer.renderTileEntityAt(dummyTE, x, y, z, 0.0F);
        //float scale = 0.07f;
        //float rotation = getRotation(engine);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5f, (float) y, (float) z + 0.5f);
        //GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);
        //GL11.glScalef(scale, scale, scale);
        ResourceLocation test = new ResourceLocation("craftingtableiv", "blocktextures/ctiv.png");
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(test);
        modelCraftingTableIV.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data){
        float scale = 0.08f;
        switch (type) {
            case ENTITY:
                render((RenderBlocks) data[0], item, 0, 0, 0, scale);
                break;
            case EQUIPPED:
                render((RenderBlocks) data[0], item, 0, 0, 0.5f, scale);
                break;
            case EQUIPPED_FIRST_PERSON:
                render((RenderBlocks) data[0], item, +0.5f, 0.5f, +0.5f, scale);
                break;
            case INVENTORY:
                render((RenderBlocks) data[0], item, -0.5f, -0.5f, -0.5f, scale);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return true;
    }
}