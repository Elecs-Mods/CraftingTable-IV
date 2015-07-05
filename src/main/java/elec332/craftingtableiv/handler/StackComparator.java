package elec332.craftingtableiv.handler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Elec332 on 11-6-2015.
 */
public class StackComparator extends ItemComparator{
    public StackComparator(ItemStack stack) {
        super(stack);
    }

    public boolean stacksEqual(ItemStack s1){
        if(s1.getItem() == stack.getItem()) {
            if(s1.getItemDamage() == stack.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE || s1.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                return true;
            }
            if(!s1.getItem().getHasSubtypes() && !stack.getItem().getHasSubtypes()) {
                return true;
            }
            return true;
        }
        return false;
    }
}
