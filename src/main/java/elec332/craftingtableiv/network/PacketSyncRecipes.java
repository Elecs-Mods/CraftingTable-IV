package elec332.craftingtableiv.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import elec332.core.network.AbstractPacket;
import elec332.craftingtableiv.blocks.container.CraftingTableIVContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class PacketSyncRecipes extends AbstractPacket {

    public PacketSyncRecipes(){
    }

    public PacketSyncRecipes(NBTTagCompound tagCompound){
        super(tagCompound);
    }

    @Override
    public IMessage onMessage(AbstractPacket abstractPacket, MessageContext messageContext) {
        Container openContainer = Minecraft.getMinecraft().thePlayer.openContainer;
        if (openContainer instanceof CraftingTableIVContainer){
            ((CraftingTableIVContainer) openContainer).craftableRecipes.readFromNBT(abstractPacket.networkPackageObject);
            ((CraftingTableIVContainer) openContainer).updateVisibleSlots(((CraftingTableIVContainer) openContainer).ScrollValue);
        }
        return null;
    }
}
