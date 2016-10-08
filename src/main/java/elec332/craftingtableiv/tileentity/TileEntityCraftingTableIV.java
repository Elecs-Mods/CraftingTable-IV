package elec332.craftingtableiv.tileentity;

import elec332.core.inventory.IDefaultInventory;
import elec332.core.tile.TileBase;
import elec332.core.util.BasicInventory;
import elec332.core.util.BlockStateHelper;
import elec332.core.util.DirectionHelper;
import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.abstraction.CraftingTableIVAbstractionLayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class TileEntityCraftingTableIV extends TileBase implements ITickable, IDefaultInventory {

    public TileEntityCraftingTableIV() {
        super();
        this.doorAngle = 0F;
        this.tablestate = 0;
        this.inventory = new BasicInventory("CraftingTable IV", 18);
    }

    public float doorAngle;
    private BasicInventory inventory;
    private static final float openspeed = 0.2F;
    private int tablestate;

    private int facing = -1;

    public int getFacing() {
        if (facing == -1) {
            facing = DirectionHelper.getNumberForDirection(WorldHelper.getBlockState(worldObj, pos).getValue(BlockStateHelper.FACING_NORMAL.getProperty()));
        }
        return facing;
    }

    @Nonnull
    @Override
    public BasicInventory getInventory() {
        return inventory;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        inventory.writeToNBT(tagCompound);
        return super.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        inventory.readFromNBT(tagCompound);
        super.readFromNBT(tagCompound);
    }

    @Override
    public void update() {
        if (CraftingTableIVAbstractionLayer.enableDoor) {
            int xCoord = pos.getX();
            int yCoord = pos.getY();
            int zCoord = pos.getZ();
            EntityPlayer entityplayer = worldObj.getClosestPlayer((float)xCoord + 0.5F, (float)yCoord + 0.5F, (float)zCoord + 0.5F, 10D, false);
            if(entityplayer != null){
                double playerDistance = entityplayer.getDistanceSq((double)xCoord, (double)yCoord, (double)zCoord);
                if(playerDistance < CraftingTableIVAbstractionLayer.doorRange){
                    doorAngle += openspeed;

                    if(tablestate != 1) {
                        tablestate = 1;
                        if (CraftingTableIVAbstractionLayer.enableNoise) {
                            this.worldObj.playSound(null, xCoord, (double)yCoord + 0.5D, zCoord, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.2F, this.worldObj.rand.nextFloat() * 0.1F + 0.2F);
                        }
                    }

                    if(doorAngle > 1.8F){
                        doorAngle = 1.8F;
                    }
                } else if(playerDistance > CraftingTableIVAbstractionLayer.doorRange) {
                    doorAngle -= openspeed;

                    if(tablestate != 0) {
                        tablestate = 0;
                        if (CraftingTableIVAbstractionLayer.enableNoise) {
                            this.worldObj.playSound(null, xCoord, (double)yCoord + 0.5D, zCoord, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.2F, this.worldObj.rand.nextFloat() * 0.1F + 0.2F);
                        }
                    }

                    if(doorAngle < 0F){
                        doorAngle = 0F;
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        return openGui(player, CraftingTableIV.instance, CraftingTableIV.guiID);
    }

}
