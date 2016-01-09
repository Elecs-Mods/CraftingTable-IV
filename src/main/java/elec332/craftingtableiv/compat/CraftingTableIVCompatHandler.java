package elec332.craftingtableiv.compat;

import elec332.core.util.AbstractCompatHandler;
import elec332.craftingtableiv.CraftingTableIV;
import elec332.craftingtableiv.abstraction.CraftingTableIVAbstractionLayer;

/**
 * Created by Elec332 on 3-10-2015.
 */
public class CraftingTableIVCompatHandler extends AbstractCompatHandler {

    public CraftingTableIVCompatHandler() {
        super(CraftingTableIVAbstractionLayer.instance.getConfig(), CraftingTableIV.logger);
    }

    @Override
    public void loadList() {

    }
}
