package elec332.craftingtableiv;

import com.google.common.reflect.ClassPath;
import elec332.craftingtableiv.abstraction.CraftingTableIVAbstractionLayer;
import elec332.craftingtableiv.abstraction.ICraftingTableIVMod;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import elec332.core.util.FileHelper;
import elec332.core.util.MCModInfo;
import elec332.core.modBaseUtils.ModInfo;
import elec332.core.network.NetworkHandler;
import elec332.craftingtableiv.blocks.BlockCraftingTableIV;
import elec332.craftingtableiv.compat.AbstractCompatModule;
import elec332.craftingtableiv.compat.CraftingTableIVCompatHandler;
import elec332.craftingtableiv.network.PacketCraft;
import elec332.craftingtableiv.network.PacketInitRecipes;
import elec332.craftingtableiv.proxies.CommonProxy;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 23-3-2015.
 */
@Mod(modid = CraftingTableIV.ModID, name = CraftingTableIV.ModName, dependencies = ModInfo.DEPENDENCIES+"@[#ELECCORE_VER#,)",
        acceptedMinecraftVersions = "[1.8.8, 1.8.9]", useMetadata = true, canBeDeactivated = true)
public class CraftingTableIV implements ICraftingTableIVMod {

    public static final String ModName = "CraftingTable-IV"; //Human readable name
    public static final String ModID = "CraftingTableIV";  //modid (usually lowercase)

    @SidedProxy(clientSide = "elec332.craftingtableiv.proxies.ClientProxy", serverSide = "elec332.craftingtableiv.proxies.CommonProxy")
    public static CommonProxy proxy;
    public static int guiID = 333;
    public static Block craftingTableIV;

    @Mod.Instance(ModID)
    public static CraftingTableIV instance;
    public static NetworkHandler networkHandler;
    public static Logger logger;
    public static CraftingTableIVCompatHandler compatHandler;
    public static CraftingTableIVAbstractionLayer abstractionLayer;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        //setting up mod stuff
        abstractionLayer = new CraftingTableIVAbstractionLayer(this, logger);
        compatHandler = new CraftingTableIVCompatHandler();
        try {
            for (ClassPath.ClassInfo classInfo : ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClasses("elec332.craftingtableiv.compat.handlers")) {
                Class clazz = Class.forName(classInfo.getName());
                if (AbstractCompatModule.class.isAssignableFrom(clazz))
                    compatHandler.addHandler((AbstractCompatModule) clazz.newInstance());
            }
        } catch (Exception e){
            throw new RuntimeException("[CraftingTableIV] Error fetching compat handlers!", e);
        }

        abstractionLayer.preInit(FileHelper.getConfigFileElec(event));
        MCModInfo.createMCModInfo(event, "Created by Elec332",
                "The CraftingTableIV mod is the successor of the CraftingTable III mod from the old tekkit days.",
                "No Link", "path/to/logo.png",
                new String[]{"Elec332"});
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        networkHandler = new NetworkHandler(ModID);
        GameRegistry.registerTileEntity(TileEntityCraftingTableIV.class, "test");
        craftingTableIV = new BlockCraftingTableIV().register().setCreativeTab(CreativeTabs.tabDecorations);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        proxy.registerRenders();
        networkHandler.registerClientPacket(PacketInitRecipes.class);
        networkHandler.registerServerPacket(PacketCraft.class);
        GameRegistry.addShapelessRecipe(new ItemStack(craftingTableIV), Blocks.crafting_table, Items.book);
        compatHandler.init();
        //register item/block

        abstractionLayer.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        //Mod compat stuff
        abstractionLayer.postInit();
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event){
        MinecraftForge.EVENT_BUS.register(this);
        abstractionLayer.serverStarted();
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        networkHandler.getNetworkWrapper().sendTo(new PacketInitRecipes(), (EntityPlayerMP)event.player);
    }

    @Override
    public List<IRecipe> getRegisteredRecipes() {
        return CraftingManager.getInstance().getRecipeList();
    }

    /**
     * Returns a string that will not be visible to the player, containing all the item data
     * (tooltip), used for the search bar. Will only ever get called on the client.
     *
     * @param stack The stack the String is requested from.
     * @return A String identifying the stack.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public String getFullItemName(ItemStack stack) {
        StringBuilder stringBuilder = new StringBuilder();
        List tooltip = stack.getTooltip(net.minecraft.client.Minecraft.getMinecraft().thePlayer, net.minecraft.client.Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        boolean appendH = false;
        for (Object o : tooltip){
            stringBuilder.append(o);
            if (appendH){
                stringBuilder.append("#");
            } else appendH = true;
        }
        return stringBuilder.toString().toLowerCase();
    }

    @Override
    public boolean isEffectiveSideClient() {
        return FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    @Override
    public World getWorld(int dim) {
        return DimensionManager.getWorld(dim);
    }

    @Override
    public void sendMessageToServer(NBTTagCompound tag) {
        networkHandler.getNetworkWrapper().sendToServer(new PacketCraft(tag));
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getItemRegistryName(ItemStack stack) {
        return GameData.getItemRegistry().getNameForObject(stack.getItem()).toString();
    }

    @Override
    public World getWorld(@Nonnull TileEntity tile) {
        return tile.getWorld();
    }

}
