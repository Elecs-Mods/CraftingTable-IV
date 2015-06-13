package elec332.craftingtableiv;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import elec332.core.helper.FileHelper;
import elec332.core.helper.MCModInfo;
import elec332.core.main.ElecCTab;
import elec332.core.modBaseUtils.ModBase;
import elec332.core.modBaseUtils.ModInfo;
import elec332.core.network.NetworkHandler;
import elec332.craftingtableiv.blocks.BlockCraftingTableIV;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.handler.StackComparator;
import elec332.craftingtableiv.init.BlockRegister;
import elec332.craftingtableiv.network.PacketSyncRecipes;
import elec332.craftingtableiv.proxies.CommonProxy;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.block.Block;
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
    public static int recursionDepth = 8;
    public static boolean nuggetFilter = true;
    public static boolean enableDoor = true;
    public static boolean enableNoise = true;
    /**********/

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
        BlockRegister.instance.init();
        GameRegistry.registerTileEntity(TECraftingTableIV.class, "test");
        GameRegistry.registerBlock(craftingTableIV = new BlockCraftingTableIV().setCreativeTab(ElecCTab.ElecTab), "ctable");
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GUIHandler());
        proxy.registerRenders();
        networkHandler.registerClientPacket(PacketSyncRecipes.class);
        //register item/block

        notifyEvent(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        loadConfiguration();
        //Mod compat stuff

        notifyEvent(event);
    }

    @Mod.EventHandler
    public void loadRecipes(FMLServerStartedEvent event){
        OreDictionary.initVanillaEntries();
        Long l = System.currentTimeMillis();
        CraftingHandler.InitRecipes();
        info("loaded "+CraftingHandler.recipeList.size()+" recipes in "+(System.currentTimeMillis()-l)+" ms");
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
