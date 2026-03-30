package com.hlysine.create_connected.content.contraption.jukebox;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.JukeboxSong;

import java.util.Optional;

public class PlayContraptionJukeboxPacket extends SimplePacketBase {

    protected ResourceLocation level;
    protected int contraptionId;
    protected BlockPos contraptionPos;
    protected BlockPos worldPos;
    protected int recordId;
    protected boolean play;
    protected boolean silent;

    public PlayContraptionJukeboxPacket(ResourceLocation level, int contraptionId, BlockPos contraptionPos, BlockPos worldPos, int recordId, boolean play, boolean silent) {
        this.level = level;
        this.contraptionId = contraptionId;
        this.contraptionPos = contraptionPos;
        this.worldPos = worldPos;
        this.recordId = recordId;
        this.play = play;
        this.silent = silent;
    }

    public PlayContraptionJukeboxPacket(RegistryFriendlyByteBuf buffer) {
        this.level = ResourceLocation.STREAM_CODEC.decode(buffer);
        this.contraptionId = buffer.readVarInt();
        this.contraptionPos = BlockPos.STREAM_CODEC.decode(buffer);
        this.worldPos = BlockPos.STREAM_CODEC.decode(buffer);
        this.recordId = buffer.readVarInt();
        this.play = buffer.readBoolean();
        this.silent = buffer.readBoolean();
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        ResourceLocation.STREAM_CODEC.encode(buffer, level);
        buffer.writeVarInt(contraptionId);
        BlockPos.STREAM_CODEC.encode(buffer, contraptionPos);
        BlockPos.STREAM_CODEC.encode(buffer, worldPos);
        buffer.writeVarInt(recordId);
        buffer.writeBoolean(play);
        buffer.writeBoolean(silent);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean handle(Context context) {
        context.enqueueWork(this::handleClient);
        return true;
    }

    @Environment(EnvType.CLIENT)
    private void handleClient() {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null || !world.dimension().location().equals(level))
            return;
        if (!world.isLoaded(worldPos))
            return;
        Entity entity = world.getEntity(contraptionId);
        if (!(entity instanceof AbstractContraptionEntity contraptionEntity))
            return;
        if (play) {
            Optional<JukeboxSong> song = world.registryAccess()
                    .registryOrThrow(Registries.JUKEBOX_SONG)
                    .getHolder(recordId)
                    .map(Holder.Reference::value);
            if (song.isEmpty())
                return;
            ContraptionMusicManager.playContraptionMusic(
                    song.get(),
                    contraptionEntity,
                    contraptionPos,
                    worldPos,
                    silent
            );
        } else {
            ContraptionMusicManager.playContraptionMusic(
                    null,
                    contraptionEntity,
                    contraptionPos,
                    worldPos,
                    silent
            );
        }
    }
}
