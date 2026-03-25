package com.simibubi.create.compat.computercraft.implementation.peripherals;

import java.util.Map;
import java.util.Optional;

import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.compat.computercraft.events.PackageEvent;
import com.simibubi.create.compat.computercraft.events.RepackageEvent;
import com.simibubi.create.compat.computercraft.implementation.ComputerUtil;
import com.simibubi.create.compat.computercraft.implementation.luaObjects.PackageLuaObject;
import org.jetbrains.annotations.NotNull;

import com.simibubi.create.content.logistics.packager.repackager.RepackagerBlockEntity;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.world.item.ItemStack;

public class RepackagerPeripheral extends SyncedPeripheral<RepackagerBlockEntity> {

	public RepackagerPeripheral(RepackagerBlockEntity blockEntity) {
		super(blockEntity);
	}

	@Override
	public void attach(@NotNull IComputerAccess computer) {
		super.attach(computer);
		blockEntity.hasCustomComputerAddress = false;
	}

	@Override
	public void detach(@NotNull IComputerAccess computer) {
		super.detach(computer);
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
		// Transfer API inventory — not directly accessible as ItemStackHandler
		return Map.of();
	}

	@LuaFunction(mainThread = true)
	public Map<String, ?> getItemDetail(int slot) throws LuaException {
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
		if (event instanceof RepackageEvent pe) {
			queueEvent("package_repackaged", new PackageLuaObject(blockEntity, pe.box), pe.count);
		} else if (event instanceof PackageEvent pe) {
			queueEvent(pe.status, new PackageLuaObject(blockEntity, pe.box));
		}
	}

	@NotNull
	@Override
	public String getType() {
		return "Create_Repackager";
	}

}
