package elec332.craftingtableiv.proxies;

import cpw.mods.fml.client.registry.ClientRegistry;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.client.CraftingTableIVItemRenderer;
import elec332.craftingtableiv.client.CraftingTableIVRenderer;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenders() {
        CraftingTableIVRenderer craftingTableIVRenderer = new CraftingTableIVRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TECraftingTableIV.class, craftingTableIVRenderer);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(CraftingTableIV.craftingTableIV), new CraftingTableIVItemRenderer());
    }
}
