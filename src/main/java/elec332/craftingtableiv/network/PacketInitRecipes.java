package elec332.craftingtableiv.network;

import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.network.packets.AbstractPacket;
import elec332.craftingtableiv.CraftingTableIV;
import net.minecraft.nbt.CompoundNBT;

/**
 * Created by Elec332 on 22-6-2015.
 */
public class PacketInitRecipes extends AbstractPacket {

    @Override
    public void onMessageThreadSafe(CompoundNBT compoundNBT, IExtendedMessageContext iExtendedMessageContext) {
        CraftingTableIV.instance.reloadRecipes(iExtendedMessageContext.getSender().world.getRecipeManager());
    }

}
