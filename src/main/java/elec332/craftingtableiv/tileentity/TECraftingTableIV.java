package elec332.craftingtableiv.tileentity;

import elec332.core.baseclasses.tileentity.BaseTileWithInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class TECraftingTableIV extends BaseTileWithInventory {
    public double playerDistance;
    public float doorAngle;
    public static final float openspeed = 0.2F;
    private int tablestate;
    private String customName;
    //public ItemStack[] theInventory = new ItemStack[18];
    private boolean enableDoor = true;
    private boolean enableNoise = true;

    public TECraftingTableIV() {
        super(18);
        this.playerDistance = 7F;
        this.doorAngle = 0F;
        this.tablestate = 0;
    }

    public int getFacing()
    {
        return getBlockMetadata();
    }

    public void updateEntity() {
        super.updateEntity();
        if (enableDoor) {
            EntityPlayer entityplayer = worldObj.getClosestPlayer((float)xCoord + 0.5F, (float)yCoord + 0.5F, (float)zCoord + 0.5F, 10D);
            if(entityplayer != null){
                playerDistance = entityplayer.getDistanceSq((double)xCoord, (double)yCoord, (double)zCoord);
                if(playerDistance < 7F){
                    doorAngle += openspeed;

                    if(tablestate != 1) {
                        tablestate = 1;
                        if (enableNoise)
                            worldObj.playSoundEffect((double)xCoord, (double)yCoord + 0.5D, (double)zCoord, "random.chestopen", 0.2F, worldObj.rand.nextFloat() * 0.1F + 0.2F);
                    }

                    if(doorAngle > 1.8F){
                        doorAngle = 1.8F;
                    }
                } else if(playerDistance > 7F) {
                    doorAngle -= openspeed;

                    if(tablestate != 0) {
                        tablestate = 0;
                        if (enableNoise)
                            worldObj.playSoundEffect((double)xCoord, (double)yCoord + 0.5D, (double)zCoord, "random.chestclosed", 0.2F, worldObj.rand.nextFloat() * 0.1F + 0.2F);
                    }

                    if(doorAngle < 0F){
                        doorAngle = 0F;
                    }
                }
            }
        }
    }

    public int getFirstEmptyStack() {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            if (this.getStackInSlot(i) == null && i > 39) {
                return i;
            }
        }
        return -1;
    }

    public int FindStack(ItemStack aStack)
    {
        for (int i=0; i < this.getSizeInventory(); i++)
        {
            if (getStackInSlot(i) != null)
                if (getStackInSlot(i).getItem() == aStack.getItem())
                    if (getStackInSlot(i).getHasSubtypes())
                    {
                        if (getStackInSlot(i).getItemDamage() == aStack.getItemDamage())
                            if (getStackInSlot(i).getMaxStackSize() > getStackInSlot(i).stackSize)
                                return i;
                    } else {
                        if (getStackInSlot(i).getMaxStackSize() > getStackInSlot(i).stackSize)
                            return i;
                    }
        }
        return -1;
    }
