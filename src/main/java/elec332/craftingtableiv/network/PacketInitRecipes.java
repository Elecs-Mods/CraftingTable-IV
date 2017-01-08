package elec332.craftingtableiv.network;

import elec332.core.network.packets.AbstractPacket;
import elec332.craftingtableiv.CraftingTableIV;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Elec332 on 22-6-2015.
 */
public class PacketInitRecipes extends AbstractPacket {

    @Override
    public IMessage onMessageThreadSafe(AbstractPacket abstractPacket, MessageContext messageContext) {
        CraftingTableIV.instance.reloadRecipes();
        return null;
    }

}
