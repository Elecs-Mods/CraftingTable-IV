package elec332.core.craftingtableiv;

import cpw.mods.fml.common.network.IGuiHandler;
import elec332.core.craftingtableiv.blocks.container.CraftingTableIVContainer;
import elec332.core.craftingtableiv.blocks.container.GuiCTableIV;
import elec332.core.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class GUIHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == CraftingTableIV.guiID)
            return new CraftingTableIVContainer(player, (TECraftingTableIV)world.getTileEntity(x,y,z));
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == CraftingTableIV.guiID)
            return new GuiCTableIV(player, (TECraftingTableIV)world.getTileEntity(x,y,z));
        return null;
    }
}