/*
    public boolean addItemStackToInventory(final ItemStack itemStack)
    {
        if (itemStack != null && itemStack.stackSize != 0 && itemStack.getItem() != null) {
            try {
                int i;
                if (itemStack.isItemDamaged()) {
                    i = this.getFirstEmptyStack();
                    if (i >= 0) {
                        this.theInventory[i] = ItemStack.copyItemStack(itemStack);
                        this.theInventory[i].animationsToGo = 5;
                        itemStack.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    do
                    {
                        i = itemStack.stackSize;
                        itemStack.stackSize = this.storePartialItemStack(itemStack);
                    }
                    while (itemStack.stackSize > 0 && itemStack.stackSize < i);



                        return itemStack.stackSize < i;

                }
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item ID", Item.getIdFromItem(itemStack.getItem()));
                crashreportcategory.addCrashSection("Item data", itemStack.getItemDamage());
                crashreportcategory.addCrashSectionCallable("Item name", new Callable() {
                    public String call() {
                        return itemStack.getDisplayName();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        else {
            return false;
        }
    }
*/
    /*private int storePartialItemStack(ItemStack p_70452_1_)
    {
        Item item = p_70452_1_.getItem();
        int i = p_70452_1_.stackSize;
        int j;

        if (p_70452_1_.getMaxStackSize() == 1)
        {
            j = this.getFirstEmptyStack();

            if (j < 0)
            {
                return i;
            }
            else
            {
                if (this.theInventory[j] == null)
                {
                    this.theInventory[j] = ItemStack.copyItemStack(p_70452_1_);
                }

                return 0;
            }
        }
        else
        {
            j = this.storeItemStack(p_70452_1_);

            if (j < 0)
            {
                j = this.getFirstEmptyStack();
            }

            if (j < 0)
            {
                return i;
            }
            else
            {
                if (this.theInventory[j] == null)
                {
                    this.theInventory[j] = new ItemStack(item, 0, p_70452_1_.getItemDamage());

                    if (p_70452_1_.hasTagCompound())
                    {
                        this.theInventory[j].setTagCompound((NBTTagCompound)p_70452_1_.getTagCompound().copy());
                    }
                }

                int k = i;

                if (i > this.theInventory[j].getMaxStackSize() - this.theInventory[j].stackSize)
                {
                    k = this.theInventory[j].getMaxStackSize() - this.theInventory[j].stackSize;
                }

                if (k > this.getInventoryStackLimit() - this.theInventory[j].stackSize)
                {
                    k = this.getInventoryStackLimit() - this.theInventory[j].stackSize;
                }

                if (k == 0)
                {
                    return i;
                }
                else
                {
                    i -= k;
                    this.theInventory[j].stackSize += k;
                    this.theInventory[j].animationsToGo = 5;
                    return i;
                }
            }
        }
    }

    private int storeItemStack(ItemStack p_70432_1_)
    {
        for (int i = 0; i < this.theInventory.length; ++i)
        {
            if (this.theInventory[i] != null && this.theInventory[i].getItem() == p_70432_1_.getItem() && this.theInventory[i].isStackable() && this.theInventory[i].stackSize < this.theInventory[i].getMaxStackSize() && this.theInventory[i].stackSize < this.getInventoryStackLimit() && (!this.theInventory[i].getHasSubtypes() || this.theInventory[i].getItemDamage() == p_70432_1_.getItemDamage()) && ItemStack.areItemStackTagsEqual(this.theInventory[i], p_70432_1_))
            {
                return i;
            }
        }

        return -1;
    }*/

    public boolean addItemStackToInventory(ItemStack aStack)
    {
        int Index = FindStack(aStack);
        while (Index > -1)
        {
            ItemStack tarStack = getStackInSlot(Index);
            if (tarStack.isStackable())
                if (tarStack.getMaxStackSize() - tarStack.stackSize >= aStack.stackSize)
                {
                    tarStack.stackSize += aStack.stackSize;
                    aStack.stackSize = 0;
                    setInventorySlotContents(Index, tarStack);
                } else {
                    aStack.stackSize -= (tarStack.getMaxStackSize() - tarStack.stackSize);
                    tarStack.stackSize = tarStack.getMaxStackSize();
                    setInventorySlotContents(Index, tarStack);
                }
            if (aStack.stackSize <= 0)
                return true;
            Index = FindStack(aStack);
        }
        if (aStack.stackSize > 0) {
            Index = getFreeSlot();
            if (Index > -1)
            {
                setInventorySlotContents(Index, aStack);
            } else {
                System.out.println("Cannot add item to TE");
                return false;
            }
        }
        return true;
    }

    public int getFreeSlot()
    {
        for (int i=0; i < this.getSizeInventory()-1; i++)
        {
            if (getStackInSlot(i) == null)
                return i;
        }
        return -1;
    }

    public TECraftingTableIV getCopy()
    {
        TECraftingTableIV Clone = new TECraftingTableIV();
        for (int i=0; i < this.getSizeInventory(); i++)
        {
            if (getStackInSlot(i) != null)
                Clone.setInventorySlotContents(i, getStackInSlot(i).copy());
            else
                Clone.setInventorySlotContents(i, null);
        }
        return Clone;
    }

    @Override
    protected String standardInventoryName() {
        return "CraftingTable IV";
    }
}
