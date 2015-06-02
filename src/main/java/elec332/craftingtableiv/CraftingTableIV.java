package elec332.craftingtableiv;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import elec332.core.helper.FileHelper;
import elec332.core.helper.MCModInfo;
import elec332.core.helper.RecipeHelper;
import elec332.core.main.ElecCTab;
import elec332.core.modBaseUtils.ModBase;
import elec332.core.modBaseUtils.ModInfo;
import elec332.craftingtableiv.blocks.BlockCraftingTableIV;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.init.BlockRegister;
import elec332.craftingtableiv.proxies.CommonProxy;
import elec332.craftingtableiv.tileentity.TECraftingTableIV;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.io.File;
import java.util.List;

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

    /**Config**/
    public static int recursionDepth = 10;
    /**********/

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.cfg = FileHelper.getConfigFileElec(event);
        loadConfiguration();
        //setting up mod stuff

        loadConfiguration();
        MCModInfo.CreateMCModInfo(event, "Created by ....",
                "mod description",
                "website link", "logo",
                new String[]{"authorList"});
        notifyEvent(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        loadConfiguration();
        BlockRegister.instance.init();
        GameRegistry.registerTileEntity(TECraftingTableIV.class, "test");
        GameRegistry.registerBlock(craftingTableIV = new BlockCraftingTableIV().setCreativeTab(ElecCTab.ElecTab), "ctable");
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GUIHandler());
        proxy.registerRenders();
        //register item/block

        notifyEvent(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        loadConfiguration();
        ItemStack stick = new ItemStack(Items.stick);
        info(RecipeHelper.getCraftingRecipe(stick).getClass().toString());
        for (Object o : ((ShapedOreRecipe)RecipeHelper.getCraftingRecipe(stick)).getInput()){
            info(o.toString()+"   "+(o instanceof ItemStack));
            info(o.toString()+"   "+(o instanceof Item));
            info(o.toString()+"   "+(o instanceof Block));
            info(o.toString()+"   "+(o instanceof ItemBlock));
            info(o.toString()+"   "+(o instanceof String));
            info(o.toString()+"   "+(o instanceof List));
            if (o instanceof List){
                for (Object obj : (List)o)
                    info(obj.toString());
            }
        }
        //Mod compat stuff

        notifyEvent(event);
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
