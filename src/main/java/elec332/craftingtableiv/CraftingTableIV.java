package elec332.craftingtableiv;

import elec332.core.ElecCore;
import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.network.ModNetworkHandler;
import elec332.core.inventory.window.WindowManager;
import elec332.core.network.IElecNetworkHandler;
import elec332.core.util.InventoryHelper;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
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
import net.minecraftforge.registries.GameData;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Elec332 on 23-3-2015.
 */
@Mod(modid = CraftingTableIV.ModID, name = CraftingTableIV.ModName, dependencies = "required-after:eleccore@[#ELECCORE_VER#,)",
        acceptedMinecraftVersions = "[1.12,)", useMetadata = true, guiFactory = "elec332.craftingtableiv.client.GuiFactory")
public class CraftingTableIV implements IElecCoreMod {

    public static final String ModName = "CraftingTable-IV"; //Human readable name
    public static final String ModID = "craftingtableiv";  //modid (usually lowercase)

    @SidedProxy(clientSide = "elec332.craftingtableiv.proxies.ClientProxy", serverSide = "elec332.craftingtableiv.proxies.CommonProxy")
    public static CommonProxy proxy;
    public static byte guiID = 33;
    public static Block craftingTableIV;
    public static Item item;

    @Mod.Instance(ModID)
    public static CraftingTableIV instance;
    @ModNetworkHandler
    public static IElecNetworkHandler networkHandler;
    public static Logger logger;
    private Configuration config;

    /**Config**/
    private static final String[] CONFIG_CATEGORIES = {"general", "client", "debug"};
    public static int recursionDepth = 5;
    public static boolean nuggetFilter = false;
    public static boolean enableDoor = true;
    public static boolean enableNoise = true;
    public static String[] disabledMods, defaultDisabledMods = {
            "ztones", "agricraft"
    };
    public static boolean debugTimings = true;
    public static float doorRange = 7f;
    public static boolean aggressiveLoopCheck = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        //setting up mod stuff
        this.config = new Configuration(new File(event.getModConfigurationDirectory(), "/Elec's Mods/"+ModID+".cfg"));

        //todo
        //MCModInfo.createMCModInfo(event, "Created by Elec332",
        //        "The CraftingTableIV mod is the successor of the CraftingTable III mod from the old tekkit days.",
        //        "No Link", "path/to/logo.png",
        //        new String[]{"Elec332"});
        craftingTableIV = GameData.register_impl(new BlockCraftingTableIV(new ResourceLocation(CraftingTableIV.ModID, CraftingTableIV.ModID))).setCreativeTab(CreativeTabs.DECORATIONS);
        item = GameData.register_impl(new ItemBlock(craftingTableIV).setRegistryName(craftingTableIV.getRegistryName()));

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityCraftingTableIV.class, new ResourceLocation(CraftingTableIV.ModID, "TileEntityCraftingTableIV"));
        WindowManager.INSTANCE.register(proxy, new ResourceLocation(ModID, "windowfactory"));
        proxy.registerRenders();
        networkHandler.registerClientPacket(PacketInitRecipes.class);
        networkHandler.registerServerPacket(PacketCraft.class);
        GameRegistry.addShapelessRecipe(new ResourceLocation(ModID, "ctivrecipe"), null, new ItemStack(craftingTableIV), Ingredient.fromStacks(new ItemStack(Blocks.CRAFTING_TABLE)), Ingredient.fromItem(Items.BOOK));
        //GameRegistry.addShapelessRecipe(new ItemStack(craftingTableIV), Blocks.CRAFTING_TABLE, Items.BOOK);
        //register item/block

        config.load();
        recursionDepth = config.getInt("Recursion depth", "general", 5, 0, 10, "Set to 0 to disable recursion");
        //nuggetFilter = config.getBoolean("NuggetFilter", "general", true, "Filters nuggets out of the recipeList, only disable if you know what you're doing!");
        enableDoor = config.getBoolean("EnableDoor", "client", true, "Set to false to disable the opening door on the CTIV");
        enableNoise = config.getBoolean("EnableNoise", "client", true, "Set to false to disable the door noise when opening and closing");
        disabledMods = config.getStringList("DisabledMods", "general", defaultDisabledMods, "Every item from the modID's specified here will not show up in the CraftingTable");
        debugTimings = config.getBoolean("DebugTimings","debug", true, "When true, will print messages to the log regarding how long it took to load all recipes in de CTIV bench (when opened)");
        doorRange = config.getFloat("Doorrange", "client", doorRange, 0, 100, "The squared distance from craftingtable -> player at which the door will start opening.");
        aggressiveLoopCheck = config.getBoolean("AggressiveLoopCheck", "general", false, "Whether to aggressively search for recipe loops, will cause some recipes to search less deep than normal.");
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

    public static Stream<ConfigCategory> getConfigCategories(){
        return Arrays.stream(CONFIG_CATEGORIES).map(instance.config::getCategory);
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
        List tooltip = InventoryHelper.getTooltip(stack, ElecCore.proxy.getClientPlayer(), net.minecraft.client.Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
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
        ResourceLocation rl = RegistryHelper.getItemRegistry().getKey(stack.getItem());
        if (rl == null){ //...
            CraftingTableIV.logger.info("Found a recipe with an unregistered item! "+stack.getItem().toString());
            return null;
        }
        return rl.toString();
    }

    public static String getItemIdentifier(@Nonnull ItemStack stack){
        return RegistryHelper.getItemRegistry().getKey(stack.getItem()).getResourceDomain();
    }

}
