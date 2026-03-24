package com.simibubi.create.content.logistics.factoryBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class FactoryPanelConfigurationPacket extends BlockEntityConfigurationPacket<FactoryPanelBlockEntity> {

	private FactoryPanelPosition position;
	private String address;
	private Map<FactoryPanelPosition, Integer> inputAmounts;
	private List<ItemStack> craftingArrangement;
	private int outputAmount;
	private int promiseClearingInterval;
	@Nullable
	private FactoryPanelPosition removeConnection;
	private boolean clearPromises;
	private boolean reset;
	private boolean redstoneReset;

	public FactoryPanelConfigurationPacket(FactoryPanelPosition position, String address,
		Map<FactoryPanelPosition, Integer> inputAmounts, List<ItemStack> craftingArrangement, int outputAmount,
		int promiseClearingInterval, @Nullable FactoryPanelPosition removeConnection, boolean clearPromises,
		boolean reset, boolean sendRedstoneReset) {
		super(position.pos());
		this.position = position;
		this.address = address;
		this.inputAmounts = inputAmounts;
		this.craftingArrangement = craftingArrangement;
		this.outputAmount = outputAmount;
		this.promiseClearingInterval = promiseClearingInterval;
		this.removeConnection = removeConnection;
		this.clearPromises = clearPromises;
		this.reset = reset;
		this.redstoneReset = sendRedstoneReset;
	}

	public FactoryPanelConfigurationPacket(RegistryFriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	protected void writeSettings(RegistryFriendlyByteBuf buffer) {
		FactoryPanelPosition.STREAM_CODEC.encode(buffer, position);
		buffer.writeUtf(address);

		buffer.writeVarInt(inputAmounts.size());
		for (Entry<FactoryPanelPosition, Integer> entry : inputAmounts.entrySet()) {
			FactoryPanelPosition.STREAM_CODEC.encode(buffer, entry.getKey());
			buffer.writeVarInt(entry.getValue());
		}

		buffer.writeVarInt(craftingArrangement.size());
		for (ItemStack stack : craftingArrangement)
			ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, stack);

		buffer.writeVarInt(outputAmount);
		buffer.writeVarInt(promiseClearingInterval);

		buffer.writeBoolean(removeConnection != null);
		if (removeConnection != null)
			FactoryPanelPosition.STREAM_CODEC.encode(buffer, removeConnection);

		buffer.writeBoolean(clearPromises);
		buffer.writeBoolean(reset);
		buffer.writeBoolean(redstoneReset);
	}

	@Override
	protected void readSettings(RegistryFriendlyByteBuf buffer) {
		position = FactoryPanelPosition.STREAM_CODEC.decode(buffer);
		address = buffer.readUtf();

		int inputCount = buffer.readVarInt();
		inputAmounts = new HashMap<>();
		for (int i = 0; i < inputCount; i++) {
			FactoryPanelPosition key = FactoryPanelPosition.STREAM_CODEC.decode(buffer);
			int value = buffer.readVarInt();
			inputAmounts.put(key, value);
		}

		int craftingCount = buffer.readVarInt();
		craftingArrangement = new ArrayList<>();
		for (int i = 0; i < craftingCount; i++)
			craftingArrangement.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer));

		outputAmount = buffer.readVarInt();
		promiseClearingInterval = buffer.readVarInt();

		if (buffer.readBoolean())
			removeConnection = FactoryPanelPosition.STREAM_CODEC.decode(buffer);
		else
			removeConnection = null;

		clearPromises = buffer.readBoolean();
		reset = buffer.readBoolean();
		redstoneReset = buffer.readBoolean();
	}

	@Override
	protected void applySettings(ServerPlayer player, FactoryPanelBlockEntity be) {
		FactoryPanelBehaviour behaviour = be.panels.get(position.slot());
		if (behaviour == null)
			return;

		behaviour.recipeAddress = reset ? "" : address;
		behaviour.recipeOutput = reset ? 1 : outputAmount;
		behaviour.promiseClearingInterval = reset ? -1 : promiseClearingInterval;
		behaviour.activeCraftingArrangement = reset ? List.of() : craftingArrangement;

		if (reset) {
			behaviour.forceClearPromises = true;
			behaviour.disconnectAll();
			behaviour.setFilter(ItemStack.EMPTY);
			behaviour.count = 0;
			be.redraw = true;
			be.notifyUpdate();
			return;
		}

		if (redstoneReset) {
			behaviour.disconnectAllLinks();
			be.notifyUpdate();
			return;
		}

		for (Entry<FactoryPanelPosition, Integer> entry : inputAmounts.entrySet()) {
			FactoryPanelPosition key = entry.getKey();
			FactoryPanelConnection connection = behaviour.targetedBy.get(key);
			if (connection != null)
				connection.amount = entry.getValue();
		}

		if (removeConnection != null) {
			behaviour.targetedBy.remove(removeConnection);
			FactoryPanelBehaviour source = FactoryPanelBehaviour.at(be.getLevel(), removeConnection);
			if (source != null) {
				source.targeting.remove(behaviour.getPanelPosition());
				source.blockEntity.sendData();
			}
		}

		if (clearPromises)
			behaviour.forceClearPromises = true;

		be.notifyUpdate();
	}

	@Override
	protected void applySettings(FactoryPanelBlockEntity be) {}
}
