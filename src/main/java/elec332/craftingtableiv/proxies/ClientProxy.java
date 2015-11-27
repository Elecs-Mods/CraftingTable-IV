package elec332.craftingtableiv.proxies;

import elec332.core.world.WorldHelper;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.blocks.container.GuiCTableIV;
import elec332.craftingtableiv.client.CraftingTableIVItemRenderer;
import elec332.craftingtableiv.client.CraftingTableIVRenderer;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenders() {
        CraftingTableIVRenderer craftingTableIVRenderer = new CraftingTableIVRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCraftingTableIV.class, craftingTableIVRenderer);
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(CraftingTableIV.craftingTableIV), new CraftingTableIVItemRenderer());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == CraftingTableIV.guiID)
            return new GuiCTableIV(player, (TileEntityCraftingTableIV) WorldHelper.getTileAt(world, new BlockPos(x, y, z)));
        return null;
    }

}
