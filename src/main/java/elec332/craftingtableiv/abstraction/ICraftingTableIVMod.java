package elec332.craftingtableiv.abstraction;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 6-1-2016.
 */
public interface ICraftingTableIVMod {

    public List<IRecipe> getRegisteredRecipes();

    /**
     * Returns a string that will not be visible to the player, containing all the item data
     * (tooltip), used for the search bar. Will only ever get called on the client.
     *
     * @param stack The stack the String is requested from.
     * @return A String identifying the stack.
     */
    public String getFullItemName(ItemStack stack);

    public boolean isEffectiveSideClient();

    public World getWorld(int dim);

    public void sendMessageToServer(NBTTagCompound tag);

    public String getItemRegistryName(ItemStack stack);

    public World getWorld(@Nonnull TileEntity tile);

}
