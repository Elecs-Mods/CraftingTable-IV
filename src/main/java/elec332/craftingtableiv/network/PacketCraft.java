package elec332.craftingtableiv.network;

import elec332.core.network.AbstractPacket;
import elec332.craftingtableiv.abstraction.CraftingTableIVAbstractionLayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Elec332 on 6-7-2015.
 */
public class PacketCraft extends AbstractPacket {

    public PacketCraft() {
    }

    public PacketCraft(NBTTagCompound tag) {
        super(tag);
    }

    @Override
    public IMessage onMessageThreadSafe(AbstractPacket abstractPacket, MessageContext messageContext) {
        CraftingTableIVAbstractionLayer.instance.onMessageReceived(abstractPacket.networkPackageObject);
        return null;
    }
}
