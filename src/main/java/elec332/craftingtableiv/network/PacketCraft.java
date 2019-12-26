package elec332.craftingtableiv.network;

import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.network.packets.AbstractPacket;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.handler.CraftingHandler;
import net.minecraft.nbt.CompoundNBT;

/**
 * Created by Elec332 on 6-7-2015.
 */
public class PacketCraft extends AbstractPacket {

    public PacketCraft() {
    }

    public PacketCraft(CompoundNBT tag) {
        super(tag);
    }

    @Override
    public void onMessageThreadSafe(CompoundNBT tag, IExtendedMessageContext iExtendedMessageContext) {
        try {
            CompoundNBT iwa = tag.getCompound("iwa");
            CraftingHandler.IWorldAccessibleInventory inventory = CraftingHandler.IWorldAccessibleInventory.class.cast(Class.forName(iwa.getString("iwa_ident"), true, getClass().getClassLoader()).newInstance()).readFromNBT(iwa);
            if (inventory == null) {
                CraftingTableIV.logger.error("Error processing crafing request, player no longer exists?!?");
            }
            CraftingHandler.onMessageReceived(inventory, tag.getCompound("recipe"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
