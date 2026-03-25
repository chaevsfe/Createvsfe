package com.simibubi.create.compat.computercraft.implementation.peripherals;

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.compat.computercraft.events.PackageEvent;
import com.simibubi.create.compat.computercraft.implementation.ComputerUtil;
import com.simibubi.create.compat.computercraft.implementation.luaObjects.PackageLuaObject;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.minecraft.world.item.ItemStack;

public class PackagerPeripheral extends SyncedPeripheral<PackagerBlockEntity> {

	public PackagerPeripheral(PackagerBlockEntity blockEntity) {
		super(blockEntity);
	}

	@Override
	public void attach(@NotNull IComputerAccess computer) {
		super.attach(computer);
		// Ephemeral nature of address, should not be set on load until a computer
		// explicitly calls setAddress again on the BE.
		blockEntity.hasCustomComputerAddress = false;
	}

	@Override
	public void detach(@NotNull IComputerAccess computer) {
		super.detach(computer);
		// Ephemeral nature of address, should not be set on load until a computer
		// explicitly calls setAddress again on the BE.
		blockEntity.hasCustomComputerAddress = false;
	}

	@LuaFunction(mainThread = true)
	public final boolean makePackage() {
		if (!blockEntity.heldBox.isEmpty())
			return false;
		blockEntity.activate();
		if (blockEntity.heldBox.isEmpty())
			return false;
		return true;
	}

	@LuaFunction(mainThread = true)
	public Map<Integer, Map<String, ?>> list() {
		ItemStackHandler inv = getTargetInventoryHandler();
		if (inv == null) return Map.of();
		return ComputerUtil.list(inv);
	}

	@LuaFunction(mainThread = true)
	public Map<String, ?> getItemDetail(int slot) throws LuaException {
		ItemStackHandler inv = getTargetInventoryHandler();
		if (inv == null) return null;
		return ComputerUtil.getItemDetail(inv, slot);
	}

	private ItemStackHandler getTargetInventoryHandler() {
		if (blockEntity.targetInventory == null || !blockEntity.targetInventory.hasInventory())
			return null;
		// Storage<ItemVariant> is a Fabric Transfer API type — we can only use ItemStackHandler for CC
		// The targetInventory is an InvManipulationBehaviour using Fabric Transfer API
		// For now, return null (inventory listing not available via Transfer API directly)
		return null;
	}

	@LuaFunction(mainThread = true)
	public final String getAddress() {
		blockEntity.updateSignAddress();
		return blockEntity.signBasedAddress;
	}

	@LuaFunction(mainThread = true)
	public final void setAddress(Optional<String> argument) {
		if (argument.isPresent()) {
			blockEntity.customComputerAddress = argument.get();
			blockEntity.signBasedAddress = argument.get();
			blockEntity.hasCustomComputerAddress = true;
		} else {
			blockEntity.customComputerAddress = "";
			blockEntity.hasCustomComputerAddress = false;
		}
	}

	@LuaFunction(mainThread = true)
	public final PackageLuaObject getPackage() {
		ItemStack box = blockEntity.heldBox;
		if (box.isEmpty())
			return null;
		return new PackageLuaObject(blockEntity, box);
	}

	@Override
	public void prepareComputerEvent(@NotNull ComputerEvent event) {
		if (event instanceof PackageEvent pe) {
			queueEvent(pe.status, new PackageLuaObject(blockEntity, pe.box));
		}
	}

	@NotNull
	@Override
	public String getType() {
		return "Create_Packager";
	}

}
