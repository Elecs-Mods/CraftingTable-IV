package elec332.craftingtableiv.blocks.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class ContainerNull extends Container {

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }

}