package elec332.craftingtableiv;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.helper.FileHelper;
import elec332.core.helper.MCModInfo;
import elec332.core.main.ElecCTab;
import elec332.core.modBaseUtils.ModBase;
import elec332.core.modBaseUtils.ModInfo;
import elec332.core.network.NetworkHandler;
import elec332.core.util.EventHelper;
import elec332.craftingtableiv.blocks.BlockCraftingTableIV;
import elec332.craftingtableiv.handler.CraftingHandler;
//import elec332.craftingtableiv.init.BlockRegister;
import elec332.craftingtableiv.network.*;
import elec332.craftingtableiv.proxies.CommonProxy;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.io.File;

/**
 * Created by Elec332 on 23-3-2015.
 */
@Mod(modid = CraftingTableIV.ModID, name = CraftingTableIV.ModName, dependencies = ModInfo.DEPENDENCIES+"@[#ELECCORE_VER#,)",
        acceptedMinecraftVersions = ModInfo.ACCEPTEDMCVERSIONS, useMetadata = true, canBeDeactivated = true)
public class CraftingTableIV extends ModBase {

    public static final String ModName = "CraftingTable-IV"; //Human readable name
    public static final String ModID = "CraftingTableIV";  //modid (usually lowercase)

    @SidedProxy(clientSide = "elec332.craftingtableiv.proxies.ClientProxy", serverSide = "elec332.craftingtableiv.proxies.CommonProxy")
    public static CommonProxy proxy;
    public static int guiID = 333;
    public static Block craftingTableIV;

    @Mod.Instance(ModID)
    public static CraftingTableIV instance;
    public static NetworkHandler networkHandler;

    /**Config**/
    public static int recursionDepth = 5;
    public static boolean nuggetFilter = true;
    public static boolean enableDoor = true;
    public static boolean enableNoise = true;
    public static String[] disabledMods;
    public static boolean debugTimings = true;
    /**********/

    private static String[] defaultDisabledMods = {"ztones"};

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.cfg = FileHelper.getConfigFileElec(event);
        loadConfiguration();
        //setting up mod stuff

        loadConfiguration();
        MCModInfo.CreateMCModInfo(event, "Created by Elec332",
                "The CraftingTableIV mod is the successor of the CraftingTable III mod from the old tekkit days.",
                "No Link", "path/to/logo.png",
                new String[]{"Elec332"});
        notifyEvent(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        networkHandler = new NetworkHandler(ModID);
        loadConfiguration();
        GameRegistry.registerTileEntity(TECraftingTableIV.class, "test");
        GameRegistry.registerBlock(craftingTableIV = new BlockCraftingTableIV().setCreativeTab(ElecCTab.ElecTab), "ctable");
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        proxy.registerRenders();
        networkHandler.registerClientPacket(PacketSyncRecipes.class);
        networkHandler.registerServerPacket(PacketSyncScroll.class);
        networkHandler.registerServerPacket(PacketSyncText.class);
        networkHandler.registerClientPacket(PacketInitRecipes.class);
        networkHandler.registerServerPacket(PacketAddStack.class);
        networkHandler.registerServerPacket(PacketDecrStackSize.class);
        networkHandler.registerServerPacket(PacketCraft.class);
        GameRegistry.addShapelessRecipe(new ItemStack(craftingTableIV), Blocks.crafting_table, Items.book);
        //register item/block

        notifyEvent(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        recursionDepth = config.getInt("Recursion depth", "general", 5, 0, 10, "Set to 0 to disable recursion");
        nuggetFilter = config.getBoolean("NuggetFilter", "general", true, "Filters nuggets out of the recipeList, only disable if you know what you're doing!");
        enableDoor = config.getBoolean("EnableDoor", "general", true, "Set to false to disable the opening door on the CTIV");
        enableNoise = config.getBoolean("EnableNoise", "general", true, "Set to false to disable the door noise when opening and closing");
        disabledMods = config.getStringList("DisabledMods", "general", defaultDisabledMods, "Every item from the modID's specified here will not show up in the CraftingTable");
        debugTimings = config.getBoolean("DebugTimings","debug", true, "When true, will print messages to the log regarding how long it took to load all recipes in de CTIV bench (when opened)");
        loadConfiguration();
        //Mod compat stuff

        notifyEvent(event);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event){
        EventHelper.registerHandlerFML(this);
        loadRecipes();
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        networkHandler.getNetworkWrapper().sendTo(new PacketInitRecipes(), (EntityPlayerMP)event.player);
    }

    public static void loadRecipes(){
        OreDictionary.initVanillaEntries();
        Long l = System.currentTimeMillis();
        CraftingHandler.InitRecipes();
        instance.info("loaded " + CraftingHandler.recipeList.size() + " recipes in " + (System.currentTimeMillis() - l) + " ms");
    }

    File cfg;

    @Override
    public File configFile() {
        return cfg;
    }

    @Override
    public String modID(){
        return ModID;
    }
}
