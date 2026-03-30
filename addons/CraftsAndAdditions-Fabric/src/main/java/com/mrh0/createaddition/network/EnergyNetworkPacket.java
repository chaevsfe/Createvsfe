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

public class EnergyNetworkPacket implements CustomPacketPayload {
    public static final Type<EnergyNetworkPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "energy_network"));
    public static final StreamCodec<FriendlyByteBuf, EnergyNetworkPacket> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> { buf.writeBlockPos(pkt.pos); buf.writeInt(pkt.demand); buf.writeInt(pkt.buff); },
            buf -> new EnergyNetworkPacket(buf.readBlockPos(), buf.readInt(), buf.readInt())
    );

    private final BlockPos pos;
    private final int demand;
    private final int buff;

    public static double clientSaturation = 0;
    public static int clientDemand = 0;
    public static int clientBuff = 0;

    public EnergyNetworkPacket(BlockPos pos, int demand, int buff) {
        this.pos = pos;
        this.demand = demand;
        this.buff = buff;
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
                    updateClientCache(payload.pos, payload.demand, payload.buff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private static void updateClientCache(BlockPos pos, int demand, int buff) {
        clientDemand = demand;
        clientBuff = buff;
        clientSaturation = buff - demand;
    }

    public static void send(BlockPos pos, int demand, int buff, ServerPlayer player) {
        ServerPlayNetworking.send(player, new EnergyNetworkPacket(pos, demand, buff));
    }
}
