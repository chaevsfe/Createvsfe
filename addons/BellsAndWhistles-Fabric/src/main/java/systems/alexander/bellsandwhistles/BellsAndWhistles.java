package systems.alexander.bellsandwhistles;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import systems.alexander.bellsandwhistles.block.ModBlocks;
import systems.alexander.bellsandwhistles.item.ModCreativeModeTabs;
import systems.alexander.bellsandwhistles.item.ModItems;
import org.slf4j.Logger;

public class BellsAndWhistles implements ModInitializer {
    public static final String MOD_ID = "bellsandwhistles";
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        ModItems.register();
        ModBlocks.register();
        ModCreativeModeTabs.register();
    }
}
