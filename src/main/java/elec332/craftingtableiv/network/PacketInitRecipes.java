package elec332.craftingtableiv.network;

import elec332.core.network.AbstractPacket;
import elec332.craftingtableiv.abstraction.CraftingTableIVAbstractionLayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Elec332 on 22-6-2015.
 */
public class PacketInitRecipes extends AbstractPacket {

    @Override
    public IMessage onMessageThreadSafe(AbstractPacket abstractPacket, MessageContext messageContext) {
        CraftingTableIVAbstractionLayer.instance.reloadRecipes();
        return null;
    }

}
