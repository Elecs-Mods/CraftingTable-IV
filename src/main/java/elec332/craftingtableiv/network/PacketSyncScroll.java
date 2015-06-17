package elec332.craftingtableiv.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import elec332.core.network.AbstractPacket;
import elec332.craftingtableiv.blocks.container.CraftingTableIVContainer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 17-6-2015.
 */
public class PacketSyncScroll extends AbstractPacket {

    public PacketSyncScroll(){
    }

    public PacketSyncScroll(NBTTagCompound tagCompound){
        super(tagCompound);
    }

    @Override
    public IMessage onMessage(AbstractPacket abstractPacket, MessageContext messageContext) {
        Container openContainer = messageContext.getServerHandler().playerEntity.openContainer;
        if (openContainer instanceof CraftingTableIVContainer){
            ((CraftingTableIVContainer) openContainer).updateVisibleSlots(abstractPacket.networkPackageObject.getFloat("scroll"));
        }
        return null;
    }
}
