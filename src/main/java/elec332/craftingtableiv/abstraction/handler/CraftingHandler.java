package elec332.craftingtableiv.abstraction.handler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.util.BasicInventory;
import elec332.core.util.DoubleInventory;
import elec332.core.util.ItemStackHelper;
import elec332.core.util.NBTHelper;
import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.abstraction.CraftingTableIVAbstractionLayer;
import elec332.craftingtableiv.api.IRecipeHandler;
import elec332.craftingtableiv.tileentity.TileEntityCraftingTableIV;
import elec332.craftingtableiv.util.WrappedInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class CraftingHandler {

    private static FastRecipeList recipeList;
    private static List<WrappedRecipe> allRecipes;
    private static List<Class<? extends IRecipe>> erroredClasses;

    public static void rebuildList(){
        clearLists();
        List<IRecipeHandler> recipeHandlers = getAbstractionLayer().api.getRegistry();
        List<IRecipe> recipeList = getAbstractionLayer().mod.getRegisteredRecipes();
        Map<String, List<WrappedRecipe>> entries = Maps.newHashMap();
        SortedMap<String, List<WrappedRecipe>> namedList = Maps.newTreeMap();
        recipeLoop:
        for (IRecipe recipe : recipeList){
            if (recipe == null || !ItemStackHelper.isStackValid(recipe.getRecipeOutput())){
                continue;
            }
            if (getAbstractionLayer().isRecipeDisabled(recipe)/* || erroredClasses.contains(recipe.getClass())*/){
                continue;
            }
            if (CraftingTableIVAbstractionLayer.instance.mod.getItemRegistryName(recipe.getRecipeOutput()) == null){
                continue;
            }
            if (CraftingTableIVAbstractionLayer.nuggetFilter && isNugget(recipe.getRecipeOutput().copy()))
                continue;
            String[] s = CraftingTableIVAbstractionLayer.instance.mod.getItemRegistryName(recipe.getRecipeOutput().copy()).replace(":", " ").split(" ");
            for (String s1 : CraftingTableIVAbstractionLayer.disabledMods) {
                if (s1.equalsIgnoreCase(s[0])) {
                    continue recipeLoop;
                }
            }
            boolean invalid = false;
            for (IRecipeHandler handler : recipeHandlers){
                if (handler.canHandleRecipe(recipe)){
                    WrappedRecipe wrappedRecipe = handleRecipe(recipe, handler);
                    if (wrappedRecipe != null){
                        if (s[0].contains("minecraft")) {
                            if (entries.get("minecraft") == null) {
                                entries.put("minecraft", Lists.<WrappedRecipe>newArrayList());
                            }
                            entries.get("minecraft").add(wrappedRecipe);
                        } else {
                            if (namedList.get(s[0]) == null) {
                                namedList.put(s[0], Lists.<WrappedRecipe>newArrayList());
                            }
                            namedList.get(s[0]).add(wrappedRecipe);
                        }
                        continue recipeLoop;
                    } else {
                        if (!handler.logHandlerErrors()) {
                            CraftingTableIVAbstractionLayer.instance.logger.warn("Recipe " + recipe.getClass().getName() + " has invalid ingredients!");
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

    @SuppressWarnings("all")
    private static WrappedRecipe handleRecipe(IRecipe recipe, IRecipeHandler handler){
        if (recipe == null || handler == null)
            return null;
        Object[] ingredients = handler.getIngredients(recipe);
        if (ingredients != null) {
            return WrappedRecipe.of(ingredients, recipe, handler);
        }
        return null;
    }

    private static CraftingTableIVAbstractionLayer getAbstractionLayer(){
        return CraftingTableIVAbstractionLayer.instance;
    }

    private static boolean isNugget(ItemStack stack){
        if (CraftingTableIVAbstractionLayer.instance.mod.getItemRegistryName(stack).contains("nugget"))
            return true;
        for (Integer i : OreDictionary.getOreIDs(stack)){
            if (OreDictionary.getOreName(i).contains("nugget"))
                return true;
        }
        return false;
    }

    public static List<WrappedRecipe> getAllRecipes() {
        return allRecipes;
    }

    public static <I extends IInventory> boolean canCraft(IWorldAccessibleInventory<I> inventory, WrappedRecipe recipe, @Nullable FastRecipeList list, boolean craft){
        ItemStack[] oldContents = inventory.getInventory().getCopyOfContents();
        WrappedInventory inv = WrappedInventory.of(new BasicInventory("", oldContents.length));
        inv.setContents(oldContents);
        boolean ret = canCraft(inventory, inv, recipe, list, craft, 0);
        if (ret && craft && !isClient()){
            inventory.getInventory().setContents(inv.getCopyOfContents());
        }
        return ret;
    }

    private static <I extends IInventory> boolean canCraft(IWorldAccessibleInventory<I> inventory, WrappedInventory wrappedInventory, WrappedRecipe recipe, @Nullable FastRecipeList list, boolean craft, int recursion){
        if (recursion >= CraftingTableIVAbstractionLayer.recursionDepth || inventory == null || recipe == null){
            return false;
        }
        int inputSize = recipe.getInput().length;
        ItemStack[] usedIngredients = new ItemStack[inputSize];
        mainLoop:
        for (int o = 0; o < inputSize; o++) {
            Object obj = recipe.getInput()[o];
            if (obj == null)
                continue;
            if (obj instanceof ItemStack){
                ItemStack stack = ((ItemStack) obj).copy();
                int i = getFirstSlotWithItemStack(wrappedInventory, stack, recipe);
                if (i >= 0){
                    usedIngredients[o] = wrappedInventory.getStackInSlot(i).copy();
                    handleStuff(inventory, wrappedInventory, i, craft);
                    continue;
                } else if (list != null) {
                    List<WrappedRecipe> recipes = list.getCraftingRecipe(stack);
                    if (canCraftAnyOf(recipes, inventory, wrappedInventory, list, craft, recursion)){
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
                if (list == null)
                    return false;
                for (ItemStack stack : stacks) {
                    stack = stack.copy();
                    List<WrappedRecipe> recipes = list.getCraftingRecipe(stack);
                    if (canCraftAnyOf(recipes, inventory, wrappedInventory, list, craft, recursion)) {
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
        if (out == null)
            return false;
        if (!wrappedInventory.addItemToInventory(out)){
            inventory.dropStack(out);
        }
        if (craft && isClient()) {
            sendCraftingMessage(inventory, recipe, usedIngredients);
        }
        return true;
    }

    private static int getFirstSlotWithItemStack(IInventory inventory, ItemStack stack, WrappedRecipe recipe){
        IRecipeHandler recipeHandler = recipe.getRecipeHandler();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (ItemStackHelper.isStackValid(stack) && recipeHandler.isValidIngredientFor(recipe.getRecipe(), stack, itemStack)){
                return i;
            }
        }
        return -1;
    }

    private static boolean canCraftAnyOf(List<WrappedRecipe> recipes, IWorldAccessibleInventory inventory, WrappedInventory wrappedInventory, @Nullable FastRecipeList list, boolean craft, int recursion){
        for (WrappedRecipe wrappedRecipe : recipes) {
            ItemStack[] copy = wrappedInventory.getCopyOfContents();
            if (canCraft(inventory, wrappedInventory, wrappedRecipe, list, craft, recursion + 1)) {
                return true;
            } else {
                wrappedInventory.setContents(copy);
            }
        }
        return false;
    }

    private static void handleStuff(IWorldAccessibleInventory worldAccessibleInventory, WrappedInventory inventory, int slot, boolean craft){
        ItemStack stack = inventory.getStackInSlot(slot).copy();
        inventory.decrStackSize(slot, 1);
        if (stack.getItem().hasContainerItem(stack)){
            ItemStack itemStack = stack.getItem().getContainerItem(stack);
            if (ItemStackHelper.isStackValid(itemStack) && itemStack.isItemStackDamageable() && itemStack.getItemDamage() > itemStack.getMaxDamage()) {
                itemStack = null;
            }

            if (itemStack != null && !inventory.addItemToInventory(itemStack) && craft) {
                worldAccessibleInventory.dropStack(itemStack);
            }
        }
    }

    public interface IWorldAccessibleInventory<I extends IInventory> {

        public void writeToNBT(NBTTagCompound tag);

        public IWorldAccessibleInventory<I> readFromNBT(NBTTagCompound tag);

        public WrappedInventory<I> getInventory();

        public void dropStack(@Nonnull ItemStack stack);

    }

    private static boolean isClient(){
        return CraftingTableIVAbstractionLayer.instance.mod.isEffectiveSideClient();
    }

    public static IWorldAccessibleInventory<DoubleInventory<InventoryPlayer, TileEntityCraftingTableIV>> forCraftingTableIV(EntityPlayer player, TileEntityCraftingTableIV craftingTableIV){
        return new CraftingTableIVHandler(player, craftingTableIV, player.getEntityWorld());
    }

    public static void onMessageReceived(IWorldAccessibleInventory inventory, NBTTagCompound recipeTag){
        List<WrappedRecipe> recipes = recipeList.getCraftingRecipe(new ItemStack(recipeTag.getCompoundTag("out")));
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
                    if (obj == null){
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
        canCraft(inventory, wrappedRecipe, null, true);
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
        getAbstractionLayer().sendCraftingMessage(inventory, recipeTag);
    }

    public static final class CraftingTableIVHandler implements IWorldAccessibleInventory<DoubleInventory<InventoryPlayer, TileEntityCraftingTableIV>> {

        @SuppressWarnings("unused")
        public CraftingTableIVHandler() {
        }

        private CraftingTableIVHandler(EntityPlayer player, TileEntityCraftingTableIV craftingTableIV, World world){
            if (player.worldObj != world && CraftingTableIVAbstractionLayer.instance.mod.getWorld(craftingTableIV) != world)
                throw new IllegalArgumentException();
            this.inventory = WrappedInventory.of(new DoubleInventory<InventoryPlayer, TileEntityCraftingTableIV>(player.inventory, craftingTableIV));
            this.world = world;
            this.craftingTableIV = craftingTableIV;
            this.player = player;
        }

        private WrappedInventory<DoubleInventory<InventoryPlayer, TileEntityCraftingTableIV>> inventory;
        private World world;
        private TileEntityCraftingTableIV craftingTableIV;
        private EntityPlayer player;

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            tag.setInteger("dimID", WorldHelper.getDimID(world));
            tag.setInteger("playerID", player.getEntityId());
            new NBTHelper(tag).addToTag(craftingTableIV.getPos());
        }

        @Override
        public IWorldAccessibleInventory<DoubleInventory<InventoryPlayer, TileEntityCraftingTableIV>> readFromNBT(NBTTagCompound tag) {
            World world = getAbstractionLayer().mod.getWorld(tag.getInteger("dimID"));
            EntityPlayer player = (EntityPlayer) world.getEntityByID(tag.getInteger("playerID"));
            TileEntityCraftingTableIV craftingTableIV = (TileEntityCraftingTableIV) WorldHelper.getTileAt(world, new NBTHelper(tag).getPos());
            return new CraftingTableIVHandler(player, craftingTableIV, world);
        }

        @Override
        public WrappedInventory<DoubleInventory<InventoryPlayer, TileEntityCraftingTableIV>> getInventory() {
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

    /*
    public static ArrayList<ItemStack> validOutputs = Lists.newArrayList();
    public static ArrayList<WrappedRecipe> recipeList = Lists.newArrayList();
    public static ArrayList<StackComparator> syncedRecipeOutput = Lists.newArrayList();
    public static ArrayList<RecipeStackComparator> stackDataList = Lists.newArrayList();
    public static Map<String, Map<ItemComparator, List<WrappedRecipe>>> recipeHash = Maps.newHashMap();
    public static Map<String, Map<StackComparator, RecipeStackComparator>> rcMap = Maps.newHashMap();
    private static List<String> noHandlerClasses = Lists.newArrayList();

    private static void addToRecipeHash(ItemStack stack, WrappedRecipe recipe){
        ItemComparator itemComparator = new ItemComparator(stack);
        String s = identifier(stack);
        if (recipeHash.get(s) == null)
            recipeHash.put(s, Maps.<ItemComparator, List<WrappedRecipe>>newHashMap());
        if (recipeHash.get(s).get(itemComparator) == null)
            recipeHash.get(s).put(itemComparator, new ArrayList<WrappedRecipe>());
        recipeHash.get(s).get(itemComparator).add(recipe);
    }

    private static void addToRCMap(StackComparator stackComparator, RecipeStackComparator recipeStackComparator){
        String s = identifier(stackComparator.getStack());
        if (rcMap.get(s) == null)
            rcMap.put(s, new HashMap<StackComparator, RecipeStackComparator>());
        rcMap.get(s).put(stackComparator, recipeStackComparator);
    }

    public static String identifier(ItemStack stack){
        return MineTweakerHelper.getItemRegistryName(stack).replace(":", " ").split(" ")[0];
    }

    @SuppressWarnings("unchecked")
    public static boolean canPlayerCraft(EntityPlayer player, TileEntityCraftingTableIV craftingTableIV, WrappedRecipe recipe, FastRecipeList canCraft, boolean executeCrafting){
        InventoryPlayer fakeInventoryPlayer = new InventoryPlayer(player);
        fakeInventoryPlayer.copyInventory(player.inventory);
        TileEntityCraftingTableIV fakeCraftingInventory = craftingTableIV.getCopy();
        boolean ret = canPlayerCraft(new DoubleInventory(fakeInventoryPlayer, fakeCraftingInventory), recipe, 0, canCraft, executeCrafting);
        if (executeCrafting && ret && isServer(player)){
            player.inventory.copyInventory(fakeInventoryPlayer);
            for (int i = 0; i < craftingTableIV.getSizeInventory(); i++) {
                craftingTableIV.setInventorySlotContents(i, fakeCraftingInventory.getStackInSlot(i));
            }
        }
        return ret;
    }

    @SuppressWarnings("all")
    public static boolean canPlayerCraft(DoubleInventory<InventoryPlayer, IInventory> inventory, WrappedRecipe recipe, int i, FastRecipeList check, boolean executeCrafting){
        //if (executeCrafting)
        //    System.out.println("Executing crafting for "+recipe.getOutputItemName());
        if (i > CraftingTableIV.recursionDepth)
            return false;

        if (inventory != null && recipe != null) {
            inputLoop:
            for (Object obj : recipe.getInput()) {
                if (obj == null)
                    continue;
                if (obj instanceof ItemStack) {
                    ItemStack itemStack = (ItemStack) obj;
                    int slotID = inventory.getFirstSlotWithItemStackNoNBT(itemStack);//CraftingHandler.getFirstInventorySlotWithItemStack(fakeInventoryPlayer, fakeCraftingInventory, itemStack);
                    if (slotID > -1) {
                        if (!handleStuff(inventory, itemStack))
                            return false;
                    } else if (i < CraftingTableIV.recursionDepth){
                        List<WrappedRecipe> valid = check.getCraftingRecipe(itemStack);
                        if (valid.isEmpty())
                            return false;
                        WrappedRecipe wrappedRecipe = canPlayerCraftAnyOf(inventory, check, i, executeCrafting, valid);
                        if (wrappedRecipe != null){
                            if (!handleStuff(inventory, wrappedRecipe.getRecipeOutput().getStack()))
                                return false;
                            continue inputLoop;
                        } else {
                            return false;
                        }
                    } else return false;
                } else if (obj instanceof List && !((List)obj).isEmpty()){
                    @SuppressWarnings("unchecked")
                    List<ItemStack> stacks = (List<ItemStack>) obj;
                    for (ItemStack itemStack : stacks) {
                        int p = inventory.getFirstSlotWithItemStackNoNBT(itemStack);//CraftingHandler.getFirstInventorySlotWithItemStack(fakeInventoryPlayer, fakeCraftingInventory, itemStack);
                        if (p >= 0) {
                            if (!handleStuff(inventory, itemStack))
                                return false;
                            continue inputLoop;
                        }
                    }
                    if (i < CraftingTableIV.recursionDepth){
                        List<WrappedRecipe> valid = check.getCraftingRecipe(stacks);
                        if (valid.isEmpty())
                            return false;
                        WrappedRecipe wrappedRecipe = canPlayerCraftAnyOf(inventory, check, i, executeCrafting, valid);
                        if (wrappedRecipe != null){
                            if (!handleStuff(inventory, wrappedRecipe.getRecipeOutput().getStack()))
                                return false;
                            continue inputLoop;
                        } else {
                            return false;
                        }
                    } else return false;
                }
            }
            if (executeCrafting && inventory.getFirstInventory().player.getEntityWorld().isRemote) {
                CraftingTableIV.networkHandler.getNetworkWrapper().sendToServer(new PacketCraft(recipe));
            }
            return inventory.addItemToInventory(recipe.getRecipeOutput().getStack().copy());
        } else return false;
    }

    private static WrappedRecipe canPlayerCraftAnyOf(DoubleInventory<InventoryPlayer, IInventory> inventory, FastRecipeList check, int i, boolean execute, List<WrappedRecipe> recipes){
        for (WrappedRecipe recipe : recipes){
            DoubleInventory<InventoryPlayer, IInventory> copy = DoubleInventory.fromInventory(new InventoryPlayer(inventory.getFirstInventory().player), new TileEntityCraftingTableIV(), inventory);
            if (canPlayerCraft(copy, recipe, i+1, check, execute)){
                inventory.copyFrom(copy);
                return recipe;
            }
        }
        return null;
    }

    private static boolean handleStuff(DoubleInventory inventory, ItemStack stack){
        int s = inventory.getFirstSlotWithItemStackNoNBT(stack.copy());
        if (s == -1)
            return false;
        if (stack.getItem().hasContainerItem(stack)){
            ItemStack itemStack = stack.getItem().getContainerItem(inventory.getStackInSlot(s));
            if (itemStack != null && itemStack.isItemStackDamageable() && itemStack.getItemDamage() > itemStack.getMaxDamage())
                itemStack = null;
            if (itemStack != null && !inventory.addItemToInventory(itemStack))
                return false;
        }
        return inventory.decrStackSize(s, 1) != null;
    }

    public static boolean isServer(EntityPlayer player){
        return !player.worldObj.isRemote;
    }


    //Validate all recipes, excluding all firework recipes, colouring recipes, ect.
    public static void InitRecipes() {
        validOutputs.clear();
        recipeList.clear();
        recipeHash.clear();
        stackDataList.clear();
        syncedRecipeOutput.clear();
        rcMap.clear();
        SortedMap<String, List<IRecipe>> namedList = Maps.newTreeMap();
        Map<String, List<IRecipe>> entries = Maps.newHashMap();
        for (Object object : CraftingManager.getInstance().getRecipeList()){
            if (object instanceof IRecipe){
                IRecipe recipe = (IRecipe) object;
                ItemStack output = recipe.getRecipeOutput();
                if (output != null && output.getItem() != null) {
                    if (Strings.isNullOrEmpty(MineTweakerHelper.getItemRegistryName(output))){
                        CraftingTableIV.instance.error("A mod has registered an invalid recipe, this is a severe error.");
                        continue;
                    }
                    if (recipe instanceof ShapelessOreRecipe && !isOreValid(((ShapelessOreRecipe)recipe).getInput()))
                        continue;
                    if (recipe instanceof ShapedOreRecipe && !isOreValid(Lists.newArrayList(((ShapedOreRecipe)recipe).getInput())))
                        continue;
                    output = output.copy();
                    if (CraftingTableIV.nuggetFilter && (MineTweakerHelper.getItemRegistryName(output).contains("nugget") || OredictHelper.getOreName(output).contains("nugget"))) // || recipeList.contains(recipe))
                        continue;
                    String[] s = MineTweakerHelper.getItemRegistryName(output).replace(":", " ").split(" ");
                    boolean b = false;
                    for (String s1 : CraftingTableIV.disabledMods) {
                        if (s1.equalsIgnoreCase(s[0])) {
                            b = true;
                        }
                    }
                    if (b)
                        continue;
                    if (s[0].contains("minecraft")) {
                        if (entries.get("minecraft") == null)
                            entries.put("minecraft", new ArrayList<IRecipe>());
                        entries.get("minecraft").add(recipe);
                    } else {
                        if (namedList.get(s[0]) == null)
                            namedList.put(s[0], new ArrayList<IRecipe>());
                        namedList.get(s[0]).add(recipe);
                    }
                }
            }
        }
        entries.putAll(namedList);
        for (List<IRecipe> recipes : entries.values()){
            for (IRecipe iRecipe : recipes){
                WrappedRecipe recipe = getWrappedRecipe(iRecipe);
                if (recipe != null) {
                    ItemStack output = recipe.getRecipeOutput().getStack();
                    validOutputs.add(output.copy());
                    recipeList.add(recipe);
                    syncedRecipeOutput.add(new StackComparator(output.copy()));
                    stackDataList.add(new RecipeStackComparator(output.copy()));
                    addToRCMap(new StackComparator(output.copy()), new RecipeStackComparator(output.copy()));
                    addToRecipeHash(output.copy(), recipe);
                }
            }
        }
    }

    private static boolean isOreValid(List list){
        if (!list.isEmpty()){
            for (Object o : list){
                if (o instanceof List){
                    if (((List) o).isEmpty())
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    public static List<WrappedRecipe> getCraftingRecipe(ItemStack stack) {
        if (!ItemStackHelper.isStackValid(stack))
            return Lists.newArrayList();
        List<WrappedRecipe> possRet;
        try {
            possRet = recipeHash.get(identifier(stack)).get(new ItemComparator(stack));
        } catch (Exception e) {
            return Lists.newArrayList();
        }
        if (possRet == null || possRet.isEmpty())
            return Lists.newArrayList();
        if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
            return possRet;
        List<WrappedRecipe> ret = Lists.newArrayList();
        for (WrappedRecipe recipe : possRet) {
            ItemStack out = recipe.getRecipeOutput().getStack();
            if (out.getItemDamage() == stack.getItemDamage() || (!out.getHasSubtypes() && !stack.getHasSubtypes()))
                ret.add(recipe);
        }
        return ret;
    }

    public static WrappedRecipe getWrappedRecipe(IRecipe irecipe) {
        try {
            if (irecipe == null)
                return null;
            else if (irecipe instanceof ShapelessRecipes) {
                return new WrappedRecipe((ShapelessRecipes) irecipe);
            } else if (irecipe instanceof ShapedRecipes) {
                return new WrappedRecipe((ShapedRecipes) irecipe);
            } else if (irecipe instanceof ShapedOreRecipe) {
                return new WrappedRecipe((ShapedOreRecipe) irecipe);
            } else if (irecipe instanceof ShapelessOreRecipe) {
                return new WrappedRecipe((ShapelessOreRecipe) irecipe);
            } else if (irecipe instanceof RecipesArmorDyes || irecipe instanceof RecipeFireworks || irecipe instanceof RecipeBookCloning || irecipe instanceof RecipesMapCloning) {
                return null;
            } else if (RecipeHandler.getCompatHandler().isDisabled(irecipe)) {
                return null;
            } else if (RecipeHandler.getCompatHandler().hasHandler(irecipe.getClass())) {
                return RecipeHandler.getCompatHandler().getHandler(irecipe).getWrappedRecipe(irecipe);
            } else {
                if (irecipe.getRecipeOutput() != null)
                    messageMissingHandler(irecipe);
                else CraftingTableIV.instance.error("ERROR: THE OUTPUT FOR THIS RECIPE IS NULL! " + irecipe.toString());
                return null;
            }
        } catch (NullPointerException e) {
            /*if (ElecCore.proxy.isClient()) {
                ElecCore.tickHandler.registerCall(new IRunOnce() {
                    @Override
                    public void run() {
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("There was an error loading some recipes, the error will be printed in the game log.");
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("Please report this to Elec332 with the entire gamelog here: https://github.com/Elecs-Mods/CraftingTable-IV/issues");
                    }
                });
            }*//*
            CraftingTableIV.instance.error("Something went wrong while trying to acquire recipe ingredients!");
            CraftingTableIV.instance.error(irecipe.toString() + " with output " + irecipe.getRecipeOutput());
            CraftingTableIV.instance.error(e);
            return null;
        }
    }

    private static void messageMissingHandler(IRecipe recipe){
        String clazz = recipe.getClass().getCanonicalName();
        if (!noHandlerClasses.contains(clazz)){
            CraftingTableIV.instance.error("ERROR FINDING HANDLER FOR RECIPE CLASS: " + clazz);
            noHandlerClasses.add(clazz);
        }
    }*/

}
