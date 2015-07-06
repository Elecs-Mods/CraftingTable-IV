package elec332.craftingtableiv.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import elec332.core.network.AbstractPacket;
import elec332.core.util.NBTHelper;
import elec332.craftingtableiv.blocks.container.CraftingTableIVContainer;
import net.minecraft.inventory.Container;

/**
 * Created by Elec332 on 6-7-2015.
 */
public class PacketDecrStackSize extends AbstractPacket {

    public PacketDecrStackSize(){
    }

    public PacketDecrStackSize(int i){
        super(new NBTHelper().addToTag(i, "i").toNBT());
    }

    @Override
    public IMessage onMessage(AbstractPacket abstractPacket, MessageContext messageContext) {
        Container container = messageContext.getServerHandler().playerEntity.openContainer;
        if (container instanceof CraftingTableIVContainer){
            int slot = abstractPacket.networkPackageObject.getInteger("i");
            int amount = 1;
            if (slot < 18)
                ((CraftingTableIVContainer) container).theTile.decrStackSize(slot, amount);
            else
                ((CraftingTableIVContainer) container).thePlayer.inventory.decrStackSize(slot-18, amount);
        }
        return null;
    }
}
