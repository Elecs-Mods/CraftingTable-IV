package elec332.craftingtableiv.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import elec332.core.network.AbstractPacket;
import elec332.core.util.NBTHelper;
import elec332.craftingtableiv.blocks.container.CraftingTableIVContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 6-7-2015.
 */
public class PacketAddStack extends AbstractPacket {

    public PacketAddStack(){
    }

    public PacketAddStack(ItemStack stack){
        super(new NBTHelper().addToTag(stack.writeToNBT(new NBTTagCompound()), "stack").toNBT());
    }

    @Override
    public IMessage onMessage(AbstractPacket abstractPacket, MessageContext messageContext) {
        Container container = messageContext.getServerHandler().playerEntity.openContainer;
        if (container instanceof CraftingTableIVContainer){
            ItemStack stack = ItemStack.loadItemStackFromNBT(abstractPacket.networkPackageObject.getCompoundTag("stack"));
            if (((CraftingTableIVContainer) container).theTile.addItemStackToInventory(stack) || ((CraftingTableIVContainer) container).thePlayer.inventory.addItemStackToInventory(stack))
                return null;
        }
        return null;
    }
}
