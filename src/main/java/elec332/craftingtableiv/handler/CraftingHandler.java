package elec332.craftingtableiv.handler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.util.*;
import elec332.core.util.recipes.RecipeHelper;
import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.api.IRecipeHandler;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import elec332.craftingtableiv.util.FastRecipeList;
import elec332.craftingtableiv.util.WrappedItemHandler;
import elec332.craftingtableiv.util.WrappedRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
        List<IRecipeHandler> recipeHandlers = RecipeHandler.getCompatHandler().getRegistry();
        List<IRecipe> recipeList = RecipeHelper.getCraftingManager().getRecipeList();
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
            if (CraftingTableIV.nuggetFilter && isNugget(recipe.getRecipeOutput().copy())) {
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
                    WrappedRecipe wrappedRecipe = handleRecipe(recipe, handler);
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
        int times = MathHelper.floor_float(max / (float) oS);
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
        int inputSize = recipe.getInput().length;
        ItemStack[] usedIngredients = new ItemStack[inputSize];
        Arrays.fill(usedIngredients, ItemStackHelper.NULL_STACK);
        mainLoop:
        for (int o = 0; o < inputSize; o++) {
            Object obj = recipe.getInput()[o];
            if (obj == null) {
                continue;
            }
            if (obj instanceof ItemStack){
                if (!ItemStackHelper.isStackValid((ItemStack) obj)){
                    continue;
                }
                ItemStack stack = ((ItemStack) obj).copy();
                int i = getFirstSlotWithItemStack(wrappedInventory, stack, recipe);
                if (i >= 0){
                    usedIngredients[o] = wrappedInventory.getStackInSlot(i).copy();
                    handleStuff(inventory, wrappedInventory, i, craft);
                    continue;
                } else if (list != null) {
                    List<WrappedRecipe> recipes = list.getCraftingRecipe(stack);
                    if (canCraftAnyOf(recipes, inventory, wrappedInventory, list, craft, recursion, recipe)){
                        i = getFirstSlotWithItemStack(wrappedInventory, stack, recipe);
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
            } else if (obj instanceof List && !((List) obj).isEmpty()){
                @SuppressWarnings("unchecked")
                List<ItemStack> stacks = (List<ItemStack>) obj;
                for (ItemStack stack : stacks){
                    stack = stack.copy();
                    int i = getFirstSlotWithItemStack(wrappedInventory, stack, recipe);
                    if (i >= 0){
                        usedIngredients[o] = wrappedInventory.getStackInSlot(i).copy();
                        handleStuff(inventory, wrappedInventory, i, craft);
                        continue mainLoop;
                    }
                }
                if (list == null) {
                    return false;
                }
                for (ItemStack stack : stacks) {
                    stack = stack.copy();
                    List<WrappedRecipe> recipes = list.getCraftingRecipe(stack);
                    if (canCraftAnyOf(recipes, inventory, wrappedInventory, list, craft, recursion, recipe)) {
                        int i = getFirstSlotWithItemStack(wrappedInventory, stack, recipe);
                        if (i < 0){
                            return false;
                        }
                        usedIngredients[o] = wrappedInventory.getStackInSlot(i).copy();
                        handleStuff(inventory, wrappedInventory, i, craft);
                        continue mainLoop;
                    }
                }
                return false;
            } else {
                return false;
            }
        }
        ItemStack out = recipe.getRecipeHandler().getCraftingResult(recipe.getRecipe(), usedIngredients);
        if (out == null) {
            return false;
        }
        if (!wrappedInventory.addItemToInventory(out, false)){
            inventory.dropStack(out);
        }
        if (craft && isClient()) {
            sendCraftingMessage(inventory, recipe, usedIngredients);
        }
        return true;
    }

    private static int getFirstSlotWithItemStack(IItemHandler inventory, ItemStack stack, WrappedRecipe recipe){
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
            if (!canCraftFrom(original, wrappedRecipe)){
                continue;
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

    private static boolean canCraftFrom(WrappedRecipe original, WrappedRecipe wrappedRecipe){
        return !(original.sameItems() && wrappedRecipe.sameItems() && wrappedRecipe.oneItem() != original.oneItem());
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
            if (list.tagCount() != recipe.getInput().length){
                continue;
            }
            ingredientLoop:
            for (int i = 0; i < list.tagCount(); i++) {
                Object obj = recipe.getInput()[i];
                ItemStack stack = ItemStackHelper.loadItemStackFromNBT(list.getCompoundTagAt(i));
                if (!ItemStackHelper.isStackValid(stack)){
                    if (obj == null || (obj instanceof ItemStack && !ItemStackHelper.isStackValid((ItemStack) obj))){
                        continue;
                    } else {
                        continue recipeLoop;
                    }
                }
                if (obj instanceof ItemStack){
                    if (recipe.getRecipeHandler().isValidIngredientFor(recipe.getRecipe(), (ItemStack) obj, stack)){
                        continue;
                    }
                    continue recipeLoop;
                } else if (obj instanceof List && !((List) obj).isEmpty()){
                    @SuppressWarnings("unchecked")
                    List<ItemStack> stacks = (List<ItemStack>) obj;
                    for (ItemStack stack1 : stacks){
                        if (recipe.getRecipeHandler().isValidIngredientFor(recipe.getRecipe(), stack1, stack)){
                            continue ingredientLoop;
                        }
                    }
                    continue recipeLoop;
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
            this.inventory = WrappedItemHandler.of(new DoubleItemHandler<>(new PlayerMainInvWrapper(player.inventory), craftingTableIV));
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
            new NBTHelper(tag).addToTag(craftingTableIV.getPos());
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
            TileEntityCraftingTableIV craftingTableIV = (TileEntityCraftingTableIV) WorldHelper.getTileAt(world, new NBTHelper(tag).getPos());
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
