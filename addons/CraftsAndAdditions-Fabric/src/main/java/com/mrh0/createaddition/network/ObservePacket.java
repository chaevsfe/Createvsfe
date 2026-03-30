package com.mrh0.createaddition.network;

import com.mrh0.createaddition.CreateAddition;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ObservePacket implements CustomPacketPayload {
    public static final Type<ObservePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "observe"));
    public static final StreamCodec<FriendlyByteBuf, ObservePacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> { buf.writeBlockPos(pkt.pos); buf.writeInt(pkt.node); },
            buf -> new ObservePacket(buf.readBlockPos(), buf.readInt())
    );

    private final BlockPos pos;
    private final int node;

    public ObservePacket(BlockPos pos, int node) {
        this.pos = pos;
        this.node = node;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void registerServer() {
        PayloadTypeRegistry.playC2S().register(TYPE, STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            context.server().execute(() -> {
                try {
                    ServerPlayer player = context.player();
                    if (player != null) {
                        sendUpdate(payload, player);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public static void registerClient() {
        // No S2C for this packet
    }

    private static void sendUpdate(ObservePacket pkt, ServerPlayer player) {
        BlockEntity te = player.level().getBlockEntity(pkt.pos);
        if (te != null) {
            if (te instanceof IObserveTileEntity ote) {
                ote.onObserved(player, pkt);
                Packet<ClientGamePacketListener> updatePacket = te.getUpdatePacket();
                if (updatePacket != null)
                    player.connection.send(updatePacket);
            }
        }
    }

    private static int cooldown = 0;

    public static void tick() {
        cooldown--;
        if (cooldown < 0) cooldown = 0;
    }

    public static boolean send(BlockPos pos, int node) {
        if (cooldown > 0) return false;
        cooldown = 10;
        ClientPlayNetworking.send(new ObservePacket(pos, node));
        return true;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getNode() {
        return node;
    }
}
