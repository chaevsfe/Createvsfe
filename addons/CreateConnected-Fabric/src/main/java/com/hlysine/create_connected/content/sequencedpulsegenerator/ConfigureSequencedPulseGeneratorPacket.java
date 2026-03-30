package com.hlysine.create_connected.content.sequencedpulsegenerator;

import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.Instruction;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ConfigureSequencedPulseGeneratorPacket extends BlockEntityConfigurationPacket<SequencedPulseGeneratorBlockEntity> {

    private ListTag instructions;

    public ConfigureSequencedPulseGeneratorPacket(BlockPos pos, Tag instructions) {
        super(pos);
        this.instructions = (ListTag) instructions;
    }

    public ConfigureSequencedPulseGeneratorPacket(RegistryFriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected void readSettings(RegistryFriendlyByteBuf buffer) {
        CompoundTag wrapper = buffer.readNbt();
        this.instructions = wrapper != null ? (ListTag) wrapper.get("data") : new ListTag();
    }

    @Override
    protected void writeSettings(RegistryFriendlyByteBuf buffer) {
        CompoundTag wrapper = new CompoundTag();
        wrapper.put("data", instructions);
        buffer.writeNbt(wrapper);
    }

    @Override
    protected int maxRange() {
        return 16;
    }

    @Override
    protected void applySettings(ServerPlayer player, SequencedPulseGeneratorBlockEntity be) {
        be.currentInstruction = -1;
        be.instructions = Instruction.deserializeAll(instructions);
        be.sendData();
    }

    @Override
    protected void applySettings(SequencedPulseGeneratorBlockEntity be) {
        // handled by applySettings(player, be)
    }
}
