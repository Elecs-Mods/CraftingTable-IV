package elec332.craftingtableiv.abstraction.handler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Elec332 on 16-6-2015.
 */
@Deprecated
public class RecipeStackComparator extends StackComparator {

    public RecipeStackComparator(ItemStack stack) {
        super(stack);
        int[] id = OreDictionary.getOreIDs(stack);
        string = OreDictionary.getOreName(id.length > 0 ? id[0] : -1);
        b = !string.equals("Unknown");
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
