package elec332.craftingtableiv.tileentity;

import elec332.core.tile.TileBase;
import elec332.core.util.BasicItemHandler;
import elec332.core.util.BlockStateHelper;
import elec332.core.util.DirectionHelper;
import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.CraftingTableIV;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class TileEntityCraftingTableIV extends TileBase implements ITickable, IItemHandlerModifiable {

    public TileEntityCraftingTableIV() {
        super();
        this.doorAngle = 0F;
        this.tablestate = 0;
        this.inventory = new BasicItemHandler(18);
    }

    public boolean showRecipeSize = true, showShaped = false;
    public float doorAngle;
    private BasicItemHandler inventory;
    private static final float openspeed = 0.2F;
    private int tablestate;

    private int facing = -1;

    public int getFacing() {
        if (facing == -1) {
            facing = DirectionHelper.getNumberForDirection(WorldHelper.getBlockState(getWorld(), pos).getValue(BlockStateHelper.FACING_NORMAL.getProperty()));
        }
        return facing;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        showRecipeSize = tagCompound.getBoolean("shR");
        showShaped = tagCompound.getBoolean("ssH");
        inventory.writeToNBT(tagCompound);
        return super.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        tagCompound.setBoolean("shR", showRecipeSize);
        tagCompound.setBoolean("ssH", showShaped);
        inventory.deserializeNBT(tagCompound);
        super.readFromNBT(tagCompound);
    }

    @Override
    public void update() {
        if (CraftingTableIV.enableDoor) {
            int xCoord = pos.getX();
            int yCoord = pos.getY();
            int zCoord = pos.getZ();
            EntityPlayer entityplayer = getWorld().getClosestPlayer((float)xCoord + 0.5F, (float)yCoord + 0.5F, (float)zCoord + 0.5F, 10D, false);
            if(entityplayer != null){
                double playerDistance = entityplayer.getDistanceSq((double)xCoord, (double)yCoord, (double)zCoord);
                if(playerDistance < CraftingTableIV.doorRange){
                    doorAngle += openspeed;

                    if(tablestate != 1) {
                        tablestate = 1;
                        if (CraftingTableIV.enableNoise) {
                            this.getWorld().playSound(null, xCoord, (double)yCoord + 0.5D, zCoord, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.2F, this.getWorld().rand.nextFloat() * 0.1F + 0.2F);
                        }
                    }

                    if(doorAngle > 1.8F){
                        doorAngle = 1.8F;
                    }
                } else if(playerDistance > CraftingTableIV.doorRange) {
                    doorAngle -= openspeed;

                    if(tablestate != 0) {
                        tablestate = 0;
                        if (CraftingTableIV.enableNoise) {
                            this.getWorld().playSound(null, xCoord, (double)yCoord + 0.5D, zCoord, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.2F, this.getWorld().rand.nextFloat() * 0.1F + 0.2F);
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
    public boolean onBlockActivated(IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            openWindow(player, CraftingTableIV.proxy, CraftingTableIV.guiID);
        }
        return true;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        inventory.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return inventory.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return inventory.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return inventory.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory.getSlotLimit(slot);
    }

}
