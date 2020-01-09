package elec332.craftingtableiv.tileentity;

import com.google.common.base.Preconditions;
import elec332.core.inventory.BasicItemHandler;
import elec332.core.tile.AbstractTileEntity;
import elec332.core.util.BlockProperties;
import elec332.core.world.WorldHelper;
import elec332.craftingtableiv.util.CTIVConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 23-3-2015.
 */
public class TileEntityCraftingTableIV extends AbstractTileEntity implements ITickableTileEntity, IItemHandlerModifiable {

    public TileEntityCraftingTableIV() {
        super();
        this.doorAngle = 0F;
        this.tablestate = 0;
        this.inventory = new BasicItemHandler(18);
    }

    public boolean showRecipeSize = true, showShaped = true;
    public float doorAngle;
    private BasicItemHandler inventory;
    private static final float openspeed = 0.2F;
    private int tablestate;

    private int facing = -1;

    public int getFacing() {
        if (facing == -1) {
            facing = WorldHelper.getBlockState(Preconditions.checkNotNull(getWorld()), pos).get(BlockProperties.FACING_NORMAL).getHorizontalIndex() + 2 % 4;
            //facing = 1;//DirectionHelper.getNumberForDirection(WorldHelper.getBlockState(getWorld(), pos).getValue(BlockStateHelper.FACING_NORMAL.getProperty()));
        }
        //System.out.println(facing);
        return facing;
    }

    @Override
    @Nonnull
    public CompoundNBT write(CompoundNBT tagCompound) {
        showRecipeSize = tagCompound.getBoolean("shR");
        showShaped = tagCompound.getBoolean("ssH");
        inventory.writeToNBT(tagCompound);
        return super.write(tagCompound);
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        tagCompound.putBoolean("shR", showRecipeSize);
        tagCompound.putBoolean("ssH", showShaped);
        inventory.deserializeNBT(tagCompound);
        super.read(tagCompound);
    }

    @Override
    public void tick() {
        if (CTIVConfig.Client.enableDoor) {
            int xCoord = pos.getX();
            int yCoord = pos.getY();
            int zCoord = pos.getZ();
            PlayerEntity PlayerEntity = Preconditions.checkNotNull(getWorld()).getClosestPlayer((float) xCoord + 0.5F, (float) yCoord + 0.5F, (float) zCoord + 0.5F, 10D, false);
            if (PlayerEntity != null) {
                double playerDistance = PlayerEntity.getDistanceSq(xCoord, yCoord, zCoord);
                float waterFactor = getBlockState().get(BlockProperties.WATERLOGGED) ? 0.2f : 1;
                if (playerDistance < CTIVConfig.Client.doorRange) {
                    doorAngle += openspeed * waterFactor;

                    if (tablestate != 1) {
                        tablestate = 1;
                        if (CTIVConfig.Client.enableNoise) {
                            this.getWorld().playSound(null, xCoord, (double) yCoord + 0.5D, zCoord, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.2F, this.getWorld().rand.nextFloat() * 0.1F + 0.2F);
                        }
                    }

                    if (doorAngle > 1.8F) {
                        doorAngle = 1.8F;
                    }
                } else if (playerDistance > CTIVConfig.Client.doorRange) {
                    doorAngle -= openspeed * waterFactor;

                    if (tablestate != 0) {
                        tablestate = 0;
                        if (CTIVConfig.Client.enableNoise) {
                            this.getWorld().playSound(null, xCoord, (double) yCoord + 0.5D, zCoord, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.2F, this.getWorld().rand.nextFloat() * 0.1F + 0.2F);
                        }
                    }

                    if (doorAngle < 0F) {
                        doorAngle = 0F;
                    }
                }
            }
        }
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

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return inventory.isItemValid(slot, stack);
    }

}
