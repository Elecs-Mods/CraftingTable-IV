package elec332.craftingtableiv.handler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Elec332 on 7-6-2015.
 */
public class ItemComparator {

    public ItemComparator(ItemStack stack){
        this.stack = stack;
        if (stack == null || stack.getItem() == null)
            throw new IllegalArgumentException("Invalid ItemStack!");
    }

    protected ItemStack stack;

    public ItemStack getStack() {
        return stack.copy();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ItemStack ? stacksEqual((ItemStack) obj) : ((obj instanceof ItemComparator) && stacksEqual(((ItemComparator) obj).getStack()));
    }

    protected boolean stacksEqual(ItemStack s1){
        if(s1.getItem() == stack.getItem()) {
            /*if(s1.getItemDamage() == stack.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE || s1.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                return true;
            }
            if(!s1.getItem().getHasSubtypes() && !stack.getItem().getHasSubtypes()) {
                return true;
            }*/
            return true;
        }
        return false;
    }
}
