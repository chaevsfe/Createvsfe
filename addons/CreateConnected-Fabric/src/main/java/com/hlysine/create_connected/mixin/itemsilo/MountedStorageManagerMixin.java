package com.hlysine.create_connected.mixin.itemsilo;

import com.hlysine.create_connected.content.itemsilo.ItemSiloBlockEntity;
import com.simibubi.create.content.contraptions.MountedStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Ensures ItemSilo block entities are properly recognized by the old MountedStorage system.
 * UfoPort uses the legacy MountedStorage system (not NeoForge 6.0.9's MountedItemStorageType API),
 * so we inject into addBlock to ensure item silos are stored correctly.
 */
@Mixin(value = MountedStorageManager.class, remap = false)
public abstract class MountedStorageManagerMixin {
    @Shadow
    protected Map<BlockPos, MountedStorage> storage;

    @Inject(
            method = "addBlock",
            at = @At("TAIL")
    )
    private void ensureItemSiloStorage(BlockPos localPos, BlockEntity be, CallbackInfo ci) {
        // ItemSiloBlockEntity implements SidedStorageBlockEntity so canUseAsStorage already picks it up.
        // This injection is a safety net — if the standard path missed it, force-add it here.
        if (be instanceof ItemSiloBlockEntity && !storage.containsKey(localPos)) {
            storage.put(localPos, new MountedStorage(be));
        }
    }
}
