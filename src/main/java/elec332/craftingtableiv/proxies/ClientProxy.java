package elec332.craftingtableiv.proxies;

import elec332.core.api.APIHandlerInject;
import elec332.core.api.client.model.IElecRenderingRegistry;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.client.CraftingTableIVRenderer;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class ClientProxy extends CommonProxy {

    //public static List<IConfigElement> getCategories(){
    //    return CraftingTableIV.getConfigCategories().map(ConfigElement::new).collect(Collectors.toList());
    //}

    @APIHandlerInject
    private static IElecRenderingRegistry renderingRegistry;

    @Override
    public void registerRenders() {
        CraftingTableIVRenderer craftingTableIVRenderer = new CraftingTableIVRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCraftingTableIV.class, craftingTableIVRenderer);
        renderingRegistry.setItemRenderer(CraftingTableIV.item, TileEntityCraftingTableIV.class);
    }

}
