package elec332.craftingtableiv.handler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.inventory.BasicItemHandler;
import elec332.core.inventory.ContainerNull;
import elec332.core.inventory.DoubleItemHandler;
import elec332.core.util.*;
import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.api.IRecipeHandler;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import elec332.craftingtableiv.util.FastRecipeList;
import elec332.craftingtableiv.util.WrappedItemHandler;
import elec332.craftingtableiv.util.WrappedRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class CraftingHandler {

    private static FastRecipeList recipeList;
    private static List<WrappedRecipe> allRecipes;
    private static List<Class<? extends IRecipe>> erroredClasses;

    public static void rebuildList(){
        clearLists();
        Set<IRecipeHandler> recipeHandlers = RecipeHandler.getCompatHandler().getRegistry();
        Iterable<IRecipe> recipeList = CraftingManager.REGISTRY;
        Map<String, List<WrappedRecipe>> entries = Maps.newHashMap();
        SortedMap<String, List<WrappedRecipe>> namedList = Maps.newTreeMap();
        recipeLoop:
        for (IRecipe recipe : recipeList){
            if (recipe == null || !ItemStackHelper.isStackValid(recipe.getRecipeOutput())){
                continue;
            }
            if (RecipeHandler.getCompatHandler().isRecipeDisabled(recipe)/* || erroredClasses.contains(recipe.getClass())*/){
                continue;
            }
            if (CraftingTableIV.getItemRegistryName(recipe.getRecipeOutput()) == null){
                continue;
            }
            if (CraftingTableIV.nuggetFilter && isNugget(recipe.getRecipeOutput())) {
                continue;
            }
            String s = CraftingTableIV.getItemIdentifier(recipe.getRecipeOutput());
            for (String s1 : CraftingTableIV.disabledMods) {
                if (s1.equalsIgnoreCase(s)) {
                    continue recipeLoop;
                }
            }
            boolean invalid = false;
            for (IRecipeHandler handler : recipeHandlers){
                if (handler.canHandleRecipe(recipe)){
                    WrappedRecipe wrappedRecipe = null;
                    try {
                        wrappedRecipe = handleRecipe(recipe, handler);
                    } catch (Exception e){
                        throw new RuntimeException("Failed to handle recipe: " + recipe.getClass().getCanonicalName() + " " + recipe, e);
                    }
                    if (wrappedRecipe != null){
                        if (s.contains("minecraft")) {
                            entries.computeIfAbsent("minecraft", k -> Lists.newArrayList()).add(wrappedRecipe);
                        } else {
                            namedList.computeIfAbsent(s, k -> Lists.newArrayList()).add(wrappedRecipe);
                        }
                        continue recipeLoop;
                    } else {
                        if (handler.logHandlerErrors()) {
                            CraftingTableIV.logger.warn("Recipe " + recipe.getClass().getName() + " has invalid ingredients!");
                        }
                        invalid = true; //Do not exit loop, there might be another valid handler in the list.
                    }
                }
            }
            if (invalid){
                erroredClasses.add(recipe.getClass());
            }
        }
        entries.putAll(namedList);
        for (List<WrappedRecipe> wrappedRecipeList : entries.values()){
            for (WrappedRecipe wrappedRecipe : wrappedRecipeList){
                CraftingHandler.recipeList.addRecipe(wrappedRecipe);
                allRecipes.add(wrappedRecipe);
            }
        }
        allRecipes = ImmutableList.copyOf(allRecipes);
    }

    private static void clearLists(){
        recipeList = new FastRecipeList();
        allRecipes = Lists.newArrayList();
    }

    @Nullable
    private static WrappedRecipe handleRecipe(IRecipe recipe, IRecipeHandler handler){
        if (recipe == null || handler == null) {
            return null;
        }
        return WrappedRecipe.of(recipe, handler);
    }

    @SuppressWarnings("all")
    private static boolean isNugget(ItemStack stack){
        if (CraftingTableIV.getItemRegistryName(stack).contains("nugget")) {
            return true;
        }
        for (Integer i : OreDictionary.getOreIDs(stack)){
            if (OreDictionary.getOreName(i).contains("nugget")) {
                return true;
            }
        }
        return false;
    }

    public static List<WrappedRecipe> getAllRecipes() {
        return allRecipes;
    }

    public static <I extends IItemHandlerModifiable> int canCraft(IWorldAccessibleInventory<I> inventory, WrappedRecipe recipe, @Nullable FastRecipeList list, boolean craft, int max){
        if (max <= 0){
            return 0;
        }
        if (max != 1) {
            int limit = getMaxStackSize(recipe.getRecipeOutput());
            if (max > limit) {
                max = limit;
            }
        }
        ItemStack[] oldContents = inventory.getInventory().getCopyOfContents();
        WrappedItemHandler inv = WrappedItemHandler.of(new BasicItemHandler(oldContents.length));
        inv.setContents(oldContents);
        int ret = maxCraft(inventory, inv, recipe, list, craft, 0, max);
        if (ret > 0 && craft && !isClient()){
            inventory.getInventory().setContents(inv.getCopyOfContents());
        }
        return ret;
    }

    private static <I extends IItemHandlerModifiable> int maxCraft(IWorldAccessibleInventory<I> inventory, WrappedItemHandler wrappedInventory, WrappedRecipe recipe, @Nullable FastRecipeList list, boolean craft, int recursion, int max) {
        int ret = 0;
        int oS = recipe.getOutputSize();
        int times = MathHelper.floor(max / (float) oS);
        for (int i = 0; i < times; i++) {
            if (canCraft(inventory, wrappedInventory, recipe, list, craft, recursion)){
                ret += oS;
                continue;
            }
            break;
        }
        return ret;
    }

    private static <I extends IItemHandlerModifiable> boolean canCraft(IWorldAccessibleInventory<I> inventory, WrappedItemHandler wrappedInventory, WrappedRecipe recipe, @Nullable FastRecipeList list, boolean craft, int recursion){
        if (recursion >= CraftingTableIV.recursionDepth || inventory == null || recipe == null){
            return false;
        }
        int inputSize = recipe.getIngredients().length;
        ItemStack[] usedIngredients = new ItemStack[inputSize];
        Arrays.fill(usedIngredients, ItemStackHelper.NULL_STACK);
        for (int o = 0; o < inputSize; o++) {
            Ingredient obj = recipe.getIngredients()[o];
            if (obj == null || obj == Ingredient.EMPTY) {
                continue;
            }
            int i = getFirstSlotWithItemStack(wrappedInventory, obj, recipe);
            if (i >= 0){
                usedIngredients[o] = wrappedInventory.getStackInSlot(i).copy();
                handleStuff(inventory, wrappedInventory, i, craft);
                continue;
            } else if (list != null) {
                List<WrappedRecipe> recipes = list.getCraftingRecipe(obj, recipe);
                if (canCraftAnyOf(recipes, inventory, wrappedInventory, list, craft, recursion, recipe)){
                    i = getFirstSlotWithItemStack(wrappedInventory, obj, recipe);
                    if (i < 0){
                        return false;
                    }
                    usedIngredients[o] = wrappedInventory.getStackInSlot(i).copy();
                    handleStuff(inventory, wrappedInventory, i, craft);
                    continue;
                }
                return false;
            }
            return false;
        }
        ItemStack out = recipe.getRecipeHandler().getCraftingResult(recipe.getRecipe(), getInv(usedIngredients));
        if (out == null || !ItemStackHelper.isStackValid(out)) {
            return false;
        }
        if (!wrappedInventory.addItemToInventory(out, false)){
            if (recursion != 0){ //Don't throw recipe ingredients out, only the recipe result
                return false;
            }
            inventory.dropStack(out);
        }
        if (craft && isClient()) {
            sendCraftingMessage(inventory, recipe, usedIngredients);
        }
        return true;
    }

    private static InventoryCrafting getInv(ItemStack[] s){
        InventoryCrafting ret = new InventoryCrafting(new ContainerNull(), 3, 3);
        for (int i = 0; i < 9; i++) {
            ItemStack stack;
            if (i >= s.length){
                stack = ItemStackHelper.NULL_STACK;
            } else {
                stack = s[i];
            }
            ret.setInventorySlotContents(i, stack);
        }
        return ret;
    }

    private static int getFirstSlotWithItemStack(IItemHandler inventory, Ingredient stack, WrappedRecipe recipe){
        IRecipeHandler recipeHandler = recipe.getRecipeHandler();
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (ItemStackHelper.isStackValid(itemStack) && recipeHandler.isValidIngredientFor(recipe.getRecipe(), stack, itemStack)){
                return i;
            }
        }
        return -1;
    }

    private static boolean canCraftAnyOf(List<WrappedRecipe> recipes, IWorldAccessibleInventory<?> inventory, WrappedItemHandler wrappedInventory, @Nullable FastRecipeList list, boolean craft, int recursion, WrappedRecipe original){
        for (WrappedRecipe wrappedRecipe : recipes) {
            if (isLoopSensitive(original, wrappedRecipe)){
                if (CraftingTableIV.aggressiveLoopCheck || isSingleLoop(original, wrappedRecipe)){
                    continue;
                }
            }
            ItemStack[] copy = wrappedInventory.getCopyOfContents();
            if (canCraft(inventory, wrappedInventory, wrappedRecipe, list, craft, recursion + 1)) {
                return true;
            } else {
                wrappedInventory.setContents(copy);
            }
        }
        return false;
    }

    private static boolean isLoopSensitive(WrappedRecipe original, WrappedRecipe wrappedRecipe){ //loop detection
        return original.sameItems() && wrappedRecipe.sameItems() && (wrappedRecipe.oneItem() != original.oneItem());
    }

    private static boolean isSingleLoop(WrappedRecipe original, WrappedRecipe wrappedRecipe) {
        ItemStack stack = null, out = null;
        if (original.oneItem()) {
            stack = original.getOneItem();
            out = wrappedRecipe.getRecipeOutput();
        } else if (wrappedRecipe.oneItem()) {
            stack = wrappedRecipe.getOneItem();
            out = original.getRecipeOutput();
        }
        return stack != null && out != null && InventoryHelper.areEqualNoSizeNoNBT(stack, out);
    }

    private static void handleStuff(IWorldAccessibleInventory worldAccessibleInventory, WrappedItemHandler inventory, int slot, boolean craft){
        ItemStack stack = inventory.getStackInSlot(slot).copy();
        inventory.extractItem(slot, 1, false);
        if (stack.getItem().hasContainerItem(stack)){
            ItemStack itemStack = stack.getItem().getContainerItem(stack);
            if (ItemStackHelper.isStackValid(itemStack) && itemStack.isItemStackDamageable() && itemStack.getItemDamage() > itemStack.getMaxDamage()) {
                itemStack = null;
            }

            if (itemStack != null && !inventory.addItemToInventory(itemStack, false) && craft) {
                worldAccessibleInventory.dropStack(itemStack);
            }
        }
    }

    public interface IWorldAccessibleInventory<I extends IItemHandlerModifiable> {

        public void writeToNBT(NBTTagCompound tag);

        public IWorldAccessibleInventory<I> readFromNBT(NBTTagCompound tag);

        public WrappedItemHandler<I> getInventory();

        public void dropStack(@Nonnull ItemStack stack);

    }

    private static boolean isClient(){
        return FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    private static int getMaxStackSize(ItemStack stack){
        return stack.getItem().getItemStackLimit(stack);
    }

    public static IWorldAccessibleInventory<?> forCraftingTableIV(EntityPlayer player, TileEntityCraftingTableIV craftingTableIV){
        return new CraftingTableIVHandler(player, craftingTableIV, player.getEntityWorld());
    }

    public static void onMessageReceived(IWorldAccessibleInventory<?> inventory, NBTTagCompound recipeTag){
        List<WrappedRecipe> recipes = recipeList.getCraftingRecipe(ItemStackHelper.loadItemStackFromNBT(recipeTag.getCompoundTag("out")));
        NBTTagList list = recipeTag.getTagList("ingredients", 10);
        WrappedRecipe wrappedRecipe = null;
        recipeLoop:
        for (WrappedRecipe recipe : recipes){
            if (list.tagCount() != recipe.getIngredients().length){
                continue;
            }
            for (int i = 0; i < list.tagCount(); i++) {
                Ingredient obj = recipe.getIngredients()[i];
                ItemStack stack = ItemStackHelper.loadItemStackFromNBT(list.getCompoundTagAt(i));
                if (!ItemStackHelper.isStackValid(stack)){
                    if (obj == null || obj == Ingredient.EMPTY){
                        continue;
                    }
                    continue recipeLoop;
                }
                if (recipe.getRecipeHandler().isValidIngredientFor(recipe.getRecipe(), obj, stack)){
                    continue;
                }
                continue recipeLoop;
            }
            wrappedRecipe = recipe;
            break;
        }
        if (wrappedRecipe == null){
            System.out.println("Unable to find recipe.");
            return;
        }
        int i = wrappedRecipe.getOutputSize();
        if (recipeTag.hasKey("recipeAmt")){
            i = recipeTag.getByte("recipeAmt");
        }
        canCraft(inventory, wrappedRecipe, null, true, i);
    }

    private static void sendCraftingMessage(IWorldAccessibleInventory inventory, WrappedRecipe recipe, ItemStack[] usedIngredients){
        NBTTagCompound recipeTag = new NBTTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        recipe.getRecipe().getRecipeOutput().writeToNBT(tag);
        recipeTag.setTag("out", tag);
        NBTTagList list = new NBTTagList();
        for (ItemStack stack : usedIngredients) {
            tag = new NBTTagCompound();
            if (stack != null){
                stack.writeToNBT(tag);
            }
            list.appendTag(tag);
        }
        recipeTag.setTag("ingredients", list);
        CraftingTableIV.instance.sendCraftingMessage(inventory, recipeTag);
    }

    public static final class CraftingTableIVHandler implements IWorldAccessibleInventory {

        @SuppressWarnings("unused")
        public CraftingTableIVHandler() {
        }

        private CraftingTableIVHandler(EntityPlayer player, TileEntityCraftingTableIV craftingTableIV, World world){
            if (player.getEntityWorld() != world && craftingTableIV.getWorld() != world) {
                throw new IllegalArgumentException();
            }
            this.inventory = WrappedItemHandler.of(new DoubleItemHandler<>(craftingTableIV, new PlayerMainInvWrapper(player.inventory)));
            this.world = world;
            this.craftingTableIV = craftingTableIV;
            this.player = player;
        }

        private WrappedItemHandler<?> inventory;
        private World world;
        private TileEntityCraftingTableIV craftingTableIV;
        private EntityPlayer player;

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            tag.setInteger("dimID", WorldHelper.getDimID(world));
            tag.setInteger("playerID", player.getEntityId());
            tag.setString("PlayerUUID", player.getUniqueID().toString());
            new NBTBuilder(tag).setBlockPos(craftingTableIV.getPos());
        }

        @Override
        public IWorldAccessibleInventory<?> readFromNBT(NBTTagCompound tag) {
            World world = DimensionManager.getWorld(tag.getInteger("dimID"));
            EntityPlayer player = (EntityPlayer) world.getEntityByID(tag.getInteger("playerID"));
            if (player == null){
                CraftingTableIV.logger.error("PlayerEntity with ID: "+tag.getInteger("playerID")+" does no longer exist?!?");
                player = world.getPlayerEntityByUUID(UUID.fromString(tag.getString("PlayerUUID")));
                if (player == null){
                    return null;
                }
            }
            TileEntityCraftingTableIV craftingTableIV = (TileEntityCraftingTableIV) WorldHelper.getTileAt(world, new NBTBuilder(tag).getBlockPos());
            return new CraftingTableIVHandler(player, craftingTableIV, world);
        }

        @Override
        public WrappedItemHandler<?> getInventory() {
            return inventory;
        }

        @Override
        public void dropStack(@Nonnull ItemStack stack) {
            if (!isClient()) {
                WorldHelper.dropStack(world, craftingTableIV.getPos(), stack);
            }
        }
    }

    static {
        erroredClasses = Lists.newArrayList();
    }

}
