package elec332.craftingtableiv.abstraction;

import elec332.craftingtableiv.abstraction.handler.CraftingHandler;
import elec332.craftingtableiv.abstraction.recipes.RecipeHandler;
import elec332.craftingtableiv.abstraction.recipes.vanilla.ForgeRecipeHandler;
import elec332.craftingtableiv.abstraction.recipes.vanilla.VanillaRecipeHandler;
import elec332.craftingtableiv.api.CraftingTableIVAPI;
import elec332.craftingtableiv.api.ICraftingTableIVAPI;
import elec332.craftingtableiv.api.IRecipeHandler;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by Elec332 on 6-1-2016.
 */
public class CraftingTableIVAbstractionLayer implements ICraftingTableIVAPI {

    public CraftingTableIVAbstractionLayer(ICraftingTableIVMod mod, Logger logger){
        this.api = CraftingTableIVAPI.getAPI();
        this.mod = mod;
        this.logger = logger;
        if (instance != null)
            throw new IllegalStateException();
        instance = this;
    }

    private final Logger logger;
    public static CraftingTableIVAbstractionLayer instance;
    public final ICraftingTableIVAPI api;
    public final ICraftingTableIVMod mod;

    public void preInit(){
    }

    public void init(){
    }

    public void postInit(){
    }

    public void serverStarted(){
        registerVanillaHandlers();
        RecipeHandler.getCompatHandler().closeRegistry();
        reloadRecipes();
    }

    public void reloadRecipes(){
        Long l = System.currentTimeMillis();
        CraftingHandler.rebuildList();
        logger.info("Initialised " + -1 + " recipes in " + (System.currentTimeMillis() - l) + " ms");
    }

    @Override
    public void registerHandler(IRecipeHandler handler) throws IllegalStateException {
        this.api.registerHandler(handler);
    }

    @Override
    public void registerDisabledRecipe(Class<? extends IRecipe> recipe) throws IllegalStateException {
        this.api.registerDisabledRecipe(recipe);
    }

    @Override
    public List<IRecipeHandler> getRegistry() throws IllegalAccessError {
        return this.api.getRegistry();
    }

    @Override
    public boolean isRecipeDisabled(IRecipe recipe) {
        return this.api.isRecipeDisabled(recipe);
    }

    @Override
    public boolean isRecipeDisabled(Class<? extends IRecipe> recipeClass) {
        return this.api.isRecipeDisabled(recipeClass);
    }

    @SuppressWarnings("all")
    public void onMessageReceived(NBTTagCompound tag){
        try {
            NBTTagCompound iwa = tag.getCompoundTag("iwa");
            CraftingHandler.IWorldAccessibleInventory inventory = CraftingHandler.IWorldAccessibleInventory.class.cast(Class.forName(iwa.getString("iwa_ident"), true, getClass().getClassLoader()).newInstance()).readFromNBT(iwa);
            CraftingHandler.onMessageReceived(inventory, tag.getCompoundTag("recipe"));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void sendCraftingMessage(CraftingHandler.IWorldAccessibleInventory inventory, NBTTagCompound recipe){
        NBTTagCompound message = new NBTTagCompound();
        NBTTagCompound iwa = new NBTTagCompound();
        inventory.writeToNBT(iwa);
        iwa.setString("iwa_ident", inventory.getClass().getName());
        message.setTag("iwa", iwa);
        message.setTag("recipe", recipe);
        mod.sendMessageToServer(message);
    }

    private void registerVanillaHandlers(){
        registerHandler(new ForgeRecipeHandler());
        registerHandler(new VanillaRecipeHandler());
    }

}
