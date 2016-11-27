package elec332.craftingtableiv.abstraction;

import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.abstraction.handler.CraftingHandler;
import elec332.craftingtableiv.abstraction.recipes.RecipeHandler;
import elec332.craftingtableiv.abstraction.recipes.vanilla.ForgeRecipeHandler;
import elec332.craftingtableiv.abstraction.recipes.vanilla.VanillaRecipeHandler;
import elec332.craftingtableiv.api.CraftingTableIVAPI;
import elec332.craftingtableiv.api.ICraftingTableIVAPI;
import elec332.craftingtableiv.api.IRecipeHandler;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.File;
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

    public final Logger logger;
    public static CraftingTableIVAbstractionLayer instance;
    public final ICraftingTableIVAPI api;
    public final ICraftingTableIVMod mod;
    private Configuration config;

    /**Config**/
    public static int recursionDepth = 5;
    public static boolean nuggetFilter = true;
    public static boolean enableDoor = true;
    public static boolean enableNoise = true;
    public static String[] disabledMods, defaultDisabledMods = {"ztones"};
    public static boolean debugTimings = true;
    public static float doorRange = 7f;
    /**********/

    public void preInit(File configLocation){
        this.config = new Configuration(configLocation);
    }

    public void init(){
        config.load();
        recursionDepth = config.getInt("Recursion depth", "general", 5, 0, 10, "Set to 0 to disable recursion");
        nuggetFilter = config.getBoolean("NuggetFilter", "general", true, "Filters nuggets out of the recipeList, only disable if you know what you're doing!");
        enableDoor = config.getBoolean("EnableDoor", "general", true, "Set to false to disable the opening door on the CTIV");
        enableNoise = config.getBoolean("EnableNoise", "general", true, "Set to false to disable the door noise when opening and closing");
        disabledMods = config.getStringList("DisabledMods", "general", defaultDisabledMods, "Every item from the modID's specified here will not show up in the CraftingTable");
        debugTimings = config.getBoolean("DebugTimings","debug", true, "When true, will print messages to the log regarding how long it took to load all recipes in de CTIV bench (when opened)");
        doorRange = config.getFloat("Doorrange", "general", doorRange, 0, 100, "The squared distance from craftingtable -> player at which the door will start opening.");
        if (config.hasChanged()) {
            config.save();
        }
    }

    public void postInit(){
        registerVanillaHandlers();
        RecipeHandler.getCompatHandler().closeRegistry();
    }

    public void serverStarted(){
        reloadRecipes();
    }

    public void reloadRecipes(){
        Long l = System.currentTimeMillis();
        CraftingHandler.rebuildList();
        logger.info("Initialised " + CraftingHandler.getAllRecipes().size() + " recipes in " + (System.currentTimeMillis() - l) + " ms");
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
            if (inventory == null){
                CraftingTableIV.logger.error("Error processing crafing request, player no longer exists?!?");
                return;
            }
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

    public Configuration getConfig() {
        return this.config;
    }

}
