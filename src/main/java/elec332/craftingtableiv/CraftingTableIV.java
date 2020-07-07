package elec332.craftingtableiv;

import com.google.common.base.Preconditions;
import elec332.core.ElecCore;
import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.mod.SidedProxy;
import elec332.core.api.network.ModNetworkHandler;
import elec332.core.api.registration.IObjectRegister;
import elec332.core.api.registration.ITileRegister;
import elec332.core.api.registration.IWorldGenRegister;
import elec332.core.config.ConfigWrapper;
import elec332.core.inventory.window.WindowManager;
import elec332.core.network.IElecNetworkHandler;
import elec332.core.util.FMLHelper;
import elec332.core.util.InventoryHelper;
import elec332.core.util.RegistryHelper;
import elec332.craftingtableiv.api.CraftingTableIVAPI;
import elec332.craftingtableiv.blocks.BlockCraftingTableIV;
import elec332.craftingtableiv.handler.CraftingHandler;
import elec332.craftingtableiv.handler.RecipeHandler;
import elec332.craftingtableiv.handler.vanilla.ForgeRecipeHandler;
import elec332.craftingtableiv.handler.vanilla.VanillaRecipeHandler;
import elec332.craftingtableiv.network.PacketCraft;
import elec332.craftingtableiv.proxies.CommonProxy;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import elec332.craftingtableiv.util.CTIVConfig;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 23-3-2015.
 */
@Mod(CraftingTableIV.MODID)
public class CraftingTableIV implements IElecCoreMod, ITileRegister {

    public CraftingTableIV() {
        if (instance != null) {
            throw new RuntimeException();
        }
        instance = this;
        logger = LogManager.getLogger(MODNAME);

        IEventBus eventBus = FMLHelper.getActiveModEventBus();
        eventBus.addListener(this::preInit);
        eventBus.addListener(this::init);
        eventBus.addListener(this::postInit);
        eventBus.register(this);
        eventBus = MinecraftForge.EVENT_BUS;
        eventBus.addListener(this::serverStarted);
        eventBus.register(this);
    }

    public static final String MODID = "craftingtableiv";
    public static final String MODNAME = FMLHelper.getModNameEarly(MODID);

    @SidedProxy(clientSide = "elec332.craftingtableiv.proxies.ClientProxy", serverSide = "elec332.craftingtableiv.proxies.CommonProxy")
    public static CommonProxy proxy;
    public static Block craftingTableIV;
    public static Item item;

    public static CraftingTableIV instance;
    @ModNetworkHandler
    public static IElecNetworkHandler networkHandler;
    public static Logger logger;
    private ConfigWrapper config;

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> blockRegister) {
        craftingTableIV = GameData.register_impl(new BlockCraftingTableIV(new ResourceLocation(CraftingTableIV.MODID, CraftingTableIV.MODID)));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> itemRegister) {
        item = GameData.register_impl(new BlockItem(craftingTableIV, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(Preconditions.checkNotNull(craftingTableIV.getRegistryName())));
    }

    @Override
    public void register(IForgeRegistry<TileEntityType<?>> registry) {
        RegistryHelper.registerTileEntity(TileEntityCraftingTableIV.class, new ResourceLocation(CraftingTableIV.MODID, "tileentity_craftingtableiv"));
    }

    @Override
    public void registerRegisters(Consumer<IObjectRegister<?>> objectHandler, Consumer<IWorldGenRegister> worldHandler) {
        objectHandler.accept(this);
    }

    private void preInit(FMLCommonSetupEvent event) {
        this.config = new ConfigWrapper(this);
    }

    public void init(InterModEnqueueEvent event) {
        WindowManager.INSTANCE.register(proxy, new ResourceLocation(MODID, "windowfactory"));
        proxy.registerRenders();
        networkHandler.registerAbstractPacket(PacketCraft.class);

        //Replaced by a stupid JSON...
        //GameRegistry.addShapelessRecipe(new ResourceLocation(MODID, "ctivrecipe"), null, new ItemStack(craftingTableIV), Ingredient.fromStacks(new ItemStack(Blocks.CRAFTING_TABLE)), Ingredient.fromItem(Items.BOOK));
        //GameRegistry.addShapelessRecipe(new ItemStack(craftingTableIV), Blocks.CRAFTING_TABLE, Items.BOOK);

        config.registerConfigWithInnerClasses(CTIVConfig.class);
        config.register();
    }

    private void postInit(InterModProcessEvent event) {
        //Mod compat stuff
        registerVanillaHandlers();
        RecipeHandler.getCompatHandler().closeRegistry();
    }

    private void serverStarted(FMLServerStartedEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        reloadRecipes(event.getServer().getRecipeManager());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRecipesReload(RecipesUpdatedEvent event) {
        reloadRecipes(event.getRecipeManager());
    }

    public void reloadRecipes(RecipeManager recipeManager) {
        long l = System.currentTimeMillis();
        CraftingHandler.rebuildList(recipeManager);
        logger.info("Initialised " + CraftingHandler.getAllRecipes().size() + " recipes in " + (System.currentTimeMillis() - l) + " ms");
    }

    private void registerVanillaHandlers() {
        CraftingTableIVAPI.getAPI().registerHandler(new ForgeRecipeHandler());
        CraftingTableIVAPI.getAPI().registerHandler(new VanillaRecipeHandler());
    }

    public void sendCraftingMessage(CraftingHandler.IWorldAccessibleInventory inventory, CompoundNBT recipe) {
        CompoundNBT message = new CompoundNBT();
        CompoundNBT iwa = new CompoundNBT();
        inventory.writeToNBT(iwa);
        iwa.putString("iwa_ident", inventory.getClass().getName());
        message.put("iwa", iwa);
        message.put("recipe", recipe);
        networkHandler.sendToServer(new PacketCraft(message));
    }

    /**
     * Returns a string that will not be visible to the player, containing all the item data
     * (tooltip), used for the search bar. Will only ever get called on the client.
     *
     * @param stack The stack the String is requested from.
     * @return A String identifying the stack.
     */
    @OnlyIn(Dist.CLIENT)
    public String getFullItemName(ItemStack stack) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> tooltip = InventoryHelper.getTooltip(stack, ElecCore.proxy.getClientPlayer(), net.minecraft.client.Minecraft.getInstance().gameSettings.advancedItemTooltips);
        boolean appendH = false;
        for (Object o : tooltip) {
            stringBuilder.append(o);
            if (appendH) {
                stringBuilder.append("#");
            } else appendH = true;
        }
        return stringBuilder.toString().toLowerCase();
    }

    public static String getItemRegistryName(ItemStack stack) {
        ResourceLocation rl = RegistryHelper.getItemRegistry().getKey(stack.getItem());
        if (rl == null) { //...
            CraftingTableIV.logger.info("Found a recipe with an unregistered item! " + stack.getItem().toString());
            return null;
        }
        return rl.toString();
    }

    public static String getItemIdentifier(@Nonnull ItemStack stack) {
        return Preconditions.checkNotNull(RegistryHelper.getItemRegistry().getKey(stack.getItem())).getNamespace();
    }

}
