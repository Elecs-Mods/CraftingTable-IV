package elec332.craftingtableiv;

import elec332.core.api.IElecCoreMod;
import elec332.core.api.network.ModNetworkHandler;
import elec332.core.api.util.IDependencyHandler;
import elec332.core.inventory.window.WindowManager;
import elec332.core.main.ElecCore;
import elec332.core.network.IElecNetworkHandler;
import elec332.core.util.FileHelper;
import elec332.core.util.MCModInfo;
import elec332.core.util.RegistryHelper;
import elec332.craftingtableiv.api.CraftingTableIVAPI;
import elec332.craftingtableiv.blocks.BlockCraftingTableIV;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.handler.RecipeHandler;
import elec332.craftingtableiv.handler.vanilla.ForgeRecipeHandler;
import elec332.craftingtableiv.handler.vanilla.VanillaRecipeHandler;
import elec332.craftingtableiv.network.PacketCraft;
import elec332.craftingtableiv.network.PacketInitRecipes;
import elec332.craftingtableiv.proxies.CommonProxy;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 23-3-2015.
 */
@Mod(modid = CraftingTableIV.ModID, name = CraftingTableIV.ModName, dependencies = "required-after:eleccore@[#ELECCORE_VER#,)",
        acceptedMinecraftVersions = "[1.10.2,)", useMetadata = true, canBeDeactivated = true)
public class CraftingTableIV implements IElecCoreMod, IDependencyHandler {

    public static final String ModName = "CraftingTable-IV"; //Human readable name
    public static final String ModID = "craftingtableiv";  //modid (usually lowercase)

    @SidedProxy(clientSide = "elec332.craftingtableiv.proxies.ClientProxy", serverSide = "elec332.craftingtableiv.proxies.CommonProxy")
    public static CommonProxy proxy;
    public static int guiID = 333;
    public static Block craftingTableIV;

    @Mod.Instance(ModID)
    public static CraftingTableIV instance;
    @ModNetworkHandler
    public static IElecNetworkHandler networkHandler;
    public static Logger logger;
    private Configuration config;

    /**Config**/
    public static int recursionDepth = 5;
    public static boolean nuggetFilter = false;
    public static boolean enableDoor = true;
    public static boolean enableNoise = true;
    public static String[] disabledMods, defaultDisabledMods = {
            "ztones", "agricraft"
    };
    public static boolean debugTimings = true;
    public static float doorRange = 7f;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        //setting up mod stuff
        this.config = new Configuration(FileHelper.getConfigFileElec(event));

        MCModInfo.createMCModInfo(event, "Created by Elec332",
                "The CraftingTableIV mod is the successor of the CraftingTable III mod from the old tekkit days.",
                "No Link", "path/to/logo.png",
                new String[]{"Elec332"});
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityCraftingTableIV.class, "test");
        craftingTableIV = GameRegistry.register(new BlockCraftingTableIV()).setCreativeTab(CreativeTabs.DECORATIONS);
        GameRegistry.register(new ItemBlock(craftingTableIV).setRegistryName(craftingTableIV.getRegistryName()));
        WindowManager.INSTANCE.register(proxy, new ResourceLocation(ModID, "windowfactory"));
        proxy.registerRenders();
        networkHandler.registerClientPacket(PacketInitRecipes.class);
        networkHandler.registerServerPacket(PacketCraft.class);
        GameRegistry.addShapelessRecipe(new ItemStack(craftingTableIV), Blocks.CRAFTING_TABLE, Items.BOOK);
        //register item/block

        config.load();
        recursionDepth = config.getInt("Recursion depth", "general", 5, 0, 10, "Set to 0 to disable recursion");
        //nuggetFilter = config.getBoolean("NuggetFilter", "general", true, "Filters nuggets out of the recipeList, only disable if you know what you're doing!");
        enableDoor = config.getBoolean("EnableDoor", "general", true, "Set to false to disable the opening door on the CTIV");
        enableNoise = config.getBoolean("EnableNoise", "general", true, "Set to false to disable the door noise when opening and closing");
        disabledMods = config.getStringList("DisabledMods", "general", defaultDisabledMods, "Every item from the modID's specified here will not show up in the CraftingTable");
        debugTimings = config.getBoolean("DebugTimings","debug", true, "When true, will print messages to the log regarding how long it took to load all recipes in de CTIV bench (when opened)");
        doorRange = config.getFloat("Doorrange", "general", doorRange, 0, 100, "The squared distance from craftingtable -> player at which the door will start opening.");
        if (config.hasChanged()) {
            config.save();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        //Mod compat stuff
        registerVanillaHandlers();
        RecipeHandler.getCompatHandler().closeRegistry();
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event){
        MinecraftForge.EVENT_BUS.register(this);
        reloadRecipes();
    }

    @Override
    public String getRequiredForgeVersion(String mcVersion) {
        return mcVersion.equals("1.11") ? "13.19.0.2149" : null;
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        networkHandler.sendTo(new PacketInitRecipes(), (EntityPlayerMP)event.player);
    }

    public void reloadRecipes(){
        Long l = System.currentTimeMillis();
        CraftingHandler.rebuildList();
        logger.info("Initialised " + CraftingHandler.getAllRecipes().size() + " recipes in " + (System.currentTimeMillis() - l) + " ms");
    }

    private void registerVanillaHandlers(){
        CraftingTableIVAPI.getAPI().registerHandler(new ForgeRecipeHandler());
        CraftingTableIVAPI.getAPI().registerHandler(new VanillaRecipeHandler());
    }

    public void sendCraftingMessage(CraftingHandler.IWorldAccessibleInventory inventory, NBTTagCompound recipe){
        NBTTagCompound message = new NBTTagCompound();
        NBTTagCompound iwa = new NBTTagCompound();
        inventory.writeToNBT(iwa);
        iwa.setString("iwa_ident", inventory.getClass().getName());
        message.setTag("iwa", iwa);
        message.setTag("recipe", recipe);
        networkHandler.sendToServer(new PacketCraft(message));
    }

    /**
     * Returns a string that will not be visible to the player, containing all the item data
     * (tooltip), used for the search bar. Will only ever get called on the client.
     *
     * @param stack The stack the String is requested from.
     * @return A String identifying the stack.
     */
    @SideOnly(Side.CLIENT)
    public String getFullItemName(ItemStack stack) {
        StringBuilder stringBuilder = new StringBuilder();
        List tooltip = stack.getTooltip(ElecCore.proxy.getClientPlayer(), net.minecraft.client.Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        boolean appendH = false;
        for (Object o : tooltip){
            stringBuilder.append(o);
            if (appendH){
                stringBuilder.append("#");
            } else appendH = true;
        }
        return stringBuilder.toString().toLowerCase();
    }

    @SuppressWarnings("all")
    public static String getItemRegistryName(ItemStack stack) {
        ResourceLocation rl = RegistryHelper.getItemRegistry().getNameForObject(stack.getItem());
        if (rl == null){ //...
            CraftingTableIV.logger.info("Found a recipe with an unregistered item! "+stack.getItem().toString());
            return null;
        }
        return rl.toString();
    }

    public static String getItemIdentifier(@Nonnull ItemStack stack){
        return RegistryHelper.getItemRegistry().getNameForObject(stack.getItem()).getResourceDomain();
    }

}
