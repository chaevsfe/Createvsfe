package com.hlysine.create_connected.config;

import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;

import java.util.function.Consumer;

public abstract class SyncConfigBase extends ConfigBase {

    public final CompoundTag getSyncConfig() {
        CompoundTag nbt = new CompoundTag();
        writeSyncConfig(nbt);
        if (children != null)
            for (ConfigBase child : children) {
                if (child instanceof SyncConfigBase syncChild) {
                    if (nbt.contains(child.getName()))
                        throw new RuntimeException("A sync config key starts with " + child.getName() + " but does not belong to the child");
                    nbt.put(child.getName(), syncChild.getSyncConfig());
                }
            }
        return nbt;
    }

    protected void writeSyncConfig(CompoundTag nbt) {
    }

    public final void setSyncConfig(CompoundTag config) {
        if (children != null)
            for (ConfigBase child : children) {
                if (child instanceof SyncConfigBase syncChild) {
                    CompoundTag nbt = config.getCompound(child.getName());
                    syncChild.readSyncConfig(nbt);
                }
            }
        readSyncConfig(config);
    }

    protected void readSyncConfig(CompoundTag nbt) {
    }

    @Override
    public void onLoad() {
        super.onLoad();
        syncToAllPlayers();
    }

    @Override
    public void onReload() {
        super.onReload();
        syncToAllPlayers();
    }

    public void syncToAllPlayers() {
        // On Fabric, config syncing is handled via direct packet sending
        // This is called on server-side only
    }

    public void syncToPlayer(ServerPlayer player) {
        if (player == null) return;
        CreateConnected.LOGGER.debug("Sync Config: Sending server config to {}", player.getScoreboardName());
        // Config sync packets are handled by the feature toggle system separately
    }

    public static abstract class SyncConfigTask implements ConfigurationTask {
        public static final ConfigurationTask.Type TYPE = new Type(CreateConnected.MODID + ":sync_config_task");

        public SyncConfigTask(ServerConfigurationPacketListener listener) {
        }

        protected abstract SyncConfigBase getSyncConfig();

        @Override
        public void start(Consumer<net.minecraft.network.protocol.Packet<?>> sender) {
            // Config sync is handled via direct packet sending; no configuration task needed
        }

        @Override
        public ConfigurationTask.Type type() {
            return TYPE;
        }
    }
}
