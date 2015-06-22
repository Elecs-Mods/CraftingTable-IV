package elec332.craftingtableiv.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import elec332.core.network.AbstractPacket;
import elec332.core.util.NBTHelper;
import elec332.craftingtableiv.blocks.container.CraftingTableIVContainer;
import net.minecraft.inventory.Container;

/**
 * Created by Elec332 on 21-6-2015.
 */
public class PacketSyncText extends AbstractPacket {

    public PacketSyncText(){
    }

    public PacketSyncText(String text){
        if (text == null)
            text = "";
        this.networkPackageObject = new NBTHelper().addToTag(text, "txt").toNBT();
    }

    @Override
    public IMessage onMessage(AbstractPacket abstractPacket, MessageContext messageContext) {
        Container openContainer = messageContext.getServerHandler().playerEntity.openContainer;
        if (openContainer instanceof CraftingTableIVContainer){
            ((CraftingTableIVContainer) openContainer).setText(abstractPacket.networkPackageObject.getString("txt"));
        }
        return null;
    }
}
