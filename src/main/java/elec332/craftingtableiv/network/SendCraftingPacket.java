package elec332.core.craftingtableiv.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import elec332.core.craftingtableiv.blocks.container.CraftingTableIVContainer;
import elec332.core.craftingtableiv.handler.CraftingHandler;
import elec332.core.craftingtableiv.handler.ItemDetail;
import elec332.core.craftingtableiv.tileentity.TECraftingTableIV;
import elec332.core.network.AbstractPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.util.ArrayList;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class SendCraftingPacket extends AbstractPacket {

    //public SendCraftingPacket(ItemStack stack, int x, int y, int z, )

    @Override
    public IMessage onMessage(AbstractPacket message, MessageContext ctx) {
        ItemStack toMake = ItemStack.loadItemStackFromNBT(message.networkPackageObject);
        int RecipeLength = message.networkPackageObject.getInteger("num");
        IRecipe RecipeToMake = CraftingHandler.getCraftingRecipe(toMake);
        //TECraftingTableIV theTile = (TECraftingTableIV)ctx.getServerHandler().playerEntity.worldObj.getTileEntity(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());

        ArrayList<ItemDetail> Temp = new ArrayList();
        for (int i=0; i<RecipeLength; i++)
        {
            //Temp.add(new ItemDetail(dataStream.readInt(),dataStream.readInt(),dataStream.readInt(),null));
        }
        int RecipeIndex = CraftingHandler.FindRecipe(Temp, new ItemDetail(toMake));
        if (RecipeIndex > -1) {
           // CraftingTableIVContainer.onRequestSingleRecipeOutput(ctx.getServerHandler().playerEntity, RecipeToMake, theTile, RecipeIndex);
        }

        //if (Proxy.SendContainerUpdate(Proxy.getPlayer(network)))
        {
        //    Proxy.SendPacketTo(Proxy.getPlayer(network), SendUpdatePacket());
        }
        return null;
    }
}
