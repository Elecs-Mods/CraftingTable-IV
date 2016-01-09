package elec332.craftingtableiv.abstraction.handler;

import elec332.core.helper.OredictHelper;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 16-6-2015.
 */
public class RecipeStackComparator extends StackComparator {

    public RecipeStackComparator(ItemStack stack) {
        super(stack);
        string = OredictHelper.getOreName(stack);
        b = true;
    }

    public RecipeStackComparator setCompareOre(boolean b) {
        this.b = b;
        return this;
    }

    private String string;
    private boolean b;

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof RecipeStackComparator && ((b && ((RecipeStackComparator) obj).b) && string.equals(((RecipeStackComparator) obj).string))) || super.equals(obj);
    }
}
