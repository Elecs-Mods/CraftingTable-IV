package elec332.craftingtableiv.handler;

import elec332.core.java.JavaHelper;
import elec332.core.util.OredictHelper;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Elec332 on 16-6-2015.
 */
public class RecipeStackComparator extends StackComparator {

    public RecipeStackComparator(ItemStack stack) {
        super(stack);
        string = OredictHelper.getOreNames(stack);
        b = true;
    }

    public RecipeStackComparator setCompareOre(boolean b) {
        this.b = b;
        return this;
    }

    private List<String> string;
    private boolean b;

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof RecipeStackComparator && ((b && ((RecipeStackComparator) obj).b) && JavaHelper.hasAtLeastOneMatch(string, ((RecipeStackComparator) obj).string))) || super.equals(obj);
    }
}
