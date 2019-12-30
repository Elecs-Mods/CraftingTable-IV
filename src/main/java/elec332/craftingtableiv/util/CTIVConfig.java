package elec332.craftingtableiv.util;

import elec332.core.api.config.Configurable;

/**
 * Created by Elec332 on 29-12-2019
 */
public class CTIVConfig {

    @Configurable(comment = "Set to 0 to disable recursion", maxValue = 10)
    public static int recursionDepth = 5;

    @Configurable(comment = "Filters nuggets out of the recipeList, only disable if you know what you're doing!")
    public static boolean nuggetFilter = true;

    @Configurable(comment = "Every item from the modID's specified here will not show up in the CraftingTable")
    public static String[] disabledMods = {
            "ztones", "agricraft"
    };

    @Configurable(comment = "Whether to aggressively search for recipe loops, will cause some recipes to search less deep than normal.")
    public static boolean aggressiveLoopCheck = false;

    @Configurable.Class
    public static class Client {

        @Configurable(comment = "Set to false to disable the opening door on the CraftingTable-IV")
        public static boolean enableDoor = true;

        @Configurable(comment = "Set to false to disable the door noise when opening and closing")
        public static boolean enableNoise = true;

        @Configurable(comment = "The squared distance from craftingtable -> player at which the door will start opening.", maxValue = 100)
        public static float doorRange = 7f;

    }

    @Configurable.Class
    public static class Debug {

        @Configurable(comment = "When true, will print messages to the log regarding how long it took to load all recipes in de CTIV bench (when opened)")
        public static boolean debugTimings = false;

    }

}
