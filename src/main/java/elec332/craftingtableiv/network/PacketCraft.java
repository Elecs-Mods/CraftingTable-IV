package elec332.craftingtableiv.network;

import elec332.core.network.packets.AbstractPacket;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.handler.CraftingHandler;
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
        NBTTagCompound tag = abstractPacket.networkPackageObject;
        try {
            NBTTagCompound iwa = tag.getCompoundTag("iwa");
            CraftingHandler.IWorldAccessibleInventory inventory = CraftingHandler.IWorldAccessibleInventory.class.cast(Class.forName(iwa.getString("iwa_ident"), true, getClass().getClassLoader()).newInstance()).readFromNBT(iwa);
            if (inventory == null){
                CraftingTableIV.logger.error("Error processing crafing request, player no longer exists?!?");
                return null;
            }
            CraftingHandler.onMessageReceived(inventory, tag.getCompoundTag("recipe"));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return null;
    }

}
