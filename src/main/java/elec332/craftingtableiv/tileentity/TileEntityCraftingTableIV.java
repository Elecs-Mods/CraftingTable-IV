package elec332.craftingtableiv.tileentity;

import elec332.core.baseclasses.tileentity.BaseTileWithInventory;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.abstraction.CraftingTableIVAbstractionLayer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class TileEntityCraftingTableIV extends BaseTileWithInventory {
    public double playerDistance;
    public float doorAngle;
    public static final float openspeed = 0.2F;
    private int tablestate;

    public TileEntityCraftingTableIV() {
        super(18);
        this.playerDistance = 7F;
        this.doorAngle = 0F;
        this.tablestate = 0;
    }

    public int getFacing() {
        return getBlockMetadata();
    }

    public void updateEntity() {
        super.updateEntity();
        if (CraftingTableIVAbstractionLayer.enableDoor) {
            EntityPlayer entityplayer = worldObj.getClosestPlayer((float)xCoord + 0.5F, (float)yCoord + 0.5F, (float)zCoord + 0.5F, 10D);
            if(entityplayer != null){
                playerDistance = entityplayer.getDistanceSq((double)xCoord, (double)yCoord, (double)zCoord);
                if(playerDistance < CraftingTableIVAbstractionLayer.doorRange){
                    doorAngle += openspeed;

                    if(tablestate != 1) {
                        tablestate = 1;
                        if (CraftingTableIVAbstractionLayer.enableNoise)
                            worldObj.playSoundEffect((double)xCoord, (double)yCoord + 0.5D, (double)zCoord, "random.chestopen", 0.2F, worldObj.rand.nextFloat() * 0.1F + 0.2F);
                    }

                    if(doorAngle > 1.8F){
                        doorAngle = 1.8F;
                    }
                } else if(playerDistance > CraftingTableIVAbstractionLayer.doorRange) {
                    doorAngle -= openspeed;

                    if(tablestate != 0) {
                        tablestate = 0;
                        if (CraftingTableIVAbstractionLayer.enableNoise)
                            worldObj.playSoundEffect((double)xCoord, (double)yCoord + 0.5D, (double)zCoord, "random.chestclosed", 0.2F, worldObj.rand.nextFloat() * 0.1F + 0.2F);
                    }

                    if(doorAngle < 0F){
                        doorAngle = 0F;
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        player.openGui(CraftingTableIV.instance, CraftingTableIV.guiID, worldObj, xCoord, yCoord, zCoord);
        return true;
    }
/*
    public int findStack(ItemStack aStack) {
        for (int i=0; i < this.getSizeInventory(); i++) {
            if (getStackInSlot(i) != null)
                if (getStackInSlot(i).getItem() == aStack.getItem())
                    if (getStackInSlot(i).getHasSubtypes()) {
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

    public boolean addItemStackToInventory(ItemStack aStack) {
        int index = findStack(aStack);
        while (index > -1) {
            ItemStack tarStack = getStackInSlot(index);
            if (tarStack.isStackable())
                if (tarStack.getMaxStackSize() - tarStack.stackSize >= aStack.stackSize)
                {
                    tarStack.stackSize += aStack.stackSize;
                    aStack.stackSize = 0;
                    setInventorySlotContents(index, tarStack);
                } else {
                    aStack.stackSize -= (tarStack.getMaxStackSize() - tarStack.stackSize);
                    tarStack.stackSize = tarStack.getMaxStackSize();
                    setInventorySlotContents(index, tarStack);
                }
            if (aStack.stackSize <= 0)
                return true;
            index = findStack(aStack);
        }
        if (aStack.stackSize > 0) {
            index = getFreeSlot();
            if (index > -1) {
                setInventorySlotContents(index, aStack);
            } else {
                //System.out.println("Cannot add item to TE");
                return false;
            }
        }
        return true;
    }

    public int getFreeSlot() {
        for (int i=0; i < this.getSizeInventory(); i++) {
            if (getStackInSlot(i) == null)
                return i;
        }
        return -1;
    }*/

    public TileEntityCraftingTableIV getCopy() {
        TileEntityCraftingTableIV clone = new TileEntityCraftingTableIV();
        for (int i=0; i < this.getSizeInventory(); i++) {
            if (getStackInSlot(i) != null)
                clone.setInventorySlotContents(i, getStackInSlot(i).copy());
            else
                clone.setInventorySlotContents(i, null);
        }
        return clone;
    }

    @Override
    protected String standardInventoryName() {
        return "CraftingTable IV";
    }
}
