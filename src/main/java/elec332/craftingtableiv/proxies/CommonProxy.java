package elec332.craftingtableiv.proxies;

import elec332.core.inventory.window.IWindowHandler;
import elec332.core.inventory.window.Window;
import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.inventory.WindowCraftingTableIV;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class CommonProxy implements IWindowHandler {

    public void registerRenders(){
    }

    @Override
    public Window createWindow(byte id, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        return new WindowCraftingTableIV((TileEntityCraftingTableIV) WorldHelper.getTileAt(world, new BlockPos(x, y, z)));
    }

}
