package elec332.craftingtableiv.proxies;

import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.client.CraftingTableIVRenderer;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class ClientProxy extends CommonProxy {

    public static List<IConfigElement> getCategories(){
        return CraftingTableIV.getConfigCategories().map(ConfigElement::new).collect(Collectors.toList());
    }

    @Override
    public void registerRenders() {
        CraftingTableIVRenderer craftingTableIVRenderer = new CraftingTableIVRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCraftingTableIV.class, craftingTableIVRenderer);
        ForgeHooksClient.registerTESRItemStack(CraftingTableIV.item, 0, TileEntityCraftingTableIV.class);
    }

}
