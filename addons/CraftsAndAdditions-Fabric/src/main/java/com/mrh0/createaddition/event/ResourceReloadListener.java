package com.mrh0.createaddition.event;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
public class ResourceReloadListener implements IdentifiableResourceReloadListener {
    @Override public ResourceLocation getFabricId() { return ResourceLocation.fromNamespaceAndPath("createaddition", "reload_listener"); }
    @Override public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, ProfilerFiller prep, ProfilerFiller apply, Executor prepExec, Executor applyExec) {
        return barrier.wait(null).thenRunAsync(() -> {}, applyExec);
    }
}
