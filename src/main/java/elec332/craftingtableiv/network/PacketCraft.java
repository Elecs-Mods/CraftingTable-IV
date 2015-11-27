package elec332.craftingtableiv.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import elec332.core.network.AbstractPacket;
import elec332.core.util.NBTHelper;
import elec332.craftingtableiv.blocks.container.CraftingTableIVContainer;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.handler.FastRecipeList;
import elec332.craftingtableiv.handler.WrappedRecipe;
import net.minecraft.inventory.Container;

/**
 * Created by Elec332 on 6-7-2015.
 */
public class PacketCraft extends AbstractPacket {

    public PacketCraft() {
    }

    public PacketCraft(WrappedRecipe recipe) {
        super(new NBTHelper().addToTag(CraftingHandler.recipeList.indexOf(recipe), "recipe").toNBT());
    }

    @Override
    public IMessage onMessageThreadSafe(AbstractPacket abstractPacket, MessageContext messageContext) {
        Container container = messageContext.getServerHandler().playerEntity.openContainer;
        if (container instanceof CraftingTableIVContainer) {
            CraftingHandler.canPlayerCraft(((CraftingTableIVContainer) container).thePlayer, ((CraftingTableIVContainer) container).theTile, CraftingHandler.recipeList.get(abstractPacket.networkPackageObject.getInteger("recipe")), new FastRecipeList(), true);
            container.detectAndSendChanges();
        }
        return null;
    }
}
