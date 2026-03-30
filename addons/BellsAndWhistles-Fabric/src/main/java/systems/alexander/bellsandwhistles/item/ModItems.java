package systems.alexander.bellsandwhistles.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import systems.alexander.bellsandwhistles.BellsAndWhistles;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModItems {
    public static final Map<ResourceLocation, Item> ITEMS = new LinkedHashMap<>();

    public static Item register(String name, Supplier<Item> supplier) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(BellsAndWhistles.MOD_ID, name);
        Item item = supplier.get();
        // Register immediately to avoid intrusive holder leak
        Registry.register(BuiltInRegistries.ITEM, id, item);
        ITEMS.put(id, item);
        return item;
    }

    public static void register() {
        // Items are now registered during static init in register(name, supplier)
        // This method just forces class loading
    }
}
