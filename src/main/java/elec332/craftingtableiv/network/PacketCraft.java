package elec332.craftingtableiv.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import elec332.core.network.AbstractPacket;
import elec332.core.util.NBTHelper;
import elec332.craftingtableiv.abstraction.CraftingTableIVAbstractionLayer;
import elec332.craftingtableiv.blocks.container.CraftingTableIVContainer;
import elec332.craftingtableiv.abstraction.handler.CraftingHandler;
import elec332.craftingtableiv.abstraction.handler.FastRecipeList;
import elec332.craftingtableiv.abstraction.handler.WrappedRecipe;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;

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
    public IMessage onMessage(AbstractPacket abstractPacket, MessageContext messageContext) {
        CraftingTableIVAbstractionLayer.instance.onMessageReceived(abstractPacket.networkPackageObject);
        return null;
    }
}
