package elec332.craftingtableiv.proxies;

import elec332.core.api.APIHandlerInject;
import elec332.core.api.client.model.IElecRenderingRegistry;
import elec332.core.client.RenderHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.client.CraftingTableIVRenderer;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 23-3-2015.
 */
@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    @APIHandlerInject
    private static IElecRenderingRegistry renderingRegistry;

    @Override
    public void registerRenders() {
        CraftingTableIVRenderer craftingTableIVRenderer = new CraftingTableIVRenderer();
        RenderHelper.registerTESR(TileEntityCraftingTableIV.class, craftingTableIVRenderer);
        renderingRegistry.setItemRenderer(CraftingTableIV.item, TileEntityCraftingTableIV.class);
    }

}
