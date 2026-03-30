package com.mrh0.createaddition.network;

import com.mrh0.createaddition.CreateAddition;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ConsProdSyncPacket implements CustomPacketPayload {
    public static final Type<ConsProdSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "cons_prod_sync"));
    public static final StreamCodec<FriendlyByteBuf, ConsProdSyncPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> { buf.writeBlockPos(pkt.pos); buf.writeInt(pkt.consumption); buf.writeInt(pkt.production); },
            buf -> new ConsProdSyncPacket(buf.readBlockPos(), buf.readInt(), buf.readInt())
    );

    private final BlockPos pos;
    private final int consumption;
    private final int production;

    public static double clientConsumption = 0;
    public static int clientProduction = 0;

    public ConsProdSyncPacket(BlockPos pos, int consumption, int production) {
        this.pos = pos;
        this.consumption = consumption;
        this.production = production;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void registerServer() {
        PayloadTypeRegistry.playS2C().register(TYPE, STREAM_CODEC);
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            context.client().execute(() -> {
                try {
                    updateClientCache(payload.pos, payload.consumption, payload.production);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private static void updateClientCache(BlockPos pos, int consumption, int production) {
        clientConsumption = consumption;
        clientProduction = production;
    }

    public static void send(BlockPos pos, int consumption, int production, ServerPlayer player) {
        ServerPlayNetworking.send(player, new ConsProdSyncPacket(pos, consumption, production));
    }
}
