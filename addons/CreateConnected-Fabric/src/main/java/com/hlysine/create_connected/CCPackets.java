package com.hlysine.create_connected;

import com.hlysine.create_connected.content.contraption.jukebox.PlayContraptionJukeboxPacket;
import com.hlysine.create_connected.content.sequencedpulsegenerator.ConfigureSequencedPulseGeneratorPacket;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

public class CCPackets {

    public static final ResourceLocation CHANNEL_NAME = CreateConnected.asResource("main");
    public static final int NETWORK_VERSION = 1;
    public static SimpleChannel channel;

    public static void register() {
        channel = new SimpleChannel(CHANNEL_NAME);
        int index = 0;
        // C2S packets
        channel.registerC2SPacket(ConfigureSequencedPulseGeneratorPacket.class, index++, ConfigureSequencedPulseGeneratorPacket::new);
        // S2C packets
        channel.registerS2CPacket(PlayContraptionJukeboxPacket.class, index++, PlayContraptionJukeboxPacket::new);
    }

    public static SimpleChannel getChannel() {
        return channel;
    }
}
