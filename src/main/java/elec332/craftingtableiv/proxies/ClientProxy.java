package elec332.craftingtableiv.proxies;

import elec332.craftingtableiv.client.CraftingTableIVRenderer;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenders() {
        CraftingTableIVRenderer craftingTableIVRenderer = new CraftingTableIVRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCraftingTableIV.class, craftingTableIVRenderer);
    }

}
